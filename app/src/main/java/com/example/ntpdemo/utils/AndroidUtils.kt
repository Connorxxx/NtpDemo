package com.example.ntpdemo.utils

import android.util.Log
import android.widget.Toast
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.ntpdemo.App
import com.example.ntpdemo.BuildConfig

fun Any.logCat(tab: String = "NTP_DEMO_LOG") {
    if (!BuildConfig.DEBUG) return
    if (this is String) Log.d(tab, this) else Log.d(tab, this.toString())
}

fun String.showToast() {
    Toast.makeText(App.app, this, Toast.LENGTH_SHORT).show()
}

fun String.host() = split(":").getOrElse(0) { "localhost" }
fun String.port() = split(":").getOrElse(1) { "123" }.toInt()

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }