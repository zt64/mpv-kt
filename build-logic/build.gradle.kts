plugins {
    `kotlin-dsl`
}

fun DependencyHandler.compileOnly(dependency: Provider<PluginDependency>) {
    compileOnly(dependency.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" })
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