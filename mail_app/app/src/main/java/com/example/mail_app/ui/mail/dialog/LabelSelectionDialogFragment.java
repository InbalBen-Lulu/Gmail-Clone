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

/**
 * Dialog for selecting labels to apply/remove from a specific mail.
 * Displays a checkbox list of all labels and applies changes via the ViewModel.
 */
public class LabelSelectionDialogFragment extends DialogFragment {

    private Consumer<FullMail> updatedCallback;
    private static final String ARG_MAIL = "mail";

    /**
     * Factory method to create a new instance of the dialog with a given mail and callback.
     */
    public static LabelSelectionDialogFragment newInstance(FullMail mail,
                                                           Consumer<FullMail> callback) {
        LabelSelectionDialogFragment fragment = new LabelSelectionDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MAIL, mail);
        fragment.setArguments(args);
        fragment.updatedCallback = callback;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_label_selection, null);
        LinearLayout checkboxContainer = dialogView.findViewById(R.id.labelCheckboxContainer);

        FragmentActivity activity = requireActivity();
        LabelViewModel labelViewModel = new ViewModelProvider(activity).get(LabelViewModel.class);
        MailViewModel mailViewModel = new ViewModelProvider(activity).get(MailViewModel.class);

        FullMail mail = (FullMail) getArguments().getSerializable(ARG_MAIL);
        if (mail == null) {
            // Show error dialog if mail is null
            return new AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.label_dialog_title_error))
                    .setMessage(getString(R.string.label_dialog_message_not_loaded))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create();
        }

        // Observe labels and dynamically build checkboxes for each one
        labelViewModel.getLabels().observe(this, labels -> {
            checkboxContainer.removeAllViews();
            Set<String> currentLabelIds = new HashSet<>();
            for (Label l : mail.getLabels()) {
                currentLabelIds.add(l.getId());
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

        // Build the actual dialog
        return new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.label_dialog_title))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.label_dialog_ok), (dialog, which) -> {
                    int[] pendingOps = {0};           // Tracks number of pending label ops
                    boolean[] labelsChanged = {false}; // Tracks if labels were actually changed

                    // Loop through each checkbox and apply/remove labels accordingly
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
                                // Add new label
                                pendingOps[0]++;
                                labelsChanged[0] = true;
                                mailViewModel.addLabelToMail(
                                        mail.getMail().getId(), labelId,
                                        () -> handleLabelOpsCompletion(mailViewModel, mail.getMail().getId(), pendingOps, labelsChanged),
                                        msg -> UiUtils.showMessage(requireActivity(), msg)
                                );
                            } else if (!checked && alreadyHasLabel) {
                                // Remove existing label
                                pendingOps[0]++;
                                labelsChanged[0] = true;
                                mailViewModel.removeLabelFromMail(
                                        mail.getMail().getId(), labelId,
                                        () -> handleLabelOpsCompletion(mailViewModel, mail.getMail().getId(), pendingOps, labelsChanged),
                                        msg -> UiUtils.showMessage(requireActivity(), msg)
                                );
                            }
                        }
                    }

                    // No changes were made – just refresh the mail list and notify
                    if (pendingOps[0] == 0) {
                        mailViewModel.reloadCurrentCategory();
                        mailViewModel.refreshSingleMail(mail.getMail().getId());
                        notifyCallbackIfNeeded(mailViewModel, mail.getMail().getId(), labelsChanged[0]);
                    }
                })
                .setNegativeButton(getString(R.string.label_dialog_cancel), null)
                .create();
    }

    /**
     * Called after each label operation (add/remove). When all complete, triggers the callback.
     */
    private void handleLabelOpsCompletion(MailViewModel mailViewModel, String mailId,
                                          int[] pendingOps, boolean[] labelsChanged) {
        pendingOps[0]--;
        if (pendingOps[0] == 0) {
            notifyCallbackIfNeeded(mailViewModel, mailId, labelsChanged[0]);
        }
    }

    /**
     * Notifies the UI via callback after label updates are complete.
     */
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
