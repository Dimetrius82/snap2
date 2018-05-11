package com.snapmail.util;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity
public class Message implements Serializable
{
    @PrimaryKey
    @NonNull
    private String emailAddress;

    @ColumnInfo(name = "message_id")
    private String firstName;

    @ColumnInfo(name = "folder_name")
    private String folderName;

    @ColumnInfo(name = "sender_name")
    private String senderName;

    @ColumnInfo(name = "subject")
    private String subject;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "attachment_link")
    private String attachmentLink;

    @ColumnInfo(name = "is_read")
    private boolean isRead;

    @ColumnInfo(name = "is_starred")
    private boolean isStarred;

    @ColumnInfo(name = "time_stamp")
    private String timeStamp;

    @NonNull
    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getFolderName()
    {
        return folderName;
    }

    public void setFolderName(String folderName)
    {
        this.folderName = folderName;
    }

    public String getSenderName()
    {
        return senderName;
    }

    public void setSenderName(String senderName)
    {
        this.senderName = senderName;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getAttachmentLink()
    {
        return attachmentLink;
    }

    public void setAttachmentLink(String attachmentLink)
    {
        this.attachmentLink = attachmentLink;
    }

    public boolean isRead()
    {
        return isRead;
    }

    public void setRead(boolean read)
    {
        isRead = read;
    }

    public boolean isStarred()
    {
        return isStarred;
    }

    public void setStarred(boolean starred)
    {
        isStarred = starred;
    }

    public String getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp)
    {
        this.timeStamp = timeStamp;
    }
}
