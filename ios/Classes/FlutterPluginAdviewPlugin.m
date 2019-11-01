#import "FlutterPluginAdviewPlugin.h"

@interface FlutterPluginAdviewPlugin()<AdCompVideoDelegate>
/** 视频广告 */
@property (nonatomic, strong) AdCompVideo *video;
/** channel **/
@property (nonatomic, strong) FlutterMethodChannel *channel;

@end

static FlutterPluginAdviewPlugin *instance = nil;
@implementation FlutterPluginAdviewPlugin

+ (instancetype)allocWithZone:(struct _NSZone *)zone {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [super allocWithZone:zone];
    });
    return instance;
}

+ (instancetype)sharedInstance {
    if (instance == nil) {
        instance = [[super alloc] init];
    }
    return instance;
}

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [FlutterPluginAdviewPlugin sharedInstance].channel = [FlutterMethodChannel
      methodChannelWithName:@"flutter_plugin_adview"
            binaryMessenger:[registrar messenger]];
  FlutterPluginAdviewPlugin* instance = [[FlutterPluginAdviewPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:[FlutterPluginAdviewPlugin sharedInstance].channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"loadVideo" isEqualToString:call.method]) {
      NSString *appId = [call.arguments firstObject];
      NSString *positionId = [call.arguments lastObject];
      self.video = [AdCompVideo playVideoWithAppId:appId positionId:positionId videoType:AdCompVideoTypeInstl delegate:self];
      [self.video getVideoAD];
  } else if ([@"showVideo" isEqualToString:call.method]) {
      [self.video showVideoWithController:[UIApplication sharedApplication].keyWindow.rootViewController];
  } else {
    result(FlutterMethodNotImplemented);
  }
}


- (void)adCompVideoDidReceiveAd:(NSString *)vastString {
    [self.channel invokeMethod:@"didReceiveAd" arguments:nil];
}

- (void)adCompVideoFailReceiveDataWithError:(NSError *)error {
    [self.channel invokeMethod:@"didError" arguments:nil];
}

- (void)adCompVideoIsReadyToPlay:(AdCompVideo *)video {
    [self.video showVideoWithController:[UIApplication sharedApplication].keyWindow.rootViewController];
    [self.channel invokeMethod:@"didReadyToPlay" arguments:nil];
}

- (void)adCompVideoPlayStarted {
    [self.channel invokeMethod:@"didStarted" arguments:nil];
}

- (void)adCompVideoPlayEnded {
    [self.channel invokeMethod:@"didEnded" arguments:nil];
}

- (void)adCompVideoClosed {
    [self.channel invokeMethod:@"didClosed" arguments:nil];
}

@end
