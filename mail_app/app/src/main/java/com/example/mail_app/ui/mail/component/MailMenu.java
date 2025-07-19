//package com.example.mail_app.ui.mail.component;
//
//import android.content.Context;
//import android.view.Menu;
//import android.view.MenuItem;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.mail_app.R;
//import com.example.mail_app.data.entity.FullMail;
//import com.example.mail_app.ui.mail.dialog.LabelSelectionDialogFragment;
//import com.example.mail_app.viewmodel.MailViewModel;
//
//public class MailMenu {
//
//    public static void setupMenu(Context context, Menu menu, FullMail mail) {
//        menu.clear();
//
//        // מחיקת מייל - תמיד
//        menu.add(Menu.NONE, R.id.action_delete, Menu.NONE, context.getString(R.string.action_delete))
//                .setIcon(R.drawable.outline_delete_24)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//
//        // תיוג - רק אם זה לא ספאם
//        if (!mail.getMail().isSpam()) {
//            menu.add(Menu.NONE, R.id.action_label, Menu.NONE, context.getString(R.string.action_label))
//                    .setIcon(R.drawable.outline_label_24)
//                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        }
//
//        // דיווח ספאם / ביטול דיווח - רק אם זה מייל שקיבלת
//        if ("received".equals(mail.getMail().getType())) {
//            boolean isSpam = mail.getMail().isSpam();
//            menu.add(Menu.NONE, R.id.action_report, Menu.NONE,
//                            context.getString(isSpam ? R.string.action_unspam : R.string.action_report))
//                    .setIcon(isSpam ? R.drawable.baseline_report_off : R.drawable.outline_report_24)
//                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        }
//    }
//
//    public static boolean handleMenuItemClick(
//            Context context,
//            MenuItem item,
//            FullMail mail,
//            MailViewModel viewModel,
//            Runnable onFinish
//    ) {
//        int itemId = item.getItemId();
//
//        if (itemId == R.id.action_delete) {
//            viewModel.deleteMail(mail.getMail().getId());
//            if (onFinish != null) onFinish.run();
//            return true;
//
//        }  else if (itemId == R.id.action_label) {
//            LabelSelectionDialogFragment dialog =
//                    LabelSelectionDialogFragment.newInstance(mail);
//            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "label_dialog");
//            if (onFinish != null) onFinish.run();
//
//        return true;
//
//        } else if (itemId == R.id.action_report) {
//            boolean newSpamState = !mail.getMail().isSpam();
//            viewModel.setSpam(mail.getMail().getId(), newSpamState, () -> {
//                viewModel.reloadCurrentCategory(); // עדכון רשימה אחרי ספאם
//            });
//            if (onFinish != null) onFinish.run();
//            return true;
//        }
//
//        return false;
//    }
//
//}package com.example.mail_app.ui.mail.component;
//
//import android.content.Context;
//import android.view.Menu;
//import android.view.MenuItem;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.mail_app.R;
//import com.example.mail_app.data.entity.FullMail;
//import com.example.mail_app.ui.mail.dialog.LabelSelectionDialogFragment;
//import com.example.mail_app.viewmodel.MailViewModel;
//
//import java.util.function.Consumer;
//
//public class MailMenu {
//
//    public static void setupMenu(Context context, Menu menu, FullMail mail) {
//        menu.clear();
//
//        // מחיקה – תמיד
//        menu.add(Menu.NONE, R.id.action_delete, Menu.NONE, context.getString(R.string.action_delete))
//                .setIcon(R.drawable.outline_delete_24)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//
//        // תיוג – רק אם לא ספאם
//        if (!mail.getMail().isSpam()) {
//            menu.add(Menu.NONE, R.id.action_label, Menu.NONE, context.getString(R.string.action_label))
//                    .setIcon(R.drawable.outline_label_24)
//                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        }
//
//        // דיווח ספאם / ביטול – רק אם זה מייל שקיבלת
//        if ("received".equals(mail.getMail().getType())) {
//            boolean isSpam = mail.getMail().isSpam();
//            menu.add(Menu.NONE, R.id.action_report, Menu.NONE,
//                            context.getString(isSpam ? R.string.action_unspam : R.string.action_report))
//                    .setIcon(isSpam ? R.drawable.baseline_report_off : R.drawable.outline_report_24)
//                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        }
//    }
//
//    public static boolean handleMenuItemClick(
//            Context context,
//            MenuItem item,
//            FullMail mail,
//            MailViewModel viewModel,
//            Runnable onFinish,
//            Consumer<FullMail> onMailUpdated
//    ) {
//        int itemId = item.getItemId();
//
//        if (itemId == R.id.action_delete) {
//            viewModel.deleteMail(mail.getMail().getId());
//            if (onFinish != null) onFinish.run();
//            return true;
//
//        } else if (itemId == R.id.action_label) {
//            LabelSelectionDialogFragment dialog = LabelSelectionDialogFragment.newInstance(mail, updated -> {
//                if (onMailUpdated != null) {
//                    onMailUpdated.accept(updated);
//                }
//            });
//            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "label_dialog");
//            return true;
//
//        } else if (itemId == R.id.action_report) {
//            boolean newSpamState = !mail.getMail().isSpam();
//            viewModel.setSpam(mail.getMail().getId(), newSpamState, () -> {
//                viewModel.reloadCurrentCategory();
//            });
//            if (onFinish != null) onFinish.run();
//            return true;
//        }
//
//        return false;
//    }
//}

