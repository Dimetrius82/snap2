package com.snapmail.util;

import com.snapmail.database.MailDatabase;
import com.snapmail.login.AuthorizationCompleteCallback;

public class Constants
{
    public static AuthorizationCompleteCallback LOGIN_NEW_ACCOUNT_CALLBACK = null;
    public static final String SAVE_EMAIL_ADDRESS_KEY = "EMAIL_ADDRESS";

    public static MailDatabase mailDatabase;
}
