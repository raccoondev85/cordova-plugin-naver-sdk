#import "NaverCordovaSDK.h"
#import <Cordova/CDVPlugin.h>
#import <NaverThirdPartyLogin/NaverThirdPartyLogin.h>

@interface NaverCordovaSDK ()

@property(strong, nonatomic) NSString *loginCallbackId;
@end

@implementation NaverCordovaSDK

- (void)pluginInitialize {
    NSLog(@"Start Naver plugin");

#if __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_9_0
    // Add notification listener for handleOpenURL
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(openURL:)
                                                 name:CDVPluginHandleOpenURLNotification object:nil];
#endif
#if __IPHONE_OS_VERSION_MAX_ALLOWED < __IPHONE_9_0
    // Add notification listener for handleOpenURL
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(openURLWithApplicationSourceAndAnnotation:)
                                                 name:CDVPluginHandleOpenURLWithAppSourceAndAnnotationNotification object:nil];
 #endif
    // Delegate 설정
    [NaverThirdPartyLoginConnection getSharedInstance].delegate = self;

    // 네이버 앱과, 인앱 브라우저 인증을 둘다 사용하도록 설정
    [[NaverThirdPartyLoginConnection getSharedInstance] setIsNaverAppOauthEnable:YES];
    [[NaverThirdPartyLoginConnection getSharedInstance] setIsInAppOauthEnable:YES];

    // 세로 화면 고정 설정
    [[NaverThirdPartyLoginConnection getSharedInstance] setOnlyPortraitSupportInIphone:YES];

    // 네이버 플러그인 데이터 설정
    NSString *serviceUrlScheme = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"NaverAppScheme"];
    NSString *consumerKey = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"NaverClientID"];
    NSString *consumerSecret = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"NaverClientSecret"];
    NSString *appName = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"NaverClientName"];

    [[NaverThirdPartyLoginConnection getSharedInstance] setServiceUrlScheme:serviceUrlScheme];
    [[NaverThirdPartyLoginConnection getSharedInstance] setConsumerKey:consumerKey];
    [[NaverThirdPartyLoginConnection getSharedInstance] setConsumerSecret:consumerSecret];
    [[NaverThirdPartyLoginConnection getSharedInstance] setAppName:appName];
    
    

}


#pragma mark - Cordova commands

/**
 * 네이버 로그인을 요청합니다
 *
 * @param command
 */
- (void)login:(CDVInvokedUrlCommand *)command {

    // 로그인 콜백 아이디 설정
    self.loginCallbackId = command.callbackId;

    // 로그인 요청
    NaverThirdPartyLoginConnection *login = [NaverThirdPartyLoginConnection getSharedInstance];
    [login requestThirdPartyLogin];
}

/**
 * 토큰을 지워 로그아웃 처리 합니다.
 *
 * @param command
 */
- (void)logout:(CDVInvokedUrlCommand *)command {
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"success"];
    [[NaverThirdPartyLoginConnection getSharedInstance] resetToken];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

 /**
  * 토큰을 지우고, 계정 연동을 해제합니다.
  *
  * @param command
  */
 - (void)unlinkApp:(CDVInvokedUrlCommand *)command {
     // 콜백 아이디 설정
     self.loginCallbackId = command.callbackId;

     // 로그아웃 요청
     NaverThirdPartyLoginConnection *loginConnection = [NaverThirdPartyLoginConnection getSharedInstance];
     [loginConnection requestDeleteToken];
 }


#pragma mark - Utility methods

- (void)presentWebviewControllerWithRequest:(NSURLRequest *)urlRequest {
    NLoginThirdPartyOAuth20InAppBrowserViewController *inAppBrowserViewController = [[NLoginThirdPartyOAuth20InAppBrowserViewController alloc] initWithRequest:urlRequest];
    inAppBrowserViewController.parentOrientation = (UIInterfaceOrientation) [[UIDevice currentDevice] orientation];
    [[self viewController] presentViewController:inAppBrowserViewController animated:NO completion:nil];
}

- (void)exchangeKey:(NSString *)aKey withKey:(NSString *)aNewKey inMutableDictionary:(NSMutableDictionary *)aDict {
    if (![aKey isEqualToString:aNewKey]) {
        id objectToPreserve = aDict[aKey];
        aDict[aNewKey] = objectToPreserve;
        [aDict removeObjectForKey:aKey];
    }
}

- (void)buildRequestMeJsonObject:(NSMutableDictionary *)dictionary {
    NSMutableDictionary *responseDict = dictionary[@"response"];
    [self exchangeKey:@"enc_id" withKey:@"encryptionId" inMutableDictionary:responseDict];
    [self exchangeKey:@"profile_image" withKey:@"profileImage" inMutableDictionary:responseDict];

    dictionary[@"response"] = responseDict;
    [self exchangeKey:@"resultcode" withKey:@"resultCode" inMutableDictionary:dictionary];
}


#pragma mark - NaverThirdPartyLoginConnectionDelegate

- (void)oauth20ConnectionDidOpenInAppBrowserForOAuth:(NSURLRequest *)request {
    NSLog(@"oauth20ConnectionDidOpenInAppBrowserForOAuth");
    [self presentWebviewControllerWithRequest:request];
}

