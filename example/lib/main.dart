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
      leading: Icon(permissionGranted ? Icons.done : Icons.help_outline),
      title: Text("check Permission"),
      onTap: () async {
        if (await Permission.locationWhenInUse.serviceStatus !=
            ServiceStatus.enabled) {
          BotToast.showText(
              text: "Please check the positioning service switch");
          return;
        }
        if (!await Permission.locationWhenInUse.request().isGranted) {
          BotToast.showText(
              text: "Please check the location permission settings");
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
        title: Text("locationOnce"),
        onTap: () async {
          if (!permissionGranted) {
            BotToast.showText(text: "Please check Permission");
            return;
          }
          await AmapLocationQuick.setApiKey(
            "28bd43ed17d636692c8803e9e0d246b2",
            "dfb64c0463cb53927914364b5c09aba0",
          );
          AmapLocationQuick.locationOnce().then((res) {
            log(res.toString());
            BotToast.showText(text: "location succes");
            locStr = res.toString();
            setState(() {});
          }).catchError((err) {
            log(err.toString());
            BotToast.showText(text: "location fail");
            locStr = err.toString();
            setState(() {});
          });
        },
      ),
      if (locStr.isNotEmpty) Text(locStr)
    ];
  }

  List<String> locStrList = [];
  String locError = "";
  List<Widget> _location(BuildContext context) {
    return [
      ListTile(
        title: Text("location 10s"),
        onTap: () async {
          if (!permissionGranted) {
            BotToast.showText(text: "Please check Permission");
            return;
          }
          await AmapLocationQuick.setApiKey(
            "28bd43ed17d636692c8803e9e0d246b2",
            "dfb64c0463cb53927914364b5c09aba0",
          );

          final _ctrl = await AmapLocationQuick.locationController();

          final _subscription = _ctrl.stream.listen((res) {
            log(res.toString());
            BotToast.showText(text: "location succes");
            locStrList.add(res.toString());
            setState(() {});
          }, onError: (err) {
            log(err.toString());
            BotToast.showText(text: "location fail");
            locError = err.toString();
            setState(() {});
          }, onDone: () {
            log("onDone");
          }, cancelOnError: true);

          await Future.delayed(Duration(seconds: 10));
          await _subscription.cancel();
          await _ctrl.sink.close();
          await _ctrl.close();
        },
      ),
      if (locError.isNotEmpty) Text(locError.toString()),
      if (locStrList.isNotEmpty) Text(locStrList.toString()),
    ];
  }
}
