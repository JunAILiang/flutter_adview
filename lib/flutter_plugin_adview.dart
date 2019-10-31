import 'package:flutter/services.dart';
import 'package:flutter_plugin_adview/adview_event_handler.dart';
export 'package:flutter_plugin_adview/adview_event_handler.dart';

class FlutterPluginAdview extends AdViewEventHander {
  static const MethodChannel _channel =
  const MethodChannel('flutter_plugin_adview');

  final String appId;
  final String positionId;
  final void Function(AdViewEvents, Map<String, dynamic>) listener;

  FlutterPluginAdview(this.appId,this.positionId,{
    this.listener
  }): super(listener) {
    if (listener != null) {
      _channel.setMethodCallHandler(handleEvent);
    }
  }

  static Future init(String key) async {
    await _channel.invokeMethod('init', key);
  }

  /// 加载视频广告
  /// ids 数组第一个为appid,第二个为广告Id
  void loadVideo() {
    print("加载广告");
    print(appId);
    print(positionId);
    _channel.invokeMethod("loadVideo", [appId, positionId]);
  }

  /// 显示视频广告
  void showVideo() {
    _channel.invokeMethod("showVideo");
  }

}
