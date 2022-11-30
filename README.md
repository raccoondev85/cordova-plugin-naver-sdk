# cordova-plugin-naver-sdk
Naver Cordova SDK Plugin Wrapper (네이버 계정 연동 플러그인 Wrapper)

## Development Environment and ETC
|type|version
|---|---
|node: 14.17.3
|npm: 9.1.2
|ionic (Ionic CLI)|5.4.16
|ionic framewordk|@ionic/angular 5.9.4
|cordova (Cordova CLI)|10.0.0
|Cordova Platforms Android|11.0.0
|Cordova Platforms IOS|5.1.1
|NaverThirdPartyLogin.framework(ios)|4.1.2
|naveridlogin-android-sdk(android)|5.3.0 (for java11)

## How to install
install cordova plugin
```
// OAUTH_CLIENT_ID: client id that you got assigned from naver development application you created
// OAUTH_CLIENT_SECRET: client secret that you got assigned from naver development application you created
// OAUTH_CLIENT_NAME: naver development application name
// OAUTH_CLIENT_APP_SCHEME: app scheme that you registered in naver development application for ios
$ cordova plugin add cordova-plugin-naver-sdk --variable OAUTH_CLIENT_ID=YOUR_CLIENT_ID --variable OAUTH_CLIENT_SECRET=YOUR_CLIENT_SECRET --variable OAUTH_CLIENT_NAME=YOUR_CLIENT_NAME --variable OAUTH_CLIENT_APP_SCHEME=YOUR_CLIENT_APP_SCHEME
```

then import __NaverCordovaSDK__ module into app.module.ts
```
import { NaverCordovaSDK } from 'cordova-plugin-naver-sdk/typescript/ngx';

@NgModule({
  providers: [
    NaverCordovaSDK
  ]
})
```

## Methods
### `login()`
If Naver app is installed in the device, will open the app and the login will be proceeded through the app, and return the values that are related to the token info and the user profile info, otherwise in case the app is not installed, just an webview will be popped up to sign in.
```
  constructor(public _naverCordovaSDK: NaverCordovaSDK) {
    this._naverCordovaSDK.login().then((res) => {
        console.log(res);
      }
    );
  }
```
beside token info(accessToken, refreshToken, expiresAt, and tokenType), other return values depend on what you set on the naver development console.

### `logout()`
```
  constructor(public _naverCordovaSDK: NaverCordovaSDK) {
    this._naverCordovaSDK.logout().then(() => {
        //do your logout proccess for your app
      }
    );
  }
```
return null

### `unlinkApp()`
Unregister app for your app service. 
```
  constructor(public _naverCordovaSDK: NaverCordovaSDK) {
    this._naverCordovaSDK.unlinkApp().then(() => {
        //do your unregister proccess for your app
      }
    );
  }
```

### `refreshAccessToken()`
Refresh access token if you need.
```
  constructor(public _naverCordovaSDK: NaverCordovaSDK) {
    this._naverCordovaSDK.refreshAccessToken().then((res) => {
        console.log(res);
      }
    );
  }
```
it returns a new access token.

### `getAccessToken()`
Get current access token.
```
  constructor(public _naverCordovaSDK: NaverCordovaSDK) {
    this._naverCordovaSDK.getAccessToken().then((res) => {
        console.log(res);
      }
    );
  }
```
it returns the current access token.

