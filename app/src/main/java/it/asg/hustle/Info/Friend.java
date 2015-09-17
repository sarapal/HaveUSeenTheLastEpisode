package it.asg.hustle.Info;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andrea on 15/09/15.
 */
public class Friend {
    public String name = null;
    public String id = null;


    public Friend(){
        return;
    }

    public Friend(String id_in, String name_in){

        name = name_in;
        id = id_in;
    }

    public Friend(JSONObject friend_JSON){
        if(friend_JSON != null) {
            try {
                this.name = friend_JSON.getString("name");
                this.id = friend_JSON.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getName(){
        return this.name;
    }


    public String getId(){
        return this.id;
    }

}
