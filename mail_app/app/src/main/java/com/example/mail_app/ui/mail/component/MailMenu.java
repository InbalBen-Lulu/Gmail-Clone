package com.example.mail_app.ui.mail.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.ui.mail.dialog.LabelSelectionDialogFragment;
import com.example.mail_app.utils.ThemeUtils;
import com.example.mail_app.utils.UiUtils;
import com.example.mail_app.viewmodel.MailViewModel;

import java.util.function.Consumer;

/**
 * Utility class for setting up and handling the Gmail-style top action menu
 * (delete, label, report spam, etc.) for a given mail item.
 */
public class MailMenu {

    public static final String LABEL_DIALOG_TAG = "label_dialog";
    private static final String RECEIVED = "received"; // Mail type indicating an inbox message

    /**
     * Dynamically adds menu items to the top bar based on mail properties.
     * Supports delete, label, and report/unspam.
     */
    public static void setupMenu(Context context, Menu menu, FullMail mail) {
        menu.clear();

        // === Delete Action ===
        Drawable deleteIcon = ContextCompat.getDrawable(context, R.drawable.outline_delete_24);
        if (deleteIcon != null) {
            deleteIcon.mutate().setTint(ThemeUtils.resolveThemeColor(context, R.attr.gray_icon));
        }
        menu.add(Menu.NONE, R.id.action_delete, Menu.NONE, context.getString(R.string.action_delete))
                .setIcon(deleteIcon)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        // === Label Action (only if NOT spam) ===
        if (!mail.getMail().isSpam()) {
            Drawable labelIcon = ContextCompat.getDrawable(context, R.drawable.outline_label_24);
            if (labelIcon != null) {
                labelIcon.mutate().setTint(ThemeUtils.resolveThemeColor(context, R.attr.gray_icon));
            }
            menu.add(Menu.NONE, R.id.action_label, Menu.NONE, context.getString(R.string.action_label))
                    .setIcon(labelIcon)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }

        // === Report/Unspam Action (only for received mails) ===
        if (RECEIVED.equals(mail.getMail().getType())) {
            boolean isSpam = mail.getMail().isSpam();
            int iconRes = isSpam ? R.drawable.baseline_report_off : R.drawable.outline_report_24;
            Drawable reportIcon = ContextCompat.getDrawable(context, iconRes);
            if (reportIcon != null) {
                reportIcon.mutate().setTint(ThemeUtils.resolveThemeColor(context, R.attr.gray_icon));
            }

            menu.add(Menu.NONE, R.id.action_report, Menu.NONE,
                            context.getString(isSpam ? R.string.action_unspam : R.string.action_report))
                    .setIcon(reportIcon)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    /**
     * Handles user click on any of the action items in the menu.
     * Performs the corresponding ViewModel operations and calls optional callbacks.
     */
    public static boolean handleMenuItemClick(
            Context context,
            MenuItem item,
            FullMail mail,
            MailViewModel viewModel,
            Runnable onFinish, // callback to close ActionMode or Activity
            Consumer<FullMail> onMailUpdated // callback to refresh mail UI (optional)
    ) {
        int itemId = item.getItemId();

        // === Delete Mail ===
        if (itemId == R.id.action_delete) {
            viewModel.deleteMail(
                    mail.getMail().getId(),
                    msg -> UiUtils.showMessage((Activity) context, msg)
            );
            if (onFinish != null) onFinish.run();
            return true;

            // === Label Dialog ===
        } else if (itemId == R.id.action_label) {
            LabelSelectionDialogFragment dialog = LabelSelectionDialogFragment.newInstance(mail, updated -> {
                if (onMailUpdated != null) {
                    onMailUpdated.accept(updated);
                }
            });
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), LABEL_DIALOG_TAG);
            return true;

            // === Report Spam / Unspam ===
        } else if (itemId == R.id.action_report) {
            boolean newSpamState = !mail.getMail().isSpam();
            viewModel.setSpam(
                    mail.getMail().getId(),
                    newSpamState,
                    () -> viewModel.reloadCurrentCategory(), // reload filtered mails
                    msg -> UiUtils.showMessage((Activity) context, msg)
            );
            if (onFinish != null) onFinish.run();
            return true;
        }

        return false;
    }
}
