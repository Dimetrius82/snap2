package com.snapmail.util;

import com.snapmail.database.MailDatabase;
import com.snapmail.login.AuthorizationCompleteCallback;

public class Constants
{
    public static final int LOGIN_NEW_ACCOUNT_REQUEST_CODE = 1;
    public static AuthorizationCompleteCallback LOGIN_NEW_ACCOUNT_CALLBACK = null;
    public static final String LOGIN_NEW_ACCOUNT_ADDED = "LOGIN_NEW_ACCOUNT_ADDED";

    public static MailDatabase mailDatabase;
}
