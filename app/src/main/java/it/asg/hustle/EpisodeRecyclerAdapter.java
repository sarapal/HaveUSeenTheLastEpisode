package it.asg.hustle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import it.asg.hustle.Info.Episode;
import it.asg.hustle.Info.Season;

/**
 * Created by sara on 8/26/15.
 */
public class EpisodeRecyclerAdapter extends RecyclerView.Adapter<EpisodeRecyclerAdapter.ViewHolder> {

    //private List<String> mItems;
    private Bitmap posterBitmap = null;
    private ImageView posterImageView;
    private ArrayList<Episode> episodes;


    //EpisodeRecyclerAdapter(List<String> items) {
    //    mItems = items;
    //}

    EpisodeRecyclerAdapter(Season season) {
        if (season.episodesList != null) {
            episodes = season.episodesList;
        }
        else{
            episodes = new ArrayList<Episode>();
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Bitmap b = episodes.get(i).bmp;
        String item = episodes.get(i).title;

        viewHolder.mTextView.setText((i+1)+": "+item);

        final Episode ep = episodes.get(i);
        String imgURL = ep.bmpPath;
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
                return bm;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                viewHolder.epImg.setImageBitmap(bitmap);
                ep.bmp = bitmap;
                viewHolder.mTextView.setHeight(ep.bmp.getHeight());
                viewHolder.cb.setHeight(ep.bmp.getHeight());

            }
        };

        // Se l'immagine dell'episodio non è stata scaricata
        if (ep.bmp == null) {
            Log.d("HUSTLE", "Downloading image: " + ep.bmpPath);
            // La scarica su un thread separato, ma solo se l'URL è diverso
            // da quello qui sotto (che significa che la serie tv non ha banner)
            if (ep.bmpPath == null) {
                return;
            }
            if (!ep.bmpPath.equals("http://thetvdb.com/banners/"))
                at.execute(ep.bmpPath);
        } else {
            // Se l'immagine della serie è già stata salvata, riusa quella
            viewHolder.mTextView.setHeight(ep.bmp.getHeight());
            viewHolder.epImg.setImageBitmap(ep.bmp);
            viewHolder.cb.setHeight(ep.bmp.getHeight());
        }
        // mette check o uncheck per l'episodio a seconda se nell'EpisodeActivity è stato o no premuto il FAB
        viewHolder.cb.setChecked(ep.checked);

        viewHolder.epImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context= v.getContext();
                Intent i = new Intent(context,EpisodeActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("picture", ep.bmp);
                b.putString("episode", ep.source.toString());
                i.putExtras(b);
                context.startActivity(i);
            }
        });
        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context= v.getContext();
                Intent i = new Intent(context,EpisodeActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("picture", ep.bmp);
                b.putString("episode", ep.source.toString());
                i.putExtras(b);
                context.startActivity(i);
            }
        });

        // TODO checkbox episodi
        viewHolder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HUSTLE","check (In EpisodeRecyclerAdapter)");
                // TODO  invia dati al db al check

                // TODO salva stato del checkbox
                ep.checked = !ep.checked;
                Log.d("HUSTLE","status checkbox (EpisodeRecyclerAdapter): "+ep.checked.toString());


            }
        });
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;
        private final ImageView epImg;
        private final CheckBox cb;


        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.list_item);
            epImg = (ImageView) v.findViewById(R.id.ep_img);
            cb= (CheckBox) v.findViewById(R.id.checkEp);
        }

    }
}

