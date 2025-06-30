package com.example.mail_app;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.mail_app.data.dao.LabelDao;
import com.example.mail_app.data.dao.MailDao;
import com.example.mail_app.data.dao.MailLabelCrossRefDao;
import com.example.mail_app.data.dao.MailRecipientCrossRefDao;
import com.example.mail_app.data.dao.UserDao;
import com.example.mail_app.data.entity.Label;
import com.example.mail_app.data.entity.Mail;
import com.example.mail_app.data.entity.MailLabelCrossRef;
import com.example.mail_app.data.entity.MailRecipientCrossRef;
import com.example.mail_app.data.entity.User;
import com.example.mail_app.data.entity.utils.Converters;

/**
 * Room database configuration.
 * Define entities and DAOs here.
 */
@Database(
        entities = {
                Mail.class,
                Label.class,
                User.class,
                MailLabelCrossRef.class,
                MailRecipientCrossRef.class
        },
        version = 1
)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MailDao mailDao();
    public abstract LabelDao labelDao();
    public abstract UserDao userDao();
    public abstract MailLabelCrossRefDao mailLabelCrossRefDao();
    public abstract MailRecipientCrossRefDao mailRecipientCrossRefDao();
}
