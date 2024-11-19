package com.truongtq_datn.firebase

import com.google.firebase.auth.FirebaseAuth
import com.truongtq_datn.extensions.Extensions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebaseAuthService {

    suspend fun registerUser(email: String, password: String, auth: FirebaseAuth): Boolean =
        suspendCancellableCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(true)
                    } else {
                        continuation.resume(false)
                    }
                }
        }

    suspend fun loginUser(email: String, password: String, auth: FirebaseAuth): Boolean =
        suspendCancellableCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(true)
                    } else {
                        continuation.resume(false)
                    }
                }
        }

    fun logoutUser(auth: FirebaseAuth) {
        auth.signOut()
    }
}
