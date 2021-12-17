package com.ashe.whatfood

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashe.whatfood.Util.after_item
import com.ashe.whatfood.Util.before_item
import com.ashe.whatfood.Util.checkLocationService
import com.ashe.whatfood.Util.isGranted
import com.ashe.whatfood.Util.permissionCheck
import com.ashe.whatfood.Util.searchResult
import com.ashe.whatfood.adapter.ListAdapter
import com.ashe.whatfood.databinding.ActivityMapBinding
import com.ashe.whatfood.dto.ListLayout
import com.ashe.whatfood.viewmodel.MapActivityViewModel
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import org.jetbrains.anko.toast

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private lateinit var mapViewModel: MapActivityViewModel
    var listItems: ArrayList<ListLayout> = arrayListOf(ListLayout())

    private var listAdapter = ListAdapter(listItems)
    private var keyword = ""
    private var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Settings()

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = listAdapter

        Observe()
        Listener()

    }

    private fun Observe() {
        mapViewModel.currentPostion.observe(this){ currentAddress ->
            val stringArray = currentAddress.split(" ")
            address = "${stringArray[1]} ${stringArray[2]} ${stringArray[3]}"
            keyword = "$address $searchResult"
            mapViewModel.searchKeyword(keyword)
        }
        mapViewModel.mapPoint.observe(this){
            if(mapViewModel.map.parent == null) {
                binding.kakaoMap.addView(mapViewModel.map)
            }
        }
        mapViewModel.listItem.observe(this){
            listItems = it
            listAdapter.itemList = listItems
            listAdapter.notifyDataSetChanged()
        }
    }

    private fun Listener(){
        listAdapter.setItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val mapPoint =
                    MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                mapViewModel.setMapCenterPointAndZoomLevel(mapPoint)
                if(before_item == null){
                    before_item = mapViewModel.map.findPOIItemByName(listItems[position].name)[0]
                }else{
                    mapViewModel.map.removePOIItem(after_item)
                    before_item!!.markerType = MapPOIItem.MarkerType.BluePin
                    mapViewModel.map.addPOIItem(before_item)

                    before_item = mapViewModel.map.findPOIItemByName(listItems[position].name)[0]
                }
                itemRenewal()
            }
        })

        binding.searchBtn.setOnClickListener {
            keyword = "$address ${binding.searchEt.text}"
            mapViewModel.searchKeyword(keyword)
        }
    }

    private fun itemRenewal() {
        after_item = before_item
        mapViewModel.map.removePOIItem(after_item)
        after_item?.markerType = MapPOIItem.MarkerType.YellowPin
        mapViewModel.map.addPOIItem(after_item)
    }

    private fun Settings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        mapViewModel = ViewModelProvider(this).get(MapActivityViewModel::class.java)
        binding.activity = mapViewModel


        if (checkLocationService(this)) {
            if(!isGranted){
                permissionCheck(this)
                mapViewModel.setCurrentLocationTracking(this)
            }
        } else {
            toast("GPS를 켜주세요 !")
        }
    }

    override fun onRestart() {
        if(binding.kakaoMap.size == 0){
            mapViewModel.reload(this)
            itemRenewal()
            binding.kakaoMap.addView(mapViewModel.map)
        }
        super.onRestart()
    }
    override fun onStop() {
        binding.kakaoMap.removeAllViews()
        mapViewModel.isFinish.postValue(false)
        super.onStop()
    }
}