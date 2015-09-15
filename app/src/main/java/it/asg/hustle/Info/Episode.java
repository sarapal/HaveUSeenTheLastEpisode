package it.asg.hustle.Info;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import it.asg.hustle.DBHelper;

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

    public Episode(String title)
    {
        this.title = title;
    }

    public Episode(JSONObject jo) {
        Log.d("HUSTLE", "Chiamato costruttore episode con parametro: " + jo.toString());

        try {
            if (jo.has("filename")) {
                this.bmpPath = jo.getString("filename");
            }
            this.title = jo.getString("episodename");
            if (jo.has("episodeid")) {
                this.episodeId = "" + jo.getLong("episodeid");
            }
            if (jo.has("seriesid")) {
                this.seriesID = "" + jo.getLong("seriesid");
            }
            if (jo.has("seasonnumber")) {
                this.season = jo.getInt("seasonnumber");
            }
            if (jo.has("episodenumber")) {
                this.episodeNumber = jo.getInt("episodenumber");
            }
            if (jo.has("overview")) {
                this.overview =jo.getString("overview");
            }

            Log.d("HUSTLE", "Show in creazione con lingua: " + jo.getString("language"));
            this.language = new String(jo.getString("language"));

            this.source = jo;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.bmp = null;
        Log.d("HUSTLE", "Show creato con lingua: " + this.language);
    }

    public JSONObject toJSON()
    {
        return this.source;
    }

    /* gbyolo, 10/9/2015 */
    public boolean addToDB(Context c) {
        Log.d("HUSTLE", "addToDB episodio chiamata");
        // prende database
        SQLiteOpenHelper helper = DBHelper.getInstance(c);
        SQLiteDatabase db = helper.getWritableDatabase();
        // crea oggetto per i valori da inserire nella tabella
        ContentValues cv = new ContentValues();
        // aggiunge i valori
        cv.put(DBHelper.EPISODEID, this.episodeId);
        cv.put(DBHelper.EPISODENUMBER, this.episodeNumber);
        cv.put(DBHelper.LANGUAGE, this.language);
        cv.put(DBHelper.OVERVIEW, this.overview);
        cv.put(DBHelper.SERIESID, this.seriesID);
        cv.put(DBHelper.FILENAME, this.bmpPath);
        cv.put(DBHelper.EPISODENAME, this.title);
        cv.put(DBHelper.SEASON, this.season);

        if (db.insert(DBHelper.EPISODES_TABLE, null, cv) == -1) {
            Log.d("HUSTLE", "Non sono riuscito a inserire l'episodio nel DB");
            return false;
        }
        Log.d("HUSTLE", "Episodio inserito correttamente");
        return true;
    }

    @Override
    public String toString() {
        return "Episode{" +
                "title='" + title + '\'' +
                ", episode id='" + episodeId + '\'' +
                ", episode #='" + episodeNumber + '\'' +
                ", season #='" + season + '\'' +
                ", series id='" + seriesID + '\'' +
                ", overview='" + overview + '\'' +
                ", language='" + language + '\'' +
                ", bmp path='" + bmpPath + '\'' +
                '}';
    }
}
