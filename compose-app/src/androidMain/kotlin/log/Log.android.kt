package top.ltfan.labailearn.log

actual object Log {
    actual fun d(tag: String, message: String) {
        android.util.Log.d(tag, message)
    }

    actual fun i(tag: String, message: String) {
        android.util.Log.i(tag, message)
    }

    actual fun w(tag: String, message: String, throwable: Throwable?) {
        android.util.Log.w(tag, message, throwable)
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        android.util.Log.e(tag, message, throwable)
    }
}