- (void)oauth20ConnectionDidFinishRequestACTokenWithAuthCode {
    NSLog(@"oauth20ConnectionDidFinishRequestACTokenWithAuthCode");
    NSString *accessToken = [[NaverThirdPartyLoginConnection getSharedInstance] accessToken];
    NSString *refreshToken = [[NaverThirdPartyLoginConnection getSharedInstance] refreshToken];
    NSDate *expiresAt = [[NaverThirdPartyLoginConnection getSharedInstance] accessTokenExpireDate];
    NSString *tokenType = [[NaverThirdPartyLoginConnection getSharedInstance] tokenType];
    NSMutableDictionary *userSession =  [NSMutableDictionary new];
    
    NSDictionary *result = @{
                             @"accessToken" : accessToken,
                             @"refreshToken" : refreshToken,
                             @"expiresAt" : [NSString stringWithFormat:@"%f", [expiresAt timeIntervalSince1970]],
                             @"tokenType" : tokenType
                             };
    [userSession addEntriesFromDictionary: result];
    
    NSString *urlString = @"https://openapi.naver.com/v1/nid/me"; // 사용자 프로필 호출 API URL
    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:urlString]];
    NSString *authValue = [NSString stringWithFormat:@"Bearer %@", accessToken];
    NSString *contentType = @"text/json;charset=utf-8";
    [urlRequest setValue:authValue forHTTPHeaderField:@"Authorization"];
    [urlRequest setValue:contentType forHTTPHeaderField:@"Content-Type"];
    
    [[[NSURLSession sharedSession] dataTaskWithRequest:urlRequest completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        NSError *serializationError;
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&serializationError];
        
        [userSession addEntriesFromDictionary: [json objectForKey:@"response"]];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:userSession];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.loginCallbackId];
        self.loginCallbackId = nil;
    }] resume];
}

- (void)oauth20ConnectionDidFinishRequestACTokenWithRefreshToken {
    NSLog(@"oauth20ConnectionDidFinishRequestACTokenWithRefreshToken");
    NSString *accessToken = [[NaverThirdPartyLoginConnection getSharedInstance] accessToken];
    NSString *refreshToken = [[NaverThirdPartyLoginConnection getSharedInstance] refreshToken];
    NSDate *expiresAt = [[NaverThirdPartyLoginConnection getSharedInstance] accessTokenExpireDate];
    NSString *tokenType = [[NaverThirdPartyLoginConnection getSharedInstance] tokenType];

    NSMutableDictionary *userSession =  [NSMutableDictionary new];
    
    NSDictionary *result = @{
                             @"accessToken" : accessToken,
                             @"refreshToken" : refreshToken,
                             @"expiresAt" : [NSString stringWithFormat:@"%f", [expiresAt timeIntervalSince1970]],
                             @"tokenType" : tokenType
                             };
    [userSession addEntriesFromDictionary: result];
  
    NSString *urlString = @"https://openapi.naver.com/v1/nid/me"; // 사용자 프로필 호출 API URL
    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:urlString]];
    NSString *authValue = [NSString stringWithFormat:@"Bearer %@", accessToken];
    NSString *contentType = @"text/json;charset=utf-8";
    [urlRequest setValue:authValue forHTTPHeaderField:@"Authorization"];
    [urlRequest setValue:contentType forHTTPHeaderField:@"Content-Type"];
    
    [[[NSURLSession sharedSession] dataTaskWithRequest:urlRequest completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        NSError *serializationError;
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&serializationError];
        
        [userSession addEntriesFromDictionary: [json objectForKey:@"response"]];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:userSession];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.loginCallbackId];
        self.loginCallbackId = nil;
    }] resume];
}

- (void)oauth20ConnectionDidFinishDeleteToken {
    NSLog(@"oauth20ConnectionDidFinishDeleteToken");
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"success"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.loginCallbackId];

    self.loginCallbackId = nil;
}

- (void)oauth20Connection:(NaverThirdPartyLoginConnection *)oauthConnection didFailWithError:(NSError *)error {
    NSLog(@"oauth20Connection");
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.description];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.loginCallbackId];

    self.loginCallbackId = nil;
}

- (void)openURL:(NSNotification *)notification {
    NSLog(@"handle url1: %@", [notification object]);
    NSURL *url = [notification object];
    
    if (![url isKindOfClass:[NSURL class]]) {
        return;
    }
    
     [[NaverThirdPartyLoginConnection getSharedInstance] application:[UIApplication sharedApplication] openURL:url options:nil];
}
   

- (void)openURLWithApplicationSourceAndAnnotation:(NSNotification *)notification {
    NSLog(@"handle url2: %@", [notification object]);
    NSDictionary*  notificationData = [notification object];
    
    if ([notificationData isKindOfClass: NSDictionary.class]){
        
        NSURL* url = notificationData[@"url"];
        NSString* sourceApplication = notificationData[@"sourceApplication"];
        id annotation = notificationData[@"annotation"];
        [[NaverThirdPartyLoginConnection getSharedInstance] application:[UIApplication sharedApplication] openURL:url sourceApplication:sourceApplication annotation:annotation];
        

        if ([url isKindOfClass:NSURL.class] && [sourceApplication isKindOfClass:NSString.class] && annotation) {
        }
    }}
@end



