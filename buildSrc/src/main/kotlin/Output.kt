package top.ltfan.labailearn.buildsrc

import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

open class Output(internal val projectVersion: ProjectVersion) {
    fun directoryOf(target: String): Provider<Directory> =
        projectVersion.project.rootProject.layout.buildDirectory.dir("outputs/binaries/${projectVersion.versionName}/$target")
}
