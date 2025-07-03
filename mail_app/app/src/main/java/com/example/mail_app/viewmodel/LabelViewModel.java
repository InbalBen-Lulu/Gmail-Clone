package com.example.mail_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.mail_app.data.entity.Label;
import com.example.mail_app.repository.LabelRepository;
import java.util.List;

/**
 * ViewModel for managing label data and exposing it to the UI.
 */
public class LabelViewModel extends ViewModel {
    private final LabelRepository repository;
    private final LiveData<List<Label>> labels;

    /**
     * Initializes the label repository and LiveData list.
     */
    public LabelViewModel() {
        repository = new LabelRepository();
        labels = repository.getAll();
    }

    /**
     * Returns LiveData of all labels.
     */
    public LiveData<List<Label>> getLabels() {
        return labels;
    }


    /**
     * Retrieves a label by its ID.
     */
    public Label getById(String id) {
        return repository.getById(id);
    }

    /**
     * Reloads the label list from the server.
     */
    public void reloadLabels() {
        repository.reload();
    }
}
