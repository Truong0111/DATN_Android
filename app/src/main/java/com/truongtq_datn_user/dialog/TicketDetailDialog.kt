package com.truongtq_datn_user.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.truongtq_datn_user.activity.MainActivity
import com.truongtq_datn_user.databinding.DialogTicketDetailBinding
import com.truongtq_datn_user.fragment.TicketFragment
import com.truongtq_datn_user.model.TicketItem

class TicketDetailDialog(
    private val item: TicketItem,
    private val mainActivity: MainActivity,
    private val ticketFragment: TicketFragment
) : DialogFragment() {
    private var _binding: DialogTicketDetailBinding? = null
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
        _binding = DialogTicketDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ticketDetailIdTicket.setText(item.idTicket)
        binding.ticketDetailPositionDoor.setText(item.positionDoor)
        binding.ticketDetailFullName.setText(item.fullName)
        binding.ticketDetailStartTime.setText(item.startTime)
        binding.ticketDetailEndTime.setText(item.endTime)
        binding.ticketDetailReason.setText(item.reason)
        binding.ticketDetailCreatedAt.setText(item.createdAt)
        binding.ticketDetailStatus.setText(if (item.isAccept) "Accepted" else "Pending")


        binding.ticketDetailBtnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}