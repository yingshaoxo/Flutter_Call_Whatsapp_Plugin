#import "CallWithWhatsappPlugin.h"
#if __has_include(<call_with_whatsapp/call_with_whatsapp-Swift.h>)
#import <call_with_whatsapp/call_with_whatsapp-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "call_with_whatsapp-Swift.h"
#endif

@implementation CallWithWhatsappPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftCallWithWhatsappPlugin registerWithRegistrar:registrar];
}
@end
