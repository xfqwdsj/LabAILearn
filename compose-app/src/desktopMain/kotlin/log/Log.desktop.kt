package top.ltfan.labailearn.log

import kotlinx.datetime.Clock

actual object Log {
    actual fun d(tag: String, message: String) {
        printLog(tag, "DEBUG", message)
    }

    actual fun i(tag: String, message: String) {
        printLog(tag, "INFO", message)
    }

    actual fun w(tag: String, message: String, throwable: Throwable?) {
        printLog(tag, "WARN", message)
        throwable?.let {
            printLog(tag, "WARN", it.stackTraceToString())
        }
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        printLog(tag, "ERROR", message)
        throwable?.let {
            printLog(tag, "ERROR", it.stackTraceToString())
        }
    }

    private fun printLog(tag: String, level: String, message: String) {
        val time = Clock.System.now()
        println("[$time] $level $tag: $message")
    }
}
