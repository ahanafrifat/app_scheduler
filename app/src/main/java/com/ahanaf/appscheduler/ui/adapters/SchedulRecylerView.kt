package com.ahanaf.appscheduler.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.ahanaf.appscheduler.databinding.ScheduleRowBinding
import com.ahanaf.appscheduler.models.ScheduleAppInfo
import com.ahanaf.appscheduler.utils.AppUtils
import java.util.*

class SchedulRecylerView(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ScheduleAppInfo>() {

        override fun areItemsTheSame(oldItem: ScheduleAppInfo, newItem: ScheduleAppInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ScheduleAppInfo,
            newItem: ScheduleAppInfo
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return SchedulViewHolder(
            ScheduleRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SchedulViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<ScheduleAppInfo>) {
        differ.submitList(list)
    }

    class SchedulViewHolder constructor(
        private val binding: ScheduleRowBinding,
        private val interaction: Interaction?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleAppInfo) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            binding.title.text = item.name
            item.time?.let {
                if (it > System.currentTimeMillis()) {
                    binding.schedule.text = "Schedule not started"
                } else {
                    binding.schedule.text = "Schedule Done"
                }
            }
            binding.time.text = item.time?.let { AppUtils.millisLongToDateMonthYearTimeString(it) }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: ScheduleAppInfo)
    }
}