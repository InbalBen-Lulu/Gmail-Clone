package com.example.mail_app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Image processing utility: crop, resize, and convert image to Base64 string.
 */
public final class ImageUtils {

    /** Allowed MIME types for image uploads. */
    public static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(
            Arrays.asList("image/jpeg", "image/png", "image/webp")
    );

    private ImageUtils() {
        /* Utility class – no instances. */
    }

    /**
     * Crops, resizes, and converts an image to Base64 PNG string for upload.
     */
    public static String resizeAndConvertToBase64(Context context, Uri uri) throws Exception {
        // 1 – MIME-type validation
        String mime = context.getContentResolver().getType(uri);
        if (!ALLOWED_IMAGE_TYPES.contains(mime)) {
            throw new IllegalArgumentException("Unsupported image type: " + mime);
        }

        // 2 – Decode the full bitmap
        Bitmap original;
        try (InputStream in = context.getContentResolver().openInputStream(uri)) {
            if (in == null) throw new IllegalArgumentException("Unable to open " + uri);
            original = BitmapFactory.decodeStream(in);
        }
        if (original == null) {
            throw new IllegalArgumentException("Unable to decode image from " + uri);
        }

        // 3 – Center-crop to square
        int side    = Math.min(original.getWidth(), original.getHeight());
        int xOffset = (original.getWidth()  - side) / 2;
        int yOffset = (original.getHeight() - side) / 2;
        Bitmap square = Bitmap.createBitmap(original, xOffset, yOffset, side, side);
        original.recycle(); // free native memory

        // 4 – Resize to 256 × 256
        Bitmap resized = Bitmap.createScaledBitmap(square, 256, 256, true);
        square.recycle();

        // 5 – Compress to PNG & encode Base64 (NO_WRAP ⇒ no line-breaks)
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.PNG, /* quality */ 100, bos);
        resized.recycle();

        String base64 = Base64.encodeToString(bos.toByteArray(), Base64.NO_WRAP);
        return "data:image/png;base64," + base64;
    }
}
