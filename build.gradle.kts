import top.ltfan.labailearn.buildsrc.*

plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
}

RootProject.project = rootProject

tasks.register("releaseAndroidApp") {
    group = "project build"
    description = "Build the Android release APK"

    dependsOn("compose-app:assembleRelease")

    doLast {
        val apkDir = file(project("compose-app").layout.buildDirectory.dir("outputs/apk/release"))
        val outputDir = file(outputDirectoryOf("android"))
        apkDir.copyRecursively(outputDir, overwrite = true)
        logger.lifecycle("output directory: ${outputDir.absolutePath}")
    }
}

tasks.register("releaseDesktopAppUberJar") {
    group = "project build"
    description = "Build the desktop release uber jar"

    dependsOn("compose-app:packageReleaseUberJarForCurrentOS")

    doLast {
        val outputDir = file(outputDirectoryOf("desktop-uber-jar"))
        file(project("compose-app").layout.buildDirectory.dir("compose/jars")).listFiles()
            ?.filter { it.name.contains(Version.versionName) && it.name.endsWith(".jar") }?.forEach {
                val arch = Regex(".+?-.+?-(.+?)-.+").find(it.name)?.groupValues?.get(1) ?: return@forEach
                it.copyTo(
                    outputDir.resolve("lal-${operatingSystem.familyName}-${Version.versionName}-${arch}.jar"),
                    overwrite = true
                )
                logger.lifecycle("output directory: ${outputDir.absolutePath}")
            }
    }
}

tasks.register<Tar>("releaseLinuxAppAndTar") {
    group = "project build"
    description = "Build the Linux release and create a tar archive"

    if (!operatingSystem.isLinux) {
        enabled = false
    }

    dependsOn("compose-app:packageReleaseAppImage")

    archiveBaseName = "lal"
    archiveAppendix = "linux"
    archiveVersion = Version.versionName
    archiveClassifier = SystemEnvironment.arch
    archiveExtension = "tar"
    compression = Compression.GZIP
    destinationDirectory = file(outputDirectoryOf("linux-tar"))
    from(file(outputDirectoryOf("desktop/main-release/app/lal")))
}

tasks.register<Zip>("releaseWindowsAppAndZip") {
    group = "project build"
    description = "Build the Windows release and create a zip archive"

    if (!operatingSystem.isWindows) {
        enabled = false
    }

    dependsOn("compose-app:packageReleaseAppImage")

    archiveBaseName = "lal"
    archiveAppendix = "windows"
    archiveVersion = Version.versionName
    archiveClassifier = SystemEnvironment.arch
    archiveExtension = "zip"
    destinationDirectory = file(outputDirectoryOf("windows-zip"))
    from(file(outputDirectoryOf("desktop/main-release/app/lal")))
}

tasks.register("releaseDesktopAppAndArchive") {
    group = "project build"
    description = "Build the desktop release and create an archive"

    if (operatingSystem.isLinux) {
        dependsOn("releaseLinuxAppAndTar")
    } else if (operatingSystem.isWindows) {
        dependsOn("releaseWindowsAppAndZip")
    }
}

tasks.register("releaseDesktopApp") {
    group = "project build"
    description = "Build the desktop release"

    dependsOn("releaseDesktopAppAndArchive", "releaseDesktopAppUberJar")

    if (operatingSystem.isLinux) {
        dependsOn("compose-app:packageReleaseDeb", "compose-app:packageReleaseRpm")
    } else if (operatingSystem.isWindows) {
        dependsOn("compose-app:packageReleaseMsi")
    }
}

tasks.register("releaseWebApp") {
    group = "project build"
    description = "Build the Web release"

    dependsOn("compose-app:wasmJsBrowserDistribution")

    doLast {
        logger.lifecycle("output directory: ${file(outputDirectoryOf("web")).absolutePath}")
    }
}

tasks.register<Tar>("releaseWebAppAndTar") {
    group = "project build"
    description = "Build the Web release and create a tar archive"

    dependsOn("releaseWebApp")

    archiveBaseName = "lal"
    archiveAppendix = "web"
    archiveVersion = Version.versionName
    archiveExtension = "tar"
    compression = Compression.GZIP
    destinationDirectory = file(outputDirectoryOf("web-tar"))
    from(file(outputDirectoryOf("web")))
}

