pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
//        remove when retrofit2 2.10.0 is released in maven central
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

include(":app")
rootProject.name = "zsme"
