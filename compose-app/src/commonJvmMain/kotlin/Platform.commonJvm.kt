package top.ltfan.labailearn

open class JvmPlatformImpl(addition: String = "") : JvmPlatform {
    override val name: String = "Java ${System.getProperty("java.version")} $addition"
}
