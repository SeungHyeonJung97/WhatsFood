package com.ashe.whatfood.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ashe.whatfood.R
import com.ashe.whatfood.dto.Place
import com.ashe.whatfood.notification.Notification.doNotify
import com.ashe.whatfood.notification.Notification.locationId
import com.ashe.whatfood.other.RecommendMenu.recentCategory
import com.ashe.whatfood.other.RecommendMenu.recentMenu
import com.ashe.whatfood.other.RecommendMenu.recentPlace
import com.ashe.whatfood.other.RecommendMenu.savePreference
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import java.util.*

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val geofencingEvent = GeofencingEvent.fromIntent(intent!!)

        locationId = geofencingEvent.triggeringGeofences[0].requestId
        val title = locationId.substring(0, locationId.indexOf(":"))
        val category = locationId.substring(locationId.lastIndexOf(":"))
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e("GeofenceBR", errorMessage)
            return
        }

        Notification.title = "${title}에 방문하셨네요 !"
        recentPlace = title
        recentCategory = category
        savePreference("", context!!)

        Notification.createNotificationChannel(
            context, NotificationManagerCompat.IMPORTANCE_DEFAULT,
            false, context.resources.getString(R.string.app_name), "App notification channel",
            "괜찮으셨다면 리뷰를 남겨주세요 !"
        )

        // Get Type
        val geofenceTransition = geofencingEvent.geofenceTransition    // 발생 이벤트 타입

        // Test
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            val triggeringGeofences = geofencingEvent.triggeringGeofences

            val transitionMsg = when (geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> "Enter"
                Geofence.GEOFENCE_TRANSITION_EXIT -> "Exit"
                Geofence.GEOFENCE_TRANSITION_DWELL -> "Dwell"
                else -> "-"
            }
            triggeringGeofences.forEach {
//                if(transitionMsg.equals("Exit")){
//                    // TODO("나갈 때")
//                }
                Log.d("Receiver", "${it.requestId} - $transitionMsg")
//                savePreference()
                doNotify(context)
            }

        } else {
            Log.d("Receiver", "Unknown")
        }
    }
}