package top.ltfan.labailearn

import android.os.Build

actual val platform: Platform = AndroidPlatformImpl

object AndroidPlatformImpl : JvmPlatformImpl("Android ${Build.VERSION.SDK_INT}"), AndroidPlatform
