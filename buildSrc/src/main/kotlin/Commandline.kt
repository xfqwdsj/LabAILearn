package top.ltfan.labailearn.buildsrc

import org.gradle.api.Action
import org.gradle.api.provider.ProviderFactory
import org.gradle.process.ExecOutput
import org.gradle.process.ExecSpec

abstract class CommandlineScope : ExecOutput {
    val exitValue by lazy { result.orNull?.exitValue }
    val standardOutputText by lazy { standardOutput.asText.orNull }

    fun <R> normalExitWithStandardOutput(block: CommandlineScope.(standardOutputText: String) -> R): R? {
        val output = standardOutputText?.trim()
        return if (exitValue == 0 && output?.isNotBlank() == true) {
            block(output)
        } else null
    }
}

class Commandline<T>(
    private val providers: ProviderFactory,
    private val execAction: Action<in ExecSpec>,
    private val onResult: CommandlineScope.() -> T
) {
    operator fun getValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>) =
        onResult(object : CommandlineScope(), ExecOutput by providers.exec(execAction) {})
}
