package com.truongtq_datn.okhttpcrud

import okhttp3.*
import java.io.IOException

class DeleteRequest(private val url: String) {

    fun execute(): Response? {
        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        return try {
            val response = HttpClient.client.newCall(request).execute()

            if (response.isSuccessful) {
                println("DELETE request successful: ${response.body?.string()}")
            } else {
                println("DELETE request failed: ${response.message}")
            }

            response
        } catch (e: IOException) {
            println("DELETE request failed: ${e.message}")
            null
        }
    }
}
