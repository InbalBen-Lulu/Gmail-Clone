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

/**
 * ActionMode.Callback implementation for handling long-press actions on a single mail item.
 * Displays the contextual action bar with options like delete, star, label, etc.
 */
public class MailActionModeCallback implements ActionMode.Callback {

    private final Context context;
    private final FullMail selectedMail;
    private final MailViewModel viewModel;
    private final MailListAdapter adapter;
    private final Runnable onExit;

    /**
     * Constructor for the ActionMode callback.
     *
     * @param context      Activity context
     * @param selectedMail The mail that was long-pressed
     * @param viewModel    The shared ViewModel for mail actions
     * @param adapter      The adapter used for the mail list
     * @param onExit       A callback to run when the ActionMode is exited
     */
    public MailActionModeCallback(Context context, FullMail selectedMail, MailViewModel viewModel, MailListAdapter adapter, Runnable onExit) {
        this.context = context;
        this.selectedMail = selectedMail;
        this.viewModel = viewModel;
        this.adapter = adapter;
        this.onExit = onExit;
    }

    /**
     * Called when the action mode is first created.
     * Inflates the action menu and hides the search bar.
     */
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.mail_action_menu, menu);

        View searchBar = ((android.app.Activity) context).findViewById(R.id.search_bar_wrapper);
        if (searchBar != null) searchBar.setVisibility(View.GONE);

        MailMenu.setupMenu(context, menu, selectedMail);

        return true;
    }

    /**
     * Called to refresh the action mode (not used here).
     */
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    /**
     * Called when a menu item is clicked (e.g. delete, star, report spam).
     * Delegates handling to the MailMenu utility.
     */
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return MailMenu.handleMenuItemClick(
                context,
                item,
                selectedMail,
                viewModel,
                mode::finish, // close ActionMode on success
                null // no UI update callback needed here
        );
    }

    /**
     * Called when the action mode is exited.
     * Clears selection, shows search bar again, and runs custom exit logic.
     */
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        adapter.clearSelection();
        onExit.run();

        View searchBar = ((android.app.Activity) context).findViewById(R.id.search_bar_wrapper);
        if (searchBar != null) searchBar.setVisibility(View.VISIBLE);
    }
}
