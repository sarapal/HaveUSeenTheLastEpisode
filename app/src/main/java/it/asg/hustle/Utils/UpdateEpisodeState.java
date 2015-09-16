package it.asg.hustle.Utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.widget.CheckBox;

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

import it.asg.hustle.Info.Episode;
import it.asg.hustle.R;

/**
 * Created by gbyolo on 9/16/15.
 */
public class UpdateEpisodeState {
    public static synchronized boolean changeState(Context c, final Episode ep, final CheckBox cb, final boolean state,final FloatingActionButton fab) {

        final String episodeid = ep.episodeId;
        final String seen = "" + state;

        final String id = c.getSharedPreferences("id_facebook", Context.MODE_PRIVATE).getString("id_facebook", null);
        String name = c.getSharedPreferences("name_facebook", Context.MODE_PRIVATE).getString("name_facebook", null);
        boolean logged = c.getSharedPreferences("logged", Context.MODE_PRIVATE).getBoolean("logged", false);

        if (id == null || name == null || !logged) {
            Log.d("HUSTLE", "Non puoi cambiare lo stato dell'episodio se non sei loggato");
            return false;
        }

        final String auth = MD5.hash(id+name);

        AsyncTask<Void, Void, String> at = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                URL url = null;
                String s = null;
                try {
                    Uri builtUri = Uri.parse("http://hustle.altervista.org/getEpisodes.php?").
                            buildUpon().
                            appendQueryParameter("episodeid", episodeid).
                            appendQueryParameter("seen", seen).
                            appendQueryParameter("auth", auth).
                            appendQueryParameter("user_id", id).
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
                try {
                    JSONObject jo = new JSONObject(s);
                    if (jo.getBoolean("error")) {
                        Log.d("HUSTLE", "Errore nel cambiare stato episodio");
                        if (cb != null) {
                            cb.setChecked(!state);
                        }
                    } else {
                        Log.d("HUSTLE", "Stato episodio cambiato");
                        ep.checked = !ep.checked;
                        ep.source.put("seen", ep.checked);
                        if (fab != null){
                            if (ep.checked){
                                fab.setImageResource(R.drawable.ic_close);

                            }else{
                                fab.setImageResource(R.drawable.ic_done);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        at.execute();

        return true;
    }
}
