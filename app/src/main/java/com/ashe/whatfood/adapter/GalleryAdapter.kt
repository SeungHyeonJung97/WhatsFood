package com.ashe.whatfood.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashe.whatfood.dto.Document
import com.ashe.whatfood.PopUpImage
import com.ashe.whatfood.adapter.GalleryAdapter.ViewHolder
import com.ashe.whatfood.databinding.PreviewImageBinding
import com.bumptech.glide.Glide

class GalleryAdapter(var itemList: List<Document>): RecyclerView.Adapter<ViewHolder>() {
    lateinit var context: Context

    inner class ViewHolder(var view: PreviewImageBinding): RecyclerView.ViewHolder(view.root) {
        fun bind(data: String) {
            Glide.with(view.root)
                .load(data)
                .fitCenter()
                .into(view.reviewImage)

            view.reviewImage.setOnClickListener { popUpImg(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PreviewImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position].image_url)
    }

    override fun getItemCount(): Int = itemList.size

    fun popUpImg(position: Int){
        val intent = Intent(context, PopUpImage::class.java)
        intent.putExtra("position",position)
        context.startActivity(intent)
    }

}

