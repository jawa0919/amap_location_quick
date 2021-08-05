import 'dart:async';
import 'dart:developer';

import 'package:flutter/services.dart';

class AmapLocationQuick {
  static const _channel = MethodChannel('amap_location_quick');
  static const _event = EventChannel('amap_location_quick_event');

  static Stream<Map<String, dynamic>> get _broadcastStream {
    return _event
        .receiveBroadcastStream()
        .map((res) => Map<String, dynamic>.from(res));
  }

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> setApiKey(String androidKey, String iosKey) async {
    Map<String, dynamic> params = {'androidKey': androidKey, 'iosKey': iosKey};
    await _channel.invokeMethod('setApiKey', params);
  }

  static Future<Map<String, dynamic>> locationOnce({
    Map<String, dynamic>? option,
  }) async {
    Map<String, dynamic> req = option ?? {};
    final res = await _channel.invokeMethod('locationOnce', req) ?? {};
    return Map<String, dynamic>.from(res);
  }

  static Future<StreamController<Map<String, dynamic>>> locationController({
    Map<String, dynamic>? option,
  }) async {
    Map<String, dynamic> req = option ?? {};
    StreamController<Map<String, dynamic>> _ctrl = StreamController(
      onCancel: () async {
        await _channel.invokeMethod('destroyLocation');
      },
    );
    _broadcastStream.listen((event) {
      log("_broadcastStream listen event$event");
      _ctrl.sink.add(event);
    }, onError: (error) {
      log("_broadcastStream onError error$error");
      _ctrl.sink.addError(error);
    }, onDone: () {
      log("_broadcastStream onDone");
      _ctrl.sink.close();
    }, cancelOnError: true);
    await _channel.invokeMethod('location', req);
    return _ctrl;
  }
}
