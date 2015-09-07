package it.asg.hustle;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    String tvShowTitle;
    RecyclerView rw;
    SearchShowRecyclerAdapter adapter;
    ArrayList<Show> shows;
    android.widget.SearchView searchv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("TV Show (Title)");

        shows = new ArrayList<Show>();

        searchv = (android.widget.SearchView) findViewById(R.id.searchView);
        rw = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new SearchShowRecyclerAdapter(shows, this);
        rw.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        rw.setAdapter(adapter);
        searchv.setSubmitButtonEnabled(true);
        searchv.setIconifiedByDefault(false);
        searchv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doSearch(searchv.getQuery().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        Bundle b = getIntent().getExtras();

        if (b != null) {
            tvShowTitle = b.getString("SearchTitle");
            searchv.setQuery(tvShowTitle, true);
        }
        Log.d("HUSTLE", "SearchActivity onCreate() completed");

    }

    private void doSearch(final String tvShowTitle) {
        // Ogni volta che viene effettuata una nuova ricerca
        // resetta l'ArrayList
        shows = new ArrayList<Show>();
        adapter = new SearchShowRecyclerAdapter(shows, this);
        rw.setAdapter(adapter);

        Log.d("HUSTLE", "Searching for serie: " + tvShowTitle);
        final ProgressDialog progDailog = new ProgressDialog(SearchActivity.this);

        // AsyncTask per prendere info limitate (id, nome, lingua) su una Serie TV in base
        // al nome. Potrebbe ritornare più elementi in un JSONArray
        AsyncTask<Void, Void, String> at = new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progDailog.setMessage("Loading...");
                progDailog.setIndeterminate(false);
                progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDailog.setCancelable(true);
                progDailog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                URL url = null;
                String s = null;
                // Prende la lingua del sistema
                String lan = Locale.getDefault().getLanguage();
                try {
                    url = new URL("http://hustle.altervista.org/getSeries.php?seriesname=" + tvShowTitle + "&language="+lan+"&full");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    s = br.readLine();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s == null)
                    return;
                Log.d("HUSTLE", s);
                JSONArray ja = null;
                try {
                    ja = new JSONArray(s);
                    ja.length();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i< (ja != null ? ja.length() : 0); i++) {
                    try {
                        JSONObject jo = ja.getJSONObject(i);
                        Show s1 = new Show(jo);
                        shows.add(s1);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                progDailog.dismiss();

                //Log.d("HUSTLE", ja.toString());
            }
        };

       at.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

