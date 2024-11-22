package com.truongtq_datn.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.truongtq_datn.databinding.ActivityRegisterBinding
import com.truongtq_datn.extensions.Extensions
import com.truongtq_datn.firebase.FirebaseServiceAccount
import kotlinx.coroutines.launch

class RegisterActivity : ComponentActivity() {
    private lateinit var binding: ActivityRegisterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerBtnLogin.setOnClickListener {
            loginClicked()
        }

        binding.registerBtnRegister.setOnClickListener {
            registerClicked(
                binding.registerInputFirstName.text.toString(),
                binding.registerInputLastName.text.toString(),
                binding.registerInputEmail.text.toString(),
                binding.registerInputPassword.text.toString(),
                binding.registerInputPhoneNumber.text.toString(),
                binding.registerInputRefId.text.toString()
            )
        }
    }

    private fun loginClicked() {
        Extensions.changeIntent(this, LoginActivity::class.java)
    }

    private fun registerClicked(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phoneNumber: String,
        refId: String
    ) {
        val firebaseService = FirebaseServiceAccount()
        lifecycleScope.launch {
            val isRegistered = firebaseService.registerAccount(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password,
                phoneNumber = phoneNumber,
                refId = refId
            )
            if (isRegistered) {
                Log.d("RegisterActivity", "User registered successfully")
                Extensions.toastCall(this@RegisterActivity, "Registered successfully")
            } else {
                Log.e("RegisterActivity", "Registration failed")
                Extensions.toastCall(this@RegisterActivity, "Registration failed")
            }
        }
    }
}