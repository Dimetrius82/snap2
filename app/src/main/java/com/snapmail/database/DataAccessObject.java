package com.snapmail.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.snapmail.util.Account;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DataAccessObject
{
    @Query("SELECT * FROM Account")
    List<Account> getAccounts();

    @Insert
    void addAccount(Account account);
}
