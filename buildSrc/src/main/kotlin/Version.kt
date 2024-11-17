package top.ltfan.labailearn.buildsrc

import java.io.ByteArrayOutputStream

object Version {
    private val _versionTag by lazy {
        val stream = ByteArrayOutputStream()
        Commandline(
            RootProject.project,
            {
                commandLine = listOf("git", "describe", "--tags", "--abbrev=0", "--match=v*")
                standardOutput = stream
                isIgnoreExitValue = true
            }
        ) { if (exitValue == 0) stream.readTextAndClear().trim().substringAfter("v") else "0.1.0" }
    }
    val versionTag by _versionTag

    private val _commitSha by lazy {
        val stream = ByteArrayOutputStream()
        Commandline(
            RootProject.project,
            {
                commandLine = listOf("git", "rev-parse", "--short", "HEAD")
                standardOutput = stream
                isIgnoreExitValue = true
            }
        ) { if (exitValue == 0) stream.readTextAndClear().trim() else "0000000" }
    }
    val commitSha by _commitSha

    val versionName get() = "$versionTag+$commitSha"

    private val _versionCode by lazy {
        val stream = ByteArrayOutputStream()
        Commandline(
            RootProject.project,
            {
                commandLine = listOf("git", "rev-list", "--count", "main")
                standardOutput = stream
                isIgnoreExitValue = true
            }
        ) { if (exitValue == 0) stream.readTextAndClear().trim().toInt() else 1 }
    }
    val versionCode by _versionCode
}
