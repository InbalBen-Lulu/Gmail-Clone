package com.example.mail_app.ui.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.mail_app.R;
import com.google.android.material.imageview.ShapeableImageView;

/**
 * Custom avatar view that displays a circular profile image
 * with optional loading spinner overlay.
 */
public class UserAvatarView extends FrameLayout {

    private ShapeableImageView imageView;
    private ProgressBar loadingSpinner;

    public UserAvatarView(Context context) {
        super(context);
        init(context);
    }

    public UserAvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UserAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_user_avatar, this);
        imageView = findViewById(R.id.avatar_image);
        loadingSpinner = findViewById(R.id.avatar_spinner);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setClipToOutline(true);
        imageView.setBackgroundResource(R.drawable.circle_background);
    }

    /**
     * Sets the profile image using a URI.
     */
    public void setImageUri(Uri uri) {
        imageView.setImageURI(uri);
    }

    /**
     * Sets the profile image from a drawable resource.
     */
    public void setImageRes(int resId) {
        imageView.setImageResource(resId);
    }

    /**
     * Shows or hides the loading spinner.
     */
    public void setLoading(boolean isLoading) {
        loadingSpinner.setVisibility(isLoading ? VISIBLE :GONE);
    }
}