package com.example.mail_app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mail_app.LocalDatabase;
import com.example.mail_app.MyApp;
import com.example.mail_app.app.api.LoggedInUserAPI;
import com.example.mail_app.data.dao.LoggedInUserDao;
import com.example.mail_app.data.entity.LoggedInUser;

import java.io.File;

public class LoggedInUserRepository {
    private final LoggedInUserDao loggedInUserDao;
    private final LoggedInUserAPI api;
    private final MutableLiveData<LoggedInUser> userLiveData;

    public LoggedInUserRepository() {
        LocalDatabase db = MyApp.getInstance().getDatabase();
        loggedInUserDao = db.userDao();
        api = new LoggedInUserAPI(loggedInUserDao, this::updateUser);
        userLiveData = new UserLiveData();
    }

    class UserLiveData extends MutableLiveData<LoggedInUser> {
        public UserLiveData() {
            super();
        }

        @Override
        protected void onActive() {
            super.onActive();
            new Thread(() -> postValue(loggedInUserDao.get())).start();
        }
    }

    private void updateUser(LoggedInUser user) {
        userLiveData.postValue(user);
    }

    public LiveData<LoggedInUser> getUser() {
        return userLiveData;
    }

    public void reload() {
        api.get();
    }

    public void save(LoggedInUser user) {
        new Thread(() -> {
            loggedInUserDao.clear();
            loggedInUserDao.insert(user);
            userLiveData.postValue(user);
        }).start();
    }

    public void clear() {
        new Thread(() -> {
            loggedInUserDao.clear();
            userLiveData.postValue(null);
        }).start();
    }

    public void uploadProfileImage(File imageFile) {
        api.uploadImage(imageFile);
    }

    public void deleteProfileImage() {
        api.deleteImage();
    }

    public void reloadFromServer() {
        api.get();
    }
}
