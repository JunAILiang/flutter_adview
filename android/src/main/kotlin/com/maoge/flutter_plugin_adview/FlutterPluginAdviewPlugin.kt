package com.maoge.flutter_plugin_adview

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class FlutterPluginAdviewPlugin: MethodCallHandler {

  companion object {
    @JvmField
    var channel: MethodChannel? = null

    @JvmStatic
    fun registerWith(registrar: Registrar) {
      channel = MethodChannel(registrar.messenger(), "flutter_plugin_adview")
      channel!!.setMethodCallHandler(FlutterPluginAdviewPlugin())
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "init"){//初始化
      AdVideoUtil.init(call.arguments as String?)
    } else if (call.method == "loadVideo"){//加载
      AdVideoUtil.load(channel)
    } else if (call.method == "showVideo"){//初始化
      AdVideoUtil.show()
    } else {
      result.notImplemented()
    }
  }

}
