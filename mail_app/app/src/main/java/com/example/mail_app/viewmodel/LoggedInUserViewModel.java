package com.example.mail_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mail_app.data.entity.LoggedInUser;
import com.example.mail_app.repository.LoggedInUserRepository;

import java.io.File;

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
}
