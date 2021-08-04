import 'dart:developer';

import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:amap_location_quick/amap_location_quick.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(App());
}

class App extends StatelessWidget {
  const App({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      builder: BotToastInit(),
      navigatorObservers: [BotToastNavigatorObserver()],
      home: MyApp(),
    );
  }
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion =
          await AmapLocationQuick.platformVersion ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }
    if (!mounted) return;
    _platformVersion = platformVersion;
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Plugin example app')),
      body: ListView(
        children: ListTile.divideTiles(context: context, tiles: [
          SizedBox(height: 24),
          Text('Running on: $_platformVersion\n'),
          _tilesPermission(context),
          ..._locationOnce(context),
          ..._location(context),
          SizedBox(height: 24),
        ]).toList(),
      ),
    );
  }

  bool permissionGranted = false;
  Widget _tilesPermission(BuildContext context) {
    return ListTile(
      title: Text("权限检查"),
      onTap: () async {
        if (await Permission.locationWhenInUse.serviceStatus !=
            ServiceStatus.enabled) {
          BotToast.showText(text: "请检查定位服务开关");
          return;
        }
        if (!await Permission.locationWhenInUse.request().isGranted) {
          BotToast.showText(text: "请检查定位权限设置");
          return;
        }
        permissionGranted = true;
        setState(() {});
      },
    );
  }

  String locStr = "";
  List<Widget> _locationOnce(BuildContext context) {
    return [
      ListTile(
        title: Text("单次定位"),
        onTap: () async {
          if (!permissionGranted) {
            BotToast.showText(text: "先权限检查");
            return;
          }
          await AmapLocationQuick.setApiKey("28bd43ed17d636692c8803e9e0d246b2",
              "dfb64c0463cb53927914364b5c09aba0");
          AmapLocationQuick.locationOnce().then((res) {
            log(res.toString());
            BotToast.showText(text: "定位成功");
            locStr = res.toString();
            setState(() {});
          }).catchError((err) {
            log(err.toString());
            BotToast.showText(text: "定位失败");
            locStr = err.toString();
            setState(() {});
          });
        },
      ),
      if (locStr.isNotEmpty) Text(locStr)
    ];
  }

  List<String> locStrList = [];
  List<Widget> _location(BuildContext context) {
    return [
      ListTile(
        title: Text("定位"),
        onTap: () async {
          if (!permissionGranted) {
            BotToast.showText(text: "先权限检查");
            return;
          }
          await AmapLocationQuick.setApiKey("28bd43ed17d636692c8803e9e0d246b2",
              "dfb64c0463cb53927914364b5c09aba0");

          final _ctrl = await AmapLocationQuick.locationController();

          final _subscription = _ctrl.stream.listen((res) {
            log(res.toString());
            BotToast.showText(text: "定位成功");
            locStrList.add(res.toString());
            setState(() {});
          }, onError: (err) {
            log(err.toString());
            BotToast.showText(text: "定位失败");
          }, onDone: () {
            log("onDone");
          });
          await Future.delayed(Duration(seconds: 10));
          await _subscription.cancel();
          await _ctrl.sink.close();
          await _ctrl.close();
        },
      ),
      if (locStrList.isNotEmpty) Text(locStrList.toString())
    ];
  }
}
