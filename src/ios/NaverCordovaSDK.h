
#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>
#import <NaverThirdPartyLogin/NaverThirdPartyLogin.h>
#import "AppDelegate.h"

@interface NaverCordovaSDK : CDVPlugin


- (void)login:(CDVInvokedUrlCommand *)command;

- (void)logout:(CDVInvokedUrlCommand *)command;

- (void)unlinkApp:(CDVInvokedUrlCommand *)command;


@end
