package com.truongtq_datn_user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.truongtq_datn_user.okhttpcrud.ApiEndpoint
import com.truongtq_datn_user.extensions.Constants
import com.truongtq_datn_user.activity.MainActivity
import com.truongtq_datn_user.databinding.FragmentProfileBinding
import com.truongtq_datn_user.dialog.ChangePasswordDialog
import com.truongtq_datn_user.extensions.Extensions
import com.truongtq_datn_user.extensions.Pref
import com.truongtq_datn_user.okhttpcrud.GetRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment(private val mainActivity: MainActivity) : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.btnChangePassword.setOnClickListener { openChangePasswordDialog() }
        initValueProfile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
        _binding = null
    }

    private fun openChangePasswordDialog() {
        val changePasswordDialog = ChangePasswordDialog(mainActivity, this)
        changePasswordDialog.show(childFragmentManager, "ChangePasswordDialog");
    }

    private fun initValueProfile() {

        job = lifecycleScope.launch(Dispatchers.IO) {
            val idAccount = Pref.getString(mainActivity, Constants.ID_ACCOUNT)
            val getAccountApi = "${ApiEndpoint.Endpoint_Account}/${idAccount}"
            val getRequest = GetRequest(getAccountApi)
            val response = getRequest.execute(true)

            withContext(Dispatchers.Main) {
                if (response != null && response.isSuccessful) {
                    val info = Extensions.extractJson(response.body!!.string())
                    binding.profileName.text =
                        buildString {
                            append(Extensions.removePreAndSuffix(info.get("firstName").toString()))
                            append(" ")
                            append(Extensions.removePreAndSuffix(info.get("lastName").toString()))
                        }

                    binding.profileEmail.text =
                        Extensions.removePreAndSuffix(info.get("email").toString())
                    binding.profilePhoneNumber.text =
                        Extensions.removePreAndSuffix(info.get("phoneNumber").toString())
                    binding.profileRefId.text =
                        Extensions.removePreAndSuffix(info.get("refId").toString())
                } else {
                    Extensions.toastCall(context, "Failed to load account")
                }
            }
        }
    }

}