package com.truongtq_datn.extensions

import android.content.Context

class Pref {
    companion object {
        fun saveData(context: Context, key: String, value: String) {
            val sharedPreferences = context.getSharedPreferences("Pref", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getData(context: Context, key: String): String {
            val sharedPreferences = context.getSharedPreferences("Pref", Context.MODE_PRIVATE)
            return sharedPreferences.getString(key, "") ?: ""
        }

        fun clearData(context: Context) {
            val sharedPreferences = context.getSharedPreferences("Pref", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
        }
    }
}