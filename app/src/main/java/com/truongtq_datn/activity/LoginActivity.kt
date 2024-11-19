package com.truongtq_datn.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.truongtq_datn.Constants
import com.truongtq_datn.extensions.Extensions
import com.truongtq_datn.databinding.ActivityLoginBinding
import com.truongtq_datn.extensions.Pref
import com.truongtq_datn.firebase.FirebaseServiceAccount
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.loginBtnRegister.setOnClickListener {
            changeIntentToRegister()
        }

        binding.loginBtnLogin.setOnClickListener {
            loginClicked(
                binding.loginInputUsername.text.toString(),
                binding.loginInputPassword.text.toString()
            )

//            loginUser(
//                binding.loginInputUsername.text.toString(),
//                binding.loginInputPassword.text.toString()
//            )
        }
    }

    private fun loginClicked(username: String, password: String) {
        if (binding.loginInputUsername.text.toString()
                .isEmpty() || binding.loginInputPassword.text.toString().isEmpty()
        ) {
            Extensions.toastCall(this, "Please enter username and password")
            return
        }
        val firebaseServiceAccount = FirebaseServiceAccount()
        lifecycleScope.launch {
            val idAccount = firebaseServiceAccount.loginAccount(username, password)
            Log.d("TestLogin", "idAccount: $idAccount")
            if (idAccount != null) {
                Log.d("TestLogin", "Login successful")
                Extensions.toastCall(this@LoginActivity, "Login successful")
                val data = firebaseServiceAccount.getDataAccount(idAccount)
                Pref.saveData(this@LoginActivity, "idAccount", idAccount)
                if (data != null) {
                    Pref.saveData(
                        this@LoginActivity,
                        Constants.FirstName,
                        data["firstName"] as String
                    )
                    Pref.saveData(
                        this@LoginActivity,
                        Constants.LastName,
                        data["lastName"] as String
                    )
                    Pref.saveData(this@LoginActivity, Constants.Email, data["email"] as String)
                    Pref.saveData(
                        this@LoginActivity,
                        Constants.PhoneNumber,
                        data["phoneNumber"] as String
                    )
                    Pref.saveData(this@LoginActivity, Constants.RefId, data["refId"] as String)
                    Pref.saveData(
                        this@LoginActivity,
                        Constants.Password,
                        data["password"] as String
                    )

                    val arrRole = data["role"] as? List<*>
                    arrRole?.forEachIndexed { index, item ->
                        val role = item as? String
                        role?.let {
                            Pref.saveData(this@LoginActivity, "role_$index", it)
                        }
                    }
                }
                changeIntentToMain()
            } else {
                Log.e("TestLogin", "Login failed")
                Extensions.toastCall(this@LoginActivity, "Login failed")
            }
        }
    }

    private fun changeIntentToMain() {
        Extensions.changeIntent(this, MainActivity::class.java)
    }

    private fun changeIntentToRegister() {
        Extensions.changeIntent(this, RegisterActivity::class.java)
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Extensions.toastCall(this, "Login failed")
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            changeIntentToMain()
            finish()
        }
    }
}