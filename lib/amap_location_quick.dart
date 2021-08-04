
import 'dart:async';

import 'package:flutter/services.dart';

class AmapLocationQuick {
  static const MethodChannel _channel =
      const MethodChannel('amap_location_quick');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
