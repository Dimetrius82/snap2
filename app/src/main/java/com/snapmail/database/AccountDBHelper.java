package com.snapmail.database;

import android.os.AsyncTask;

import com.snapmail.util.Account;
import com.snapmail.util.Constants;

import java.util.ArrayList;

public class AccountDBHelper
{
    public static void addAccountToDatabase(Account account, AccountDBCallback callback)
    {
        new AddAccountToDatabase(account, callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class AddAccountToDatabase extends AsyncTask<Void, Void, Void>
    {
        private Account account;
        private AccountDBCallback callback;

        AddAccountToDatabase(Account account, AccountDBCallback callback)
        {
            this.account = account;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                Constants.mailDatabase.dataAccessObject().addAccount(account);
            }
            catch (Exception e)
            {
                // TODO Handle exception
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            callback.onAccountsAddedToDatabase();
        }
    }

    public static void getAccountsFromDatabase(AccountDBCallback callback)
    {
        new GetAccountsFromDatabase(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class GetAccountsFromDatabase extends AsyncTask<Void, Void, Void>
    {
        private AccountDBCallback callback;
        private ArrayList<Account> accounts;

        GetAccountsFromDatabase(AccountDBCallback callback)
        {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                accounts = new ArrayList<>(Constants.mailDatabase.dataAccessObject().getAccounts());
            }
            catch (Exception e)
            {
                // TODO Handle exception
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            callback.onAccountsGottenFromDatabase(accounts);
        }
    }
}
