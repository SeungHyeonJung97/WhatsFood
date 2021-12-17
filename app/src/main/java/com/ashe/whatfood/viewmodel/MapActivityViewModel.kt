package com.ashe.whatfood.viewmodel

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ashe.whatfood.*
import com.ashe.whatfood.Util.API_KEY
import com.ashe.whatfood.Util.api
import com.ashe.whatfood.Util.currentLocation
import com.ashe.whatfood.Util.currentLocationlat
import com.ashe.whatfood.Util.currentLocationlon
import com.ashe.whatfood.Util.sharedListItems
import com.ashe.whatfood.Util.urlList
import com.ashe.whatfood.dto.ListLayout
import com.ashe.whatfood.dto.ResultSearchKeyword
import com.ashe.whatfood.dto.ResultThumbnailImage
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MapActivityViewModel : ViewModel() {
    var listener: MapView.CurrentLocationEventListener? = null
    private lateinit var eventListener: MarkerEventListener
    lateinit var map: MapView

    var listItems = arrayListOf<ListLayout>()
    private var searchResult: ResultSearchKeyword? = null

    private var _currentPosition = MutableLiveData<String>()
    val currentPostion: LiveData<String>
        get() = _currentPosition

    private var _mapPoint = MutableLiveData<MapPoint>()
    val mapPoint: LiveData<MapPoint>
        get() = _mapPoint

    private var _listItem = MutableLiveData<ArrayList<ListLayout>>()
    val listItem: LiveData<ArrayList<ListLayout>>
        get() = _listItem

    var isFinish = MutableLiveData<Boolean>()

    fun setCurrentLocationTracking(context: Context) {
        map = MapView(context)
        map.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading

        getCurrentPosition(context)
        map.setCurrentLocationEventListener(listener)
        _mapPoint.postValue(map.mapCenterPoint)

        currentLocationlon = map.mapCenterPoint.mapPointGeoCoord.longitude
        currentLocationlat = map.mapCenterPoint.mapPointGeoCoord.latitude

        eventListener = MarkerEventListener(context)

        map.setCalloutBalloonAdapter(CustomBalloonAdapter(context.layoutInflater, listItems))
        map.setPOIItemEventListener(eventListener)

    }

    fun searchKeyword(keyword: String) {
        val call = api.getSearchKeyword(API_KEY, keyword)

        call.enqueue(object : Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                searchResult = null
                addItemsAndMarkers(response.body())
                searchResult = response.body()
                Log.e("!!!", "${response.body()}")
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                Log.e("Retrofit", "통신 실패 : ${t.message}")
            }

        })
    }

    fun getThumbnailImage(keyword: String) {
        val call = api.getThumbnailImage(API_KEY, keyword)

        call.enqueue(object : Callback<ResultThumbnailImage> {
            override fun onResponse(
                call: Call<ResultThumbnailImage>,
                response: Response<ResultThumbnailImage>
            ) {
                urlList.value =
                    (response.body()!!.documents.filter { it.display_sitename.contains("네이버") })
            }

            override fun onFailure(call: Call<ResultThumbnailImage>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun reload(context: Context) {

        map = MapView(context)
        map.setCurrentLocationEventListener(listener)
        map.setCalloutBalloonAdapter(CustomBalloonAdapter(context.layoutInflater, listItems))
        map.setPOIItemEventListener(eventListener)
        addItemsAndMarkers(searchResult)

    }

    fun getCurrentPosition(context: Context) {

        listener = object : MapView.CurrentLocationEventListener {
            override fun onCurrentLocationUpdate(
                mapView: MapView?,
                currentLocation: MapPoint?,
                accuracyInMeters: Float
            ) {
                var result = ""
                val mapPointGeo = currentLocation?.mapPointGeoCoord
                val geocoder = Geocoder(context)
                var addresses: List<Address>? = null

                try {
                    addresses = geocoder.getFromLocation(
                        mapPointGeo!!.latitude,
                        mapPointGeo!!.longitude,
                        7
                    )
                    currentLocationlat = mapPointGeo.latitude
                    currentLocationlon = mapPointGeo.longitude
                } catch (ioException: IOException) {
                    //네트워크 문제
                    context.toast("지오코더 서비스 사용불가")
                    context.toast("지오코더 서비스 사용불가")
                } catch (illegalArgumentException: IllegalArgumentException) {
                    context.toast("잘못된 GPS 좌표")
                }
                if (addresses == null || addresses.size == 0) {
                    context.toast("주소 미발견")
                }

                val address = addresses!!.get(0);
                result = address.getAddressLine(0).toString() + "\n";
                Util.currentLocation = result
                _currentPosition.postValue(result)
            }

            override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {
                Log.e("1", "onCurrentLocationDeviceHeadingUpdate")

            }

            override fun onCurrentLocationUpdateFailed(p0: MapView?) {
                Log.e("2", "onCurrentLocationUpdateFailed")
            }

            override fun onCurrentLocationUpdateCancelled(p0: MapView?) {
                Log.e("3", "onCurrentLocationUpdateCancelled")

            }
        }


    }

    fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {

        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear()                   // 리스트 초기화
            map.removeAllPOIItems() // 지도의 마커 모두 제거
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = ListLayout(
                    document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble(),
                    document.category_name,
                    document.phone,
                    document.place_url
                )
                listItems.add(item)

                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(
                        document.y.toDouble(),
                        document.x.toDouble()
                    )
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                map.addPOIItem(point)
            }
            _listItem.postValue(listItems)

        } else {
            // 검색 결과 없음
        }
    }

    fun setMapCenterPointAndZoomLevel(mapPoint: MapPoint) {
        map.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
        _mapPoint.postValue(map.mapCenterPoint)
    }


    inner class CustomBalloonAdapter(
        inflater: LayoutInflater,
        val listItems: ArrayList<ListLayout>
    ) :
        CalloutBalloonAdapter {
        val mCalloutBalloon: View = inflater.inflate(R.layout.balloon_layout, null)
        val name: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_name)
        val address: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_address)

        override fun getCalloutBalloon(p0: MapPOIItem?): View {
            name.text = p0?.itemName
            address.text = listItems.filter { it.name.equals(p0?.itemName) }[0].category
            return mCalloutBalloon
        }

        override fun getPressedCalloutBalloon(p0: MapPOIItem?): View {
            address.text = "getPressedCalloutBalloon"
            return mCalloutBalloon
        }
    }

    inner class MarkerEventListener(val context: Context) : MapView.POIItemEventListener {
        override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
            // 말풍선 클릭 시
        }

        override fun onCalloutBalloonOfPOIItemTouched(
            mapView: MapView?,
            poiItem: MapPOIItem?,
            p2: MapPOIItem.CalloutBalloonButtonType?
        ) {
//            val builder = AlertDialog.Builder(context)
//            val itemList = arrayOf("전화 걸기", "리뷰 남기기", "취소")
//            builder.setTitle("${poiItem?.itemName}")
//            builder.setItems(itemList) { dialog, which ->
//                when (which) {
//                    0 -> {
//                        val tel =
//                            "tel:${listItems.filter { it.name.equals(poiItem?.itemName) }[0].phone}"
//                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(tel))
//                        context.startActivity(intent)
//                    }
//                    1 -> {
//                        val intent = Intent(context, ReviewActivity::class.java)
//                        intent.putExtra("itemName", "${poiItem?.itemName}")
//                        context.startActivity(intent)
//                    }
//                    2 -> dialog.dismiss()   // 대화상자 닫기
//                }
//            }
//            builder.show()
            getThumbnailImage(poiItem!!.itemName)
            sharedListItems = listItems
            val index = searchResult!!.documents.filter{ it.place_name.equals(poiItem?.itemName) }
            val url = index[0].place_url
            val destLocation = doubleArrayOf(index[0].y.toDouble(), index[0].x.toDouble())

            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("itemName", "${poiItem?.itemName}")
            intent.putExtra("itemUrl", url)
            intent.putExtra("destLocation", destLocation)
            context.startActivity(intent)
        }

        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
            TODO("Not yet implemented")
        }

    }

    override fun onCleared() {
        Log.e("ViewModel", "cleared")
        super.onCleared()
    }
}