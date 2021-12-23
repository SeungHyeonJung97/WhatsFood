package com.ashe.whatfood

import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.ashe.whatfood.databinding.ActivityHomeBinding
import com.ashe.whatfood.dto.Place
import com.ashe.whatfood.notification.GeofenceBroadcastReceiver
import com.ashe.whatfood.notification.Notification.addDinnerPush
import com.ashe.whatfood.notification.Notification.addLunchPush
import com.ashe.whatfood.notification.Notification.getGeofence
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
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