package com.snapmail.util;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity
public class Account implements Serializable
{
    @PrimaryKey
    @NonNull
    private String emailAddress;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "password")
    private String password;

    @NonNull
    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(@NonNull String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
