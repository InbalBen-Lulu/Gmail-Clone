package com.example.mail_app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mail_app.data.entity.Label;
import com.example.mail_app.repository.LabelRepository;

import java.util.List;

public class LabelViewModel extends AndroidViewModel {
    private final LabelRepository repository;
    private final LiveData<List<Label>> labels;

    public LabelViewModel(@NonNull Application application) {
        super(application);
        repository = new LabelRepository(application);
        labels = repository.getAll();
    }

    public LiveData<List<Label>> getLabels() {
        return labels;
    }

    public void addLabel(Label label) {
        repository.add(label);
    }

    public void deleteLabel(Label label) {
        repository.delete(label);
    }

    public void renameLabel(String id, String newName) {
        repository.rename(id, newName);
    }

    public void setColor(String id, String color) {
        repository.setColor(id, color);
    }

    public void resetColor(String id) {
        repository.resetColor(id);
    }

    public void reloadLabels() {
        repository.reload();
    }
}
