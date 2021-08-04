package com.example.amap_location_quick

import android.app.Activity

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel

class AmapLocationQuickPlugin : FlutterPlugin, ActivityAware {
    private val TAG = "AmapLocationQuickPlugin"

    private var methodChannel: MethodChannel? = null
    private var binding: FlutterPlugin.FlutterPluginBinding? = null
    private var activity: Activity? = null

    private fun setup(messenger: BinaryMessenger, activity: Activity) {
        this.methodChannel = MethodChannel(messenger, "amap_location_quick")
        this.activity = activity
        this.methodChannel?.setMethodCallHandler(MethodCallHandlerImpl(activity))
    }

    private fun teardown() {
        this.methodChannel?.setMethodCallHandler(null)
        this.activity = null
        this.methodChannel = null
    }


    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        this.binding = binding
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        this.binding = null
    }

    override fun onDetachedFromActivity() {
        teardown()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        this.binding?.let { setup(it.binaryMessenger, binding.activity) }
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }
}
