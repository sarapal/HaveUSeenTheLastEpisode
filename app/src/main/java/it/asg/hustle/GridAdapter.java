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
import android.widget.ProgressBar;
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
import java.util.Locale;

import it.asg.hustle.Info.Episode;
import it.asg.hustle.Info.Show;
import it.asg.hustle.Utils.BitmapHelper;
import it.asg.hustle.Utils.CheckConnection;
import it.asg.hustle.Utils.ImageDownloader;

/**
 * Created by sara on 17/09/2015.
 */
public class GridAdapter  extends RecyclerView.Adapter<GridAdapter.ViewHolder> {
    ArrayList<GridItem> mItems;
    Context ctx;
    public String user_id_adapter=null;
    public String name_id="";

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
        viewHolder.progressBar.setVisibility(View.INVISIBLE);
        viewHolder.progressBar.setMax(10000);
        return viewHolder;
    }

    public void reset() {
        this.mItems.clear();
        this.mItems = new ArrayList<GridItem>();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final GridItem item = mItems.get(i);
        //viewHolder.title.setText(item.getName());
        viewHolder.progressBar.setMax(10000);
        if(item.getProgress() != -1){
            viewHolder.progressBar.setProgress(item.getProgress());
            viewHolder.progressBar.setVisibility(View.VISIBLE);
        }
        if(this.user_id_adapter!=null) {
            viewHolder.progressBar.setVisibility(View.INVISIBLE);

            doGetProgress(this.ctx, item.getShow().id, this.user_id_adapter + "", item.getShow(), viewHolder.progressBar, item);



        }

        if(mItems.get(i).getFriends()>0) {
            viewHolder.friendIndicator.setVisibility(View.VISIBLE);
            viewHolder.friendIndicator.setText(mItems.get(i).getFriends() + "");
        }
        else{
            viewHolder.friendIndicator.setVisibility(View.INVISIBLE);
        }
        final int reqWidth = (int) ctx.getResources().getDimension(R.dimen.grid_item_ImageView_width);
        final int reqHeight = (int) ctx.getResources().getDimension(R.dimen.grid_item_ImageView_height);

        if (item.getThumbnail() != null) {
            Log.d("HUSTLE", "La bitmap già c'è");
            viewHolder.thumbnail.setImageBitmap(item.getThumbnail());
        } else {
            if (item.getShow().poster != null) {
                if (!item.getShow().poster.equals("http://thetvdb.com/banners/")) {
                    new ImageDownloader(ctx, reqWidth, reqHeight).download(item.getShow().poster, viewHolder.thumbnail, item);
                }
            } else {
                Log.d("NOPOSTER", "Lo show " + item.getShow().title + " non ha poster: " + item.getShow());
            }
        }
        viewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ShowActivity.class);
                intent.putExtra("show", item.getShow().toJSON().toString());
                intent.putExtra("idProgress",user_id_adapter);
                Log.d("HUSTLE", "devo far nome: " + name_id);
                intent.putExtra("nameProgress",name_id);
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
        public TextView friendIndicator;
        public ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
            title = (TextView)itemView.findViewById(R.id.title);
            friendIndicator = (TextView) itemView.findViewById(R.id.friend_indicator);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar_show);
        }
    }

    private static void doGetProgress(final Context context,final String series_id, final String user_id, final Show showProgress,final ProgressBar progressBar,final GridItem griditem){

        AsyncTask<String, Void, String> progress_asynctask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                Episode lastEpisode;
                JSONObject lastEpisodeJSON = null;
                String s = null;

                //richiesta dati episodi
                try {
                    Uri builtUri = Uri.parse("http://hustle.altervista.org/getSeries.php?").
                            buildUpon().
                            appendQueryParameter("progress", "true").
                            appendQueryParameter("seriesid", series_id).
                            appendQueryParameter("user_id", user_id).
                            appendQueryParameter("language", Locale.getDefault().getLanguage()).
                            build();
                    String u = builtUri.toString();
                    Log.d("HUSTLE", "requesting: " + u);
                    URL url = new URL(u);
                    //URL url = new URL("http://hustle.altervista.org/getEpisodes.php?seriesid=" + series_id + "&season=all&user_id=" + friend_id + "&short=true");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    s = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }


                //creazione array dalla risposta
                try {
                    lastEpisodeJSON = new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }

                if (lastEpisodeJSON == null)
                    return null;

                lastEpisode = new Episode(lastEpisodeJSON);
                int numberOfSeasons = showProgress.seasonNumber;
                int actualEpisodeNumber = lastEpisode.episodeNumber;
                int actualSeason  = lastEpisode.season;
                int actualSeasonNumberEpisodes  = lastEpisode.seasonEpisodeNumber;
                if (numberOfSeasons ==0 || actualSeasonNumberEpisodes==0){
                    return null;
                }
                Log.d("HUSTLEPROGRESS", "SeasonTot:" +numberOfSeasons+ ";SeasonNumber:"+actualSeason+";EpisodeNumber:"+actualEpisodeNumber+" of "+actualSeasonNumberEpisodes+" episodes");


                return ""+((10000/numberOfSeasons)*(actualSeason-1) + (10000/numberOfSeasons/actualSeasonNumberEpisodes)*actualEpisodeNumber);
            }

            @Override
            protected void onPostExecute(String n) {
                super.onPostExecute(n);
                if (n != null){
                    griditem.setProgress(Integer.parseInt(n));
                    progressBar.setMax(10000);
                    progressBar.setProgress(Integer.parseInt(n));
                    showProgress.progress = Integer.parseInt(n);
                    progressBar.setVisibility(View.VISIBLE);
                    Log.d("HUSTLEprogress", "progresso di "+showProgress.title+": "+Integer.parseInt(n) + " di 10000");
                }
            }
        };
        if (CheckConnection.isConnected(context)) {
            progress_asynctask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

}