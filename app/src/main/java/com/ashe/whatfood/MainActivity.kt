package com.ashe.whatfood

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.ashe.whatfood.other.Util.searchResult
import com.ashe.whatfood.roulette.RotateListener
import com.ashe.whatfood.databinding.ActivityMainBinding
import com.ashe.whatfood.other.Util
import com.ashe.whatfood.roulette.RouletteSettings.getRouletteSetting
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this

        binding.roulette.apply {
            getRouletteSetting(binding.roulette)
        }

        binding.resultBtn.setOnClickListener {
            if (Util.checkLocationService(this)) {
                Util.permissionCheck(this)
            } else {
                toast("GPS를 켜주세요 !")
            }
        }
    }

    fun rotateRoulette() {
        val rotateListener = object : RotateListener {
            override fun onRotateStart() {
            }

            override fun onRotateEnd(result: String) {
                binding.textView11.text = "$result 입니다."
                searchResult = result
                binding.resultBtn.visibility = View.VISIBLE
            }
        }
        val toDegrees = (2000..10000).random().toFloat()
        binding.roulette.rotateRoulette(toDegrees, 3000, rotateListener)
    }
}