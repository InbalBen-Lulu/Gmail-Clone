package com.example.mail_app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mail_app.data.entity.Label;
import com.example.mail_app.repository.LabelRepository;

import java.util.List;

public class LabelViewModel extends ViewModel {
    private final LabelRepository repository;
    private final LiveData<List<Label>> labels;

    public LabelViewModel() {
        repository = new LabelRepository();
        labels = repository.getAll();
    }

    public LiveData<List<Label>> getLabels() {
        return labels;
    }

    public Label getById(String id) {
        return repository.getById(id);
    }

    public void reloadLabels() {
        repository.reload();
    }
}
