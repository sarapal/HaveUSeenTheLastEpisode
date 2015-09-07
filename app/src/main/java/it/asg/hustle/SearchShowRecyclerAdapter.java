package it.asg.hustle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import it.asg.hustle.Info.Show;

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
        Log.d("HUSTLE", "AGGIUNGO ALLA LISTA: "+ item.toString());
        Log.d("HUSTLE", "La lingua è " + item.language);
        viewHolder.mTextView.setText(item.title + " (" + item.language + ")");
        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent(context, ShowActivity.class);
                i.putExtra("show", item.source.toString());
                context.startActivity(i);
            }
        });
        viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent(context,ShowActivity.class);
                i.putExtra("show", item.source.toString());
                context.startActivity(i);
            }
        });

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
                progressDialog.dismiss();
            }
        };

        // Se l'immagine della serie non è stata scaricata
        if (item.bmp == null) {
            Log.d("HUSTLE", "Downloading image: " + item.banner);
            // La scarica su un thread separato, ma solo se l'URL è diverso
            // da quello qui sotto (che significa che la serie tv non ha banner)
            if (item.banner == null) {
                return;
            }
            if (!item.banner.equals("http://thetvdb.com/banners/"))
                at.execute(item.banner);
        } else {
            // Se l'immagine della serie è già stata salvata, riusa quella
            viewHolder.mImageView.setImageBitmap(item.bmp);
        }

    }

    @Override
    public int getItemCount() {
        return shows.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;
        private final ImageView mImageView;

        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.title);
            mImageView = (ImageView) v.findViewById(R.id.poster);
        }

    }
}