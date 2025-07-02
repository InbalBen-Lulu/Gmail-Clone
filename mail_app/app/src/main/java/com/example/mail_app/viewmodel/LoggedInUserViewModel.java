package com.example.mail_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mail_app.data.dto.LoginResponse;
import com.example.mail_app.data.entity.LoggedInUser;
import com.example.mail_app.repository.LoggedInUserRepository;

import java.io.File;

import retrofit2.Callback;

public class LoggedInUserViewModel extends ViewModel {
    private final LoggedInUserRepository repository;
    private final LiveData<LoggedInUser> userLiveData;

    public LoggedInUserViewModel() {
        repository = new LoggedInUserRepository();
        userLiveData = repository.getUser();
    }

    public LiveData<LoggedInUser> getUser() {
        return userLiveData;
    }

    public void saveUser(LoggedInUser user) {
        repository.save(user);
    }

    public void clearUser() {
        repository.clear();
    }

    public void reloadFromServer() {
        repository.reloadFromServer();
    }

    public void uploadProfileImage(File imageFile) {
        repository.uploadProfileImage(imageFile);
    }

    public void deleteProfileImage() {
        repository.deleteProfileImage();
    }

    // 🔹 התחברות
    public void login(String userId, String password, Callback<LoginResponse> callback) {
        repository.login(userId, password, callback);
    }

    // 🔹 התנתקות
    public void logout(Callback<Void> callback) {
        repository.logout(callback);
    }
}
