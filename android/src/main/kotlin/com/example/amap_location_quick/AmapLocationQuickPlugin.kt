package com.example.amap_location_quick

import android.app.Activity
import android.util.Log

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

class AmapLocationQuickPlugin : FlutterPlugin, ActivityAware {
    private val TAG = "AmapLocationQuickPlugin"

    private var binding: FlutterPlugin.FlutterPluginBinding? = null

    private var methodChannel: MethodChannel? = null
    private var eventChannel: EventChannel? = null
    private var activity: Activity? = null

    private fun setup(messenger: BinaryMessenger, activity: Activity) {
        Log.i(TAG, "setup: ")
        this.methodChannel = MethodChannel(messenger, "amap_location_quick")
        this.eventChannel = EventChannel(messenger, "amap_location_quick_event")
        this.activity = activity

        val handlerImpl = HandlerImpl(activity)
        this.methodChannel?.setMethodCallHandler(handlerImpl)
        this.eventChannel?.setStreamHandler(handlerImpl)
    }

    private fun teardown() {
        Log.i(TAG, "teardown: ")
        this.methodChannel?.setMethodCallHandler(null)
        this.eventChannel?.setStreamHandler(null)

        this.activity = null
        this.eventChannel = null
        this.methodChannel = null
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        Log.i(TAG, "onAttachedToEngine: ")
        this.binding = binding
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        Log.i(TAG, "onDetachedFromEngine: ")
        this.binding = null
    }

    override fun onDetachedFromActivity() {
        Log.i(TAG, "onDetachedFromActivity: ")
        teardown()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        Log.i(TAG, "onReattachedToActivityForConfigChanges: ")
        onAttachedToActivity(binding)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        Log.i(TAG, "onAttachedToActivity: ")
        this.binding?.let { setup(it.binaryMessenger, binding.activity) }
    }

    override fun onDetachedFromActivityForConfigChanges() {
        Log.i(TAG, "onDetachedFromActivityForConfigChanges: ")
        onDetachedFromActivity()
    }
}
