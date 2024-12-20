package com.truongtq_datn_user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truongtq_datn_user.activity.MainActivity
import com.truongtq_datn_user.databinding.DoorItemLayoutBinding
import com.truongtq_datn_user.dialog.DoorDetailDialog
import com.truongtq_datn_user.fragment.DoorFragment
import com.truongtq_datn_user.model.DoorItem

class DoorAdapter(
    private val itemList: List<DoorItem>,
    private val mainActivity: MainActivity,
    private val fragmentManager: androidx.fragment.app.FragmentManager,
    private val doorFragment: DoorFragment
) :
    RecyclerView.Adapter<DoorAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: DoorItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            DoorItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.doorTitle.text = item.position
        "Last update: ${item.lastUpdate}".also { holder.binding.doorDescription.text = it }
        holder.binding.doorBtnViewDoor.setOnClickListener { showDoorInfo(item) }
    }

    override fun getItemCount() = itemList.size

    private fun showDoorInfo(item: DoorItem) {
        val dialog = DoorDetailDialog(item, mainActivity, doorFragment)
        dialog.show(fragmentManager, "DoorDetailDialog")
    }
}
