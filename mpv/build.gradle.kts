import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import dev.zt64.mpvkt.gradle.native
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import tel.schich.dockcross.execute.DockerRunner
import tel.schich.dockcross.execute.NonContainerRunner
import tel.schich.dockcross.tasks.DockcrossRunTask

plugins {
    id("kmp-lib")
    alias(libs.plugins.android.library)
    alias(libs.plugins.kover)
    alias(libs.plugins.publish)
    alias(libs.plugins.dockcross)
    alias(libs.plugins.shadow)
}

description = "libmpv Kotlin bindings"

val isCi = System.getenv("CI") != null

val archDetectConfiguration by configurations.registering {
    isCanBeConsumed = true
}

val createJniDir by tasks.registering {
    doLast {
        val jniDir = layout.buildDirectory.dir("jni").get().asFile
        if (!jniDir.exists()) {
            jniDir.mkdirs()
        }
    }
}

val jniGluePath = layout.buildDirectory.get().dir("jni/")

enum class NativeLinkMode {
    DYNAMIC,
    STATIC
}

data class BuildTarget(
    val image: String?,
    val classifier: String,
    val mode: NativeLinkMode,
    val archDetect: Boolean
)

val nativeGroup = "native"
val targets = buildList {
    fun MutableList<BuildTarget>.add(
        platform: String,
        image: String,
        classifier: String,
        mode: NativeLinkMode = NativeLinkMode.DYNAMIC,
        archDetect: Boolean = false
    ) = add(BuildTarget("$platform-$image", "$platform-$classifier", mode, archDetect))

    add(platform = "linux", image = "x64", classifier = "x86_64", archDetect = true)
    add(platform = "linux", image = "x86", classifier = "x86_32", archDetect = true)
    add(platform = "linux", image = "armv7", classifier = "armv7", archDetect = true)
    add(platform = "linux", image = "armv7a", classifier = "armv7a", archDetect = true)
    add(platform = "linux", image = "arm64", classifier = "aarch64", archDetect = true)
    add(platform = "linux", image = "riscv32", classifier = "riscv32", archDetect = true)
    add(platform = "linux", image = "riscv64", classifier = "riscv64", archDetect = true)
    add(platform = "android", image = "arm", classifier = "arm")
    add(platform = "android", image = "arm64", classifier = "arm64")
    add(platform = "android", image = "x86_64", classifier = "x86_64")
    add(platform = "android", image = "x86", classifier = "x86_32")
    add(platform = "windows", image = "shared-x64", classifier = "x86_64")
}

val compileNativeAll by tasks.registering(DefaultTask::class) {
    group = nativeGroup
}

val compileNativeAllExceptAndroid by tasks.registering(DefaultTask::class) {
    group = nativeGroup
}

val buildReleaseBinaries = project.findProperty("mpv-kt.build-release-binaries")
    ?.toString()
    .toBoolean()

fun Project.dockcrossProp(prop: String, classifier: String) =
    findProperty("dockcross.$prop.$classifier")?.toString()

fun DockcrossRunTask.baseConfigure(linkMode: NativeLinkMode, outputTo: Directory) {
    group = nativeGroup

    inputs.dir(project.layout.projectDirectory.dir("native/include"))
    inputs.dir(project.layout.projectDirectory.dir("native/src"))
    inputs.file(project.layout.projectDirectory.file("native/CMakeLists.txt"))

    mountSource = project.rootProject.layout.projectDirectory.asFile

    // dependsOn(tasks.withType<KotlinJvmCompile>())

    javaHome = javaToolchains.launcherFor(java.toolchain).map { it.metadata.installationPath }
    output = outputTo.dir("native")

    val relativePathToProject =
        output.get().asFile.toPath().relativize(project.layout.projectDirectory.asFile.toPath())
            .toString()

    script = listOf(
        listOf(
            "meson",
            "setup",
            "./",
            "$relativePathToProject/native",
            "--buildtype=${if (buildReleaseBinaries) "release" else "debug"}",
            "--cross-file=$relativePathToProject/native/native.ini",
            "-Dfribidi:tests=false"
        ),
        listOf("meson", "compile", "-C", "./")
    )
}

fun Jar.baseConfigure(compileTask: TaskProvider<DockcrossRunTask>, buildOutputDir: Directory) {
    group = nativeGroup

    dependsOn(compileTask)

    from(buildOutputDir) {
        include("native/*.so")
    }
}

