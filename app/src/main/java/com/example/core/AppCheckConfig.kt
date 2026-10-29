package com.example.core

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory

private const val TAG = "AppCheckInit"
private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        ensureFirebaseInitialized(context)
        if (BuildConfig.DEBUG) {
            setupDebugAppCheck(context, intent)
        } else {
            setupReleaseAppCheck(context)
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize Firebase App Check", e)
    }
}

private fun ensureFirebaseInitialized(context: Context) {
    if (FirebaseApp.getApps(context).isEmpty()) {
        FirebaseApp.initializeApp(context)
    }
}

private fun setupDebugAppCheck(context: Context, intent: Intent?) {
    val debugToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)
    if (!debugToken.isNullOrEmpty()) {
        persistDebugToken(context, debugToken)
    } else {
        Log.w(TAG, "No debug token found in Intent extra.")
    }
    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
        DebugAppCheckProviderFactory.getInstance()
    )
    Log.d(TAG, "DebugAppCheckProviderFactory installed.")
}

private fun persistDebugToken(context: Context, token: String) {
    val persistenceKey = FirebaseApp.getInstance().persistenceKey
    val prefs = context.getSharedPreferences(
        "$DEBUG_STORE_PREFIX$persistenceKey",
        Context.MODE_PRIVATE
    )
    prefs.edit().putString(DEBUG_SECRET_KEY, token).commit()
    Log.d(TAG, "App Check debug secret configured successfully.")
}

private fun setupReleaseAppCheck(context: Context) {
    val resId = context.resources.getIdentifier(
        RECAPTCHA_SITE_KEY_RES,
        "string",
        context.packageName
    )
    if (resId != 0) {
        val siteKey = context.getString(resId)
        if (siteKey.isNotBlank()) {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                RecaptchaAppCheckProviderFactory.getInstance(siteKey)
            )
            Log.d(TAG, "RecaptchaAppCheckProviderFactory installed.")
        }
    }
}
