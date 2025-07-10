package top.ltfan.labailearn.log

expect object Log {
    fun d(tag: String, message: String)

    fun i(tag: String, message: String)

    fun w(tag: String, message: String, throwable: Throwable? = null)

    fun e(tag: String, message: String, throwable: Throwable? = null)
}
