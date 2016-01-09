package it.asg.hustle.Info;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by andrea on 15/09/15.
 */
public class Friend {
    public String name = null;
    public String id = null;
    public ArrayList<Show> shows = null;

    public Friend(){
        this.shows = new ArrayList<Show>();
        return;
    }

    public Friend(String id_in, String name_in){
        this.shows = new ArrayList<Show>();
        name = name_in;
        id = id_in;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Friend friend = (Friend) o;

        return id.equals(friend.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Friend(JSONObject friend_JSON){
        this.shows = new ArrayList<Show>();

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

    public JSONObject toJSON(){
        JSONObject friendJSON = new JSONObject();
        try {
            friendJSON.put("name", (String) this.name);
            friendJSON.put("id", (String) this.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return friendJSON;
    }

}
