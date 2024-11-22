package com.truongtq_datn.activity

import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.truongtq_datn.Constants
import com.truongtq_datn.databinding.ActivityMainBinding
import com.truongtq_datn.fragment.ChangePasswordDialogFragment
import com.truongtq_datn.extensions.*
import com.truongtq_datn.firebase.FirebaseServiceManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val firebaseServiceManager = FirebaseServiceManager()
    private var buttons: Array<Button> = arrayOf()
    private lateinit var viewFlipper: ViewFlipper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupEventListeners()
    }

    //Init
    private fun setupUI() {
        val fullName = Pref.getData(this, Constants.FirstName + " " + Constants.LastName)
        binding.mainTextUsername.text = fullName
        loadDoorsIntoSpinner()
    }

    private fun setupEventListeners() {
        viewFlipper = binding.viewFlipper

        buttons = arrayOf(
            binding.mainBtnQrCode,
            binding.mainBtnProfile,
            binding.mainBtnTickets,
            binding.mainBtnLogout
        )

        buttons[0].isSelected = true

        binding.mainBtnQrCode.setOnClickListener {
            viewFlipper.displayedChild = 0
            updateButtonSelection(binding.mainBtnQrCode)
        }

        binding.mainBtnTickets.setOnClickListener {
            viewFlipper.displayedChild = 2
            loadDoorsIntoSpinner()
            updateButtonSelection(binding.mainBtnTickets)
        }

        binding.ticketBtnStartDate.setOnClickListener {
            Extensions.showDateTimePickerDialog(this, binding.ticketStartDateInput)
        }

        binding.ticketBtnEndDate.setOnClickListener {
            Extensions.showDateTimePickerDialog(this, binding.ticketEndDateInput)
        }

        binding.qrBtnScan.setOnClickListener { checkPermissionsCamera(this) }
        binding.mainBtnProfile.setOnClickListener { showProfileView() }
        binding.mainBtnLogout.setOnClickListener { logout() }
        binding.btnChangePassword.setOnClickListener { openChangePasswordDialog() }
        binding.ticketBtnSendTicket.setOnClickListener { submitTicketRequest() }
    }

    private fun updateButtonSelection(selectedButton: Button) {
        buttons.forEach { it.isSelected = false }
        selectedButton.isSelected = true
    }

    //QR functions
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            run {
                if (isGranted) {
                    openQRScanner()
                } else {
                    Extensions.toastCall(this, "Camera permission is required")
                }
            }
        }

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Extensions.toastCall(this, "Cancelled")
        } else {
            Extensions.toastCall(this, result.contents)
        }
    }

    private fun checkPermissionsCamera(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openQRScanner()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Extensions.toastCall(context, "Camera permission is required")
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun openQRScanner() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("Scan QR to access door")
            setCameraId(0)
            setBeepEnabled(false)
            setBarcodeImageEnabled(true)
            setOrientationLocked(false)
        }
        scanLauncher.launch(options)
    }

    //Profile Functions
    private fun initValueProfile() {
        val fullName =
            Pref.getData(this, Constants.FirstName) + Pref.getData(this, Constants.LastName)
        binding.profileName.text = fullName
        binding.profileEmail.text = Pref.getData(this, Constants.Email)
        binding.profilePhoneNumber.text = Pref.getData(this, Constants.PhoneNumber)
        binding.profileRefId.text = Pref.getData(this, Constants.RefId)
    }

    private fun showProfileView() {
        initValueProfile()
        viewFlipper.displayedChild = 1
        updateButtonSelection(binding.mainBtnProfile)
    }

    private fun openChangePasswordDialog() {
        ChangePasswordDialogFragment().show(supportFragmentManager, "ChangePasswordDialog")
    }

    //Ticket functions
    private fun loadDoorsIntoSpinner() {
        lifecycleScope.launch {
            val doorNames = firebaseServiceManager.getDoorNames()
            if (doorNames != null) {
                val adapter =
                    ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, doorNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.ticketDoorSpinner.adapter = adapter
            } else {
                Extensions.toastCall(this@MainActivity, "Failed to load doors")
            }
        }
    }

    private fun submitTicketRequest() {
        val startTime = binding.ticketStartDateInput.text.toString()
        val endTime = binding.ticketEndDateInput.text.toString()
        val reason = binding.ticketReasonInput.text.toString()
        val selectedDoor = binding.ticketDoorSpinner.selectedItem.toString()

        if (startTime.isEmpty() || endTime.isEmpty() || reason.isEmpty() || selectedDoor.isEmpty()) {
            Extensions.toastCall(this, "All fields are required")
            return
        }

        lifecycleScope.launch {
            val accountId = Pref.getData(this@MainActivity, "idAccount")
            val doorId = firebaseServiceManager.getDoorId(selectedDoor)
            if (doorId != null) {
                val success = firebaseServiceManager.registerTicket(
                    this@MainActivity,
                    accountId,
                    doorId,
                    startTime,
                    endTime,
                    reason
                )
                if (success) {
                    Extensions.toastCall(this@MainActivity, "Ticket request submitted")
                    clearTicketForm()
                } else {
                    Extensions.toastCall(this@MainActivity, "Failed to submit request")
                }
            } else {
                Extensions.toastCall(this@MainActivity, "Invalid door selection")
            }
        }
    }

    private fun clearTicketForm() {
        binding.ticketStartDateInput.text?.clear()
        binding.ticketEndDateInput.text?.clear()
        binding.ticketReasonInput.text?.clear()
        binding.ticketDoorSpinner.setSelection(0)
    }

    //Logout Function
    private fun logout() {
        Pref.clearData(this)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
