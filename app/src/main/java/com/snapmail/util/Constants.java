package com.snapmail.util;

import com.snapmail.database.MailDatabase;

public class Constants
{
    public static final int LOGIN_NEW_ACCOUNT_REQUEST_CODE = 1;
    public static final String LOGIN_NEW_ACCOUNT_ADDED = "LOGIN_NEW_ACCOUNT_ADDED";
    public static final String LOGIN_NEW_ACCOUNT_EMAIL_ADDRESS = "LOGIN_NEW_ACCOUNT_EMAIL_ADDRESS";
    public static final String LOGIN_NEW_ACCOUNT_NAME = "LOGIN_NEW_ACCOUNT_NAME";
    public static final String LOGIN_NEW_ACCOUNT_AUTH_TOKEN = "LOGIN_NEW_ACCOUNT_AUTH_TOKEN";
    public static final String LOGIN_NEW_ACCOUNT_REFRESH_TOKEN = "LOGIN_NEW_ACCOUNT_REFRESH_TOKEN";

    public static final String LOGIN_ACTIVITY_STAGE = "LOGIN_ACTIVITY_STAGE";
    public static final String LOGIN_ACTIVITY_STAGE_CHOOSE_EMAIL_SERVICE = "ACTIVITY_CHOOSE_EMAIL_SERVICE";
    public static final String LOGIN_ACTIVITY_STAGE_COMPLETE_AUTHORIZATION = "ACTIVITY_COMPLETE_AUTHORIZATION";

    public static MailDatabase mailDatabase;
}
