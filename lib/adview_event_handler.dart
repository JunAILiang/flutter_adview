import 'package:flutter/services.dart';

abstract class AdViewEventHander {
  final Function(AdViewEvents, Map<String, dynamic>) _listener;

  AdViewEventHander(Function(AdViewEvents, Map<String, dynamic>) listener) : _listener = listener;

  Future<dynamic> handleEvent(MethodCall call) async {
    print("走进了回调");

    switch (call.method) {
      case 'didReceiveAd':
        _listener(AdViewEvents.didReceiveAd, null);
        break;
      case 'didError':
        _listener(AdViewEvents.didError, null);
        break;
      case 'didReadyToPlay':
        _listener(AdViewEvents.didReadyToPlay, null);
        break;
      case 'didStarted':
        _listener(AdViewEvents.didStarted, null);
        break;
      case 'didEnded':
        _listener(AdViewEvents.didEnded, null);
        break;
      case 'didClosed':
        _listener(AdViewEvents.didClosed, null);
        break;
    }

    return null;
  }

}



enum AdViewEvents {
  didReceiveAd,
  didError,
  didReadyToPlay,
  didStarted,
  didEnded,
  didClosed,
}