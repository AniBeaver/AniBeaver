import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    /*
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
     */

    jvm("desktop")

    sourceSets {
        val desktopMain by getting
        val ktor_version = "3.2.3"

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            implementation("com.liftric:kvault:1.12.0")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.navigation.compose)

            implementation(libs.ktor.client.core)
            implementation("io.ktor:ktor-server-core:${ktor_version}")
            implementation("io.ktor:ktor-server-cio:${ktor_version}")
            implementation("io.ktor:ktor-client-cio:${ktor_version}")
            implementation("io.ktor:ktor-client-content-negotiation:${ktor_version}")
            implementation("io.ktor:ktor-server-content-negotiation:${ktor_version}")
            implementation("io.ktor:ktor-serialization-kotlinx-json:${ktor_version}")

            implementation("io.coil-kt.coil3:coil-compose:3.3.0")
            implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)

            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            implementation("org.jetbrains.compose.material3:material3-window-size-class:1.7.3")
            implementation("org.jetbrains.compose.material:material-icons-core:1.7.3") // Material Icons Core
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3") // Material Icons Extended
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation("org.netbeans.api:org-netbeans-modules-keyring:RELEASE260")
            implementation("org.netbeans.modules:org-netbeans-modules-keyring-impl:RELEASE260")
            implementation("org.netbeans.api:org-openide-util-lookup:RELEASE260")
            implementation("net.java.dev.jna:jna:5.14.0")
            implementation("net.java.dev.jna:jna-platform:5.14.0")
        }
        /*
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation("com.liftric:kvault:1.12.0")
        }
         */
    }
}

android {
    namespace = "org.anibeaver.anibeaver"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    lint {
        disable += "NullSafeMutableLiveData"
    }

    defaultConfig {
        applicationId = "org.anibeaver.anibeaver"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)

    add("kspAndroid", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)

    //add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    // FIXME: add back when re-enabling iOS target
    // add("kspIosX64", libs.androidx.room.compiler)
    // add("kspIosArm64", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

compose.desktop {
    application {
        mainClass = "org.anibeaver.anibeaver.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.anibeaver.anibeaver"
            packageVersion = "1.0.0"
        }

        jvmArgs += "-Dapple.awt.application.appearance=NSAppearanceNameDarkAqua"
    }
}
