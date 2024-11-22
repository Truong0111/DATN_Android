package com.truongtq_datn.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.truongtq_datn.ApiEndpoint
import com.truongtq_datn.extensions.Extensions
import com.truongtq_datn.databinding.ActivityLoginBinding
import com.truongtq_datn.okhttpcrud.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtnRegister.setOnClickListener {
            Extensions.changeIntent(this, RegisterActivity::class.java)
        }

        binding.loginBtnLogin.setOnClickListener {
            loginClicked(
                binding.loginInputUsername.text.toString(),
                binding.loginInputPassword.text.toString()
            )
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun loginClicked(username: String, password: String) {
        if (binding.loginInputUsername.text.toString()
                .isEmpty() || binding.loginInputPassword.text.toString().isEmpty()
        ) {
            Extensions.toastCall(this, "Please enter username and password")
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            val loginApi = ApiEndpoint.Endpoint_Account_Login
            val requestBody =
                """{"username": "$username", "password": "$password", "typeApp": "client"}"""
            val postRequest = PostRequest(loginApi, requestBody)
            val response = postRequest.execute()

            withContext(Dispatchers.Main) {
                if (response != null && response.isSuccessful) {
                    Extensions.toastCall(applicationContext, "Login successful.")
                    Extensions.changeIntent(this@LoginActivity, MainActivity::class.java);
                } else {
                    Extensions.toastCall(applicationContext, "Login failed.")
                }
            }
        }
    }
}