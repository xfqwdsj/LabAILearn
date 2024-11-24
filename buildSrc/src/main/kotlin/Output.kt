package top.ltfan.labailearn.buildsrc

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

fun Project.outputDirectoryOf(target: String): Provider<Directory> =
    rootProject.layout.buildDirectory.dir("outputs/binaries/${Version.versionName}/$target")
