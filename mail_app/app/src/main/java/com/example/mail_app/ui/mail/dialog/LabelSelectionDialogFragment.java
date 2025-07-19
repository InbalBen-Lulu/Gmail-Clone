package com.example.mail_app.ui.mail.dialog;

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
import com.example.mail_app.utils.UiUtils;
import com.example.mail_app.viewmodel.LabelViewModel;
import com.example.mail_app.viewmodel.MailViewModel;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class LabelSelectionDialogFragment extends DialogFragment {

    private Consumer<FullMail> updatedCallback;

    public static LabelSelectionDialogFragment newInstance(FullMail mail,
                                                           Consumer<FullMail> callback) {
        LabelSelectionDialogFragment fragment = new LabelSelectionDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("mail", mail);
        fragment.setArguments(args);
        fragment.updatedCallback = callback;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_label_selection, null);
        LinearLayout checkboxContainer = dialogView.findViewById(R.id.labelCheckboxContainer);

        FragmentActivity activity = requireActivity();
        LabelViewModel labelViewModel = new ViewModelProvider(activity).get(LabelViewModel.class);
        MailViewModel mailViewModel = new ViewModelProvider(activity).get(MailViewModel.class);

        FullMail mail = (FullMail) getArguments().getSerializable("mail");
        if (mail == null) {
            return new AlertDialog.Builder(requireContext())
                    .setTitle("Label as")
                    .setMessage("Mail data not loaded yet.")
                    .setNegativeButton("Cancel", null)
                    .create();
        }

        labelViewModel.getLabels().observe(this, labels -> {
            checkboxContainer.removeAllViews();
            Set<String> currentLabelIds = new HashSet<>();
            for (Label l : mail.getLabels()) {
                currentLabelIds.add(l.getId());
            }

            for (Label label : labels) {
                CheckBox checkBox = (CheckBox) getLayoutInflater()
                        .inflate(R.layout.item_label_checkbox, checkboxContainer,
                                false);
                checkBox.setText(label.getName());
                checkBox.setChecked(currentLabelIds.contains(label.getId()));
                checkBox.setTag(label.getId());
                checkboxContainer.addView(checkBox);
            }
        });

        return new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.label_dialog_title))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.label_dialog_ok), (dialog,
                                                                         which) -> {
                    int[] pendingOps = {0};
                    boolean[] labelsChanged = {false};

                    for (int i = 0; i < checkboxContainer.getChildCount(); i++) {
                        View child = checkboxContainer.getChildAt(i);
                        if (child instanceof CheckBox) {
                            CheckBox cb = (CheckBox) child;
                            String labelId = (String) cb.getTag();
                            boolean checked = cb.isChecked();

                            boolean alreadyHasLabel = false;
                            for (Label l : mail.getLabels()) {
                                if (l.getId().equals(labelId)) {
                                    alreadyHasLabel = true;
                                    break;
                                }
                            }

                            if (checked && !alreadyHasLabel) {
                                pendingOps[0]++;
                                labelsChanged[0] = true;
                                mailViewModel.addLabelToMail(
                                        mail.getMail().getId(), labelId,
                                        () -> handleLabelOpsCompletion(mailViewModel,
                                                mail.getMail().getId(), pendingOps, labelsChanged),
                                        msg -> UiUtils.showMessage(requireActivity(), msg)
                                );
                            } else if (!checked && alreadyHasLabel) {
                                pendingOps[0]++;
                                labelsChanged[0] = true;
                                mailViewModel.removeLabelFromMail(
                                        mail.getMail().getId(), labelId,
                                        () -> handleLabelOpsCompletion(mailViewModel,
                                                mail.getMail().getId(), pendingOps, labelsChanged),
                                        msg -> UiUtils.showMessage(requireActivity(), msg)
                                );
                            }
                        }
                    }

                    // אם לא הייתה פעולה בכלל
                    if (pendingOps[0] == 0) {
                        mailViewModel.reloadCurrentCategory();
                        mailViewModel.refreshSingleMail(mail.getMail().getId());
                        notifyCallbackIfNeeded(mailViewModel, mail.getMail().getId(),
                                labelsChanged[0]);
                    }
                })
                .setNegativeButton(getString(R.string.label_dialog_cancel), null)
                .create();
    }

    private void handleLabelOpsCompletion(MailViewModel mailViewModel, String mailId,
                                          int[] pendingOps, boolean[] labelsChanged) {
        pendingOps[0]--;
        if (pendingOps[0] == 0) {
//            mailViewModel.reloadCurrentCategory();
//            mailViewModel.refreshSingleMail(mailId);
            notifyCallbackIfNeeded(mailViewModel, mailId, labelsChanged[0]);
        }
    }

    private void notifyCallbackIfNeeded(MailViewModel viewModel, String mailId,
                                        boolean shouldNotify) {
        if (shouldNotify && updatedCallback != null) {
            viewModel.getLiveMailById(mailId).observe(this, updated -> {
                if (updated != null) {
                    updatedCallback.accept(updated);
                }
            });
        }
    }
}
