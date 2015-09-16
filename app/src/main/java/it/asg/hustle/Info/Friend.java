package it.asg.hustle.Info;

import android.graphics.Bitmap;

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

    public String getName(){
        return this.name;
    }


    public String getId(){
        return this.id;
    }

}
