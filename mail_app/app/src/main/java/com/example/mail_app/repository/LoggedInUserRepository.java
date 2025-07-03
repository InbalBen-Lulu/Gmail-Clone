package com.example.mail_app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.mail_app.LocalDatabase;
import com.example.mail_app.MyApp;
import com.example.mail_app.app.api.LoggedInUserAPI;
import com.example.mail_app.data.dao.LoggedInUserDao;
import com.example.mail_app.data.dto.LoginResponse;
import com.example.mail_app.data.entity.LoggedInUser;
import java.io.File;
import retrofit2.Callback;

/**
 * Repository class responsible for managing the currently logged-in user.
 * Handles interactions with the local database and server API.
 */
public class LoggedInUserRepository {
    private final LoggedInUserDao loggedInUserDao;
    private final LoggedInUserAPI api;
    private final MutableLiveData<LoggedInUser> userLiveData;

    /** Initializes the repository with database access and API. */
    public LoggedInUserRepository() {
        LocalDatabase db = MyApp.getInstance().getDatabase();
        loggedInUserDao = db.userDao();
        api = new LoggedInUserAPI(loggedInUserDao);
        userLiveData = new UserLiveData();
    }

    /**
     * LiveData that loads user data from the database once it becomes active.
     */
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

    /** Returns LiveData containing the currently logged-in user. */
    public LiveData<LoggedInUser> getUser() {
        return userLiveData;
    }

    /** Triggers a reload of user data from the server and updates the local state. */
    public void reload() {
        api.get();
        new Thread(() -> {
            LoggedInUser user = loggedInUserDao.get();
            userLiveData.postValue(user);
        }).start();
    }

    /** Saves the given user into the local database and updates the LiveData. */
    public void save(LoggedInUser user) {
        new Thread(() -> {
            loggedInUserDao.clear();
            loggedInUserDao.insert(user);
            userLiveData.postValue(user);
        }).start();
    }

    /** Clears the local user data and sets LiveData to null. */
    public void clear() {
        new Thread(() -> {
            loggedInUserDao.clear();
            userLiveData.postValue(null);
        }).start();
    }

    /** Uploads a new profile image and refreshes user data. */
    public void uploadProfileImage(File imageFile) {
        api.uploadImage(imageFile);
        reload();
    }

    /** Deletes the user's profile image and refreshes user data. */
    public void deleteProfileImage() {
        api.deleteImage();
        reload();
    }

    /** Refreshes user data from the server. */
    public void reloadFromServer() {
        reload();
    }

    /** Performs user login via the API and triggers the given callback. */
    public void login(String userId, String password, Callback<LoginResponse> callback) {
        api.login(userId, password, callback);
    }

    /** Performs user logout via the API and triggers the given callback. */
    public void logout(Callback<Void> callback) {
        api.logout(callback);
    }
}