tasks.register("ciReleaseLinuxApp") {
    group = "ci"
    description = "Build on the Linux platform"

    if (!operatingSystem.isLinux) {
        enabled = false
    }

    dependsOn("releaseAndroidApp", "releaseDesktopApp", "releaseWebApp", "releaseWebAppAndTar")

    doLast {
        val assetsDir = file(layout.buildDirectory.dir("assets"))
        file(outputDirectoryOf("android")).listFiles()?.filter { it.name.endsWith(".apk") }?.forEach { file ->
            file.name.substringAfter("compose-app-").substringBefore("-release").let {
                file.copyTo(
                    assetsDir.resolve("lal-android-${Version.versionName}-$it.apk"), overwrite = true
                )
            }
        }
        file(outputDirectoryOf("desktop/main-release/deb")).listFiles()?.first()?.copyTo(
            assetsDir.resolve("lal-linux-${Version.versionName}-${SystemEnvironment.arch}.deb"),
            overwrite = true
        )
        file(outputDirectoryOf("desktop/main-release/rpm")).listFiles()?.first()?.copyTo(
            assetsDir.resolve("lal-linux-${Version.versionName}-${SystemEnvironment.arch}.rpm"),
            overwrite = true
        )
        file(outputDirectoryOf("linux-tar")).copyRecursively(assetsDir, overwrite = true)
        file(outputDirectoryOf("web")).copyRecursively(assetsDir.resolve("web"), overwrite = true)
        file(outputDirectoryOf("web-tar")).copyRecursively(assetsDir, overwrite = true)
    }
}

tasks.register("ciReleaseWindowsApp") {
    group = "ci"
    description = "Build on the Windows platform"

    if (!operatingSystem.isWindows) {
        enabled = false
    }

    dependsOn("releaseDesktopApp")

    doLast {
        val assetsDir = file(layout.buildDirectory.dir("assets"))
        file(outputDirectoryOf("windows-zip")).copyRecursively(assetsDir, overwrite = true)
        file(outputDirectoryOf("desktop/main-release/msi")).listFiles()?.first()?.copyTo(
            assetsDir.resolve("lal-windows-${Version.versionName}-${SystemEnvironment.arch}.msi"),
            overwrite = true
        )
    }
}

tasks.register("ciReleaseApp") {
    group = "ci"
    description = "Build the release app"

    if (operatingSystem.isLinux) {
        dependsOn("ciReleaseLinuxApp")
    } else if (operatingSystem.isWindows) {
        dependsOn("ciReleaseWindowsApp")
    }

    doLast {
        val assetsDir = file(layout.buildDirectory.dir("assets"))
        file(outputDirectoryOf("desktop-uber-jar")).copyRecursively(assetsDir, overwrite = true)
    }
}

tasks.register("release") {
    group = "project build"
    description = "Create a new release"

    dependsOn(
        "releaseAndroidApp",
        "releaseDesktopApp",
        "releaseWebApp"
    )
}

tasks.register("cleanReleases") {
    group = "project build"
    description = "Clean the releases"

    doLast {
        file(layout.buildDirectory.dir("outputs/binaries")).deleteRecursively()
        file(layout.buildDirectory.dir("assets")).deleteRecursively()
    }
}

tasks.configureEach {
    if (name == "clean") {
        dependsOn("cleanReleases")
    }
}

tasks.register("runDesktopApp") {
    group = "project build"
    description = "Run the desktop app"

    dependsOn("compose-app:run")
}

tasks.register("runWebApp") {
    group = "project build"
    description = "Run the web app"

    dependsOn("compose-app:wasmJsBrowserDevelopmentRun")
}

tasks.register("installReleaseAndroidApp") {
    group = "project build"
    description = "Install the Android release APK"

    dependsOn("releaseAndroidApp")

    doLast {
        val apk = file(outputDirectoryOf("android")).listFiles()!!.first { it.name.endsWith(".apk") }!!
        val cmd = mutableListOf("adb")
        if (hasProperty("device")) {
            cmd.add("-s")
            cmd.add(property("device").toString())
        }
        cmd.addAll(listOf("install", "-r", apk.absolutePath))

        exec {
            commandLine = cmd
        }
    }
}
