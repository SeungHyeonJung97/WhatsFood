package com.ashe.whatfood.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ashe.whatfood.R
import com.ashe.whatfood.other.RecommendMenu
import com.ashe.whatfood.other.RecommendMenu.loadPreference
import java.util.*


class LunchReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "AlarmReceiver"
    }


    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received intent : $intent")

        loadPreference(context)

        Notification.title = "점심 시간이에요 !"

        Notification.createNotificationChannel(context, NotificationManagerCompat.IMPORTANCE_DEFAULT,
            false, context.resources.getString(R.string.app_name), "App notification channel",
            "점심 메뉴로 ${RecommendMenu.recommend()} 은(는) 어떠세요 ??")

        Log.e("lunch","lunch")

        Notification.doNotify(context)
    }
}