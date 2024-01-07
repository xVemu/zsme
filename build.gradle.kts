plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
    id("com.google.devtools.ksp") version "1.9.21-1.0.15" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.4" apply false
    id("com.autonomousapps.dependency-analysis") version "1.28.0" // buildHealth
}
