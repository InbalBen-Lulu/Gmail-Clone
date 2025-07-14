package com.example.mail_app.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.mail_app.R;
import com.example.mail_app.ui.auth.LoginActivity;
import com.example.mail_app.ui.view.UserAvatarView;
import com.example.mail_app.viewmodel.LoggedInUserViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ProfileDialogFragment displays a dialog with user actions such as
 * viewing personal info, changing the profile picture, and signing out.
 * It observes the LoggedInUserViewModel to show user details.
 */
public class ProfileDialogFragment extends DialogFragment {
    private LoggedInUserViewModel userViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Apply custom dialog style with dimming
        setStyle(STYLE_NORMAL, R.style.CustomDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_profile, container, false);

        // Initialize ViewModel to observe user data
        userViewModel = new ViewModelProvider(requireActivity()).get(LoggedInUserViewModel.class);

        // Find views
        ImageButton btnClose = view.findViewById(R.id.btn_close);
        Button btnSignOut = view.findViewById(R.id.btn_sign_out);
        Button btnViewInfo = view.findViewById(R.id.btn_view_info);
        UserAvatarView avatarButton = view.findViewById(R.id.profile_image);
        TextView userName = view.findViewById(R.id.user_name);
        TextView userEmail = view.findViewById(R.id.user_email);

        // Update UI with user data
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                userName.setText(user.getName());
                String emailDomain = getString(R.string.email_domain); // e.g., "@mailme.com"
                userEmail.setText(user.getUserId() + emailDomain);

                if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                    avatarButton.setImageUrl(user.getProfileImage());
                }
            }
        });

        // Set click listeners
        btnClose.setOnClickListener(v -> dismiss());
        btnSignOut.setOnClickListener(v -> signOutUser());
        btnViewInfo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PersonalInfoActivity.class);
            startActivity(intent);
            dismiss();
        });

        avatarButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ProfilePictureActivity.class);
            startActivity(intent);
            dismiss();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Ensure dialog width fills screen, height wraps content
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setLayout(width, height);
        }
    }

    /**
     * Signs out the current user:
     * - Calls ViewModel to log out (clears SharedPreferences, Room, and server session).
     * - Navigates to LoginActivity and clears the back stack.
     */
    private void signOutUser() {
        // Get the LoggedInUserViewModel instance
        LoggedInUserViewModel viewModel = new ViewModelProvider(requireActivity()).get(LoggedInUserViewModel.class);

        // Call logout on the ViewModel to clear local data and notify the server
        viewModel.logout(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                navigateToLogin();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                navigateToLogin();
            }
        });
    }

    /**
     * Navigates to LoginActivity and clears the current activity stack.
     */
    private void navigateToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        dismiss();
    }
}
