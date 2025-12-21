package com.example.p3_dss

import android.content.Context
import android.util.Base64

object AdminSession {
    private const val PREF = "admin_session"
    private const val KEY_USER = "user"
    private const val KEY_PASS = "pass"

    fun save(context: Context, user: String, pass: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_USER, user.trim())
            .putString(KEY_PASS, pass)
            .apply()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return !sp.getString(KEY_USER, null).isNullOrBlank() && !sp.getString(KEY_PASS, null).isNullOrBlank()
    }

    fun authHeader(context: Context): String? {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val u = sp.getString(KEY_USER, null)?.trim() ?: return null
        val p = sp.getString(KEY_PASS, null) ?: return null
        val token = Base64.encodeToString("$u:$p".toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        return "Basic $token"
    }
}
