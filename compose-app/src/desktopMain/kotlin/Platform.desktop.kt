package top.ltfan.labailearn

actual val platform: Platform by lazy { DesktopPlatform.current }

val DesktopPlatform.Companion.current: DesktopPlatform
    get() = System.getProperty("os.name").let {
        when {
            it?.startsWith("Linux") == true -> object : JvmPlatformImpl("Linux"), DesktopPlatform.Linux {}
            it?.startsWith("Win") == true -> object : JvmPlatformImpl("Windows"), DesktopPlatform.Windows {}
            it == "Mac OS X" -> object : JvmPlatformImpl("MacOS"), DesktopPlatform.MacOs {}
            else -> object : JvmPlatformImpl("Unknown"), DesktopPlatform.Unknown {}
        }
    }
