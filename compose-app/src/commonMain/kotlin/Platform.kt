package top.ltfan.labailearn

interface Platform {
    val name: String
}

expect val platform: Platform

interface JvmPlatform : Platform

interface AndroidPlatform : JvmPlatform

sealed interface DesktopPlatform : JvmPlatform {
    interface Linux : DesktopPlatform
    interface Windows : DesktopPlatform
    interface MacOs : DesktopPlatform
    interface Unknown : DesktopPlatform

    companion object
}

data object WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

sealed interface NativePlatform : Platform {
    data object LinuxArm64 : NativePlatform {
        override val name: String = "Linux Arm64"
    }

    data object LinuxX64 : NativePlatform {
        override val name: String = "Linux x64"
    }

    data object WindowsX64 : NativePlatform {
        override val name: String = "Windows x64"
    }
}
