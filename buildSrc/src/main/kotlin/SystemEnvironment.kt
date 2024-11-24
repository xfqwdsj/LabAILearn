package top.ltfan.labailearn.buildsrc

import org.gradle.internal.os.OperatingSystem

val operatingSystem: OperatingSystem = OperatingSystem.current()

object SystemEnvironment {
    val arch: String = System.getProperty("os.arch")
}
