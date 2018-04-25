package com.raccoondev85.plugin.naver;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;


public class NaverCordovaSDK extends CordovaPlugin {

    private static final String LOG_TAG = "NaverCordovaSDK";
    private static final boolean DEBUG_LOG = true;
    private OAuthLogin mOAuthLoginInstance;

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();

        try{
            NaverResources.initResources(cordova.getActivity().getApplication());
            mOAuthLoginInstance = OAuthLogin.getInstance();
            mOAuthLoginInstance.showDevelopersLog(DEBUG_LOG);
            mOAuthLoginInstance.init(cordova.getActivity(), NaverResources.OAUTH_CLIENT_ID,
                    NaverResources.OAUTH_CLIENT_SECRET, NaverResources.OAUTH_CLIENT_NAME);
        }catch (Exception e) {

        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("login")) {
            this.login(callbackContext);
            return true;
        } else if (action.equals("logout")) {
            this.logout(callbackContext);
            return true;
        } else if (action.equals("unlinkApp")) {
            this.unlinkApp(callbackContext);
            return true;
        } else if (action.equals("refreshAccessToken")) {
            this.refreshAccessToken(callbackContext);
            return true;
        } else if (action.equals("getAccessToken")) {
            this.getAccessToken(callbackContext);
            return true;
        }

        return false;
    }


    private void login(final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                OAuthLoginDefine.LOGIN_BY_WEBVIEW_ONLY = true;
                mOAuthLoginInstance.startOauthLoginActivity(
                        cordova.getActivity(),
                        new NaverOAuthLoginHandler(cordova.getActivity(), callbackContext)
                );
            }
        });

    }


    private void logout(CallbackContext callbackContext) {
        mOAuthLoginInstance.logout(cordova.getActivity());
        callbackContext.success();
    }


    private void unlinkApp(CallbackContext callbackContext) {
        Context context = cordova.getActivity();
        boolean isSuccessDeleteToken = mOAuthLoginInstance.logoutAndDeleteToken(cordova.getActivity());
        try {
            if (isSuccessDeleteToken) {
                callbackContext.success();
            } else {
                JSONObject jsonObject = new JSONObject();

                String errorCode = mOAuthLoginInstance.getLastErrorCode(context).getCode();
                String errorDescription = mOAuthLoginInstance.getLastErrorDesc(context);

                jsonObject.put("code", errorCode);
                jsonObject.put("description", errorDescription);

                callbackContext.error(jsonObject);
            }
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }


    private void refreshAccessToken(CallbackContext callbackContext) {
        String accessToken = mOAuthLoginInstance.refreshAccessToken(cordova.getActivity());
        callbackContext.success(accessToken);
    }


    private void getAccessToken(CallbackContext callbackContext) {
        String accessToken = mOAuthLoginInstance.getAccessToken(cordova.getActivity());
        callbackContext.success(accessToken);
    }

    private class NaverOAuthLoginHandler extends OAuthLoginHandler {

        private CallbackContext mCallbackContext;
        private Context mContext;

        NaverOAuthLoginHandler(Context context, CallbackContext callbackContext) {
            mContext = context;
            mCallbackContext = callbackContext;
        }

        @Override
        public void run(boolean isSuccess) {
            Log.d(LOG_TAG, "isSuccess "+isSuccess);

            JSONObject resultObject = new JSONObject();
            try {
                if (isSuccess) {
                    new RequestApiTask().execute(mContext, mCallbackContext);
                } else {
                    String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
                    String errorDescription = mOAuthLoginInstance.getLastErrorDesc(mContext);

                    resultObject.put("code", errorCode);
                    resultObject.put("description", errorDescription);

                    mCallbackContext.error(resultObject);
                }
            } catch (Exception e) {
                mCallbackContext.error(e.getMessage());
            }
        }
    }


    private class RequestApiTask extends AsyncTask<Object, Void, String> {
        private CallbackContext mCallbackContext;
        private Context mContext;

        @Override
        protected String doInBackground(Object... args) {
            mContext = (Context) args[0];
            mCallbackContext = (CallbackContext)args[1];

            String url = "https://openapi.naver.com/v1/nid/me";
            String at = mOAuthLoginInstance.getAccessToken(mContext);
            return mOAuthLoginInstance.requestApi(mContext, at, url);
        }

        @Override
        protected void onPreExecute() {

        }


        protected void onPostExecute(String content) {
            if(content == null){
                mCallbackContext.error("API call failed");
                return;
            }
            JSONObject resultObject = new JSONObject();

            try{
                String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
                String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
                String tokenType = mOAuthLoginInstance.getTokenType(mContext);

                JSONObject userinfo = new JSONObject();

                userinfo.put("accessToken", accessToken);
                userinfo.put("refreshToken", refreshToken);
                userinfo.put("expiresAt", expiresAt);
                userinfo.put("tokenType", tokenType);

                JSONObject prop = new JSONObject(content.toString());
                JSONObject[] objs = new JSONObject[] { userinfo, (JSONObject) prop.get("response") };
                for (JSONObject obj : objs) {
                    Iterator it = obj.keys();
                    while (it.hasNext()) {
                        String key = (String)it.next();
                        resultObject.put(key, obj.get(key));
                    }
                }
                mCallbackContext.success(resultObject);
            }catch (Exception e){
                mCallbackContext.error(e.getMessage());
            }

        }
    }
}
