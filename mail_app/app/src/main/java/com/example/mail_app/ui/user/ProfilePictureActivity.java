package com.example.mail_app.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.mail_app.R;
import com.example.mail_app.ui.view.UserAvatarView;
import com.example.mail_app.utils.ImageUtils;
import com.example.mail_app.viewmodel.LoggedInUserViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for changing the user's profile picture.
 * Allows uploading a new image or removing the existing one.
 */
public class ProfilePictureActivity extends AppCompatActivity {

    private UserAvatarView avatarView;
    private Button addButton, changeButton, removeButton;
    private LoggedInUserViewModel userViewModel;

    // Launcher to pick an image from the gallery
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();

                    avatarView.setLoading(true); // Spinner start

                    try {
                        long sizeInBytes = getContentResolver()
                                .openAssetFileDescriptor(imageUri, "r")
                                .getLength();
                        if (sizeInBytes > 5 * 1024 * 1024) {
                            showError("Selected image is too large (max 5MB)");
                            avatarView.setLoading(false);
                            return;
                        }
                    } catch (Exception e) {
                        showError("Unable to check image size");
                        avatarView.setLoading(false);
                        return;
                    }

                    // Run Base64 conversion and upload on background thread
                    new Thread(() -> {
                        try {
                            String base64Image = ImageUtils.resizeAndConvertToBase64(this, imageUri);
                            runOnUiThread(() -> {
                                userViewModel.uploadProfileImage(base64Image, new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        avatarView.setLoading(false);
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        avatarView.setLoading(false);
                                        showError("Failed to upload image");
                                    }
                                });
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                showError("Failed to process image");
                                avatarView.setLoading(false);
                            });
                        }
                    }).start();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        avatarView = findViewById(R.id.avatar_view);
        addButton = findViewById(R.id.add_picture_button);
        changeButton = findViewById(R.id.change_picture_button);
        removeButton = findViewById(R.id.remove_picture_button);
        ImageButton backButton = findViewById(R.id.back_button);

        userViewModel = new ViewModelProvider(this).get(LoggedInUserViewModel.class);

        // Observe user to update UI state
        userViewModel.getUser().observe(this, user -> {
            if (user == null) return;
            if (user.getProfileImage() != null)
                avatarView.setImageUrl(user.getProfileImage());

            boolean hasImage = user.hasCustomImage();
            addButton.setVisibility(hasImage ? View.GONE : View.VISIBLE);
            changeButton.setVisibility(hasImage ? View.VISIBLE : View.GONE);
            removeButton.setVisibility(hasImage ? View.VISIBLE : View.GONE);
        });

        backButton.setOnClickListener(v -> finish());
        addButton.setOnClickListener(v -> pickImage());
        changeButton.setOnClickListener(v -> pickImage());
        removeButton.setOnClickListener(v -> {
            avatarView.setLoading(true);
            userViewModel.deleteProfileImage(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    avatarView.setLoading(false);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    avatarView.setLoading(false);
                    showError("Failed to delete image");
                }
            });
        });
    }

    /**
     * Opens the image picker to select a new profile photo.
     */
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    /**
     * Displays an error message via Toast.
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
