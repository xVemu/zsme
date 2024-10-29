plugins {
    id("com.android.application") version "8.6.0-alpha07" apply false // https://developer.android.com/build/releases/past-releases
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false // https://kotlinlang.org/docs/whatsnew2020.html
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20" apply false // https://kotlinlang.org/docs/whatsnew2020.html
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply false // https://github.com/google/ksp/releases
    id("com.google.gms.google-services") version "4.4.2" apply false // https://firebase.google.com/support/release-notes/android
    id("com.autonomousapps.dependency-analysis") version "2.1.4" apply false // unused dependencies, :buildHealth https://github.com/autonomousapps/dependency-analysis-gradle-plugin/blob/main/CHANGELOG.md
    id("de.jensklingenberg.ktorfit") version "2.1.0" apply false // https://foso.github.io/Ktorfit/CHANGELOG/
    kotlin("plugin.serialization") version "2.0.20" apply false
}
