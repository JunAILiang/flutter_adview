package com.maoge.flutter_plugin_adview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.kyview.InitConfiguration;
import com.kyview.interfaces.AdViewVideoListener;
import com.kyview.manager.AdViewVideoManager;

import io.flutter.plugin.common.MethodChannel;

/**
 * 视频广告工具类
 */
public class AdVideoUtil {

    private static String key;
    private static MethodChannel channel;
    private static int type;

    static class MyThread extends Thread {

        @Override
        public void run() {
            handler.sendEmptyMessage(type);
        }
    }

    static Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0x111) { // 开始播放
                channel.invokeMethod("didStarted", null);
            } else if(msg.what == 0x222) { // 播放结束
                channel.invokeMethod("didEnded", null);
            } else if(msg.what == 0x404) { // 加载失败
                channel.invokeMethod("didError", null);
            } else if(msg.what == 0x202) { // 加载到广告
                channel.invokeMethod("didReceiveAd", null);
            } else if(msg.what == 0x400) { // 关闭广告
                channel.invokeMethod("didClosed", null);
            } else if(msg.what == 0x200) { // 广告加载完成并可以播放
                show();
//                channel.invokeMethod("didReadyToPlay", null);
            }
        }
    };

    public static void init(String key){
        AdVideoUtil.key = key;
        InitConfiguration initConfig = new InitConfiguration.Builder(getContext())
                .setUpdateMode(InitConfiguration.UpdateMode.EVERYTIME)
                .setRunMode(InitConfiguration.RunMode.TEST)
                .build();
        //初始化视频的广告
        AdViewVideoManager.getInstance(getContext()).init(initConfig, new String[]{key});
    }
    public static void load(final MethodChannel channel){
        AdVideoUtil.channel = channel;
        AdViewVideoManager.getInstance(getContext()).requestAd(getContext(), key, new AdViewVideoListener() {
            @Override
            public void onAdPlayStart(String s) {
                AdVideoUtil.type = 0x111;
                new MyThread().start();
            }
            @Override
            public void onAdPlayEnd(String s, Boolean aBoolean) {
                AdVideoUtil.type = 0x222;
                new MyThread().start();
            }
            @Override
            public void onAdFailed(String s) {
                AdVideoUtil.type = 0x404;
                new MyThread().start();
            }
            @Override
            public void onAdRecieved(String s) {
                AdVideoUtil.type = 0x202;
                new MyThread().start();
            }
            @Override
            public void onAdClose(String s) {
                AdVideoUtil.type = 0x400;
                new MyThread().start();
            }
            @Override
            public void onAdReady(String s) {
                AdVideoUtil.type = 0x200;
                new MyThread().start();
            }
        });
    }

    public static void show(){
        AdViewVideoManager.getInstance(getContext()).playVideo(getContext(), key);
    }

    private static Context mContext;
    public static void setContext(Context context){
        AdVideoUtil.mContext = context;
    }
    public static Context getContext(){
        return mContext;
    }
}
