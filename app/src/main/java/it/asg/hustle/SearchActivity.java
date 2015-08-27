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
import android.widget.Button;
import android.widget.EditText;

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

public class SearchActivity extends AppCompatActivity {
    String tvShowTitle;
    EditText edtTxt;
    Button btn;
    RecyclerView rw;
    SearchShowRecyclerAdapter adapter;
    ArrayList<Show> shows;

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

        edtTxt = (EditText) findViewById(R.id.finder);
        btn = (Button) findViewById(R.id.search_button);
        rw = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new SearchShowRecyclerAdapter(shows, this);
        rw.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        rw.setAdapter(adapter);

        Bundle b = getIntent().getExtras();

        if (b != null) {
            tvShowTitle = b.getString("SearchTitle");
            edtTxt.setText(tvShowTitle);
            edtTxt.setSelection(edtTxt.getText().length());
            
            doSearch(tvShowTitle);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch(edtTxt.getText().toString());
            }
        });

    }

    private void doSearch(final String tvShowTitle) {

        shows = new ArrayList<Show>();
        adapter = new SearchShowRecyclerAdapter(shows, this);
        rw.setAdapter(adapter);

        Log.d("HUSTLE", "Searching for serie: " + tvShowTitle);
        final ProgressDialog progDailog = new ProgressDialog(SearchActivity.this);


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
                try {
                    url = new URL("http://192.168.0.111/getSeries.php?seriesname=" + tvShowTitle);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i<ja.length(); i++) {
                    try {
                        JSONObject jo = ja.getJSONObject(i);
                        // TODO: prendi la serie tramite l'id con un nuovo AsyncTask
                        Show s1 = new Show(jo);
                        (new GetSerieByID()).execute(s1);
                        //Log.d("HUSTLE", "Title: " + s1.toString());
                        //shows.add(s1);
                        //adapter.notifyDataSetChanged();
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

    class GetSerieByID extends AsyncTask<Show, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Show... params) {
            URL url = null;
            String s = null;
            JSONObject jo = null;
            try {
                url = new URL("http://192.168.0.111/getSeries.php?seriesid=" + params[0].id);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                s = br.readLine();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                jo = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("HUSTLE", "GET SERIE BY ID: " + jo.toString());
            try {
                Log.d("HUSTLE", "GET SERIE BY ID, language: " + jo.getString("language"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jo;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Show s = new Show(jsonObject);
            Log.d("HUSTLE", "Creo show: " + jsonObject);
            shows.add(s);
            adapter.notifyDataSetChanged();
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