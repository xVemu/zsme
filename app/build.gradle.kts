import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-parcelize")
    kotlin("plugin.serialization")
    id("com.autonomousapps.dependency-analysis")
    id("de.jensklingenberg.ktorfit")
    id("androidx.baselineprofile")
}

android {
    namespace = "pl.vemu.zsme"
    compileSdk = 35

    defaultConfig {
        applicationId = "pl.vemu.zsme"
        // Can't be lower, because android versions < 9, includes `org.apache.http.legacy` in `/system/framework` classpath,
        // and apk for some reason prioritize this over apache bundled in app, so skrapeit breaks on API 26.
        minSdk = 28
        targetSdk = 35
        versionCode = 50
        versionName = "2.3.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations += listOf("en", "pl")
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")

            val keystoreProperties = Properties()

            keystoreProperties.load(FileInputStream(keystorePropertiesFile))


            storeFile =
                file(System.getenv("STORE_FILE") ?: keystoreProperties["storeFile"] as String)
            storePassword =
                System.getenv("STORE_PASSWORD") ?: keystoreProperties["storePassword"] as String
            keyAlias = System.getenv("KEY_ALIAS") ?: keystoreProperties["keyAlias"] as String
            keyPassword =
                System.getenv("KEY_PASSWORD") ?: keystoreProperties["keyPassword"] as String
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }

        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    /*composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
    }*/

    packaging {
        resources {
            // https://github.com/skrapeit/skrape.it/issues/184#issuecomment-1204545852
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/INDEX.LIST",
                "mozilla/public-suffix-list.txt"
            )
        }
    }
    // Could be replaced with on demand download, but it makes no real difference in size.
    bundle.language.enableSplit = false
}

baselineProfile {
    automaticGenerationDuringBuild = true
    dexLayoutOptimization = true
}

dependencies {
    implementation("androidx.profileinstaller:profileinstaller:1.4.1")
    "baselineProfile"(project(":baselineprofile"))
    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("org.mockito:mockito-core:5.14.1")
    androidTestImplementation("org.mockito:mockito-android:5.14.1")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")

    // Koin
    val koin = "4.0.0"
    implementation("io.insert-koin:koin-androidx-compose:$koin")
    implementation("io.insert-koin:koin-androidx-compose-navigation:$koin")
    implementation("io.insert-koin:koin-androidx-startup:$koin")
    val koinAnnotations = "2.0.0-Beta1"
    implementation("io.insert-koin:koin-annotations:$koinAnnotations")
    ksp("io.insert-koin:koin-ksp-compiler:$koinAnnotations")

    // Room
    val room = "2.6.1"
    implementation("androidx.room:room-ktx:$room")
    implementation("androidx.room:room-paging:$room")
    ksp("androidx.room:room-compiler:$room")

    // Paging
    val paging = "3.3.2" // https://developer.android.com/jetpack/androidx/releases/paging
    implementation("androidx.paging:paging-runtime-ktx:$paging")
    implementation("androidx.paging:paging-compose:$paging")

    // Other
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
    implementation("androidx.core:core-ktx:1.15.0") // https://developer.android.com/jetpack/androidx/releases/core
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    implementation("androidx.startup:startup-runtime:1.2.0")

    // Compose
    val compose = "1.7.5"
    implementation("androidx.compose.material3:material3:1.3.1") // https://developer.android.com/jetpack/androidx/releases/compose-material3
    implementation("androidx.compose.material:material-icons-extended:$compose") // https://developer.android.com/jetpack/androidx/releases/compose-material
    implementation("androidx.activity:activity-compose:1.9.3") // https://developer.android.com/jetpack/androidx/releases/activity
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7") // https://developer.android.com/jetpack/androidx/releases/lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7") // https://developer.android.com/jetpack/androidx/releases/lifecycle
    // Adaptive
    // currentWindowAdaptiveInfo()
    implementation("androidx.compose.material3.adaptive:adaptive:1.0.0") // https://developer.android.com/jetpack/androidx/releases/compose-material3-adaptive
    // ListDetailPaneScaffold, SupportingPaneScaffold TODO Integrate with compose-navigation https://issuetracker.google.com/issues/294612000
//    implementation("androidx.compose.material3.adaptive:adaptive-layout:1.0.0") // https://developer.android.com/jetpack/androidx/releases/compose-material3-adaptive
    // rememberListDetailPaneScaffoldNavigator, rememberSupportingPaneScaffoldNavigator
//    implementation("androidx.compose.material3.adaptive:adaptive-navigation:1.0.0") // https://developer.android.com/jetpack/androidx/releases/compose-material3-adaptive
    // AdaptiveNavigationBar
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.3.1") // https://developer.android.com/jetpack/androidx/releases/compose-material3
    // Test
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose")
    // Preview
    implementation("androidx.compose.ui:ui-tooling-preview:$compose") // https://developer.android.com/jetpack/androidx/releases/compose-ui
    debugImplementation("androidx.compose.ui:ui-tooling:$compose") // https://developer.android.com/jetpack/androidx/releases/compose-ui

    // Network
    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:2.1.0") // https://foso.github.io/Ktorfit/CHANGELOG/
    implementation("io.ktor:ktor-client-content-negotiation:2.0.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.3")
    // Wait for stable release
    implementation("it.skrape:skrapeit:1.3.0-alpha.2") {
        exclude("it.skrape", "skrapeit-http-fetcher")
    }

    // Chrome custom tabs
    implementation("androidx.browser:browser:1.8.0") // https://developer.android.com/jetpack/androidx/releases/browser

    // Navigation
    implementation("io.github.raamcosta.compose-destinations:core:2.1.0-beta14") // https://github.com/raamcosta/compose-destinations/releases
    ksp("io.github.raamcosta.compose-destinations:ksp:2.1.0-beta14") // https://github.com/raamcosta/compose-destinations/releases
    implementation("io.github.fornewid:material-motion-compose-core:2.0.1") // https://github.com/fornewid/material-motion-compose/releases

    // Settings
    implementation("androidx.datastore:datastore-preferences:1.1.1") // https://developer.android.com/jetpack/androidx/releases/datastore
    implementation("com.github.alorma:compose-settings-ui-m3:1.0.3")

    // Firebase
    implementation("com.google.firebase:firebase-config:22.0.1") // https://firebase.google.com/support/release-notes/android
    implementation("com.google.firebase:firebase-messaging:24.0.3") // https://firebase.google.com/support/release-notes/android
    implementation("com.google.firebase:firebase-crashlytics:19.2.1")

    // 3-rd party
    implementation("io.coil-kt:coil-compose:2.7.0") // https://github.com/coil-kt/coil/blob/main/CHANGELOG.md
    implementation("net.engawapg.lib:zoomable:1.6.2") // https://github.com/mxalbert1996/Zoomable/releases
    implementation("com.github.YarikSOffice:lingver:1.3.0")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14") // https://square.github.io/leakcanary/changelog/
}
