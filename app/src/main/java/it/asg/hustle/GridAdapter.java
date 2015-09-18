package it.asg.hustle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.asg.hustle.Info.Show;
import it.asg.hustle.Utils.BitmapHelper;

/**
 * Created by sara on 17/09/2015.
 */
public class GridAdapter  extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    List<GridItem> mItems;
    Context ctx;

    public GridAdapter(Context ctx) {
        super();
        mItems = new ArrayList<GridItem>();
        this.ctx = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final GridItem item = mItems.get(i);
        viewHolder.title.setText(item.getName());
        //viewHolder.imgThumbnail.setImageBitmap(item.getThumbnail());

        // AsyncTask per scaricare l'immagine dell'episodio
        AsyncTask<String, Void, Bitmap> at = new AsyncTask<String, Void, Bitmap>() {

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
                // Salva la bitmap nelle shared preferences
                //BitmapHelper.saveToPreferences(ctx, bm, item.getShow().id + "_poster");
                return bm;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                // Imposta l'immagine nella ImageView
                viewHolder.thumbnail.setImageBitmap(bitmap);
                // salva l'oggetto BitMap nell'oggetto Episode
                item.setThumbnail(bitmap);
            }
        };

        // Se l'immagine dell'episodio non è stata scaricata
        if (item.getThumbnail() == null) {
            // La scarica su un thread separato, ma solo se l'URL è diverso
            // da quello qui sotto (che significa che l'episodio non ha banner)
            if (item.getShow().poster == null) {
                return;
            }
            //Bitmap b = BitmapHelper.getFromPreferences(ctx,item.getShow().id+"_poster");
            //if (b != null) {
            //    Log.d("HUSTLE", "Prendo poster dalle preferenze");
            //    item.setThumbnail(b);
            //    viewHolder.thumbnail.setImageBitmap(b);
            //} else {
                if (!item.getShow().poster.equals("http://thetvdb.com/banners/")) {
                    Log.d("HUSTLE", "Downloading image: " + item.getShow().poster);
                    at.execute(item.getShow().poster);
                }
            //}
        } else {
            // Se l'immagine dell'episodio è già stata salvata, riusa quella
            viewHolder.thumbnail.setImageBitmap(item.getThumbnail());
        }

        viewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ShowActivity.class);
                intent.putExtra("show", item.getShow().source.toString());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView thumbnail;
        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
            title = (TextView)itemView.findViewById(R.id.title);
        }
    }
}