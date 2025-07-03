package com.example.mail_app.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.mail_app.data.entity.PublicUser;
import com.example.mail_app.repository.PublicUserRepository;

/**
 * ViewModel for accessing and storing public user data.
 * Delegates operations to PublicUserRepository.
 */
public class PublicUserViewModel extends ViewModel {

    private final PublicUserRepository repository;

    /**
     * Initializes the repository for public users.
     */
    public PublicUserViewModel() {
        repository = new PublicUserRepository();
    }

    /**
     * Returns a public user by ID.
     */
    public PublicUser getById(String id) {
        return repository.getById(id);
    }

    /**
     * Saves a public user to the repository.
     */
    public void save(PublicUser user) {
        repository.save(user);
    }
}
