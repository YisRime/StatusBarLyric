@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
}

val buildTime = System.currentTimeMillis()
val localProperties = Properties()
if (rootProject.file("local.properties").canRead()) {
    localProperties.load(rootProject.file("local.properties").inputStream())
}

android {
    namespace = "de.yisrime.lrcbar"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.yisrime.lrcbar"
        minSdk = 30
        targetSdk = 36
        versionCode = 103
        versionName = "1.0.3"
        buildConfigField("long", "BUILD_TIME", "$buildTime")
        buildConfigField("int", "COMPOSE_CONFIG_VERSION", "1")
    }
    val config = localProperties.getProperty("androidStoreFile")?.let {
        signingConfigs.create("config") {
            storeFile = file(it)
            storePassword = localProperties.getProperty("androidStorePassword")
            keyAlias = localProperties.getProperty("androidKeyAlias")
            keyPassword = localProperties.getProperty("androidKeyPassword")
            enableV3Signing = true
            enableV4Signing = true
        }
    }
    buildTypes {
        all {
            signingConfig = config ?: signingConfigs["debug"]
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            vcsInfo.include = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }
    applicationVariants.all {
        outputs.all {
            (this as BaseVariantOutputImpl).outputFileName = "StatusBarLyric-$versionName($versionCode)-$name-$buildTime.apk"
        }
    }
    aaptOptions.cruncherEnabled = false
    buildFeatures.buildConfig = true
    dependenciesInfo.includeInApk = false
    kotlin.jvmToolchain(21)
    packaging.resources.excludes += "**"
    packaging.resources.merges += "META-INF/xposed/*"
}

dependencies {
    compileOnly(libs.libxposed.api)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)

    implementation(libs.haze)
    implementation(libs.miuix)

    implementation(libs.ezxhelper.core)
    implementation(libs.ezxhelper.xposed)
    implementation(libs.ezxhelper.android.utils)
    implementation(libs.libxposed.service)
    implementation(libs.lyric.getter.api)

    debugImplementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
}
