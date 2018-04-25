# cordova-plugin-naver-sdk
Naver Cordova SDK Plugin (네이버 계정 연동 플러그인)

## Development Environment and ETC
|type|version
|---|---
|ionic (Ionic CLI)|3.19.1
|cordova (Cordova CLI)|8.0.0
|Cordova Platforms Android|6.4.0
|Cordova Platforms IOS|4.5.4
|Ionic Framework|ionic-angular 3.9.2
|NaverThirdPartyLogin.framework(ios)|4.0.9
|naveridlogin-android-sdk(android)|4.2.0

## How to install
install cordova plugin
```
// OAUTH_CLIENT_ID: client id that you got assigned from naver development application you created
// OAUTH_CLIENT_SECRET: client secret that you got assigned from naver development application you created
// OAUTH_CLIENT_NAME: naver development application name
// OAUTH_CLIENT_APP_SCHEME: app scheme that you registered in naver development application for ios
$ cordova plugin add cordova-plugin-naver-sdk --variable OAUTH_CLIENT_ID=YOUR_CLIENT_ID --variable OAUTH_CLIENT_SECRET=YOUR_CLIENT_SECRET --variable OAUTH_CLIENT_NAME=YOUR_CLIENT_NAME --variable OAUTH_CLIENT_APP_SCHEME=YOUR_CLIENT_APP_SCHEME
```

install wrapper for naver cordova sdk plugin to interface
```
$ npm install --save naver-sdk
```

then import __NaverCordovaSDK__ module into app.module.ts
```
import { NaverCordovaSDK } from 'naver-sdk';

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


## TO-DO
Current NaverThirdPartyLogin Library does not officially support types of authentication to log-in, but they do have some logics inside their libary to pop up the webview to login if the Naver app was not installed. 
So next coming up release will take care of giving the login authentication options in order for users to select whether they want login through the Naver app or the webview.