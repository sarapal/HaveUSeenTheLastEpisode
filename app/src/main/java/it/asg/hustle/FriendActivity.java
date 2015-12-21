package it.asg.hustle;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Locale;

import it.asg.hustle.Info.Episode;
import it.asg.hustle.Info.Show;
import it.asg.hustle.Utils.CheckConnection;

public class FriendActivity extends AppCompatActivity {

    private com.facebook.login.widget.ProfilePictureView profilePicture = null;
    private de.hdodenhof.circleimageview.CircleImageView circleImageView = null;

    // Crea un array di gridAdapter
    GridAdapter gridAdapter;
    // RecyclerView
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeView;
    // Spinner
    ProgressBar spinner;

    String id;
    String name;
    int numOfElements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        spinner = (ProgressBar) findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        numOfElements = getDisplayDimensions(getWindowManager().getDefaultDisplay());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext() , numOfElements);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Crea un nuovo gridAdapter
        gridAdapter = new GridAdapter(getApplicationContext());
        // Imposta l'adapter sulla View
        recyclerView.setAdapter(gridAdapter);

        Bundle b = getIntent().getExtras();
        id = b.getString("id");
        name = b.getString("name");
        gridAdapter.user_id = id;

        profilePicture = (com.facebook.login.widget.ProfilePictureView) findViewById(R.id.profilePicture);
        profilePicture.setProfileId(id);

        TextView friend_name = (TextView) findViewById(R.id.friend_name);
        friend_name.setText(name);


        if (CheckConnection.isConnected(getApplicationContext())) {
            Log.d("HUSTLE", "onCreate, connesso a internet, scarico le serie");
            downloadMySeries(gridAdapter, id);
        }
    }

    private void showSeries(String new_series, GridAdapter gridAdapter) {
        JSONArray jsonArraySeries;
        try {
            jsonArraySeries = new JSONArray(new_series);
        } catch (JSONException e) {
            jsonArraySeries = null;
            e.printStackTrace();
        }
        if (jsonArraySeries == null)
            return;

        gridAdapter.reset();
        int len = jsonArraySeries.length();

        for(int i=0;i<len;i++) {
            try {
                GridItem g = new GridItem();
                JSONObject jo = jsonArraySeries.getJSONObject(i);
                Show s = new Show(jo);

                g.setShow(s);
                g.setName(s.title);
                g.setThumbnail(s.bmp);

                gridAdapter.mItems.add(g);
                gridAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void downloadMySeries(final GridAdapter gridAdapter, final String id) {
        // Definisce un AsyncTask che scaricherà le serie viste dall'utente
        AsyncTask<String,Void,String> at = new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {
                URL url = null;
                String s = null;
                try {
                    Uri builtUri = Uri.parse("http://hustle.altervista.org/getSeries.php?").
                            buildUpon().
                            appendQueryParameter("user_id", id).
                            appendQueryParameter("language", Locale.getDefault().getLanguage()).
                            build();
                    String u = builtUri.toString();
                    Log.d("HUSTLE", "requesting: " + u);
                    url = new URL(u);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    s = br.readLine();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                //Log.d("HUSTLE", "returned: " + s);

                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s!=null) {
                    showSeries(s, gridAdapter);
                }
                spinner.setVisibility(View.GONE);
            }
        };
        // Se l'id è diverso da null e l'utente è connesso a internet, esegue l'AsyncTask
        if (id != null && CheckConnection.isConnected(getApplicationContext())) {
            at.execute(id);
        }
    }


    public int getDisplayDimensions(Display d){
        Point size = new Point();
        d.getSize(size);
        int widthPX = size.x;
        int widthDP = pxToDp(widthPX);


        int wPX = (int) getResources().getDimension(R.dimen.grid_item_RelativeLayout_width);
        int wDP = pxToDp(wPX);
        int num = (int) Math.floor(widthPX/wPX);

        return num;
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        int dp = (int) ((px/displayMetrics.density)+0.5);
        return dp;
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
                    Log.d("SEASON", "requesting: " + u);
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
                //Log.d("HUSTLEPROGRESS", "SeasonTot:" +numberOfSeasons+ ";SeasonNumber:"+actualSeason+";EpisodeNumber:"+actualEpisodeNumber+" of "+actualSeasonNumberEpisodes+" episodes");


                return ""+((10000/numberOfSeasons)*(actualSeason-1) + (10000/numberOfSeasons/actualSeasonNumberEpisodes)*actualEpisodeNumber);
            }

            @Override
            protected void onPostExecute(String n) {
                super.onPostExecute(n);
                if (n != null){
                    griditem.setProgress(Integer.parseInt(n));
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setMax(10000);
                    progressBar.setProgress(Integer.parseInt(n));
                    //Log.d("HUSTLEprogress", "progresso di "+showProgress.title+": "+Integer.parseInt(n) + " di 10000");
                }
            }
        };
        if (CheckConnection.isConnected(context)) {
            progress_asynctask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

}
