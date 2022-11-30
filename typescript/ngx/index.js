import { Injectable } from '@angular/core';
import { cordova, IonicNativePlugin } from '@ionic-native/core';

var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};

var NaverCordovaSDK = /** @class */ (function(_super) {
    __extends(NaverCordovaSDK, _super);
    function NaverCordovaSDK() {
        return (_super !== null && _super.apply(this, arguments)) || this;
    }
    NaverCordovaSDK.prototype.login = function() {
        return cordova(this, 'login', {}, arguments);
    };
    NaverCordovaSDK.prototype.logout = function () {
        return cordova(this, 'logout', {}, arguments);
    };
    NaverCordovaSDK.prototype.unlinkApp = function () {
        return cordova(this, 'unlinkApp', {}, arguments);
    };
    NaverCordovaSDK.prototype.refreshAccessToken = function () {
        return cordova(this, 'refreshAccessToken', {}, arguments);
    };
    NaverCordovaSDK.prototype.getAccessToken = function () {
        return cordova(this, 'getAccessToken', {}, arguments);
    };
    NaverCordovaSDK.pluginName = 'Naver Cordova SDK Plugin';
    NaverCordovaSDK.plugin = 'cordova-plugin-naver-sdk';
    NaverCordovaSDK.pluginRef = 'NaverCordovaSDK';
    NaverCordovaSDK.repo = 'https://github.com/raccoondev85/cordova-plugin-naver-sdk';
    NaverCordovaSDK.platforms = ['Android', 'iOS'];
    NaverCordovaSDK = __decorate([
        Injectable()
    ], NaverCordovaSDK);
    return NaverCordovaSDK;
})(IonicNativePlugin);
export { NaverCordovaSDK };
