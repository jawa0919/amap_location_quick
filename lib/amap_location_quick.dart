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

  /// 定位单次
  ///
  /// TODO 2021-08-04 10:46:14 加一些参数
  Future<Map<String, dynamic>> locationOnce() async {
    Map<String, dynamic> params = {};
    await _channel.invokeMethod('locationOnce', params);
    return {};
  }
}
