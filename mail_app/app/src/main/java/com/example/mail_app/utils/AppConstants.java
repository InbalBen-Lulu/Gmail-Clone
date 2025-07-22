package com.example.mail_app.utils;

/**
 * Global application constants shared across multiple classes.
 */
public final class AppConstants {

    private AppConstants() {
        /* Utility class – no instances. */
    }

    /** Intent extra: show sign-out Snackbar message on LoginActivity. */
    public static final String EXTRA_SHOW_SIGN_OUT_MESSAGE = "show_sign_out_message";

    public static final String[] ALLOWED_MIME_TYPES = {"image/jpeg", "image/png", "image/webp"};

    public static final String INTENT_TYPE_IMAGE = "image/*";

    public static final long MAX_IMAGE_SIZE_BYTES = 5L * 1024 * 1024; // 5 MB

    public static final String DATE_FORMAT_DISPLAY = "dd MMM yyyy";

    public static final int SEARCH_DEBOUNCE_DELAY_MS = 300;

    public static final float SIDEBAR_WIDTH_RATIO = 0.75f;

    public static final int DEFAULT_PAGE_SIZE = 50;

    public static final int DEFAULT_SEARCH_SIZE = 5;

    public static final String EXTRA_ORIGIN = "origin";
    public static final String ORIGIN_MAIL = "mail";
    public static final String ORIGIN_PERSONAL_INFO = "personal_info";

    public static final int DEFAULT_PAGE_OFFSET = 0;

    public static final String TAG_PROFILE_DIALOG = "ProfileDialog";

}
