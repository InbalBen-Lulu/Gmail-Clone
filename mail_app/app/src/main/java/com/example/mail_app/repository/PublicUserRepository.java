package com.example.mail_app.repository;

import com.example.mail_app.MyApp;
import com.example.mail_app.data.dao.PublicUserDao;
import com.example.mail_app.data.entity.PublicUser;

import java.util.List;

public class PublicUserRepository {
    private final PublicUserDao dao;

    public PublicUserRepository() {
        dao = MyApp.getInstance().getDatabase().publicUserDao();
    }

    public PublicUser getById(String userId) {
        return dao.getById(userId);
    }

    public void save(PublicUser user) {
        new Thread(() -> dao.insert(user)).start();
    }

    public void saveAll(List<PublicUser> users) {
        new Thread(() -> dao.insertAll(users)).start();
    }
}
