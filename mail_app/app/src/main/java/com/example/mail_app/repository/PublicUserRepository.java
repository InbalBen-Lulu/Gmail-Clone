package com.example.mail_app.repository;

import com.example.mail_app.MyApp;
import com.example.mail_app.data.dao.PublicUserDao;
import com.example.mail_app.data.entity.PublicUser;
import java.util.List;

/**
 * Repository for managing access to public user data using Room.
 */
public class PublicUserRepository {
    private final PublicUserDao dao;

    /**
     * Initializes the DAO for public users from the app database.
     */
    public PublicUserRepository() {
        dao = MyApp.getInstance().getDatabase().publicUserDao();
    }

    /**
     * Returns a public user by ID from the local database.
     */
    public PublicUser getById(String userId) {
        return dao.getById(userId);
    }

    /**
     * Inserts a single public user asynchronously.
     */
    public void save(PublicUser user) {
        new Thread(() -> dao.insert(user)).start();
    }

    /**
     * Inserts a list of public users asynchronously.
     */
    public void saveAll(List<PublicUser> users) {
        new Thread(() -> dao.insertAll(users)).start();
    }
}
