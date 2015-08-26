package it.asg.hustle;

import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SearchActivity extends AppCompatActivity {
    String tvShowTitle;
    EditText edtTxt;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("TV Show (Title)");

        edtTxt = (EditText) findViewById(R.id.finder);
        btn = (Button) findViewById(R.id.search_button);

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
        Log.d("HUSTLE", "Searching for serie: " + tvShowTitle);

        AsyncTask<Void, Void, String> at = new AsyncTask<Void, Void, String>() {
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
                Log.d("HUSTLE", s);
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