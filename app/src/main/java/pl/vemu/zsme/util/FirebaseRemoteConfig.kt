package pl.vemu.zsme.util

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

val FirebaseRemoteConfig.apiUrl
    get() = getString("apiUrl")

val FirebaseRemoteConfig.baseUrl
    get() = getString("baseUrl")

val FirebaseRemoteConfig.journalUrl
    get() = getString("journalLrl")

val FirebaseRemoteConfig.scheduleLogin
    get() = getString("scheduleLogin")

val FirebaseRemoteConfig.schedulePassword
    get() = getString("schedulePassword")

val FirebaseRemoteConfig.scheduleUrl
    get() = getString("scheduleUrl")
