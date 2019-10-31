package com.maoge.flutter_plugin_adview_example

import android.os.Bundle
import com.maoge.flutter_plugin_adview.AdVideoUtil

import io.flutter.app.FlutterActivity
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity: FlutterActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AdVideoUtil.setContext(this)
    GeneratedPluginRegistrant.registerWith(this)
  }
}
