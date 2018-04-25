package com.raccoondev85.plugin.naver;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

public class NaverResources {

    private static int naver_client_id;
    private static int naver_client_secret;
    private static int naver_client_name;
    public static String OAUTH_CLIENT_ID;
    public static String OAUTH_CLIENT_SECRET;
    public static String OAUTH_CLIENT_NAME;
    public static Context mContext;

    public static void initResources(Application _app){
        final Application app  = _app;
        final String package_name = app.getPackageName();
        final Resources resources = app.getResources();

        naver_client_id = resources.getIdentifier("naver_client_id", "string", package_name);
        naver_client_secret = resources.getIdentifier("naver_client_secret", "string", package_name);
        naver_client_name = resources.getIdentifier("naver_client_name", "string", package_name);

        OAUTH_CLIENT_ID = app.getString(naver_client_id);
        OAUTH_CLIENT_SECRET = app.getString(naver_client_secret);
        OAUTH_CLIENT_NAME = app.getString(naver_client_name);

    }

}
