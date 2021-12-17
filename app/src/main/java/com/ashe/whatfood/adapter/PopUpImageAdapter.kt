package com.ashe.whatfood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import androidx.viewpager.widget.PagerAdapter

import com.ashe.whatfood.dto.Document
import com.ashe.whatfood.R
import com.ashe.whatfood.databinding.PopupImageBinding
import com.ashe.whatfood.databinding.PreviewImageBinding
import com.bumptech.glide.Glide
import org.jetbrains.anko.contentView


class PopUpImageAdapter(val context: Context, val itemList: List<Document>, val binding: PopupImageBinding) : PagerAdapter(){

    override fun getCount(): Int = itemList.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (view == `object`)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.preview_image, null)
        val image: ImageView = view.findViewById(R.id.review_image)

        Glide.with(context)
            .load(itemList[position].image_url)
            .fitCenter()
            .into(image)

        binding.textView2.text = context.resources.getString(R.string.index_photo, position, itemList.size)

        container.addView(image)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
}
