package com.example.amap_location_quick

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.amap.api.location.AMapLocationClient
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class HandlerImpl(private val activity: Activity) : MethodChannel.MethodCallHandler,
    EventChannel.StreamHandler {
    private val TAG = "HandlerImpl"

    private var eventSink: EventChannel.EventSink? = null
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
                val client = LocationClientImpl(this.activity)
                client.setOnceListener { result.success(it) }
                client.setErrorListener { code, msg, des -> result.error(code, msg, des) }
                client.locationOnce(args)
            }
            "location" -> {
                val args: HashMap<*, *> = call.arguments as HashMap<*, *>
                this.client = LocationClientImpl(activity)
                this.client?.let {
                    it.setErrorListener { code, msg, des -> result.error(code, msg, des) }
                    it.location(this.eventSink, args)
                    result.success(null)
                }
            }
            "destroyLocation" -> {
                this.client?.let {
                    it.setErrorListener { code, msg, des -> result.error(code, msg, des) }
                    it.destroyLocation()
                    result.success(null)
                }
            }
            else -> result.notImplemented()
        }
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        Log.i(TAG, "onListen: ")
        this.eventSink = events;
    }

    override fun onCancel(arguments: Any?) {
        Log.i(TAG, "onCancel: ")
    }
}