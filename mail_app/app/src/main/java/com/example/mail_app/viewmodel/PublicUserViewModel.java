package com.example.mail_app.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.mail_app.data.entity.PublicUser;
import com.example.mail_app.repository.PublicUserRepository;

public class PublicUserViewModel extends ViewModel {
    private final PublicUserRepository repository;

    public PublicUserViewModel() {
        repository = new PublicUserRepository();
    }

    public PublicUser getById(String id) {
        return repository.getById(id);
    }

    public void save(PublicUser user) {
        repository.save(user);
    }
}
