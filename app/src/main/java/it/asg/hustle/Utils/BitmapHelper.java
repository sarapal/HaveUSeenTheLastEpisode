package it.asg.hustle.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sara on 9/18/15.
 */
public class BitmapHelper {

    // Stringa che identifica le shared preferences
    public static final String BMP_PREFERENCE = "bitmaps";

    // Sfrutta la classe decodeSampledBitmapFromStream per scaricare una BitMap con le
    // dimensioni passate per parametro
    public static Bitmap downloadBitmapFromURL(String url, int reqWidth, int reqHeight) {
        Bitmap bm = null;
        InputStream in1 = null;
        InputStream in2 = null;
        try {
            in1 = new java.net.URL(url).openStream();
            // TODO: trova un modo alternativo senza riscaricare lo stream
            if (reqWidth != 0 && reqHeight != 0)
                in2 = new java.net.URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapHelper.decodeSampledBitmapFromStream(in1, in2, reqWidth, reqHeight);
    }


    // Dato l'InputStream, decodifica una BitMap con le dimensioni passate come parametro
    // Usa 2 InputStream perché dopo la prima decodeStream, in1 è "consumato"
    public static Bitmap decodeSampledBitmapFromStream(InputStream in1, InputStream in2, int reqWidth, int reqHeight) {

        // Decodifica con inJustDecodeBounds=true per prendere solo le dimensioni
        final BitmapFactory.Options options = new BitmapFactory.Options();

        if (reqWidth != 0 && reqHeight != 0) {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in1, null, options);
            // Calcola il fattore di sampling
            options.inSampleSize = BitmapHelper.calculateInSampleSize(options, reqWidth, reqHeight);
            Log.d("DIMENSIONI", "sampling: " + options.inSampleSize);
        }

        // Crea la BitMap con il valore di sampling
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(in2 != null ? in2 : in1, null, options);
    }

    // Calcola un valore per SampleSize che è una potenza di due in modo che il sampling ottenuto
    // sia maggiore di un'immagine reqWidth X rewHeight
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Altezza e Larghezza dell'immagine
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        // Adatta solo se l'img è più grande del posto in cui deve stare
        if (height > reqHeight || width > reqWidth) {

            // dimezza
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // mantiene width e height maggiori di reqWidth e reqHeight
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

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

    public static String getBase64Bitmap(Context c, String id) {
        return c.getSharedPreferences(BitmapHelper.BMP_PREFERENCE, Context.MODE_PRIVATE).getString(id, null);
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
