package com.truongtq_datn_user.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.truongtq_datn_user.extensions.Constants
import com.truongtq_datn_user.activity.LoginActivity
import com.truongtq_datn_user.activity.MainActivity
import com.truongtq_datn_user.databinding.FragmentSettingBinding
import com.truongtq_datn_user.extensions.BiometricPromptManager
import com.truongtq_datn_user.extensions.Extensions
import com.truongtq_datn_user.extensions.Pref
import kotlinx.coroutines.Job

class SettingFragment(private val mainActivity: MainActivity) : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var biometricPromptManager: BiometricPromptManager
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        biometricPromptManager = mainActivity.biometricPromptManager
        binding.settingSwitchBiometric.isChecked = biometricPromptManager.isBiometricEnabled()
        binding.settingSwitchBiometric.setOnClickListener {
            if (binding.settingSwitchBiometric.isChecked) {
                turnOnBiometric()
            } else {
                showDialog()
            }
        }

        binding.settingBtnLogout.setOnClickListener { logout() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
        _binding = null
    }

    private fun turnOnBiometric() {
        mainActivity.showBiometric(
            description = "Authenticate to next login",
            successAction = {
                biometricPromptManager.enableBiometric()
                val tokenBiometric =
                    Pref.getString(mainActivity, Constants.TOKEN_BIOMETRIC)
                Extensions.saveAuthToken(mainActivity, tokenBiometric)
                Extensions.toastCall(mainActivity, "Set biometric success on setting")
            },
            failAction = {
                Extensions.toastCall(mainActivity, "Set biometric failed on setting")
            },
        )
    }

    private fun showDialog() {
        val dialog = MaterialAlertDialogBuilder(mainActivity)
            .setTitle("Confirm")
            .setMessage("Are you sure to continue")
            .setPositiveButton("Yes") { _, _ ->
                mainActivity.showBiometric(
                    description = "Please authenticate to disable biometric",
                    successAction = {
                        biometricPromptManager.disableBiometric()
                        Pref.remove(mainActivity, Constants.TOKEN_BIOMETRIC)
                        Extensions.toastCall(mainActivity, "Disable biometric success")
                    },
                    failAction = {

                    },
                )
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                binding.settingSwitchBiometric.isChecked = true
                dialogInterface.dismiss()
            }
            .setOnDismissListener {

            }

        dialog.show()
    }

    private fun logout() {
        Pref.remove(mainActivity, Constants.TOKEN_BIOMETRIC_TIME)
        startActivity(Intent(activity, LoginActivity::class.java))
    }
}