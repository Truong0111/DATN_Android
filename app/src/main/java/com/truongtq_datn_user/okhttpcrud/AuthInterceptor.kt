package com.truongtq_datn_user.okhttpcrud

import com.truongtq_datn_user.extensions.Constants
import com.truongtq_datn_user.activity.MyApplication
import com.truongtq_datn_user.extensions.Pref
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val currentActivity = MyApplication.instance?.activityTracker?.getCurrentActivity()
        val token = currentActivity?.let { Pref.getString(it, Constants.JWT) }

        if (token.isNullOrEmpty()) {
            return chain.proceed(chain.request())
        } else {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            return chain.proceed(request)
        }
    }
}
