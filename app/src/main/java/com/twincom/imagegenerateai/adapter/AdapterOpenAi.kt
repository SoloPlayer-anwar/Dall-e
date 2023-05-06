package com.twincom.imagegenerateai.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.twincom.imagegenerateai.R
import com.twincom.imagegenerateai.databinding.ItemGeneratorBinding
import com.twincom.imagegenerateai.response.Data

class AdapterOpenAi(private val list: List<Data>): RecyclerView.Adapter<AdapterOpenAi.ViewHolder>() {

    inner class ViewHolder(
        private val binding : ItemGeneratorBinding,
        private val context: Context
    ): RecyclerView.ViewHolder(binding.root) {
        fun bindItem(position: Int) {
            Glide.with(context)
                .load(list[position].url)
                .placeholder(R.drawable.animasi)
                .into(binding.ivGenerate)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGeneratorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(position)
    }

    override fun getItemCount(): Int = list.size
}