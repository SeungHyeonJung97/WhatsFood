package com.ashe.whatfood.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashe.whatfood.databinding.ReviewImageBinding

class ReviewAdapter (var imageList: MutableList<String>) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.ViewHolder {
        val binding = ReviewImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ViewHolder (val binding: ReviewImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: String){
            binding.reviewImage.setImageURI(Uri.parse(data))
        }
    }
}