package com.example.amap_location_quick

import android.app.Activity
import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel

class LocationClientImpl(private val activity: Activity) : AMapLocationListener {
    private val TAG = "LocationClientImpl"

    private val client = AMapLocationClient(this.activity)
    private var isOnce = false
    private var eventSink: EventChannel.EventSink? = null

    override fun onLocationChanged(p0: AMapLocation?) {
        Log.i(TAG, "onLocationChanged: ")
        p0?.let {
            if (it.errorCode == AMapLocation.LOCATION_SUCCESS) {
                val res = formatLocation(it)
                if (isOnce) {
                    isOnce = false
                    destroyLocation()
                    onceListener(res)
                }
                eventSink?.success(res)
            } else {
                errorListener(it.errorCode.toString(), it.errorInfo, null)
            }
        }
    }

    private lateinit var onceListener: ((HashMap<String, Any>) -> Unit)
    fun setOnceListener(listener: ((HashMap<String, Any>) -> Unit)) {
        this.onceListener = listener;
    }

    private lateinit var errorListener: ((String, String?, String?) -> Unit)
    fun setErrorListener(listener: ((String, String?, String?) -> Unit)) {
        this.errorListener = listener;
    }

    fun locationOnce(map: HashMap<*, *>) {
        this.isOnce = true
        try {
            client.setLocationListener(this)
            val option = AMapLocationClientOption()
            option.isOnceLocation = true
            option.isLocationCacheEnable = false
            setupClientOption(option, map)
            client.setLocationOption(option)
            client.startLocation()
        } catch (e: Exception) {
            errorListener(e.message ?: "locationOnce error", null, null)
        }
    }

    fun location(eventSink: EventChannel.EventSink?, map: HashMap<*, *>) {
        this.eventSink = eventSink
        try {
            client.setLocationListener(this)
            val option = AMapLocationClientOption()
            map["interval"]?.let { option.setInterval(it as Long) }
            setupClientOption(option, map)
            client.setLocationOption(option)
            client.startLocation()
        } catch (e: Exception) {
            errorListener(e.message ?: "location error", null, null)
        }
    }

    fun destroyLocation() {
        try {
            client.stopLocation()
            client.setLocationListener(null)
            client.onDestroy()
        } catch (e: Exception) {
            errorListener(e.message ?: "destroyLocation error", null, null)
        }
    }

    private fun formatLocation(location: AMapLocation): HashMap<String, Any> {
        val result: HashMap<String, Any> = HashMap()
        result.put("locationTime", location.getTime());
        result.put("locationType", location.getLocationType());
        result.put("latitude", location.getLatitude());
        result.put("longitude", location.getLongitude());
        result.put("accuracy", location.getAccuracy());
        result.put("altitude", location.getAltitude());
        result.put("bearing", location.getBearing());
        result.put("speed", location.getSpeed());
        result.put("country", location.getCountry());
        result.put("province", location.getProvince());
        result.put("city", location.getCity());
        result.put("district", location.getDistrict());
        result.put("street", location.getStreet());
        result.put("streetNumber", location.getStreetNum());
        result.put("cityCode", location.getCityCode());
        result.put("adCode", location.getAdCode());
        result.put("address", location.getAddress());
        result.put("description", location.getDescription());
        return result
    }

    private fun setupClientOption(option: AMapLocationClientOption, map: HashMap<*, *>) {
        /// 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
        map["locationPurpose"]?.let {
            val values = AMapLocationClientOption.AMapLocationPurpose.values()
            option.locationPurpose = values[it as Int]
        }
        /// 设置定位模式，高精度/低功耗/仅设备。 默认选择使用高精度。
        map["locationMode"]?.let {
            val values = AMapLocationClientOption.AMapLocationMode.values()
            option.locationMode = values[it as Int]
        }
//        /// 设置单次定位
//        map["isOnceLocation"]?.let {
//            /// 设置单次定位 默认为false
//            option.isOnceLocation = it as Boolean
//        }
//        map["isOnceLocationLatest"]?.let {
//            /// 获取最近3s内精度最高的一次定位结果
//            option.isOnceLocationLatest = it as Boolean
//        }
//        /// 自定义连续定位 默认采用连续定位模式，时间间隔2000ms
//        map["interval"]?.let {
//            option.interval = it as Long
//        }
        /// 设置是否返回地址信息（默认返回地址信息）
        map["isNeedAddress"]?.let {
            option.isNeedAddress = it as Boolean
        }
        /// 设置是否允许模拟软件Mock位置结果，多为模拟GPS定位结果，默认为true，允许模拟位置。
        map["isMockEnable"]?.let {
            option.isMockEnable = it as Boolean
        }
        /// 设置定位请求超时时间，默认为30秒
        map["httpTimeOut"]?.let {
            option.httpTimeOut = it as Long
        }
        /// 设置是否开启定位缓存机制 缓存机制默认开启
        map["isLocationCacheEnable"]?.let {
            option.isLocationCacheEnable = it as Boolean
        }
    }
}