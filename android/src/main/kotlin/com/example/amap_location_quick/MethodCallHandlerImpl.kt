package com.example.amap_location_quick

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.amap.api.location.AMapLocationClient
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class MethodCallHandlerImpl(private val activity: Activity) : MethodChannel.MethodCallHandler {

    private val TAG = "MethodCallHandlerImpl"

    private val mainHandler = Handler(Looper.getMainLooper())

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
            else -> result.notImplemented()
        }
    }
}