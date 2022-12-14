import 'dart:io';

import 'package:android_id/android_id.dart';
import 'package:device_information/device_information.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:unique_ids/unique_ids.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String androidId = 'Unknown';
  String imei = 'Unknown';
  String aaid = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    if (Platform.isAndroid) {
      const AndroidId().getId().then((value) {
        androidId = value ?? "";
        setState(() {});
      });
    }
    Permission.phone.request().isGranted.then((value) async {
      if (value) {
        imei = await DeviceInformation.deviceIMEINumber;
        setState(() {});
      }
    });

    UniqueIds.adId.then((value) {
      aaid = value ?? "unknown";
      setState(() {});
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              Text('androidId is : $androidId\n'),
              Text('imei is : $imei\n'),
              Text('aaid is : $aaid\n'),
            ],
          ),
        ),
      ),
    );
  }
}
