package it.asg.hustle;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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
    public JSONObject source;

    public Show(JSONObject jo) {
        Log.d("HUSTLE", "Chiamato costruttore show con parametro: " + jo.toString());

        try {
            if (jo.has("poster")) {
                this.banner = jo.getString("banner");
            }
            this.title = jo.getString("seriesname");
            if (jo.has("id")) {
                this.id = ""+jo.getLong("id");
            } else if (jo.has("seriesid")) {
                this.id = "" + jo.getLong("seriesid");
            }

            Log.d("HUSTLE", "Show in crazione con lingua: " + jo.getString("language"));
            this.language = new String(jo.getString("language"));

            this.source = jo;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.bmp = null;
        Log.d("HUSTLE", "Show creato con lingua: " + this.language);
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
