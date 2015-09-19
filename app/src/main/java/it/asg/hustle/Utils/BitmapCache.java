package it.asg.hustle.Utils;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by gbyolo on 9/19/15.
 */
public class BitmapCache {

    private static LruCache<String, Bitmap> mMemoryCache;

    public BitmapCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Log.d("HUSTLE", "max memory: " + maxMemory);

        // Use 1/8th of the available memory for this memory cache.
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
        if (getBitmapFromMemCache(key) == null) {
            Log.d("HUSTLE", "metto foto in cache");
            mMemoryCache.put(key, bitmap);
        }
    }

    public static synchronized Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
