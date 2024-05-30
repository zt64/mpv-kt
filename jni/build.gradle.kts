plugins {
    alias(libs.plugins.nokee.jni)
    alias(libs.plugins.nokee.cpp)
}

library {
    targetMachines = listOf(
        machines.linux,
        machines.windows,
        machines.macOS
    )

    variants.configureEach {
        sharedLibrary {
            compileTasks.configureEach {
                this as AbstractNativeCompileTask

                includes("/usr/include")
            }

            linkTask {
                linkerArgs.add("-lmpv")
                linkerArgs.add("-lGL")
                linkerArgs.add("-lGLEW")
            }
        }

        resourcePath = "libs/"
    }
}