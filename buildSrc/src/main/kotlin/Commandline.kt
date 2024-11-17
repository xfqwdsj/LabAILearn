package top.ltfan.labailearn.buildsrc

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

interface CommandlineScope : ExecResult {
    fun ByteArrayOutputStream.readTextAndClear() = toString().also { reset() }
}

class Commandline<T>(
    private val project: Project,
    private val execAction: Action<in ExecSpec>,
    private val onResult: CommandlineScope.() -> T
) {
    operator fun getValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>) =
        onResult(object : CommandlineScope, ExecResult by project.exec(execAction) {})
}
