#import "AmapLocationQuickPlugin.h"
#if __has_include(<amap_location_quick/amap_location_quick-Swift.h>)
#import <amap_location_quick/amap_location_quick-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "amap_location_quick-Swift.h"
#endif

@implementation AmapLocationQuickPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAmapLocationQuickPlugin registerWithRegistrar:registrar];
}
@end
