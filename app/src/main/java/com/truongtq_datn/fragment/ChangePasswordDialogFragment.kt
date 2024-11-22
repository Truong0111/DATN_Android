package com.truongtq_datn.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.truongtq_datn.Constants
import com.truongtq_datn.databinding.DialogChangepasswordLayoutBinding
import com.truongtq_datn.extensions.Extensions
import com.truongtq_datn.extensions.Pref
import kotlinx.coroutines.launch

class ChangePasswordDialogFragment : DialogFragment() {

    private var _binding: DialogChangepasswordLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogChangepasswordLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            this.dismiss()
        }

        binding.btnChangePassword.setOnClickListener {
            val currentPassword = Pref.getData(requireContext(), Constants.Password)
            val oldPasswordInput = binding.oldPassword.text.toString()

            if (currentPassword != Extensions.sha256(oldPasswordInput)) {
                Extensions.toastCall(requireContext(), "Current password is not correct")
                return@setOnClickListener
            }

            val newPassword = binding.newPassword.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()

            if (currentPassword == Extensions.sha256(newPassword)) {
                Extensions.toastCall(
                    requireContext(),
                    "New password cannot be the same as the old password"
                )
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Extensions.toastCall(requireContext(), "Confirm password is not correct")
                return@setOnClickListener
            }

            val accountId = Pref.getData(requireContext(), "idAccount")

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
