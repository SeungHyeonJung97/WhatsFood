package com.ashe.whatfood

import android.app.Activity
import android.os.Bundle
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.ashe.whatfood.other.Util.urlList
import com.ashe.whatfood.adapter.PopUpImageAdapter
import com.ashe.whatfood.databinding.PopupImageBinding

class PopUpImage: Activity() {
    private lateinit var binding: PopupImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DataBindingUtil.setContentView(this, R.layout.popup_image)
        val itemList = urlList.value!!.toList()
        var currentIndex = intent.getIntExtra("position", 0)

        binding.viewPager.clipToPadding = true
        var adapter = PopUpImageAdapter(this,itemList, binding)

        binding.viewPager.adapter = adapter

        binding.btnCancel.setOnClickListener{
            finish()
        }

    }
}