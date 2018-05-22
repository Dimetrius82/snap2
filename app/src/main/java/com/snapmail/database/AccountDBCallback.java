package com.snapmail.database;

import com.snapmail.util.Account;

import java.util.ArrayList;

public interface AccountDBCallback
{
    void onAccountsAddedToDatabase();

    void onAccountsGottenFromDatabase(ArrayList<Account> accounts);
}
