package com.example.amap_location_quick

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.amap.api.location.AMapLocationClient
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class MethodCallHandlerImpl(
    private val activity: Activity,
    private val binaryMessenger: BinaryMessenger
) : MethodChannel.MethodCallHandler {

    private val TAG = "MethodCallHandlerImpl"

    private val mainHandler = Handler(Looper.getMainLooper())

    private var client: LocationClientImpl? = null

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        Log.i(TAG, "onMethodCall: ${call.method}")
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "setApiKey" -> {
                val androidKey = call.argument<String>("androidKey") ?: ""
                AMapLocationClient.setApiKey(androidKey)
                result.success(null)
            }
            "locationOnce" -> {
                val args: HashMap<*, *> = call.arguments as HashMap<*, *>
                val client = LocationClientImpl(activity)
                client.locationOnce(args)
                client.setOnceListener { result.success(it) }
                client.setErrorListener { code, msg, des -> result.error(code, msg, des) }
            }
            "location" -> {
                val args: HashMap<*, *> = call.arguments as HashMap<*, *>
                client = LocationClientImpl(activity)
                client?.let {
                    it.location(binaryMessenger, args)
                    it.setErrorListener { code, msg, des -> result.error(code, msg, des) }
                    result.success(null)
                }
            }
            "destroyLocation" -> {
                client?.let {
                    it.destroyLocation()
                    result.success(null)
                }
            }
            else -> result.notImplemented()
        }
    }
}