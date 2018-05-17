package com.snapmail.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.snapmail.util.Account;
import com.snapmail.util.Folder;
import com.snapmail.util.Message;

@Database(entities = {Account.class, Folder.class, Message.class}, exportSchema = false, version = 1)
public abstract class MailDatabase extends RoomDatabase
{
    public abstract DataAccessObject dataAccessObject();
}

