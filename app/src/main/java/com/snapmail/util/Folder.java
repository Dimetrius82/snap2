package com.snapmail.util;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(primaryKeys = {"email_address", "folder_name"})
public class Folder implements Serializable
{
    @ColumnInfo(name = "email_address")
    @NonNull
    private String emailAddress;

    @ColumnInfo(name = "folder_name")
    @NonNull
    private String folderName;

    @NonNull
    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(@NonNull String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    @NonNull
    public String getFolderName()
    {
        return folderName;
    }

    public void setFolderName(@NonNull String folderName)
    {
        this.folderName = folderName;
    }
}
