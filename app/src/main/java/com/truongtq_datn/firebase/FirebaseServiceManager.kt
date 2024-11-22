package com.truongtq_datn.firebase

import android.content.Context
import com.truongtq_datn.extensions.Extensions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseServiceManager {
    private val accountService = FirebaseServiceAccount()
    private val doorService = FirebaseServiceDoor()
    private val ticketService = FirebaseServiceTicket()

    // Change password
    suspend fun changePassword(accountId: String, newPassword: String): Boolean {
        return withContext(Dispatchers.IO) {
            accountService.changePassword(accountId, newPassword)
        }
    }

    // Get door names
    suspend fun getDoorNames(): List<String>? {
        return withContext(Dispatchers.IO) {
            doorService.getDoorsPosition()
        }
    }

    // Get door ID by name
    suspend fun getDoorId(doorName: String): String? {
        return withContext(Dispatchers.IO) {
            doorService.getDoorID(doorName)
        }
    }

    // Register a ticket
    suspend fun registerTicket(
        context: Context,
        accountId: String,
        doorId: String,
        startTime: String,
        endTime: String,
        reason: String
    ): Boolean {
        return withContext(Dispatchers.IO) {

            val startTimeInDate = Extensions.convertStringToDate(startTime)
            val endTimeInDate = Extensions.convertStringToDate(endTime)
            if (startTimeInDate == null || endTimeInDate == null) {
                Extensions.toastCall(context, "Invalid start or end date")
                false
            } else {
                ticketService.registerTicket(
                    accountId,
                    doorId,
                    startTimeInDate,
                    endTimeInDate,
                    reason
                )
                true
            }
        }
    }
}
