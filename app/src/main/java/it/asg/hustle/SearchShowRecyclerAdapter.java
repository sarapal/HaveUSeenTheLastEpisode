package it.asg.hustle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import it.asg.hustle.Info.Show;
import it.asg.hustle.Utils.BitmapHelper;
import it.asg.hustle.Utils.ImageDownloader;

/**
 * Created by sara on 8/26/15.
 */
public class SearchShowRecyclerAdapter extends RecyclerView.Adapter<SearchShowRecyclerAdapter.ViewHolder> {

    private ArrayList<Show> shows;
    Context c = null;
    // Passo anche il context così posso creare il ProgressDialog
    SearchShowRecyclerAdapter(ArrayList<Show> shows, Context c) {
        this.shows = shows;
        this.c = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_show_row, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Show item = shows.get(i);
        final int width = getDisplayWidthPX();

        int maxImagePX = (2*width)/3;

        viewHolder.mImageView.setMaxWidth((2 * width) / 3);

        viewHolder.mTextView.setWidth(width / 3);
        viewHolder.mTextView.setText(item.title + " (" + item.language + ")");

        //Log.d("HUSTLE", "AGGIUNGO ALLA LISTA: "+ item.toString());
        //Log.d("HUSTLE", "La lingua è " + item.language);

        // onClick sulla cardview
        viewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent(context, ShowActivity.class);
                i.putExtra("show", item.source.toString());
                context.startActivity(i);
            }
        });
/*
        final ProgressDialog progressDialog = new ProgressDialog(c);
        final String msg_loading = c.getResources().getString(R.string.loading_image);

        AsyncTask<String, Void, Bitmap> at = new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setMessage(msg_loading);
                progressDialog.setIndeterminate(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(true);
                progressDialog.show();
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bm = null;
                InputStream in = null;
                try {
                    in = new java.net.URL(params[0]).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bm = BitmapFactory.decodeStream(in);
                return bm;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                viewHolder.mImageView.setImageBitmap(bitmap);
                item.bmp = bitmap;
                viewHolder.mTextView.setText(item.title + " (" + item.language + ")");
                progressDialog.dismiss();
            }
        };*/
        /*
        // Se l'immagine della serie non è stata scaricata
        if (item.bmp == null) {
            // La scarica su un thread separato, ma solo se l'URL è diverso
            // da quello qui sotto (che significa che la serie tv non ha banner)
            if (item.banner == null) {
                return;
            }
            // Se l'url è come questo qui sotto, signfica che la serie non ha banner
            if (!item.banner.equals("http://thetvdb.com/banners/")) {
                at.execute(item.banner);
            }
        } else {
            // Se l'immagine della serie è già stata salvata, riusa quella
            viewHolder.mImageView.setImageBitmap(item.bmp);
        }*/
        if (!item.banner.equals("http://thetvdb.com/banners/")) {
            new ImageDownloader(c,maxImagePX,maxImagePX).download(item.banner, viewHolder.mImageView, item);
        }


    }

    public int getDisplayWidthPX(){
        //prende la larghezza in pixel dello schermo
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widthPX = size.x;

        return widthPX;
    }

    @Override
    public int getItemCount() {
        return shows.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;
        private final ImageView mImageView;
        private final CardView mCardView;

        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.title);
            mImageView = (ImageView) v.findViewById(R.id.poster);
            mCardView = (CardView) v.findViewById(R.id.cardview);

        }

    }
}