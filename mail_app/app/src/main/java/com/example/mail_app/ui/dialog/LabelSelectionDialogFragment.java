package com.example.mail_app.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.data.entity.Label;
import com.example.mail_app.viewmodel.LabelViewModel;
import com.example.mail_app.viewmodel.MailViewModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LabelSelectionDialogFragment extends DialogFragment {

    private static final String ARG_MAIL_ID = "mailId";

    public static LabelSelectionDialogFragment newInstance(String mailId) {
        LabelSelectionDialogFragment fragment = new LabelSelectionDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MAIL_ID, mailId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getArguments() != null;
        String mailId = getArguments().getString(ARG_MAIL_ID);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_label_selection, null);
        LinearLayout checkboxContainer = dialogView.findViewById(R.id.labelCheckboxContainer);

        FragmentActivity activity = requireActivity();
        LabelViewModel labelViewModel = new ViewModelProvider(activity).get(LabelViewModel.class);
        MailViewModel mailViewModel = new ViewModelProvider(activity).get(MailViewModel.class);

        FullMail mail = null;
        List<FullMail> currentMails = mailViewModel.getMails().getValue();
        if (currentMails != null) {
            for (FullMail m : currentMails) {
                if (m.getMail().getId().equals(mailId)) {
                    mail = m;
                    break;
                }
            }
        }

        FullMail finalMail = mail;

        labelViewModel.getLabels().observe(this, labels -> {
            checkboxContainer.removeAllViews();
            Set<String> currentLabelIds = new HashSet<>();
            if (finalMail != null) {
                for (Label l : finalMail.getLabels()) {
                    currentLabelIds.add(l.getId());
                }
            }

            for (Label label : labels) {
                CheckBox checkBox = (CheckBox) getLayoutInflater()
                        .inflate(R.layout.item_label_checkbox, checkboxContainer, false);
                checkBox.setText(label.getName());
                checkBox.setChecked(currentLabelIds.contains(label.getId()));
                checkBox.setTag(label.getId());
                checkboxContainer.addView(checkBox);
            }
        });

        return new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.label_dialog_title))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.label_dialog_ok), (dialog, which) -> {
                    int[] pendingOps = {0};

                    for (int i = 0; i < checkboxContainer.getChildCount(); i++) {
                        View child = checkboxContainer.getChildAt(i);
                        if (child instanceof CheckBox) {
                            CheckBox cb = (CheckBox) child;
                            String labelId = (String) cb.getTag();
                            boolean checked = cb.isChecked();

                            if (finalMail != null) {
                                boolean alreadyHasLabel = false;
                                for (Label l : finalMail.getLabels()) {
                                    if (l.getId().equals(labelId)) {
                                        alreadyHasLabel = true;
                                        break;
                                    }
                                }

                                if (checked && !alreadyHasLabel) {
                                    pendingOps[0]++;
                                    mailViewModel.addLabelToMail(mailId, labelId, () -> {
                                        if (--pendingOps[0] == 0) {
                                            mailViewModel.reloadCurrentCategory();
                                        }
                                    });
                                } else if (!checked && alreadyHasLabel) {
                                    pendingOps[0]++;
                                    mailViewModel.removeLabelFromMail(mailId, labelId, () -> {
                                        if (--pendingOps[0] == 0) {
                                            mailViewModel.reloadCurrentCategory();
                                        }
                                    });
                                }
                            }
                        }
                    }

                    if (pendingOps[0] == 0) {
                        mailViewModel.reloadCurrentCategory();
                    }
                })
                .setNegativeButton(getString(R.string.label_dialog_cancel), null)
                .create();
    }
}
