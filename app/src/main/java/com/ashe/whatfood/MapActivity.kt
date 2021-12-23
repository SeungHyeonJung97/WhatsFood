package com.ashe.whatfood

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashe.whatfood.other.Util.searchResult
import com.ashe.whatfood.adapter.ListAdapter
import com.ashe.whatfood.databinding.ActivityMapBinding
import com.ashe.whatfood.dto.ListLayout
import com.ashe.whatfood.notification.GeofenceBroadcastReceiver
import com.ashe.whatfood.notification.Notification
import com.ashe.whatfood.notification.Notification.getGeofence
import com.ashe.whatfood.other.Util
import com.ashe.whatfood.other.Util.saveOk
import com.ashe.whatfood.other.Util.savedItem
import com.ashe.whatfood.viewmodel.MapActivityViewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.jetbrains.anko.toast

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private lateinit var mapViewModel: MapActivityViewModel
    var listItems: ArrayList<ListLayout> = arrayListOf(ListLayout())

    lateinit var map: MapView
    private var listAdapter = ListAdapter(listItems)
    private var keyword = ""
    private var address = ""

    var before_item = MapPOIItem()
    var after_item = MapPOIItem()

    private val MY_PERMISSIONS_REQ_ACCESS_FINE_LOCATION = 100
    private val MY_PERMISSIONS_REQ_ACCESS_BACKGROUND_LOCATION = 101

    private val geofencingClient: GeofencingClient by lazy {
        LocationServices.getGeofencingClient(this)
    }

    val geofenceList: MutableList<Geofence> by lazy {
        mutableListOf(
            getGeofence("서밋코퍼레이션:음식점 > 회사", Pair(37.40088, 126.96938))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("MapActivity", "onCreate")
        Settings()

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = listAdapter

        Observe()
        Listener()

        Notification.addLunchPush(this)
        Notification.addDinnerPush(this)
    }

    private fun Observe() {
        mapViewModel.currentPostion.observe(this) { currentAddress ->
            val stringArray = currentAddress.split(" ")
            address = "${stringArray[1]} ${stringArray[2]} ${stringArray[3]}"
            keyword = "$address $searchResult"
            savedItem = keyword
            saveOk = true
            mapViewModel.searchKeyword(keyword)
        }
        mapViewModel.mapPoint.observe(this) {
            if (mapViewModel.map.parent == null) {
                binding.kakaoMap.addView(mapViewModel.map)
            }
        }
        mapViewModel.listItem.observe(this) {
            listItems = it
            listItems.forEach {
                val reqId = "${it.name}:${it.category}"
                geofenceList.add(getGeofence(reqId, Pair(it.y, it.x)))
            }
            binding.clRecyclerView.visibility = View.VISIBLE
            listAdapter.itemList = listItems
            listAdapter.notifyDataSetChanged()
        }
        mapViewModel.nearbyRestaurants.observe(this) { places ->
            places.forEach {
                val reqId = "${it.place_name}:${it.category_name}"
                geofenceList.add(
                    getGeofence(
                        reqId,
                        Pair(it.y.toDouble(), it.x.toDouble())
                    )
                )
            }
            // Log.e("Map","${geofenceList}")
        }
    }

    private fun Listener() {
        listAdapter.setItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val mapPoint =
                    MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                mapViewModel.setMapCenterPointAndZoomLevel(mapPoint)
                if (before_item == null) {
                    before_item = mapViewModel.map.findPOIItemByName(listItems[position].name)[0]
                } else {
                    mapViewModel.map.removePOIItem(after_item)
                    before_item!!.markerType = MapPOIItem.MarkerType.BluePin
                    mapViewModel.map.addPOIItem(before_item)

                    before_item = mapViewModel.map.findPOIItemByName(listItems[position].name)[0]
                }
                after_item = before_item
                mapViewModel.map.removePOIItem(after_item)
                after_item?.markerType = MapPOIItem.MarkerType.YellowPin
                mapViewModel.map.addPOIItem(after_item)
            }
        })

        binding.searchBtn.setOnClickListener {
            keyword = "$address ${binding.searchEt.text}"
            mapViewModel.searchKeyword(keyword)
        }
    }

    private fun Settings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)

        map = MapView(this)

        mapViewModel =
            ViewModelProvider(this).get(
                MapActivityViewModel::class.java
            )

        mapViewModel.setMapView(map)

        binding.activity = mapViewModel

        mapViewModel.setCurrentLocationTracking(this)

        if (saveOk) {
            binding.clRecyclerView.visibility = View.VISIBLE
            mapViewModel.searchKeyword(savedItem)
        }
    }

    private fun addGeofences() {
        checkPermission()
        geofencingClient.addGeofences(getGeofencingRequest(geofenceList), geofencePendingIntent)
            .run {
                addOnSuccessListener {
                  //   Toast.makeText(this@MapActivity, "add Success", Toast.LENGTH_LONG).show()
                }
                addOnFailureListener {
                  //   Toast.makeText(this@MapActivity, "add Fail", Toast.LENGTH_LONG).show()
                }
            }
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getGeofencingRequest(list: List<Geofence>): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            // Geofence 이벤트는 진입시 부터 처리할 때
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(list)    // Geofence 리스트 추가
        }.build()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQ_ACCESS_FINE_LOCATION,
            MY_PERMISSIONS_REQ_ACCESS_BACKGROUND_LOCATION -> {
                grantResults.apply {
                    if (this.isNotEmpty()) {
                        this.forEach {
                            if (it != PackageManager.PERMISSION_GRANTED) {
                                checkPermission()
                                return
                            }
                        }
                    } else {
                        checkPermission()
                    }
                }
            }
        }
    }

    private fun checkPermission() {
        val permissionAccessFineLocationApproved = ActivityCompat
            .checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        if (permissionAccessFineLocationApproved) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val backgroundLocationPermissionApproved = ActivityCompat
                    .checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) ==
                        PackageManager.PERMISSION_GRANTED

                if (!backgroundLocationPermissionApproved) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        MY_PERMISSIONS_REQ_ACCESS_BACKGROUND_LOCATION
                    )
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQ_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRestart() {
        Log.e("MapActivity", "onRestart")

        if (binding.kakaoMap.size == 0) {
            mapViewModel.reload(this)
            binding.kakaoMap.addView(mapViewModel.map)
        }
        super.onRestart()
    }

    override fun onStop() {
        Log.e("MapActivity", "onStop")
        binding.kakaoMap.removeAllViews()
        mapViewModel.isFinish.postValue(false)

        addGeofences()
        Log.e("Map", "${geofenceList}")
        super.onStop()
    }

    override fun onDestroy() {
        Log.e("MapActivity", "onDestroy")
        super.onDestroy()
    }
}