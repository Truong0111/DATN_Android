package com.truongtq_datn.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.truongtq_datn.ApiEndpoint
import com.truongtq_datn.databinding.ActivityRegisterBinding
import com.truongtq_datn.extensions.Extensions
import com.truongtq_datn.okhttpcrud.PostRequest
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    @OptIn(DelicateCoroutinesApi::class)
    private fun registerClicked(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phoneNumber: String,
        refId: String
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val loginApi = ApiEndpoint.Endpoint_Account_Register
            val requestBody =
                """{"firstName": "$firstName", "lastName": "$lastName", "email": "$email", "password": "$password", "phoneNumber": "$phoneNumber", "refId": "$refId", "role" : ["user"]}"""
            val postRequest = PostRequest(loginApi, requestBody)
            val response = postRequest.execute()

            withContext(Dispatchers.Main) {
                if (response != null && response.isSuccessful) {
                    Extensions.toastCall(applicationContext, "Register successful.")
                    Extensions.changeIntent(this@RegisterActivity, LoginActivity::class.java);
                } else {
                    Extensions.toastCall(applicationContext, "Registers failed.")
                }
            }
        }
    }
}