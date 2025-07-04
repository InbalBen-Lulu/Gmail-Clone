package com.example.mail_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.mail_app.data.dto.LoginResponse;
import com.example.mail_app.data.dto.RegisterRequest;
import com.example.mail_app.data.entity.LoggedInUser;
import com.example.mail_app.data.entity.PublicUser;
import com.example.mail_app.repository.LoggedInUserRepository;
import java.util.Date;
import retrofit2.Callback;

/**
 * ViewModel for managing the currently logged-in user.
 * Handles login, logout, registration, and profile image actions.
 */
public class LoggedInUserViewModel extends ViewModel {
    private final LoggedInUserRepository repository;
    private final LiveData<LoggedInUser> userLiveData;

    /** Initializes the user repository and LiveData. */
    public LoggedInUserViewModel() {
        repository = new LoggedInUserRepository();
        userLiveData = repository.getUser();
    }

    /** Returns LiveData of the logged-in user. */
    public LiveData<LoggedInUser> getUser() {
        return userLiveData;
    }

    /** Uploads a new profile image for the user. */
    public void uploadProfileImage(String base64Image, Callback<Void> callback) {
        repository.uploadProfileImage(base64Image, callback);
    }

    /** Deletes the user's profile image. */
    public void deleteProfileImage(Callback<Void> callback) {
        repository.deleteProfileImage(callback);
    }

    /** Logs in the user with given credentials. */
    public void login(String userId, String password, Callback<LoginResponse> callback) {
        repository.login(userId, password, callback);
    }

    /** Logs out the current user. */
    public void logout(Callback<Void> callback) {
        repository.logout(callback);
    }

    /**
     * Registers a new user with the given fields and logs them in automatically.
     */
    public void registerUser(String userId, String password, String name, String gender,
                             Date birthDate, Callback<LoginResponse> callback) {
        RegisterRequest request = new RegisterRequest(userId, password, name, gender, birthDate);
        repository.registerUser(request, callback);
    }

    /**
     * Retrieves public info about a user (for profile viewing).
     */
    public void getPublicUserInfo(String userId, Callback<PublicUser> callback) {
        repository.getPublicUserInfo(userId, callback);
    }
}