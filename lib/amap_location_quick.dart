import 'dart:async';

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
}
