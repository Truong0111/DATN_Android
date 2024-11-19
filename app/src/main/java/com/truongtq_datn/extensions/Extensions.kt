package com.truongtq_datn.extensions

import android.app.Activity
import android.content.*
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.truongtq_datn.activity.LoginActivity
import java.security.MessageDigest
import android.util.Base64
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Extensions {
    companion object {
        fun sha256(param: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(param.toByteArray())
            return Base64.encodeToString(bytes, Base64.NO_WRAP)
        }

        fun toastCall(view: Context?, string: String) {
            view?.let {
                Toast.makeText(it, string, Toast.LENGTH_SHORT).show()
                return
            }
        }

        fun <T : Activity> changeIntent(view: Context?, activityClass: Class<T>) {
            view?.let {
                val intent = Intent(it, activityClass)
                startActivity(it, intent, null)
            }
        }

        fun convertStringToDate(dateString: String): Date? {
            return try {
                // Định dạng phù hợp với chuỗi của bạn, ví dụ: "dd/MM/yyyy HH:mm"
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                dateFormat.parse(dateString)  // Trả về một đối tượng Date
            } catch (e: Exception) {
                e.printStackTrace()
                null  // Trả về null nếu chuỗi không đúng định dạng
            }
        }

        fun log(tag: String, message: String) {
            Log.d(tag, message)
        }

        fun logError(tag: String, message: String) {
            Log.e(tag, message)
        }
    }
}