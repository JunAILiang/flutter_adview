import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_plugin_adview/flutter_plugin_adview.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  FlutterPluginAdview _adview;

  @override
  void initState() {
    super.initState();

    _adview = FlutterPluginAdview("SDK20191911071055ez1mj78nh8bdwcc","POSID7zvfqc4h7lbe",
        listener: (AdViewEvents events, Map<String, dynamic> args) {
          print("我走进了回调" + events.toString());
          if (events == AdViewEvents.didReadyToPlay) { /// 可以播放
//            _adview.showVideo();
          }
        }
    );

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
            children: <Widget>[
              Text('Running on: $_platformVersion\n'),
              FlatButton(
                onPressed: (){
                  FlutterPluginAdview.init('SDK20191911071051f5hwn4ozsjgskok');
                },
                child: Text('init'),
              ),
              FlatButton(
                onPressed: (){
                  _adview.loadVideo();
                },
                child: Text('load'),
              ),
              FlatButton(
                onPressed: (){
                  _adview.showVideo();
                },
                child: Text('show'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
