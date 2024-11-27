package com.truongtq_datn_user.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.truongtq_datn_user.activity.MainActivity
import com.truongtq_datn_user.databinding.DialogDoorDetailsBinding
import com.truongtq_datn_user.fragment.DoorFragment
import com.truongtq_datn_user.model.DoorItem

class DoorDetailDialog(
    private val item: DoorItem,
    private val mainActivity: MainActivity,
    private val doorFragment: DoorFragment
) : DialogFragment() {

    private var _binding: DialogDoorDetailsBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogDoorDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.doorDetailIdDoor.setText(item.idDoor)
        binding.doorDetailIdAccountCreate.setText(item.idAccountCreate)
        binding.doorDetailPosition.setText(item.position)
        binding.doorDetailCreatedAt.setText(item.createdAt)
        binding.doorDetailLastUpdate.setText(item.lastUpdate)

        binding.doorDetailBtnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}