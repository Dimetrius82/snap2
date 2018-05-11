package com.snapmail.login;

import com.snapmail.util.Account;

public interface AuthorizationCompleteCallback
{
    public void onAuthorizationComplete(Account account);
}
