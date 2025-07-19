package com.example.mail_app.ui.view;

import android.content.Context;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.ui.dialog.LabelSelectionDialogFragment;
import com.example.mail_app.ui.view.MailListAdapter;
import com.example.mail_app.viewmodel.MailViewModel;

import java.util.HashMap;
import java.util.Map;

public class MailActionModeCallback implements ActionMode.Callback {

    private final Context context;
    private final FullMail selectedMail;
    private final MailViewModel viewModel;
    private final MailListAdapter adapter;
    private final Runnable onExit;

    public MailActionModeCallback(Context context, FullMail selectedMail, MailViewModel viewModel, MailListAdapter adapter, Runnable onExit) {
        this.context = context;
        this.selectedMail = selectedMail;
        this.viewModel = viewModel;
        this.adapter = adapter;
        this.onExit = onExit;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.mail_action_menu, menu);

        View searchBar = ((android.app.Activity) context).findViewById(R.id.search_bar_wrapper);
        if (searchBar != null) searchBar.setVisibility(View.GONE);
        menu.clear();

        menu.add(Menu.NONE, R.id.action_delete, Menu.NONE, context.getString(R.string.action_delete))
                .setIcon(R.drawable.outline_delete_24)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        if (!selectedMail.getMail().isSpam()) {
            menu.add(Menu.NONE, R.id.action_label, Menu.NONE, context.getString(R.string.action_label))
                    .setIcon(R.drawable.outline_label_24)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }

        if ("received".equals(selectedMail.getMail().getType())) {
            boolean isSpam = selectedMail.getMail().isSpam();

            menu.add(Menu.NONE, R.id.action_report, Menu.NONE,
                            context.getString(isSpam ? R.string.action_unspam : R.string.action_report))
                    .setIcon(isSpam ? R.drawable.baseline_report_off : R.drawable.outline_report_24)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (selectedMail == null) return false;

        int itemId = item.getItemId();

        if (itemId == R.id.action_delete) {
            viewModel.deleteMail(selectedMail.getMail().getId());
            mode.finish();
            return true;
        } else if (itemId == R.id.action_label) {
            LabelSelectionDialogFragment dialog =
                    LabelSelectionDialogFragment.newInstance(selectedMail.getMail().getId());
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "label_dialog");
            mode.finish();
            return true;
        } else if (itemId == R.id.action_report) {
            boolean isSpam = !selectedMail.getMail().isSpam();
            viewModel.setSpam(selectedMail.getMail().getId(), isSpam, () -> viewModel.reloadCurrentCategory());

            mode.finish();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        adapter.clearSelection();
        onExit.run();

        View searchBar = ((android.app.Activity) context).findViewById(R.id.search_bar_wrapper);
        if (searchBar != null) searchBar.setVisibility(View.VISIBLE);
    }
}
