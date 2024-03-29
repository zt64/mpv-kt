# mpv-kt

![Maven Central Version](https://img.shields.io/maven-central/v/dev.zt64.mpvkt/mpvkt)
![GitHub License](https://img.shields.io/github/license/zt64/mpv-kt)

Kotlin multiplatform wrapper for mpv

## Supported Platforms

- Linux (x86_64, arm64)
- Windows (x86_64)
- macOS (x86_64, arm64)
- Android (arm64-v8a, x86_64)
- JVM

## Setup

### Gradle

```
implementation("dev.zt64:mpvkt:x.y.z")
```

## Usage

```kt
// Create a mpv instance by calling the constructor
val mpv = Mpv()

mpv.requestLogMessages("v")
mpv.setProperty()

// Set up the mpv instance
mpv.init()

mpv.command("loadfile", "path/to/file.mp4")

while (true) {
    val event = mpv.waitEvent()
    if (event is MpvEvent.FileLoaded) {
        break
    }
}

mpv.close()
```

## Contributing

Contributions are welcome!

## License

This project is licensed under the GNU GPL v3.0 License - see the [LICENSE](LICENSE) file for details