for (target in targets) {
    val classifier = target.classifier
    val dockcrossVersion = "20240727-3995c0c"
    val dockcrossImage = project.dockcrossProp(prop = "image", classifier)
        ?: target.image?.let { "docker.io/dockcross/$it:$dockcrossVersion" }
        ?: error("No image configured for target: $target")

    val (repo, tag) = dockcrossImage.split(":", limit = 2)
    val linkMode = (project.dockcrossProp(prop = "link-mode", classifier) ?: target.mode.name)
        .uppercase().let(NativeLinkMode::valueOf)

    val buildOutputDir = project.layout.buildDirectory.dir("dockcross/$classifier")
    val taskSuffix = classifier.split("[-]".toRegex())
        .joinToString(separator = "") { it.lowercase().replaceFirstChar(Char::uppercase) }

    val compileNative = tasks.register("compileNativeFor$taskSuffix", DockcrossRunTask::class) {
        baseConfigure(linkMode, buildOutputDir.get())

        dockcrossRepository = repo
        dockcrossTag = tag
        image = dockcrossImage
        containerName = "dockcross-${project.name}-$classifier"

        if (isCi) {
            runner(DockerRunner())
            doLast {
                exec {
                    commandLine("docker", "image", "rm", "$repo:$tag")
                }
            }
        }
    }


    val packageNative = tasks.register("packageNativeFor$taskSuffix", Jar::class) {
        baseConfigure(compileNative, buildOutputDir.get())

        archiveClassifier = classifier
    }

    publishing.publications.withType<MavenPublication>().configureEach {
        artifact(packageNative)
    }

    compileNativeAll.configure {
        dependsOn(packageNative)
    }

    if (!classifier.startsWith("android-")) {
        compileNativeAllExceptAndroid.configure {
            dependsOn(packageNative)
        }
    }

    if (target.archDetect) {
        artifacts.add(archDetectConfiguration.name, packageNative)
    }
}

val nativeForHostOutputDir: Directory = project.layout.buildDirectory.dir("dockcross/host").get()
val compileNativeForHost by tasks.registering(DockcrossRunTask::class) {
    baseConfigure(NativeLinkMode.DYNAMIC, nativeForHostOutputDir)
    image = "host"
    runner(NonContainerRunner)
    unsafeWritableMountSource = true
}

val packageNativeForHost by tasks.registering(Jar::class) {
    baseConfigure(compileNativeForHost, nativeForHostOutputDir)
    archiveClassifier = "host"
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    explicitApi()

    applyDefaultHierarchyTemplate {
        common {
            group("jvmCommon") {
                withAndroidTarget()
                withJvm()
            }
        }
    }

    jvm()

    androidTarget {
        publishLibraryVariants("release")
    }

    native {
        compilations.getByName("main") {
            cinterops.create("libmpv")
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
                implementation(libs.io)
            }
        }

        named("jvmCommonMain") {
            dependencies {
            }
        }

        jvmMain {
            dependencies {
                files(tasks.named("packageNativeForLinuxX86_64").get()).also {
                    implementation(it)
                }
            }
        }
    }

    compilerOptions {
        optIn.addAll("kotlinx.cinterop.ExperimentalForeignApi")
    }
}

tasks {
    val compileKotlinJvm by existing(KotlinJvmCompile::class)

    compileKotlinJvm.configure {
        dependsOn(createJniDir)
        // options.compilerArgs.addAll(listOf("-Agenerate.jni.headers=true"))
        // options.headerOutputDirectory = jniGluePath
    }

    withType(AbstractCompile::class.java).configureEach {
        dependsOn(createJniDir)
    }

    // add output of compiled libraries to the jar file resources
}

val nativeLibs by configurations.registering

android {
    namespace = "$group"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        // externalNativeBuild {
        //     ndkBuild {
        //         targets("Foo")
        //     }
        // }
    }

    // externalNativeBuild {
    //     cmake {
    //         path = file("src/androidMain/cpp/CMakeLists.txt")
    //     }
    // }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()
    coordinates("$group", "mpv-kt", "$version")
    configure(KotlinMultiplatform(sourcesJar = true))

    pom {
        val githubUrlBase = "github.com/zt64/mpv-kt"
        val githubUrl = "https://$githubUrlBase"

        name = "mpv-kt"
        description = "MPV Kotlin bindings"
        inceptionYear = "2024"
        url = githubUrl

        licenses {
            license {
                name = "MIT"
                url = "https://opensource.org/licenses/MIT"
            }
        }

        developers {
            developer {
                id = "zt64"
                name = "zeet"
                url = "https://zt64.dev"
            }
        }

        scm {
            url = githubUrl
            connection = "scm:git:$githubUrlBase.git"
            developerConnection = "scm:git:ssh://$githubUrlBase.git"
        }
    }
}