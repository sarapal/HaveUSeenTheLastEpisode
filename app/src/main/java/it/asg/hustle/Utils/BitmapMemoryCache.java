package it.asg.hustle.Utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by gbyolo on 9/19/15.
 */
public class BitmapMemoryCache {

    private static LruCache<String, Bitmap> mMemoryCache;

    public BitmapMemoryCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Log.d("HUSTLE", "max memory: " + maxMemory);

        final int cacheSize = maxMemory / 4;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static synchronized void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (key == null || bitmap == null)
            return;
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static synchronized Bitmap getBitmapFromMemCache(String key) {
        if (key == null)
            return null;
        return mMemoryCache.get(key);
    }
}
