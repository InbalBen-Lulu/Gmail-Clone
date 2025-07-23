package com.example.mail_app.ui.view;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mail_app.R;
import com.google.android.material.imageview.ShapeableImageView;

/**
 * Custom view to display a circular user avatar with an optional loading spinner overlay.
 * Supports setting the image from a URL, drawable resource, or URI.
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
    }

    /**
     * Checks if there is an active internet connection.
     */
    private boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    /**
     * Sets the profile image from a drawable resource.
     */
    public void setImageRes(int resId) {
        imageView.setImageResource(resId);
    }

    /**
     * Sets the profile image using a full or relative image URL.
     * If the path starts with "/profilePics", it is resolved using the base server URL.
     */
    public void setImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            imageView.setImageResource(R.drawable.default_avatar);
            return;
        }

        String fullUrl;
        if (imagePath.startsWith("/profilePics")) {
            String baseUrl = getContext().getString(R.string.BaseUrl);
            fullUrl = baseUrl.replaceAll("/api/?$", "") + imagePath;
        } else {
            fullUrl = imagePath;
        }

        boolean isConnected = hasInternetConnection();

        Glide.with(getContext())
                .load(fullUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(isConnected)
                .error(R.drawable.default_avatar)
                .into(imageView);
    }

    /**
     * Shows or hides the loading spinner.
     */
    public void setLoading(boolean isLoading) {
        loadingSpinner.setVisibility(isLoading ? VISIBLE : GONE);
    }
}
