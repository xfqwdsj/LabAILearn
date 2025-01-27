import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalDistributionDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import java.time.Year
import java.util.*

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("top.ltfan.gradle-plugin")
}

val androidCompileSdk: String by project
val androidMinSdk: String by project
val androidTargetSdk: String by project

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "lalapp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "lalapp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
            @OptIn(ExperimentalDistributionDsl::class) distribution {
                outputDirectory = output.directoryOf("web")
            }
        }
        binaries.executable()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.animation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.haze)
            }
        }

        val commonJvmMain by creating {
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependsOn(commonJvmMain)
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.androidx.core.splashscreen)
                implementation(libs.androidx.window)
                implementation(libs.androidx.activity.compose)
            }
        }

        val desktopMain by getting {
            dependsOn(commonJvmMain)
            dependencies {
                implementation(libs.kotlinx.coroutines.swing)
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

android {
    namespace = "top.ltfan.labailearn"
    compileSdk = androidCompileSdk.toInt()

    defaultConfig {
        applicationId = "top.ltfan.labailearn"
        minSdk = androidMinSdk.toInt()
        targetSdk = androidTargetSdk.toInt()
        versionCode = projectVersion.versionCode
        versionName = projectVersion.versionName
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        val store = file("key.jks")
        if (store.exists()) {
            val properties = Properties()
            file("key.properties").run {
                if (exists()) {
                    properties.load(inputStream())

                    create("release") {
                        storeFile = store
                        storePassword = properties.getProperty("storePassword")
                        keyAlias = properties.getProperty("keyAlias")
                        keyPassword = properties.getProperty("keyPassword")
                    }
                }
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt")
            )

            try {
                signingConfig = signingConfigs.getByName("release")
            } catch (e: UnknownDomainObjectException) {
                logger.error("${e.message} Maybe a key.jks or key.properties file is missing?")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    splits {
        abi {
            isEnable = true
            isUniversalApk = true
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }
}

compose.desktop {
    application {
        mainClass = "top.ltfan.labailearn.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.AppImage, TargetFormat.Msi)
            packageName = "LabAILearn"
            description = "LabAILearn"
            copyright = "© 2024${
                Year.now().value.let {
                    if (it > 2024) "-$it" else ""
                }
            } xfqwdsj. All Rights Reserved."
            vendor = "xfqwdsj"
            licenseFile = rootProject.file("LICENSE")
            outputBaseDir = output.directoryOf("desktop")

            linux {
                packageVersion = projectVersion.versionName
                debMaintainer = "xfqwdsj@qq.com"
                rpmPackageVersion = projectVersion.versionName.replace("-", "")
                rpmLicenseType = "GPL-3.0-or-later"
                menuGroup = "Utility"
                iconFile.set(rootProject.file("res/lal.png"))
            }

            windows {
                packageVersion = Regex("""(\d+\.\d+\.\d+).*""").find(projectVersion.versionTag)?.groupValues?.get(1)!!
                dirChooser = true
                menuGroup = "LabAILearn"
                upgradeUuid = "01935949-91c0-7a0d-bbfb-3fcee898e745"
                iconFile.set(rootProject.file("res/lal.ico"))
            }
        }

        buildTypes {
            release {
                proguard {
                    version = "7.6.0"
                    configurationFiles.from("proguard-desktop.pro")
                }
            }
        }
    }
}
