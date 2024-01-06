import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "pl.vemu.zsme"
    compileSdk = 34

    defaultConfig {
        applicationId = "pl.vemu.zsme"
        minSdk = 26
        targetSdk = 34
        versionCode = 43
        versionName = "2.1.0"
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.6"
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
    bundle.language.enableSplit = false // TODO
}

dependencies {
    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("org.mockito:mockito-core:5.5.0")
    androidTestImplementation("org.mockito:mockito-android:5.5.0")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")

    // Hilt
    val hilt = "2.48"
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
    val paging = "3.2.1"
    implementation("androidx.paging:paging-runtime-ktx:$paging")
    implementation("androidx.paging:paging-compose:$paging")

    // Other
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.android.play:core-ktx:1.8.1")
    implementation("androidx.core:core-ktx:1.12.0")

    // Compose
    val compose = "1.5.4"
    implementation("androidx.compose.material3:material3:1.2.0-beta01")
    implementation("androidx.compose.material:material-icons-extended:$compose")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    // Landscape
    implementation("androidx.compose.material3:material3-adaptive:1.0.0-alpha03")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.0.0-alpha02")
    // Test
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose")
    // Preview
    implementation("androidx.compose.ui:ui-tooling-preview:$compose")
    debugImplementation("androidx.compose.ui:ui-tooling:$compose")

    // Network
    implementation("com.squareup.retrofit2:retrofit:2.10.0-SNAPSHOT")
    implementation("com.squareup.retrofit2:converter-gson:2.10.0-SNAPSHOT")
    implementation("it.skrape:skrapeit:1.2.2")
    // Chrome custom tabs
    implementation("androidx.browser:browser:1.7.0")

    // Navigation
    implementation("io.github.raamcosta.compose-destinations:core:1.9.53")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.9.53")
    implementation("io.github.fornewid:material-motion-compose-core:1.0.7")

    // Settings
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.github.alorma:compose-settings-ui-m3:0.27.0")

    // Firebase
    implementation("com.google.firebase:firebase-config:21.6.0")
    implementation("com.google.firebase:firebase-messaging:23.4.0")

    // 3-rd party
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("net.engawapg.lib:zoomable:1.5.2")
    implementation("com.github.YarikSOffice:lingver:1.3.0")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")
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
