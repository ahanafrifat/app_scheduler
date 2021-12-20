package com.ahanaf.appscheduler.ui.adapters

import android.content.pm.ApplicationInfo
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.ahanaf.appscheduler.R
import com.ahanaf.appscheduler.databinding.AppsRowBinding
import com.bumptech.glide.RequestManager
import android.graphics.drawable.Drawable

class AppsRecyclerView(private val interaction: Interaction? = null, private val requestManager: RequestManager) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ApplicationInfo>() {

        override fun areItemsTheSame(oldItem: ApplicationInfo, newItem: ApplicationInfo): Boolean {
            return oldItem.uid==newItem.uid
        }

        override fun areContentsTheSame(oldItem: ApplicationInfo, newItem: ApplicationInfo): Boolean {
            return oldItem.uid==newItem.uid
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ViewHolder(AppsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false), interaction, requestManager)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<ApplicationInfo>) {
        differ.submitList(list)
    }

    class ViewHolder (private val binding: AppsRowBinding, private val interaction: Interaction?, private val glide: RequestManager?) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ApplicationInfo)  {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            val packageManager = binding.root.context?.packageManager

            packageManager?.let {

                val icon: Drawable = it.getApplicationIcon(item.packageName)

                glide?.load(icon)?.centerCrop()?.placeholder(R.drawable.ic_baseline_android_24)?.into(binding.image)
                binding.title.text = item.loadLabel(it)
                binding.address.text = item.packageName
            }

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: ApplicationInfo)
    }
}