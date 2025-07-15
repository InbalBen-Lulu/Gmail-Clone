package com.example.mail_app.ui.user;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.example.mail_app.R;
import com.example.mail_app.ui.view.UserAvatarView;
import com.example.mail_app.utils.AppConstants;
import com.example.mail_app.utils.ImageUtils;
import com.example.mail_app.viewmodel.LoggedInUserViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for updating the user's profile picture.
 * Allows picking an image from gallery or camera, removing the current image,
 * and uploading a processed Base64 image to the server.
 */
public class ProfilePictureActivity extends AppCompatActivity {

    private UserAvatarView avatarView;
    private Button addButton, changeButton, removeButton;
    private LoggedInUserViewModel userViewModel;

    /** Temporary URI used to store captured image from camera before upload. */
    private Uri tempCameraImageUri;

    /**
     * Activity result launcher handling image picking result from gallery or camera,
     * including image size check, Base64 conversion, and upload.
     */
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri imageUri = result.getData() != null ? result.getData().getData() : tempCameraImageUri;

                    if (imageUri == null) {
                        showMessage(getString(R.string.failed_to_get_image));
                        return;
                    }

                    avatarView.setLoading(true); // Show spinner

                    try (AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(imageUri, "r")) {
                        if (afd == null) {
                            showMessage(getString(R.string.failed_to_check_image_size));
                            avatarView.setLoading(false);
                            return;
                        }
                        long sizeInBytes = afd.getLength();
                        if (sizeInBytes > AppConstants.MAX_IMAGE_SIZE_BYTES) {
                            showMessage(getString(R.string.image_too_large));
                            avatarView.setLoading(false);
                            return;
                        }
                    } catch (Exception e) {
                        showMessage(getString(R.string.unable_to_check_image_size));
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
                                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                        avatarView.setLoading(false);
                                        showMessage(getString(R.string.profile_picture_updated));
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                        avatarView.setLoading(false);
                                        showMessage(getString(R.string.failed_to_upload_image));
                                    }
                                });
                            });
                        } catch (IllegalArgumentException e) {
                            runOnUiThread(() -> {
                                avatarView.setLoading(false);
                                showMessage(getString(R.string.unsupported_image_type));
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                avatarView.setLoading(false);
                                showMessage(getString(R.string.failed_to_process_image));
                            });
                        }
                    }).start();
                }
            });

    /**
     * Initializes the activity, binds views, sets up listeners,
     * and observes user profile data.
     */
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

        // Observe user to update UI state (avatar image and button visibility)
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
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    avatarView.setLoading(false);
                    showMessage(getString(R.string.profile_picture_updated));
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    avatarView.setLoading(false);
                    showMessage(getString(R.string.failed_to_delete_image));
                }
            });
        });
    }

    /**
     * Opens an intent chooser to select a new profile photo from gallery or camera.
     */
    private void pickImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType(AppConstants.INTENT_TYPE_IMAGE);
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, AppConstants.ALLOWED_MIME_TYPES);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tempCameraImageUri = createTempImageUri();
        if (tempCameraImageUri == null) {
            showMessage(getString(R.string.failed_to_create_temp_file));
            return;
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempCameraImageUri);

        Intent chooser;
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            chooser = Intent.createChooser(galleryIntent, getString(R.string.choose_image));
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        } else {
            chooser = galleryIntent;
            showMessage(getString(R.string.camera_not_available));
        }

        pickImageLauncher.launch(chooser);
    }

    /**
     * Creates a temporary file URI to store the camera image.
     *
     * @return URI for the temporary image file.
     */
    private Uri createTempImageUri() {
        try {
            File tempFile = File.createTempFile("profile_image_", ".jpg", getCacheDir());
            return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", tempFile);
        } catch (IOException e) {
            Log.e("ProfilePictureActivity", "Failed to create temp image file", e);
            showMessage(getString(R.string.failed_to_create_temp_file));
            return null;
        }
    }

    /**
     * Displays a Snackbar message at the bottom of the screen.
     *
     * @param message The message to display.
     */
    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
