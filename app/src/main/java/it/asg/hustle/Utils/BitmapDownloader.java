package it.asg.hustle.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import it.asg.hustle.Interfaces.ThumbnailViewer;


/**
 * Created by gbyolo on 9/19/15.
 */
class BitmapDownloader extends AsyncTask<String, Void, Bitmap> {
    // The WeakReference to the ImageView ensures that the AsyncTask does not prevent the ImageView and anything it references from being garbage collected
    private final WeakReference<ImageView> imageViewReference;
    private final Context context;
    private String url;
    private int reqWidth;
    private int reqHeight;
    private ThumbnailViewer tv;

    public BitmapDownloader(Context ctx, ImageView imageView, int reqWidth, int reqHeight, ThumbnailViewer tv) {
        // Usa WeakReference per assicurarsi che l'ImageView può essere raccolta dal GarbageCollector
        // (altrimenti, poiché l'AsyncTask la referenzia, non verrebbe mai raccolta)
        imageViewReference = new WeakReference<ImageView>(imageView);
        context = ctx;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
        this.tv = tv;
    }

    public String getUrl() {
        return this.url;
    }

    // Scarica l'immagine e la ritorna come BitMap
    @Override
    protected Bitmap doInBackground(String... params) {
        url = params[0];
        //Log.d("HUSTLE", "BITMAPDOWNLOADER IN ESECUZ");
        // Scarica la BitMap e imposta le dimensioni
        return BitmapHelper.downloadBitmapFromURL(url, reqWidth, reqHeight);
    }

    // Quando la BitMap è stata scaricata, vede se il reference all'ImageView
    // è ancora li e gli imposta la BitMap
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        //Log.d("HUSTLE", "BitmapDownloader onPostExecute");
        // Se il task è stato cancellato, non ritornare niente
        if (isCancelled()) {
            //Log.d("HUSTLE", "BitmapDownloader cancellato :(");
            bitmap = null;
        }
        // Se il reference è diverso da null e l'immagine anche
        if (imageViewReference != null && bitmap != null) {
            //Log.d("HUSTLE", "BitmapDownloader Ho una imageView");
            // Prende l'ImageView dal riferimento
            ImageView imageView = imageViewReference.get();
            // Prende l'AsyncTask associato all'ImageView
            BitmapDownloader bitmapDownloader = ImageDownloader.getBitmapDownloader(imageView);
            // Se l'AsyncTask associato all'ImageView è me stesso, allora setta l'immagine
            // altrimenti no
            if (this == bitmapDownloader && imageView != null) {
                //Log.d("HUSTLE", "BitmapDownloader può settare l'img");
                imageView.setImageBitmap(bitmap);
                tv.setThumbnail(bitmap);
            }
            //Log.d("HUSTLE", "Img scaricata ma non settata");
        }
    }
}
