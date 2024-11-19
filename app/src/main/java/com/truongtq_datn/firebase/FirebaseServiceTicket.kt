package com.truongtq_datn.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FirebaseServiceTicket {
    private val db = FirebaseFirestore.getInstance()

    suspend fun registerTicket(
        idAccount: String,
        idDoor: String,
        startTime: Date,
        endTime: Date,
        reason: String
    ): Boolean {
        return try {

            val isoFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }

            val ticketData = hashMapOf(
                "idAccount" to idAccount,
                "idDoor" to idDoor,
                "reason" to reason,
                "startTime" to isoFormat.format(startTime),
                "endTime" to isoFormat.format(endTime),
                "createAt" to isoFormat.format(Date()),
                "isAccept" to false
            )

            val documentReference = db.collection("tickets").add(ticketData).await()
            documentReference.update("idTicket", documentReference.id).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getDataTicket(idTicket: String): Map<String, Any>? {
        return try {
            val documentReference = db.collection("tickets").document(idTicket)
            val documentSnapshot: DocumentSnapshot = documentReference.get().await()

            if (documentSnapshot.exists()) {
                documentSnapshot.data
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}