package top.ltfan.labailearn.buildsrc

import org.gradle.api.Plugin
import org.gradle.api.Project

class GradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val projectVersion = target.extensions.create("projectVersion", ProjectVersion::class.java, target)
        target.extensions.create("output", Output::class.java, projectVersion)
    }
}
