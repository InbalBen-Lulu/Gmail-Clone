package com.example.mail_app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.mail_app.LocalDatabase;
import com.example.mail_app.MyApp;
import com.example.mail_app.app.api.LoggedInUserAPI;
import com.example.mail_app.data.dao.LoggedInUserDao;
import com.example.mail_app.data.dto.LoginResponse;
import com.example.mail_app.data.dto.RegisterRequest;
import com.example.mail_app.data.entity.LoggedInUser;
import com.example.mail_app.data.entity.PublicUser;
import retrofit2.Callback;

/**
 * Repository class responsible for managing the currently logged-in user.
 * Handles interactions with the local database and server API.
 */
public class LoggedInUserRepository {
    private final LoggedInUserDao loggedInUserDao;
    private final LoggedInUserAPI api;
    private final MutableLiveData<LoggedInUser> userLiveData;

    /**
     * Initializes the repository with database access and API.
     */
    public LoggedInUserRepository() {
        LocalDatabase db = MyApp.getInstance().getDatabase();
        loggedInUserDao = db.userDao();
        userLiveData = new UserLiveData();
        api = new LoggedInUserAPI(loggedInUserDao, userLiveData);
    }

    /**
     * LiveData that loads user data from the database once it becomes active.
     */
    class UserLiveData extends MutableLiveData<LoggedInUser> {
        public UserLiveData() {
            super();
            setValue(null);
        }

        @Override
        protected void onActive() {
            super.onActive();
            new Thread(() -> {
                userLiveData.postValue(loggedInUserDao.get());
            }).start();
        }
    }

    /**
     * Returns LiveData containing the currently logged-in user.
     */
    public LiveData<LoggedInUser> getUser() {
        return userLiveData;
    }

    /**
     * Triggers a reload of user data from the server and updates the local state.
     */
    public void reload(Callback<LoggedInUser> callback) {
        api.get(callback);
    }

    /**
     * Uploads a new profile image and refreshes user data.
     */
    public void uploadProfileImage(String base64Image, Callback<Void> callback) {
        api.uploadImage(base64Image, callback);
    }

    /**
     * Deletes the user's profile image and refreshes user data.
     */
    public void deleteProfileImage(Callback<Void> callback) {
        api.deleteImage(callback);
    }

    /**
     * Performs user login via the API and triggers the given callback.
     */
    public void login(String userId, String password, Callback<LoginResponse> callback) {
        api.login(userId, password, callback);
    }

    /**
     * Performs user registration + login and triggers the given callback.
     */
    public void registerUser(RegisterRequest request, Callback<LoginResponse> callback) {
        api.registerUser(request, callback);
    }

    /**
     * Performs user logout via the API and triggers the given callback.
     */
    public void logout(Callback<Void> callback) {
        api.logout(callback);
    }

    /**
     * Fetches public info of a user without requiring authentication.
     */
    public void getPublicUserInfo(String userId, Callback<PublicUser> callback) {
        api.getPublicUserInfo(userId, callback);
    }
}
