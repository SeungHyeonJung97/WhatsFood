package com.ashe.whatfood.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ashe.whatfood.MainActivity
import com.ashe.whatfood.R
import com.ashe.whatfood.dto.ListLayout
import com.ashe.whatfood.dto.Place
import com.google.android.gms.location.Geofence
import java.util.*

object Notification {
    lateinit var builder: NotificationCompat.Builder
    val ALARM_ID = 200
    var locationId = ""
    var title = ""

    fun createNotificationChannel(
        context: Context, importance: Int, showBadge: Boolean,
        name: String, description: String, content: String
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val channelId =
            "${context.packageName}-${context.resources.getString(R.string.app_name)}" // 2

        val intent2 = Intent(context, MainActivity::class.java)
        intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context, 0,
            intent2, PendingIntent.FLAG_UPDATE_CURRENT
        )

        builder = NotificationCompat.Builder(context, channelId)
        builder.setSmallIcon(R.drawable.roulette)
        builder.setContentTitle(title)
        builder.setContentText(content)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setAutoCancel(true)
        builder.setContentIntent(pendingIntent)
    }

    fun doNotify(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.notify(ALARM_ID, builder.build())
    }

    fun addLunchPush(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, LunchReceiver::class.java)
        val pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            if(before(Calendar.getInstance())){
                add(Calendar.DATE, 1)
            }
        }


        // 지정한 시간에 매일 알림
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pIntent
        )

    }

    fun addDinnerPush(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DinnerReceiver::class.java)
        val pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            if(before(Calendar.getInstance())){
                add(Calendar.DATE, 1)
            }
        }

        // 지정한 시간에 매일 알림
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pIntent
        )
    }

    fun getGeofence(
        reqId: String,
        geo: Pair<Double, Double>,
        radius: Float = 30f
    ): Geofence {
        return Geofence.Builder()
            .setRequestId(reqId)    // 이벤트 발생시 BroadcastReceiver에서 구분할 id
            .setCircularRegion(geo.first, geo.second, radius)    // 위치 및 반경(m)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)        // Geofence 만료 시간
            .setLoiteringDelay(600000)                            // 머물기 체크 시간
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER                // 진입 감지시
                        or Geofence.GEOFENCE_TRANSITION_EXIT    // 이탈 감지시
                        or Geofence.GEOFENCE_TRANSITION_DWELL // 머물기 감지시
            )
            .setNotificationResponsiveness(100000)
            .build()
    }
}