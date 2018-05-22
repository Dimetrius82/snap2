package com.snapmail.login;

import com.snapmail.util.Account;

public interface AuthorizationCompleteCallback
{
    void onAuthorizationComplete(Account account);
}
