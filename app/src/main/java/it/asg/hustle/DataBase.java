package it.asg.hustle;

import android.database.sqlite.SQLiteDatabase;

/**
 *
 * Created by gbyolo on 9/7/15.
 * Classe per aprire il database.
 *
 */
public class DataBase {
    private static SQLiteDatabase db = null;

    private DataBase()
    {

    }

    public static SQLiteDatabase getInstance()
    {
        if (db == null) {
            db = SQLiteDatabase.openOrCreateDatabase("series", null);
        }

        return db;
    }
}
