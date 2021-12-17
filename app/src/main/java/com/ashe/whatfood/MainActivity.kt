package com.ashe.whatfood

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.ashe.whatfood.Util.searchResult
import com.ashe.whatfood.roulette.RotateListener
import com.ashe.whatfood.databinding.ActivityMainBinding
import org.jetbrains.anko.startActivity
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this

        binding.roulette.apply {
            setRouletteDataList(listOf("한식", "중식", "양식", "일식"))
            setRouletteSize(getRouletteDataList().size)
            setRouletteTextColor(R.color.black)
            setRouletteBorderLineColor(R.color.white)

            getRouletteTextColor()
            getRouletteTextSize()
        }

        binding.resultBtn.setOnClickListener {
            startActivity<MapActivity>()
            searchResult = binding.textView.text.toString()
        }

    }

    fun rotateRoulette() {
        val rotateListener = object : RotateListener {
            override fun onRotateStart() {
            }

            override fun onRotateEnd(result: String) {
                binding.textView.text = "$result"
                binding.resultBtn.visibility = View.VISIBLE
            }
        }
        val toDegrees = (2000..10000).random().toFloat()
        binding.roulette.rotateRoulette(toDegrees, 3000, rotateListener)
    }
}