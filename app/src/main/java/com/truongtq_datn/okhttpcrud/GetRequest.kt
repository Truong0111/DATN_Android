package com.truongtq_datn.okhttpcrud

import okhttp3.*
import java.io.IOException

class GetRequest(private val url: String) {

    fun execute(): Response? {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return try {
            val response = HttpClient.client.newCall(request).execute()

            if (response.isSuccessful) {
                println("GET request successful: ${response.body?.string()}")
            } else {
                println("GET request failed: ${response.message}")
            }

            response
        } catch (e: IOException) {
            println("GET request failed: ${e.message}")
            null
        }
    }
}
