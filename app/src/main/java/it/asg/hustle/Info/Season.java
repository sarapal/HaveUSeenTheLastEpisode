package it.asg.hustle.Info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by andrea on 07/09/15.
 */
public class Season {
    public ArrayList<Episode> episodesList;
    public JSONArray source = null;
    public int seasonNumber;
    public int episodeNumber;


    public Season(JSONArray seasonJSON) {
        this.source = source;
        this.episodesList = new ArrayList<Episode>();
        for (int i = 0; i< (seasonJSON != null ? seasonJSON.length() : 0); i++) {
            try {
                JSONObject jo = seasonJSON.getJSONObject(i);
                this.episodesList.add(new Episode(jo));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.episodeNumber =0;
        this.seasonNumber=0;
        try {
            this.episodeNumber = seasonJSON.length();
            this.seasonNumber = this.episodesList.get(0).season;
        }
         catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fromJson(JSONArray seasonJSON){
        this.source = source;
        for (int i = 0; i< (seasonJSON != null ? seasonJSON.length() : 0); i++) {
            try {
                JSONObject jo = seasonJSON.getJSONObject(i);
                this.episodesList.add(new Episode(jo));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.episodeNumber =0;
        this.seasonNumber=0;
        try {
            this.episodeNumber = seasonJSON.length();
            this.seasonNumber = this.episodesList.get(0).season;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Season(){
        this.episodesList = new ArrayList<Episode>();
        return;
    }

    public JSONArray toJson(){return this.source;}

}
