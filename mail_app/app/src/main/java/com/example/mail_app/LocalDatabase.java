package com.example.mail_app;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.mail_app.data.dao.LabelDao;
import com.example.mail_app.data.dao.LoggedInUserDao;
import com.example.mail_app.data.dao.MailDao;
import com.example.mail_app.data.dao.PublicUserDao;
import com.example.mail_app.data.entity.Label;
import com.example.mail_app.data.entity.LoggedInUser;
import com.example.mail_app.data.entity.Mail;
import com.example.mail_app.data.entity.MailLabelCrossRef;
import com.example.mail_app.data.entity.MailRecipientCrossRef;
import com.example.mail_app.data.entity.PublicUser;
import com.example.mail_app.data.entity.utils.Converters;

/**
 * Room database configuration.
 * Define entities and DAOs here.
 */
@Database(
        entities = {
                Mail.class,
                LoggedInUser.class,
                PublicUser.class,
                Label.class,
                MailLabelCrossRef.class,
                MailRecipientCrossRef.class
        },
        version = 1
)
@TypeConverters(Converters.class)
public abstract class LocalDatabase extends RoomDatabase {

    public abstract MailDao mailDao();
    public abstract LabelDao labelDao();
    public abstract PublicUserDao publicUserDao();
    public abstract LoggedInUserDao userDao();
}
