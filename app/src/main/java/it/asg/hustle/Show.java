package it.asg.hustle;

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
    public String poster;

    public Show(JSONObject jo) {
        try {
            if (jo.has("poster")) {
                this.poster = jo.getString("poster");
            }
            this.title = jo.getString("seriesname");
            this.id = ""+jo.getLong("id");
            this.language = jo.getString("language");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Show{" +
                "title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", overview='" + overview + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
