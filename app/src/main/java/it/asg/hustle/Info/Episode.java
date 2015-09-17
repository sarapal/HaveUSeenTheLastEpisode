package it.asg.hustle.Info;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

import it.asg.hustle.Utils.MD5;

/**
 * Created by andrea on 9/7/15.
 */
public class Episode{
    public String title;
    public int season;
    public int episodeNumber;
    public String episodeId;
    public String seriesID;
    public String overview;
    public String language;
    public String bmpPath;
    public Bitmap bmp;
    public JSONObject source;
    public Boolean checked = false;
    public ArrayList<Friend> watchingFriends = null;

    public Episode(String title)
    {
        this.watchingFriends =new ArrayList<Friend>();
        this.title = title;
    }

    public Episode(JSONObject jo) {
        Log.d("HUSTLE", "Chiamato costruttore episode con parametro: " + jo.toString());
        this.watchingFriends =new ArrayList<Friend>();
        try {
            if (jo.has("filename")) {
                this.bmpPath = jo.getString("filename");
            }
            if (jo.has("episodename")) {
                this.title = jo.getString("episodename");
            }
            if (jo.has("episodeid")) {
                this.episodeId = "" + jo.getLong("episodeid");
            }
            if (jo.has("seriesid")) {
                this.seriesID = "" + jo.getLong("seriesid");
            }
            if (jo.has("seasonnumber")) {
                this.season = jo.getInt("seasonnumber");
            }
            if (jo.has("seen")) {
                this.checked = jo.getBoolean("seen");
            }
            if (jo.has("episodenumber")) {
                this.episodeNumber = jo.getInt("episodenumber");
            }
            if (jo.has("overview")) {
                this.overview =jo.getString("overview");
            }

            if (jo.has("language")) {
                Log.d("HUSTLE", "Episode in creazione con lingua: " + jo.getString("language"));
                this.language = new String(jo.getString("language"));
            }

            this.source = jo;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.bmp = null;
        Log.d("HUSTLE", "Episode creato ");
    }

    public JSONObject toJSON()
    {
        //TODO:salvare gli amici che guardano la serie
        return this.source;
    }

    @Override
    public String toString() {
        return "Episode{" +
                "title='" + title + '\'' +
                ", season=" + season +
                ", episodeNumber=" + episodeNumber +
                ", episodeId='" + episodeId + '\'' +
                ", seriesID='" + seriesID + '\'' +
                ", overview='" + overview + '\'' +
                ", language='" + language + '\'' +
                ", bmpPath='" + bmpPath + '\'' +
                ", bmp=" + bmp +
                ", source=" + source +
                ", checked=" + checked +
                '}';
    }
}
