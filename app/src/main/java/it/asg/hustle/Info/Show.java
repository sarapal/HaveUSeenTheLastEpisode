package it.asg.hustle.Info;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.asg.hustle.DBHelper;

/**
 * Created by sara on 8/26/15.
 */
public class Show {
    public String title;
    public String id;
    public String overview;
    public String language;
    public String banner;
    public Bitmap bmp;
    public int seasonNumber;
    public JSONObject source;
    public ArrayList<Season> seasonsList;


    public Show(String title)
    {
        this.title = title;
    }

    public Show(JSONObject jo) {
        Log.d("HUSTLE", "Chiamato costruttore show con parametro: " + jo.toString());

        try {
            if (jo.has("poster")) {
                this.banner = jo.getString("banner");
            }if (jo.has("seasons")) {
                this.seasonNumber = jo.getInt("seasons");
            }
            this.title = jo.getString("seriesname");
            if (jo.has("id")) {
                this.id = ""+jo.getLong("id");
            } else if (jo.has("seriesid")) {
                this.id = "" + jo.getLong("seriesid");
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

    public boolean addToDB(Context c) {
        String query = "INSERT INTO tvseries (seriesid, Actors, Airs_DayOfWeek, Airs_Time, FirstAired, Genre, Language, Network, " +
                    "Overview, Rating, SeriesName, Status, banner, fanart, lastupdated, poster, seasons) VALUES (\"?\", \"?\",\"?\", " +
                    "\"?\", \"?\", \"?\", \"?\", \"?\", \"?\", \"?\", \"?\", \"?\", \"?\", \"?\", \"?\", \"?\", \"?\");";
        String[] args = new String[]{"ciao", "ops"};
        //SQLiteDatabase db = new DBHelper(c).getWritableDatabase();
        //db.rawQuery(query, args);
        // TODO: aggiungi la serie al DB e ritorna il risultato
        return false;
    }

    @Override
    public String toString() {
        return "Show{" +
                "title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", overview='" + overview + '\'' +
                ", language='" + language + '\'' +
                ", banner='" + banner + '\'' +
                '}';
    }
}
