package com.ashe.whatfood

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ashe.whatfood.other.Util.urlList
import com.ashe.whatfood.adapter.GalleryAdapter
import com.ashe.whatfood.databinding.PageGalleryBinding

class GalleryFragment : Fragment() {
    private lateinit var binding: PageGalleryBinding
    private lateinit var adapter: GalleryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PageGalleryBinding.inflate(inflater, container, false)

        try{
            binding.rvGallery.layoutManager = GridLayoutManager(activity, 3)
            binding.rvGallery.setHasFixedSize(true)
            adapter = GalleryAdapter(urlList.value!!)
            binding.rvGallery.adapter = adapter
        }catch(e: Exception){
            Log.d("Gallery",e.toString())
        }


        urlList.observe(viewLifecycleOwner){
            adapter.itemList = it
            adapter.notifyDataSetChanged()
        }
        return binding.root
    }
}

