package com.example.mail_app.ui.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
 * Supports selecting an image from the gallery or capturing a photo using the camera.
 * Also allows removing the current profile picture and uploading a new one to the server.
 */
public class ProfilePictureActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;

    private UserAvatarView avatarView;
    private Button addButton, changeButton, removeButton;
    private LoggedInUserViewModel userViewModel;
    private Uri tempCameraImageUri;

    /**
     * Activity result launcher to handle the image selection result from gallery or camera.
     */
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri imageUri = result.getData() != null ? result.getData().getData() : tempCameraImageUri;

                    if (imageUri == null) {
                        showMessage(getString(R.string.failed_to_get_image));
                        return;
                    }

                    avatarView.setLoading(true);

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
     * Initializes the UI, binds views, and observes user data to update profile picture buttons.
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
        addButton.setOnClickListener(v -> checkCameraPermissionAndPickImage());
        changeButton.setOnClickListener(v -> checkCameraPermissionAndPickImage());
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
     * Checks camera permission. If granted, proceeds to image picking;
     * otherwise, requests the permission.
     */
    private void checkCameraPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            pickImage();
        }
    }

    /**
     * Handles camera permission result. Launches image picker if granted.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(this, "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Opens an intent chooser that allows the user to pick an image from the gallery
     * or capture a photo with the camera.
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
     * Creates a temporary file URI for storing the captured image from the camera.
     *
     * @return a URI to the temporary image file.
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
     * @param message the message to display.
     */
    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
