package com.truongtq_datn.extensions

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.*
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.truongtq_datn.activity.LoginActivity
import java.security.MessageDigest
import android.util.Base64
import android.util.Log
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
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
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                dateFormat.parse(dateString)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun log(tag: String, message: String) {
            Log.d(tag, message)
        }

        fun logError(tag: String, message: String) {
            Log.e(tag, message)
        }

        fun showDateTimePickerDialog(context: Context, input: TextInputEditText) {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val datePickerDialog = DatePickerDialog(
                context, { _, selectedYear, selectedMonth, selectedDay ->
                    calendar.set(selectedYear, selectedMonth, selectedDay)

                    val timePickerDialog = TimePickerDialog(
                        context, { _, selectedHour, selectedMinute ->
                            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                            calendar.set(Calendar.MINUTE, selectedMinute)

                            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                            val formattedTime = timeFormat.format(calendar.time)

                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val formattedDate = dateFormat.format(calendar.time)

                            val formattedDateTime = "$formattedDate $formattedTime"

                            input.setText(formattedDateTime)
                        }, hour, minute, true
                    )

                    timePickerDialog.show()
                }, year, month, day
            )

            datePickerDialog.show()
        }
    }
}