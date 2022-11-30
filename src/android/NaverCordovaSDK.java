package com.raccoondev85.plugin.naver;

import android.content.Context;
import android.util.Log;

import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.OAuthLoginCallback;
import com.navercorp.nid.oauth.NidOAuthBehavior;
import com.navercorp.nid.oauth.NidOAuthLogin;
import com.navercorp.nid.profile.NidProfileCallback;
import com.navercorp.nid.profile.data.NidProfileResponse;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;

import java.util.Iterator;

public class NaverCordovaSDK extends CordovaPlugin {

    private static final String LOG_TAG = "NaverCordovaSDK";
    private static final boolean DEBUG_LOG = true;
    private NaverIdLoginSDK mOAuthLoginInstance;

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();

        try{
            NaverResources.initResources(cordova.getActivity().getApplication());

            mOAuthLoginInstance = NaverIdLoginSDK.INSTANCE;
            mOAuthLoginInstance.showDevelopersLog(DEBUG_LOG);
            mOAuthLoginInstance.initialize(cordova.getActivity(), NaverResources.OAUTH_CLIENT_ID,
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
                mOAuthLoginInstance.setBehavior(NidOAuthBehavior.DEFAULT);
                mOAuthLoginInstance.authenticate(cordova.getActivity(), new NaverOAuthLoginHandler(cordova.getActivity(), callbackContext));
            }
        });

    }


    private void logout(CallbackContext callbackContext) {
        mOAuthLoginInstance.logout();
        callbackContext.success();
    }


    private void unlinkApp(CallbackContext callbackContext) {
        Context context = cordova.getActivity();
        new NidOAuthLogin().callDeleteTokenApi(cordova.getActivity(), new OAuthLoginCallback() {
            @Override
            public void onSuccess() {
                callbackContext.success();
            }

            @Override
            public void onFailure(int i, String s) {
                errorCallback(callbackContext);
            }

            @Override
            public void onError(int i, String s) {
                errorCallback(callbackContext);
            }
        });

    }


    private void refreshAccessToken(CallbackContext callbackContext) {
        new NidOAuthLogin().callRefreshAccessTokenApi(cordova.getActivity(), new OAuthLoginCallback() {
            @Override
            public void onSuccess() {
                String accessToken = mOAuthLoginInstance.getAccessToken();
                callbackContext.success(accessToken);
            }

            @Override
            public void onFailure(int i, String s) {
                errorCallback(callbackContext);
            }

            @Override
            public void onError(int i, String s) {
                errorCallback(callbackContext);
            }
        });

    }


    private void getAccessToken(CallbackContext callbackContext) {
        String accessToken = mOAuthLoginInstance.getAccessToken();
        callbackContext.success(accessToken);
    }

    private void errorCallback(CallbackContext callbackContext) {
        JSONObject resultObject = new JSONObject();
        try {

            String errorCode = mOAuthLoginInstance.getLastErrorCode().getCode();
            String errorDescription = mOAuthLoginInstance.getLastErrorDescription();

            resultObject.put("code", errorCode);
            resultObject.put("description", errorDescription);

            callbackContext.error(resultObject);

        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private class NaverOAuthLoginHandler implements OAuthLoginCallback {

        private CallbackContext mCallbackContext;
        private Context mContext;


        NaverOAuthLoginHandler(Context context, CallbackContext callbackContext) {
            mContext = context;
            mCallbackContext = callbackContext;
        }

        @Override
        public void onError(int i, String s) {
            errorCallback(mCallbackContext);
        }

        @Override
        public void onFailure(int i, String s) {
            errorCallback(mCallbackContext);
        }

        @Override
        public void onSuccess() {
            try {
                new NidOAuthLogin().callProfileApi(new NidProfileCallback<NidProfileResponse>() {
                    @Override
                    public void onSuccess(NidProfileResponse nidProfileResponse) {
                        Gson gson = new Gson();

                        JSONObject resultObject = new JSONObject();

                        try{
                            String jsonString = gson.toJson(nidProfileResponse);
                            JSONObject prop = new JSONObject(jsonString);

                            String accessToken = mOAuthLoginInstance.getAccessToken();
                            String refreshToken = mOAuthLoginInstance.getRefreshToken();
                            long expiresAt = mOAuthLoginInstance.getExpiresAt();
                            String tokenType = mOAuthLoginInstance.getTokenType();

                            JSONObject userinfo = new JSONObject();

                            userinfo.put("accessToken", accessToken);
                            userinfo.put("refreshToken", refreshToken);
                            userinfo.put("expiresAt", expiresAt);
                            userinfo.put("tokenType", tokenType);

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
                            e.printStackTrace();
                            mCallbackContext.error(e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        errorCallback(mCallbackContext);
                    }

                    @Override
                    public void onError(int i, String s) {
                        errorCallback(mCallbackContext);
                    }
                });
            } catch (Exception e) {
                mCallbackContext.error(e.getMessage());
            }
        }
    }
}
