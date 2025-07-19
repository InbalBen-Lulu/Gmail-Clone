package com.example.mail_app.ui.mail.action;

import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.ui.mail.dialog.LabelSelectionDialogFragment;
import com.example.mail_app.ui.mail.component.MailMenu;
import com.example.mail_app.ui.mail.adapter.MailListAdapter;
import com.example.mail_app.viewmodel.MailViewModel;

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
        MailMenu.setupMenu(context, menu, selectedMail);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return MailMenu.handleMenuItemClick(context, item, selectedMail, viewModel,
                mode::finish, null );
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        adapter.clearSelection();
        onExit.run();

        View searchBar = ((android.app.Activity) context).findViewById(R.id.search_bar_wrapper);
        if (searchBar != null) searchBar.setVisibility(View.VISIBLE);
    }
}
