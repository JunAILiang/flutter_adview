#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'flutter_plugin_adview'
  s.version          = '0.0.1'
  s.summary          = 'A new flutter plugin project.'
  s.description      = <<-DESC
A new flutter plugin project.
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*.{h,m}'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  
  s.frameworks = [
    'WebKit',
    'Foundation',
    'MessageUI',
    'QuartzCore',
    'CFNetwork',
    'AVFoundation',
    'StoreKit',
    'CoreLocation',
    'MediaPlayer',
    'MobileCoreServices',
    'CoreMedia',
    'AudioToolbox',
    'CoreMotion',
    'Security',
    'CoreTelephony',
    'SystemConfiguration',
    'AdSupport',
    'CoreGraphics',
    'AddressBook',
  ]
  s.libraries = [
    'z',
    'resolv',
    'sqlite3',
    'xml2',
    'c++',
  ]

  
  s.vendored_frameworks = [
  'Classes/Frameworks/AdHubSDK.framework',
  'Classes/Frameworks/BaiduLib/BaiduMobAdSDK.framework',
  'Classes/Frameworks/ToutiaoLib/BUAdSDK.framework',
  ]
  s.vendored_libraries = [
  'Classes/Frameworks/libAdCompViewSDK.a',
  'Classes/Frameworks/GDTLib/libGDTMobSDK.a',
  ]
  s.resource = [
  'Classes/Frameworks/AdViewRes.bundle',
  'Classes/Frameworks/BaiduLib/baidumobadsdk.bundle',
  'Classes/Frameworks/ToutiaoLib/BUAdSDK.bundle',
  ]

  s.ios.deployment_target = '8.0'
end

