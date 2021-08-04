import 'dart:async';
import 'dart:developer';

import 'package:flutter/services.dart';

class AmapLocationQuick {
  static const _channel = MethodChannel('amap_location_quick');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> setApiKey(String androidKey, String iosKey) async {
    Map<String, dynamic> params = {'androidKey': androidKey, 'iosKey': iosKey};
    await _channel.invokeMethod('setApiKey', params);
  }

  static Future<Map<String, dynamic>> locationOnce() async {
    /// TODO 2021-08-04 10:46:14 Add LocationOption
    Map<String, dynamic> req = {};
    final res = await _channel.invokeMethod('locationOnce', req) ?? {};
    return Map<String, dynamic>.from(res);
  }

  static Future<StreamController<Map<String, dynamic>>>
      locationController() async {
    /// TODO 2021-08-04 10:46:14 Add LocationOption
    Map<String, dynamic> req = {};
    const _event = EventChannel('amap_location_quick_event');
    await _channel.invokeMethod('location', req);

    final stream = _event.receiveBroadcastStream().map((res) {
      return Map<String, dynamic>.from(res);
    });

    StreamController<Map<String, dynamic>> _ctrl =
        StreamController(onCancel: () async {
      await _channel.invokeMethod('destroyLocation');
    });
    stream.listen((event) {
      log("location listen event$event");
      _ctrl.sink.add(event);
    }, onError: (error) {
      log("location listen error$error");
      _ctrl.sink.addError(error);
    }, onDone: () {
      log("location listen  onDone");
      _ctrl.sink.close();
    }, cancelOnError: true);
    return _ctrl;
  }

  static Stream<Map<String, dynamic>> location() async* {
    /// TODO 2021-08-04 10:46:14 Add LocationOption
    Map<String, dynamic> req = {};
    const _event = EventChannel('amap_location_quick_event');
    await _channel.invokeMethod('location', req);

    yield* _event
        .receiveBroadcastStream()
        .asBroadcastStream()
        .map((res) => Map<String, dynamic>.from(res));
  }
}
