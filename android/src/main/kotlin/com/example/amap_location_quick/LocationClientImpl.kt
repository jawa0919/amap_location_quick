package com.example.amap_location_quick

import android.app.Activity
import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel

class LocationClientImpl(activity: Activity) : AMapLocationListener, EventChannel.StreamHandler {
    private val TAG = "LocationClientImpl"

    private val client = AMapLocationClient(activity)
    private var isOnce = false

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

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        Log.i(TAG, "onListen: ")
        this.eventSink = events;
    }

    override fun onCancel(arguments: Any?) {
        Log.i(TAG, "onCancel: ")
        destroyLocation()
    }

    private lateinit var onceListener: ((HashMap<String, Any>) -> Unit)
    fun setOnceListener(listener: ((HashMap<String, Any>) -> Unit)) {
        this.onceListener = listener;
    }

    private lateinit var errorListener: ((String, String?, String?) -> Unit)
    fun setErrorListener(listener: ((String, String?, String?) -> Unit)) {
        this.errorListener = listener;
    }

    // TODO: 2021/8/4 Add Location Option
    fun locationOnce(map: HashMap<*, *>) {
        try {
            this.isOnce = true
            client.setLocationListener(this)
            val option = AMapLocationClientOption()
            option.isOnceLocation = true
            option.isLocationCacheEnable = false
            client.setLocationOption(option)
            client.startLocation()
        } catch (e: Exception) {
            errorListener(e.message ?: "error", null, null)
        }
    }

    private var event: EventChannel? = null
    private var eventSink: EventChannel.EventSink? = null

    // TODO: 2021/8/4 Add Location Option
    fun location(binaryMessenger: BinaryMessenger, map: HashMap<*, *>) {
        try {
            event = EventChannel(binaryMessenger, "amap_location_quick_event")
            event?.setStreamHandler(this)
            client.setLocationListener(this)
            val option = AMapLocationClientOption()
            map["interval"]?.let { option.setInterval(it as Long) }
            client.setLocationOption(option)
            client.startLocation()
        } catch (e: Exception) {
            errorListener(e.message ?: "error", null, null)
        }
    }

    fun destroyLocation() {
        client.stopLocation()
        client.setLocationListener(null)
        client.onDestroy()
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
}