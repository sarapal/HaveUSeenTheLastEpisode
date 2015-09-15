package it.asg.hustle.Info;

import android.graphics.Bitmap;

/**
 * Created by andrea on 15/09/15.
 */
public class Friend {
    public Bitmap profile_photo = null;
    public String name = null;
    public String id = null;


    public Friend(){
        return;
    }

    public Friend(String id_in, Bitmap bmp, String name_in){
        profile_photo = bmp;
        name = name_in;
        id = id_in;
    }

    public String getName(){
        return this.name;
    }

    public Bitmap getImage(){
        return this.profile_photo;
    }

    public String getId(){
        return this.id;
    }

//TODO: funzione che accetta l'url della foto e con async task scarica la foto

}
