package it.asg.hustle.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by sara on 9/18/15.
 */
public class BitmapHelper {

    public static final String BMP_PREFERENCE = "bitmaps";

    // Codifica la bitmap in base64
    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    // decodifica una stringa base64 in una bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    // Salva la bitmap nelle preferenze
    public static void saveToPreferences(Context c, Bitmap bmp, String id) {
        SharedPreferences o = c.getSharedPreferences(BitmapHelper.BMP_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = o.edit();
        editor.putString(id, BitmapHelper.encodeToBase64(bmp));
        editor.commit();
    }

    // Prende la bitmap dalle preferenze
    public static Bitmap getFromPreferences(Context c, String id) {
        String base64_bmp = c.getSharedPreferences(BitmapHelper.BMP_PREFERENCE, Context.MODE_PRIVATE).getString(id, null);
        if (base64_bmp == null) {
            return null;
        }
        return BitmapHelper.decodeBase64(base64_bmp);
    }
}
