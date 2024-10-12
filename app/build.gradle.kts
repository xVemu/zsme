import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("com.autonomousapps.dependency-analysis")
}

android {
    namespace = "pl.vemu.zsme"
    compileSdk = 35

    defaultConfig {
        applicationId = "pl.vemu.zsme"
        minSdk = 26
        targetSdk = 35
        versionCode = 49
        versionName = "2.3.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            // https://github.com/skrapeit/skrape.it/issues/184#issuecomment-1204545852
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "mozilla/public-suffix-list.txt",
            )
        }
    }
    // Could be replaced with on demand download, but it makes no real difference in size.
    bundle.language.enableSplit = false
}

dependencies {
    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("org.mockito:mockito-core:5.14.1")
    androidTestImplementation("org.mockito:mockito-android:5.14.1")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")

    // Hilt
    val hilt = "2.52" // https://github.com/google/dagger/releases
    implementation("com.google.dagger:hilt-android:$hilt")
    ksp("com.google.dagger:hilt-compiler:$hilt")
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hilt")
    kspAndroidTest("com.google.dagger:hilt-android-compiler:$hilt")

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
    implementation("androidx.core:core-ktx:1.13.1") // https://developer.android.com/jetpack/androidx/releases/core

    // Compose
    val compose = "1.7.3"
    implementation("androidx.compose.material3:material3:1.2.0")
//    implementation("androidx.compose.material3:material3:1.3.0") // https://developer.android.com/jetpack/androidx/releases/compose-material3
    implementation("androidx.compose.material:material-icons-extended:$compose") // https://developer.android.com/jetpack/androidx/releases/compose-material
    implementation("androidx.activity:activity-compose:1.9.2") // https://developer.android.com/jetpack/androidx/releases/activity
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6") // https://developer.android.com/jetpack/androidx/releases/lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6") // https://developer.android.com/jetpack/androidx/releases/lifecycle
    // Landscape
    implementation("androidx.compose.material3:material3-adaptive:1.0.0-alpha06")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.0.0-alpha03")
//    implementation("androidx.compose.material3.adaptive:adaptive:1.0.0") // https://developer.android.com/jetpack/androidx/releases/compose-material3-adaptive
//    implementation("androidx.compose.material3.adaptive:adaptive-navigation:1.0.0")  // https://developer.android.com/jetpack/androidx/releases/compose-material3-adaptive
    // Test
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose")
    // Preview
    implementation("androidx.compose.ui:ui-tooling-preview:$compose") // https://developer.android.com/jetpack/androidx/releases/compose-ui
    debugImplementation("androidx.compose.ui:ui-tooling:$compose") // https://developer.android.com/jetpack/androidx/releases/compose-ui

    // Network
    implementation("com.squareup.retrofit2:retrofit:2.11.0") // https://github.com/square/retrofit/blob/trunk/CHANGELOG.md
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0") // https://github.com/square/retrofit/blob/trunk/CHANGELOG.md
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3") // https://github.com/Kotlin/kotlinx.serialization/releases
    // Wait for stable release
    implementation("it.skrape:skrapeit:1.3.0-alpha.1")
    // Chrome custom tabs
    implementation("androidx.browser:browser:1.8.0") // https://developer.android.com/jetpack/androidx/releases/browser

    // Navigation
    implementation("io.github.raamcosta.compose-destinations:core:2.1.0-beta12") // https://github.com/raamcosta/compose-destinations/releases
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0") // https://developer.android.com/jetpack/androidx/releases/hilt
    ksp("io.github.raamcosta.compose-destinations:ksp:2.1.0-beta12") // https://github.com/raamcosta/compose-destinations/releases
    implementation("io.github.fornewid:material-motion-compose-core:2.0.1") // https://github.com/fornewid/material-motion-compose/releases

    // Settings
    implementation("androidx.datastore:datastore-preferences:1.1.1") // https://developer.android.com/jetpack/androidx/releases/datastore
    implementation("com.github.alorma:compose-settings-ui-m3:1.0.3")

    // Firebase
    implementation("com.google.firebase:firebase-config:22.0.0") // https://firebase.google.com/support/release-notes/android
    implementation("com.google.firebase:firebase-messaging:24.0.2") // https://firebase.google.com/support/release-notes/android

    // 3-rd party
    implementation("io.coil-kt:coil-compose:2.7.0") // https://github.com/coil-kt/coil/blob/main/CHANGELOG.md
    implementation("net.engawapg.lib:zoomable:1.6.2") // https://github.com/mxalbert1996/Zoomable/releases
    implementation("com.github.YarikSOffice:lingver:1.3.0")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14") // https://square.github.io/leakcanary/changelog/
    implementation("com.github.viluahealthcare:compose-html:1.0.3")
}

// https://github.com/androidx/androidx/blob/08c6116/compose/compiler/design/compiler-metrics.md
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    val path = layout.buildDirectory.dir("composeReports").get().asFile.path
    compilerOptions.freeCompilerArgs.addAll(
        "-P",
        "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$path"
    )
}
