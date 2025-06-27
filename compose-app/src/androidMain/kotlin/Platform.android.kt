package top.ltfan.labailearn

import android.annotation.SuppressLint
import android.os.Build
import kotlin.reflect.full.declaredFunctions

actual val platform: Platform = AndroidPlatformImpl

object AndroidPlatformImpl : JvmPlatformImpl("Android ${Build.VERSION.SDK_INT}"), AndroidPlatform

val isMiui: Boolean
    @SuppressLint("PrivateApi") get() {
        val clazz = Class.forName("android.os.SystemProperties").kotlin
        val method = clazz.declaredFunctions.firstOrNull { it.name == "get" && it.parameters.size == 1 }
        return method?.call("ro.miui.ui.version.name") != ""
    }