package com.example.mail_app.ui.mail.component;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mail_app.R;
import com.example.mail_app.data.entity.FullMail;
import com.example.mail_app.ui.mail.dialog.LabelSelectionDialogFragment;
import com.example.mail_app.viewmodel.MailViewModel;

import java.util.function.Consumer;

public class MailMenu {

    public static void setupMenu(Context context, Menu menu, FullMail mail) {
        menu.clear();

        // מחיקה – תמיד
        menu.add(Menu.NONE, R.id.action_delete, Menu.NONE, context.getString(R.string.action_delete))
                .setIcon(R.drawable.outline_delete_24)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        // תיוג – רק אם לא ספאם
        if (!mail.getMail().isSpam()) {
            menu.add(Menu.NONE, R.id.action_label, Menu.NONE, context.getString(R.string.action_label))
                    .setIcon(R.drawable.outline_label_24)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }

        // דיווח ספאם / ביטול – רק אם זה מייל שקיבלת
        if ("received".equals(mail.getMail().getType())) {
            boolean isSpam = mail.getMail().isSpam();
            menu.add(Menu.NONE, R.id.action_report, Menu.NONE,
                            context.getString(isSpam ? R.string.action_unspam : R.string.action_report))
                    .setIcon(isSpam ? R.drawable.baseline_report_off : R.drawable.outline_report_24)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    public static boolean handleMenuItemClick(
            Context context,
            MenuItem item,
            FullMail mail,
            MailViewModel viewModel,
            Runnable onFinish,
            Consumer<FullMail> onMailUpdated
    ) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_delete) {
            viewModel.deleteMail(mail.getMail().getId());
            if (onFinish != null) onFinish.run();
            return true;

        } else if (itemId == R.id.action_label) {
            LabelSelectionDialogFragment dialog = LabelSelectionDialogFragment.newInstance(mail, updated -> {
                if (onMailUpdated != null) {
                    onMailUpdated.accept(updated);
                }
            });
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "label_dialog");
            return true;

        } else if (itemId == R.id.action_report) {
            boolean newSpamState = !mail.getMail().isSpam();
            viewModel.setSpam(mail.getMail().getId(), newSpamState, () -> {
                viewModel.reloadCurrentCategory();
            });
            if (onFinish != null) onFinish.run();
            return true;
        }

        return false;
    }
}
