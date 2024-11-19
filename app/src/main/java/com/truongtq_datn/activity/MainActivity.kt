package com.truongtq_datn.activity

import android.R
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.ViewFlipper
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.truongtq_datn.Constants
import com.truongtq_datn.databinding.ActivityMainBinding
import com.truongtq_datn.databinding.DialogChangepasswordLayoutBinding
import com.truongtq_datn.extensions.Extensions
import com.truongtq_datn.extensions.Pref
import com.truongtq_datn.extensions.Qr
import com.truongtq_datn.firebase.FirebaseServiceAccount
import com.truongtq_datn.firebase.FirebaseServiceDoor
import com.truongtq_datn.firebase.FirebaseServiceTicket
import com.truongtq_datn.firebase.FirebaseServiceToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var changePasswordBinding: DialogChangepasswordLayoutBinding
    private var buttons: Array<Button> = arrayOf()
    private lateinit var viewFlipper: ViewFlipper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        initComponent()
        initValue()
    }

    private fun initComponent() {
        //Main Tab
        viewFlipper = mainBinding.viewFlipper

        mainBinding.mainBtnQrCode.setOnClickListener {
            viewFlipper.displayedChild = 0
            createDoorSpinner(mainBinding.qrDoorSpinner)
            updateButtonSelection(mainBinding.mainBtnQrCode)
        }

        mainBinding.mainBtnProfile.setOnClickListener {
            viewFlipper.displayedChild = 1
            updateButtonSelection(mainBinding.mainBtnProfile)
        }

        mainBinding.mainBtnTickets.setOnClickListener {
            viewFlipper.displayedChild = 2
            createDoorSpinner(mainBinding.ticketDoorSpinner)
            updateButtonSelection(mainBinding.mainBtnTickets)
        }

        mainBinding.mainBtnLogout.setOnClickListener {
            logout()
        }

        buttons = arrayOf(
            mainBinding.mainBtnQrCode,
            mainBinding.mainBtnProfile,
            mainBinding.mainBtnTickets,
            mainBinding.mainBtnLogout
        )

        buttons[0].isSelected = true

        //QR Tab
        createDoorSpinner(mainBinding.qrDoorSpinner)
        mainBinding.qrCodeImage.visibility = View.GONE
        mainBinding.qrBtnUpdate.setOnClickListener {
            showQRCode()
        }

        //Profile Tab
        mainBinding.btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        //Ticket Tab
        mainBinding.ticketBtnStartDate.setOnClickListener {
            showDateTimePickerDialog(mainBinding.ticketStartDateInput)
        }

        mainBinding.ticketBtnEndDate.setOnClickListener {
            showDateTimePickerDialog(mainBinding.ticketEndDateInput)
        }

        mainBinding.ticketStartDateInput.setOnClickListener {
            showDateTimePickerDialog(mainBinding.ticketStartDateInput)
        }

        mainBinding.ticketEndDateInput.setOnClickListener {
            showDateTimePickerDialog(mainBinding.ticketEndDateInput)
        }

        mainBinding.ticketBtnSendTicket.setOnClickListener {
            sendTicket()
        }
    }

    private fun initValue() {
        val fullName =
            Pref.getData(this, Constants.FirstName) + Pref.getData(this, Constants.LastName)
        mainBinding.profileName.text = fullName
        mainBinding.profileEmail.text = Pref.getData(this, Constants.Email)
        mainBinding.profilePhoneNumber.text = Pref.getData(this, Constants.PhoneNumber)
        mainBinding.profileRefId.text = Pref.getData(this, Constants.RefId)
    }

    private fun showQRCode() {
        val firebaseServiceToken = FirebaseServiceToken()
        val firebaseServiceDoor = FirebaseServiceDoor()

        lifecycleScope.launch {
            val idDoor =
                firebaseServiceDoor.getDoorID(mainBinding.qrDoorSpinner.selectedItem.toString())
                    ?: return@launch

            val qrCode = firebaseServiceToken.getToken(
                idDoor, Pref.getData(this@MainActivity, "idAccount")
            )
            if (qrCode == null) {
                mainBinding.qrCodeImage.visibility = View.GONE
                Extensions.toastCall(this@MainActivity, "Access deny!")
                return@launch

            } else {
                val qrCodeBitmap = Qr.generateQRCode(qrCode)
                if (qrCodeBitmap == null) {
                    mainBinding.qrCodeImage.visibility = View.GONE
                } else {
                    mainBinding.qrCodeImage.setImageBitmap(qrCodeBitmap)
                    mainBinding.qrCodeImage.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateButtonSelection(selectedButton: Button) {
        buttons.forEach { it.isSelected = false }
        selectedButton.isSelected = true
    }

    private fun logout() {
        Pref.clearData(this)
        finish()
    }

    private fun showChangePasswordDialog() {
        val dialog = Dialog(this)
        changePasswordBinding = DialogChangepasswordLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(changePasswordBinding.root)

        changePasswordBinding.btnChangePassword.setOnClickListener {
            val currentPassword = Pref.getData(this, Constants.Password)
            val oldPasswordInput = changePasswordBinding.oldPassword.text.toString()

            if (currentPassword != Extensions.sha256(oldPasswordInput)) {
                Extensions.toastCall(this, "Current password is not correct")
                return@setOnClickListener
            }

            val newPassword = changePasswordBinding.newPassword.text.toString()
            val confirmPassword = changePasswordBinding.confirmPassword.text.toString()

            if (currentPassword == Extensions.sha256(newPassword)) {
                Extensions.toastCall(this, "New password cannot be the same as the old password")
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Extensions.toastCall(this, "Confirm password is not correct")
                return@setOnClickListener
            }

            val firebaseServiceAccount = FirebaseServiceAccount()
            lifecycleScope.launch {
                val id = Pref.getData(this@MainActivity, "idAccount")
                val changePasswordSuccess = withContext(Dispatchers.IO) {
                    firebaseServiceAccount.changePassword(id, newPassword)
                }

                if (changePasswordSuccess) {
                    Pref.saveData(
                        this@MainActivity,
                        Constants.Password,
                        Extensions.sha256(newPassword)
                    )
                    dialog.dismiss()
                    Extensions.toastCall(this@MainActivity, "Change password success")
                } else {
                    Extensions.toastCall(this@MainActivity, "Change password failed")
                }
            }
        }

        dialog.show()
    }

    private fun createDoorSpinner(spinner: Spinner) {
        val firebaseServiceDoor = FirebaseServiceDoor()

        lifecycleScope.launch {
            val doorNames = firebaseServiceDoor.getDoorsPosition()
            doorNames?.let {
                val adapter = ArrayAdapter(
                    this@MainActivity, R.layout.simple_spinner_item, it
                )
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            } ?: run {
                Extensions.toastCall(this@MainActivity, "No door found")
            }
        }
    }

    private fun showDateTimePickerDialog(input: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val datePickerDialog = DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)

                val timePickerDialog = TimePickerDialog(
                    this, { _, selectedHour, selectedMinute ->
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                        calendar.set(Calendar.MINUTE, selectedMinute)

                        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                        val formattedTime = timeFormat.format(calendar.time)

                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val formattedDate = dateFormat.format(calendar.time)

                        val formattedDateTime = "$formattedDate $formattedTime"

                        input.setText(formattedDateTime)
                    }, hour, minute, true
                )

                timePickerDialog.show()
            }, year, month, day
        )

        datePickerDialog.show()
    }

    private fun sendTicket() {
        val firebaseServiceTicket = FirebaseServiceTicket()
        val firebaseServiceDoor = FirebaseServiceDoor()

        lifecycleScope.launch {
            val doorId =
                firebaseServiceDoor.getDoorID(mainBinding.ticketDoorSpinner.selectedItem.toString())
            if (doorId == null) {
                Extensions.toastCall(this@MainActivity, "No door found")
                return@launch
            }

            val startTime =
                Extensions.convertStringToDate(mainBinding.ticketStartDateInput.text.toString())
            if (startTime == null) {
                Extensions.toastCall(this@MainActivity, "Invalid start date")
                return@launch
            }
            val endTime =
                Extensions.convertStringToDate(mainBinding.ticketEndDateInput.text.toString())
            if (endTime == null) {
                Extensions.toastCall(this@MainActivity, "Invalid end date")
                return@launch
            }

            val sendTicketSuccess = firebaseServiceTicket.registerTicket(
                Pref.getData(this@MainActivity, "idAccount"),
                doorId,
                startTime,
                endTime,
                mainBinding.ticketReasonInput.text.toString(),
            )

            if (sendTicketSuccess) {
                Extensions.toastCall(this@MainActivity, "Send ticket success")
                mainBinding.ticketReasonInput.text?.clear()
                mainBinding.ticketStartDateInput.text?.clear()
                mainBinding.ticketEndDateInput.text?.clear()
            } else {
                Extensions.toastCall(this@MainActivity, "Send ticket failed")
            }
        }
    }
}