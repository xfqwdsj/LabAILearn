package top.ltfan.labailearn.buildsrc

import org.gradle.kotlin.dsl.provideDelegate
import java.io.File
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class BuildProperties(private val file: File) : Properties(), ReadWriteProperty<Any?, String?> {
    init {
        with(file) {
            if (exists()) {
                load(inputStream())
            }
        }
    }

    private fun save() = with(file) { store(outputStream(), null) }

    override fun getValue(thisRef: Any?, property: KProperty<*>): String? = getProperty(property.name, null)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        setProperty(property.name, value)
        save()
    }

    operator fun invoke(defaultValue: String) = invoke(defaultValue, { it }, { it })

    operator fun invoke(defaultValue: Int) = invoke(defaultValue, String::toInt, Int::toString)

    inline operator fun <reified V> invoke(
        defaultValue: V, crossinline transform: (String) -> V, crossinline reverse: (V) -> String
    ) = object : ReadWriteProperty<Any?, V> {
        override fun getValue(thisRef: Any?, property: KProperty<*>) =
            this@BuildProperties.getValue(thisRef, property)?.let(transform) ?: defaultValue

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) =
            this@BuildProperties.setValue(thisRef, property, reverse(value))
    }
}
