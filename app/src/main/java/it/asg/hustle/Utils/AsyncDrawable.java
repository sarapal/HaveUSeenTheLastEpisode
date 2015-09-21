package it.asg.hustle.Utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import java.lang.ref.WeakReference;

/**
 * Created by gbyolo on 9/19/15.
 * Si estende la classe Drawable in modo che ogni ImageView abbia un reference
 * al suo AsyncTask;
 */

class AsyncDrawable extends ColorDrawable {

    private final WeakReference<BitmapDownloader> bitmapDownloaderReference;

    public AsyncDrawable(BitmapDownloader bitmapDownloader) {
        super(Color.RED);
        bitmapDownloaderReference = new WeakReference<BitmapDownloader>(bitmapDownloader);
    }

    public BitmapDownloader getBitmapDownloader() {
        return bitmapDownloaderReference.get();
    }
}