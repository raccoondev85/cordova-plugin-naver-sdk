import { IonicNativePlugin } from '@ionic-native/core';
export declare class NaverCordovaSDK extends IonicNativePlugin {
    login(): Promise<any>;
    logout(): Promise<any>;
    unlinkApp(): Promise<any>;
    refreshAccessToken(): Promise<string>;
    getAccessToken(): Promise<string>;
}
