package top.ltfan.labailearn.buildsrc

import org.gradle.api.Project

open class ProjectVersion(internal val project: Project) {
    private val _versionTag by lazy {
        Commandline(
            project.providers, {
                commandLine = listOf("git", "describe", "--tags", "--abbrev=0", "--match=v*")
                isIgnoreExitValue = true
            }) { normalExitWithStandardOutput { it.substringAfter("v") } ?: "0.1.0" }
    }
    val versionTag by _versionTag

    private val _commitSha by lazy {
        Commandline(
            project.providers, {
                commandLine = listOf("git", "rev-parse", "--short", "HEAD")
                isIgnoreExitValue = true
            }) { normalExitWithStandardOutput { it } ?: "0000000" }
    }
    val commitSha by _commitSha

    val versionName get() = "$versionTag+$commitSha"

    private val _versionCode by lazy {
        Commandline(
            project.providers, {
                commandLine = listOf("git", "rev-list", "--count", "main")
                isIgnoreExitValue = true
            }) { normalExitWithStandardOutput { it.toInt() } ?: 1 }
    }
    val versionCode by _versionCode
}
