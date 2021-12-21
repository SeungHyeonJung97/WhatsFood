package com.ashe.whatfood

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ashe.whatfood.databinding.ActivityHomeBinding
import org.jetbrains.anko.startActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        binding.btnGoRoulette.setOnClickListener { startActivity<MainActivity>() }
        binding.btnGoSetting.setOnClickListener { startActivity<SettingActivity>() }
    }

    var backpressedTime = 0L

    override fun onBackPressed() {
        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis()
            Toast.makeText(this, "\'뒤로\' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish()
        }
    }
}