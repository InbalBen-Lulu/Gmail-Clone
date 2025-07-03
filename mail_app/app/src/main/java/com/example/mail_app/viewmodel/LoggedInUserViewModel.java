package com.example.mail_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.mail_app.data.dto.LoginResponse;
import com.example.mail_app.data.entity.LoggedInUser;
import com.example.mail_app.repository.LoggedInUserRepository;
import java.io.File;
import retrofit2.Callback;

/**
 * ViewModel for managing the currently logged-in user.
 * Handles login, logout, and profile image actions.
 */
public class LoggedInUserViewModel extends ViewModel {
    private final LoggedInUserRepository repository;
    private final LiveData<LoggedInUser> userLiveData;

    /**
     * Initializes the user repository and LiveData.
     */
    public LoggedInUserViewModel() {
        repository = new LoggedInUserRepository();
        userLiveData = repository.getUser();
    }

    /**
     * Returns LiveData of the logged-in user.
     */
    public LiveData<LoggedInUser> getUser() {
        return userLiveData;
    }

    /**
     * Saves user details locally.
     */
    public void saveUser(LoggedInUser user) {
        repository.save(user);
    }

    /**
     * Clears the saved user.
     */
    public void clearUser() {
        repository.clear();
    }

    /**
     * Reloads user details from the server.
     */
    public void reloadFromServer() {
        repository.reloadFromServer();
    }

    /**
     * Uploads a new profile image for the user.
     */
    public void uploadProfileImage(File imageFile) {
        repository.uploadProfileImage(imageFile);
    }

    /**
     * Deletes the user's profile image.
     */
    public void deleteProfileImage() {
        repository.deleteProfileImage();
    }

    /**
     * Logs in the user with given credentials.
     */
    public void login(String userId, String password, Callback<LoginResponse> callback) {
        repository.login(userId, password, callback);
    }

    /**
     * Logs out the current user.
     */
    public void logout(Callback<Void> callback) {
        repository.logout(callback);
    }
}
