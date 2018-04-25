
var exec = require('cordova/exec');

var NaverCordovaSDK = {
    login: function (s, f) {
        exec(s, f, 'NaverCordovaSDK', 'login', []);
    },

    logout: function (s, f) {
        exec(s, f, 'NaverCordovaSDK', 'logout', []);
    },

    unlinkApp: function (s, f) {
        exec(s, f, 'NaverCordovaSDK', 'unlinkApp', []);
    },

    refreshAccessToken: function (s, f) {
        exec(s, f, 'NaverCordovaSDK', 'refreshAccessToken', []);
    },

    getAccessToken: function (s, f) {
        exec(s, f, 'NaverCordovaSDK', 'getAccessToken', []);
    }

};

module.exports = NaverCordovaSDK;