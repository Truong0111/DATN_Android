package com.truongtq_datn.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.truongtq_datn.extensions.Extensions
import kotlinx.coroutines.tasks.await

class FirebaseServiceAccount {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun registerAccount(
        firstName: String,
        lastName: String,
        refId: String,
        email: String,
        phoneNumber: String,
        password: String
    ): Boolean {
        return try {
            val userData = hashMapOf(
                "firstName" to firstName,
                "lastName" to lastName,
                "refId" to refId,
                "email" to email,
                "phoneNumber" to phoneNumber,
                "password" to Extensions.sha256(password),
                "arrDoor" to mutableListOf<String>(),
                "role" to listOf("user")
            )

            val documentReference = db.collection("accounts").add(userData).await()
            documentReference.update("idAccount", documentReference.id).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun loginAccount(username: String, password: String): String? {
        return try {
            val queryEmailSnapshot: QuerySnapshot =
                db.collection("accounts").whereEqualTo("email", username)
                    .whereEqualTo("password", Extensions.sha256(password))
                    .get()
                    .await()
            if (queryEmailSnapshot.isEmpty) {
                val queryPhoneNumberSnapshot: QuerySnapshot =
                    db.collection("accounts").whereEqualTo("phoneNumber", username)
                        .whereEqualTo("password", Extensions.sha256(password))
                        .get()
                        .await()
                if (queryPhoneNumberSnapshot.isEmpty) {
                    null
                } else {
                    queryPhoneNumberSnapshot.documents[0].get("idAccount").toString()
                }
            } else {
                queryEmailSnapshot.documents[0].get("idAccount").toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getDataAccount(idAccount: String): Map<String, Any>? {
        return try {
            val documentReference = db.collection("accounts").document(idAccount)
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

    suspend fun updateDataAccount(idAccount: String, data: Map<String, Any>): Boolean {
        return try {
            val documentReference = db.collection("accounts").document(idAccount)
            documentReference.update(data).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun changePassword(idAccount: String, newPassword: String): Boolean {
        return try {
            val documentReference = db.collection("accounts").document(idAccount)
            documentReference.update("password", Extensions.sha256(newPassword)).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}