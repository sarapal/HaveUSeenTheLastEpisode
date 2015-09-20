package it.asg.hustle.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by gbyolo on 9/20/15.
 */
public class BitmapCache {
    private static BitmapCache cache = null;
    private static boolean cache_created = false;
    private BitmapMemoryCache memCache = null;
    private BitmapDiskCache diskCache = null;
    private Context context = null;

    public BitmapCache(Context context) {
        Log.d("CACHE", "creo cache");
        this.memCache = new BitmapMemoryCache();
        this.diskCache = new BitmapDiskCache(context);
        this.context = context;
    }

    public static synchronized BitmapCache createOrOpen(Context context) {
        // Si assicura che ci sia solo un'istanza della cache in azione
        if (!cache_created) {
            cache = new BitmapCache(context);
        }
        return cache;
    }

    public static synchronized Bitmap get(String url) {
        if (cache == null)
            return null;
        String key = MD5.hash(url);
        Bitmap bmp = BitmapMemoryCache.getBitmapFromMemCache(key);
        // se non c'è in RAM
        if (bmp == null) {
            // cerca nel disco rigido
            bmp = cache.diskCache.getBitmapFromDiskCache(key);
            // se non c'è nel disco rigido
            if (bmp == null) {
                Log.d("CACHE", "non c'è nè nel disco nè in RAM");
                // ritorna null
                return null;
            }
            // la aggiunge in RAM
            BitmapMemoryCache.addBitmapToMemoryCache(key, bmp);
            // ritorna la bitmap dal disco
            Log.d("CACHE", "ritorna bitmap da disco");
            return bmp;
        }
        // ritorna la bitmap dalla RAM
        Log.d("CACHE", "ritorna bitmap da RAM");
        return bmp;
    }

    public static synchronized void put(String url, Bitmap bitmap) {
        if (cache == null)
            return;
        Log.d("CACHE", "Aggiungo in RAM e in DISCO");
        String key = MD5.hash(url);
        BitmapMemoryCache.addBitmapToMemoryCache(key, bitmap);
        cache.diskCache.addBitmapToCache(key, bitmap);

    }
}
