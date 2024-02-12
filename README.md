# ZSME

Mobile application based on [ZSME site](https://zsme.tarnow.pl).

## Versions

- 1.x used Java & XML.
- 2.0 used Kotlin & experimental Jetpack Compose.
- 2.1 used Material 3.

## Download

App is available on [Play Store](https://play.google.com/store/apps/details?id=pl.vemu.zsme) or
in [releases section](https://github.com/xVemu/zsme/releases).

## Technologies used:

* Jetpack Compose
* Hilt
* Room
* Material Design 3
* Retrofit
* Firebase - Messaging, Remote Config and Cloud Functions
* Compose Destinations

# Setup

1. Import project to [Android Studio](https://developer.android.com/studio).
2. Setup [Firebase](). Place your config in `app/google-services.json`.
3. Setup [Firebase Messaging](https://firebase.google.com/docs/cloud-messaging/android/client).
4.
Setup [Remote Config](https://firebase.google.com/docs/remote-config/get-started?platform=android)
and populate it with values from `app/src/main/res/xml/remote_config_defaults.xml`.
5. Setup [Cloud Functions](https://firebase.google.com/docs/functions/get-started?gen=2nd) located
   in `firebase` directory.
