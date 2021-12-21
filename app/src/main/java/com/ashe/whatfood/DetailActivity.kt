package com.ashe.whatfood

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.MenuItem
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ashe.whatfood.other.Util.currentLocationlat
import com.ashe.whatfood.other.Util.currentLocationlon
import com.ashe.whatfood.other.Util.itemName
import com.ashe.whatfood.databinding.ActivityDetailBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.regex.Pattern

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    var state = MutableLiveData<String>()
    private lateinit var destLocation: DoubleArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        state.observe(this) {
            Log.e("Detail","${state.value}")
            if (it.equals("Review")) {
                binding.button2.visibility = View.VISIBLE
            }else{
                binding.button2.visibility = View.GONE
            }
        }

        if (intent!!.hasExtra("itemName")) {
            itemName = intent!!.getStringExtra("itemName").toString()
        }
        var url = ""
        if (intent.hasExtra("itemName") || intent.hasExtra("itemUrl")) {
            binding.tvName.text = intent.getStringExtra("itemName")
            url = intent.getStringExtra("itemUrl").toString()
        }
        if(intent.hasExtra("destLocation")) {
            val data = intent.getDoubleArrayExtra("destLocation")!!
            destLocation = doubleArrayOf(data[0], data[1])
        }

        binding.viewPager.adapter =
            ViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.registerOnPageChangeCallback(ViewPagerPageChangeCallback())

        val pattern = Pattern.compile("상세 정보 확인하기")

        val mTransform = Linkify.TransformFilter { match, url -> "" }

        binding.button2.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra("itemName", itemName)
            startActivity(intent)
        }

        binding.ivPrev.setOnClickListener { finish() }


        Linkify.addLinks(binding.textView3, pattern, url, null, mTransform)

        binding.navBottom.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.page_gallery -> {
                        binding.viewPager.currentItem = 0
                        state.postValue("Gallery")
                        return true
                    }
                    R.id.page_navigate -> {
                        Log.e("detail","${destLocation[0]}, ${destLocation[1]}")
                        val scheme = "kakaomap://route?sp=$currentLocationlat,$currentLocationlon&ep=${destLocation[0]},${destLocation[1]}&by=FOOT"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scheme))
                        startActivity(intent)
                    }
                    R.id.page_review -> {
                        binding.viewPager.currentItem = 1
                        state.postValue("Review")
                        return true
                    }
                }
                return false
            }
        })
    }

    inner class ViewPagerPageChangeCallback : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {

            binding.navBottom.selectedItemId = when (position) {
                0 -> R.id.page_gallery
                1 -> R.id.page_review
                else -> error("No id")
            }
        }
    }

    inner class ViewPagerAdapter(
        supportFragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) :
        FragmentStateAdapter(supportFragmentManager, lifecycle) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> GalleryFragment()
                1 -> ReviewFragment() // ReviewActivity()
                else -> error("No Fragment")
            }
        }
    }
}
