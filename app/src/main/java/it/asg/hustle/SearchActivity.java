package it.asg.hustle;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import it.asg.hustle.Info.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    String locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        locale = Locale.getDefault().getLanguage();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

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

    private void hideKeyboard() {
        searchv.clearFocus();
    }

    private JSONArray searchDB(String tvShowTitle) {
        Log.d("HUSTLE", "Ricerca nel DB locale... " + tvShowTitle);
        SQLiteDatabase db = DBHelper.getInstance(this).getReadableDatabase();
        Cursor c = db.query(DBHelper.SERIES_TABLE, null, "SeriesName LIKE ? AND Language=?", new String[]{tvShowTitle, this.locale}, null, null, null);
        if (c == null || c.getCount() == 0) {
            Log.d("HUSTLE", "cursor non ha elementi...non ho trovato niente nel DB locale");
            c.close();
            return null;
        }
        // crea un nuovo JSONArray dove inserire le serie trovate
        JSONArray ja = new JSONArray();
        // riporta il cursore all'inizio
        if (c.moveToFirst()) {
            // itera sui risultati della query
            do {
                JSONObject jo = new JSONObject();
                try {
                    jo.put("seriesid", c.getInt(c.getColumnIndex(DBHelper.SERIESID)));
                    jo.put("language", c.getString(c.getColumnIndex(DBHelper.LANGUAGE)));
                    jo.put("overview", c.getString(c.getColumnIndex(DBHelper.OVERVIEW)));
                    jo.put("seriesname", c.getString(c.getColumnIndex(DBHelper.SERIESNAME)));
                    jo.put("poster", c.getString(c.getColumnIndex(DBHelper.POSTER)));
                    jo.put("banner", c.getString(c.getColumnIndex(DBHelper.BANNER)));
                    jo.put("fanart", c.getString(c.getColumnIndex(DBHelper.FANART)));
                    jo.put("seasons", c.getString(c.getColumnIndex(DBHelper.SEASONS)));
                    Log.d("HUSTLE", "Ricerca da DB OK, aggiungo serie: " + jo.toString());
                    ja.put(jo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        c.close();
        return ja;
    }

    private void doSearch(final String tvShowTitle) {
        hideKeyboard();
        // Ogni volta che viene effettuata una nuova ricerca
        // resetta l'ArrayList
        shows = new ArrayList<Show>();
        adapter = new SearchShowRecyclerAdapter(shows, this);
        rw.setAdapter(adapter);
        // Effettua la ricerca nel DB locale
        JSONArray ja = searchDB(tvShowTitle);
        if (ja != null) {
            handleJson(ja, false);
            return;
        }

        Log.d("HUSTLE", "Searching for serie: " + tvShowTitle);
        final ProgressDialog progDailog = new ProgressDialog(SearchActivity.this);
        final String msg_loading = getResources().getString(R.string.searching);

        // AsyncTask per prendere info su una Serie TV in base
        // al nome. Potrebbe ritornare più elementi in un JSONArray
        AsyncTask<Void, Void, String> at = new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progDailog.setMessage(msg_loading);
                progDailog.setIndeterminate(false);
                progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDailog.setCancelable(true);
                progDailog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                URL url = null;
                String s = null;
                try {
                    Uri builtUri = Uri.parse("http://hustle.altervista.org/getSeries.php?").
                            buildUpon().
                            appendQueryParameter("seriesname", tvShowTitle).
                            appendQueryParameter("language", locale).
                            appendQueryParameter("full", null).
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("HUSTLE", "returned: " + s);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s == null)
                    return;
                //Log.d("HUSTLE", s);
                JSONArray ja = null;
                try {
                    ja = new JSONArray(s);
                    ja.length();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handleJson(ja, true);
                progDailog.dismiss();
                //Log.d("HUSTLE", ja.toString());
            }
        };

        at.execute();
    }
    // add è true se la stagione va aggiunta al DB, falso altrimenti
    public void handleJson(JSONArray ja, boolean add) {
        for (int i = 0; i< (ja != null ? ja.length() : 0); i++) {
            try {
                JSONObject jo = ja.getJSONObject(i);
                Log.d("HUSTLE", "Show: " + jo.toString());
                Show s1 = new Show(jo);
                shows.add(s1);
                adapter.notifyDataSetChanged();
                if (add) {
                    //Log.d("HUSTLE", "Sto per aggiungere la serie al DB");
                    s1.addToDB(this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

