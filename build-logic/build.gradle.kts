plugins {
    `kotlin-dsl`
}

fun Provider<PluginDependency>.mapVersion(): Provider<String> {
    return map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
}

fun DependencyHandler.compileOnly(dependency: Provider<PluginDependency>) {
    compileOnly(dependency.mapVersion())
}

fun DependencyHandler.implementation(dependency: Provider<PluginDependency>) {
    implementation(dependency.mapVersion())
}

dependencies {
    compileOnly(libs.plugins.kotlin.multiplatform)
}

gradlePlugin {
    plugins {
        register("kmp-lib") {
            id = "kmp-lib"
            implementationClass = "dev.zt64.mpvkt.gradle.KmpLibPlugin"
        }
    }
}