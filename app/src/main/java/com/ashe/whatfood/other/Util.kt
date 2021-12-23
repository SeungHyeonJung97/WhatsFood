package com.ashe.whatfood.other

import android.content.Context
import android.content.ContextWrapper
import android.location.LocationManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.ashe.whatfood.MapActivity
import com.ashe.whatfood.dto.Document
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import net.daum.mf.map.api.MapPOIItem
import org.jetbrains.anko.toast
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.ashe.whatfood.R
import com.ashe.whatfood.dto.ListLayout
import com.ashe.whatfood.dto.Place
import com.ashe.whatfood.dto.ReviewData
import org.jetbrains.anko.startActivity


object Util {
    val BASE_URL = "https://dapi.kakao.com/"
    val API_KEY = "KakaoAK 7287bd87c547ad006a543655d7e19faf"
    var searchResult = ""

    var ivIdList = listOf(R.id.star1_iv, R.id.star2_iv, R.id.star3_iv, R.id.star4_iv, R.id.star5_iv)
    var isGranted = false

    var urlList = MutableLiveData<List<Document>>()
    var itemName = ""

    var targetKey = ""

    var currentLocation = ""

    var currentLocationlat = 0.0
    var currentLocationlon = 0.0
    var sharedListItems = arrayListOf<ListLayout>()

    lateinit var savedPost:ReviewData

    lateinit var savedItem: String
    var saveOk = false

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(KakaoApi::class.java)

    fun permissionCheck(context: Context) {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                context.startActivity<MapActivity>()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            }
        }

        TedPermission.with(context)
            .setPermissionListener(permissionListener)
            .setDeniedMessage("접근 거부하셨습니다.\n[설정] - [권한]에서 권한을 허용해주세요.")
            .setPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
            .check()
    }

    fun checkLocationService(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun Context.lifecycleOwner(): LifecycleOwner? {
        var curContext = this
        var maxDepth = 20
        while (maxDepth-- > 0 && this !is LifecycleOwner) {
            curContext = (this as ContextWrapper).baseContext
        }
         return if (curContext is LifecycleOwner) {
             curContext
        } else {
            null
        }
    }
}