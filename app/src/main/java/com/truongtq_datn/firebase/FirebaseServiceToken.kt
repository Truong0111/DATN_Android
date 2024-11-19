package com.truongtq_datn.firebase

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

class FirebaseServiceToken {

    private val rtdb = FirebaseDatabase.getInstance()
    private val tokensReference = rtdb.getReference("tokens")

    suspend fun getToken(idDoor: String, idAccount: String): String? {
        return try {
            val key = "$idDoor-$idAccount"
            val documentReference = tokensReference.child(key)
            val snapshot = documentReference.get().await()

            if (!snapshot.exists()) {
                return null
            }
            Log.d("TestQR", "${((snapshot.value) as Map<*, *>)["value"]}")
            val value = ((snapshot.value) as Map<*, *>)["value"] as String
            value
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TestQR", "${e.message}")
            null
        }
    }

    fun listenForTokenChanges(idDoor: String, idAccount: String, listener: ValueEventListener) {
        val key = "$idDoor-$idAccount"
        val documentReference = tokensReference.child(key)

        documentReference.addValueEventListener(listener)
    }

    fun removeTokenListener(idDoor: String, idAccount: String, listener: ValueEventListener) {
        val key = "$idDoor-$idAccount"
        val documentReference = tokensReference.child(key)

        documentReference.removeEventListener(listener)
        Log.d("TokenListener", "Listener đã bị xóa cho key: $key")
    }
}
