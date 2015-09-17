package it.asg.hustle.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.asg.hustle.Info.Episode;
import it.asg.hustle.Info.Season;
import it.asg.hustle.Info.Show;

/**
 * Created by gbyolo on 9/7/15.
 * Classe DBHelper, così ogni volta che l'utente apre il DB lui prova a creare le tabelle
 * se non esistono già. Inoltre garantisce l'utilizzo tra vari thread mediante il pattern Singleton
 * ovvero, si usa una variabile privata statica, e il metodo getInstance() che, se l'istanza è null
 * la crea e la ritorna, altrimenti ritorna l'istanza perché esisteva già. Inoltre con l'attributo
 * synchronized, il metodo getInstance() è privo di race condition quando chiamato da più thread.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "series.db";
    private static final int DATABASE_VERSION = 1;

    public static final String SERIES_TABLE = "tvseries";
    public static final String EPISODES_TABLE = "episodes";
    public static final String SEEN_EPISODES_TABLE = "seen_episodes";

    private static final String CREATE_EPISODES_TABLE = "CREATE TABLE IF NOT EXISTS `episodes` (\n" +
            "  id integer primary key autoincrement,\n" +
            "  episodeid int(10) NOT NULL,\n" +
            "  Director text,\n" +
            "  EpisodeName varchar(255) DEFAULT NULL,\n" +
            "  EpisodeNumber int(10) DEFAULT NULL,\n" +
            "  FirstAired varchar(45) DEFAULT NULL,\n" +
            "  GuestStars text,\n" +
            "  IMDB_ID varchar(25) DEFAULT NULL,\n" +
            "  Language varchar(2) DEFAULT NULL,\n" +
            "  Overview text,\n" +
            "  ProductionCode varchar(45) DEFAULT NULL,\n" +
            "  Rating float DEFAULT NULL,\n" +
            "  RatingCount int(10) DEFAULT NULL,\n" +
            "  SeasonNumber int(10) DEFAULT NULL,\n" +
            "  Writer text,\n" +
            "  absolute_number int(3) DEFAULT NULL,\n" +
            "  filename varchar(100) DEFAULT NULL,\n" +
            "  lastupdated int(10) DEFAULT NULL,\n" +
            "  seasonid int(10) DEFAULT NULL,\n" +
            "  seriesid int(10) DEFAULT NULL,\n" +
            "  thumb_added datetime DEFAULT NULL,\n" +
            "  thumb_height smallint(5) DEFAULT NULL,\n" +
            "  thumb_width smallint(5) DEFAULT NULL,\n" +
            "  seen boolean DEFAULT false\n" +
            ");";

    private static final String CREATE_SERIES_TABLE = "CREATE TABLE IF NOT EXISTS `tvseries` (\n" +
            "  id integer primary key autoincrement,\n" +
            "  seriesid int(10) NOT NULL,\n" +
            "  Actors text,\n" +
            "  Airs_DayOfWeek varchar(45) DEFAULT NULL,\n" +
            "  Airs_Time varchar(45) DEFAULT NULL,\n" +
            "  ContentRating varchar(45) DEFAULT NULL,\n" +
            "  FirstAired varchar(100) DEFAULT NULL,\n" +
            "  Genre varchar(100) DEFAULT NULL,\n" +
            "  IMDB_ID varchar(25) DEFAULT NULL,\n" +
            "  Language varchar(2) DEFAULT NULL,\n" +
            "  Network varchar(100) DEFAULT NULL,\n" +
            "  NetworkID int(10) DEFAULT NULL,\n" +
            "  Overview text,\n" +
            "  Rating float DEFAULT NULL,\n" +
            "  RatingCount int(10) DEFAULT NULL,\n" +
            "  Runtime varchar(100) DEFAULT NULL,\n" +
            "  SeriesName varchar(255) DEFAULT NULL,\n" +
            "  Status varchar(100) DEFAULT NULL,\n" +
            "  added datetime DEFAULT NULL,\n" +
            "  banner varchar(100) DEFAULT NULL,\n" +
            "  fanart varchar(100) DEFAULT NULL,\n" +
            "  lastupdated int(10) DEFAULT NULL,\n" +
            "  poster varchar(100) DEFAULT NULL,\n" +
            "  zap2it_id varchar(12) DEFAULT NULL,\n" +
            "  seasons int(10) DEFAULT NULL\n" +
            ");";
    private static final String CREATE_SEEN_EPISODES_TABLE = "CREATE TABLE IF NOT EXISTS `seen_episodes` (\n" +
            "  id integer primary key autoincrement,\n" +
            "  user_id int(12) NOT NULL,\n" +
            "  episodeid int(10) NOT NULL,\n" +
            "  seriesid int(10) NOT NULL\n" +
            ");";

    public static final String SERIESID = "seriesid";
    public static final String LANGUAGE = "Language";
    public static final String OVERVIEW = "Overview";
    public static final String SERIESNAME = "SeriesName";
    public static final String BANNER = "banner";
    public static final String POSTER = "poster";
    public static final String FANART = "fanart";
    public static final String SEASONS = "seasons";

    public static final String EPISODEID = "episodeid";
    public static final String EPISODENAME = "EpisodeName";
    public static final String EPISODENUMBER = "EpisodeNumber";
    public static final String SEASON = "SeasonNumber";
    public static final String FILENAME = "filename";
    public static final String SEEN = "seen";

    private static DBHelper instance;
    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SERIES_TABLE);
        db.execSQL(CREATE_EPISODES_TABLE);
        db.execSQL(CREATE_SEEN_EPISODES_TABLE);
    }

    // synchronized significa che più thread possono chiamare questo metodo senza creare
    // eventi di race condition
    public static synchronized DBHelper getInstance(Context c) {
        if (instance == null) {
            instance = new DBHelper(c);
        }
        return instance;
    }

    public static synchronized JSONArray getSeriesByNameFromDB(String title, String lan) {
        Log.d("HUSTLE", "Ricerca serie con nome nel DB locale... " + title);
        if (instance == null) {
            return null;
        }
        SQLiteDatabase db = instance.getReadableDatabase();
        // i due % indicano qualsiasi altro carattere (così se scrivo grey's lui trova grey's anatomy!)
        Cursor c = db.query(DBHelper.SERIES_TABLE, null, "SeriesName LIKE ? AND Language=?", new String[]{"%"+title+"%", lan}, null, null, null);
        if (c == null || c.getCount() == 0) {
            Log.d("HUSTLE", "cursor non ha elementi...non ho trovato niente nel DB locale");
            c.close();
            return null;
        }
        // crea un nuovo JSONArray dove inserire le serie trovate
        JSONArray ja = new JSONArray();
        // riporta il cursore all'inizio
        if (c.moveToFirst()) {
            // itera sui risultati della query
            do {
                JSONObject jo = new JSONObject();
                try {
                    jo.put("seriesid", c.getInt(c.getColumnIndex(DBHelper.SERIESID)));
                    jo.put("language", c.getString(c.getColumnIndex(DBHelper.LANGUAGE)));
                    jo.put("overview", c.getString(c.getColumnIndex(DBHelper.OVERVIEW)));
                    jo.put("seriesname", c.getString(c.getColumnIndex(DBHelper.SERIESNAME)));
                    jo.put("poster", c.getString(c.getColumnIndex(DBHelper.POSTER)));
                    jo.put("banner", c.getString(c.getColumnIndex(DBHelper.BANNER)));
                    jo.put("fanart", c.getString(c.getColumnIndex(DBHelper.FANART)));
                    jo.put("seasons", c.getString(c.getColumnIndex(DBHelper.SEASONS)));
                    Log.d("HUSTLE", "Ricerca da DB OK, aggiungo nell'array la serie: " + jo.toString());
                    ja.put(jo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        c.close();
        return ja;
    }

    public static synchronized JSONObject getSerieByIDFromDB(String id) {
        JSONObject jo = new JSONObject();
        if (instance == null) {
            return null;
        }
        SQLiteDatabase db = instance.getReadableDatabase();
        Cursor c = db.query(DBHelper.SERIES_TABLE, null, "seriesid=?", new String[]{id}, null, null, null);
        if (c == null || c.getCount() == 0) {
            Log.d("HUSTLE", "cursor non ha elementi...non ho trovato niente nel DB locale");
            c.close();
            return null;
        }
        // riporta il cursore all'inizio
        if (c.moveToFirst()) {
            try {
                jo.put("seriesid", c.getInt(c.getColumnIndex(DBHelper.SERIESID)));
                jo.put("language", c.getString(c.getColumnIndex(DBHelper.LANGUAGE)));
                jo.put("overview", c.getString(c.getColumnIndex(DBHelper.OVERVIEW)));
                jo.put("seriesname", c.getString(c.getColumnIndex(DBHelper.SERIESNAME)));
                jo.put("poster", c.getString(c.getColumnIndex(DBHelper.POSTER)));
                jo.put("banner", c.getString(c.getColumnIndex(DBHelper.BANNER)));
                jo.put("fanart", c.getString(c.getColumnIndex(DBHelper.FANART)));
                jo.put("seasons", c.getString(c.getColumnIndex(DBHelper.SEASONS)));
                Log.d("HUSTLE", "Ricerca da DB OK, aggiungo nell'array la serie: " + jo.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        c.close();

        return jo;
    }

    public static synchronized boolean addSerieToDB(Show s) {
        Log.d("HUSTLE", "addSerieToDB chiamata");
        // prende database
        if (instance == null) {
            return false;
        }
        SQLiteDatabase db = instance.getWritableDatabase();
        // crea oggetto per i valori da inserire nella tabella
        ContentValues cv = new ContentValues();
        // aggiunge i valori
        cv.put(DBHelper.SERIESID, s.id);
        cv.put(DBHelper.LANGUAGE, s.language);
        cv.put(DBHelper.OVERVIEW, s.overview);
        cv.put(DBHelper.SERIESNAME, s.title);
        cv.put(DBHelper.BANNER, s.banner);
        cv.put(DBHelper.POSTER, s.poster);
        cv.put(DBHelper.FANART, s.fanart);
        cv.put(DBHelper.SEASONS, s.seasonNumber);

        if (db.insert(DBHelper.SERIES_TABLE, null, cv) == -1) {
            Log.d("HUSTLE", "Non sono riuscito a inserire la serie nel DB");
            return false;
        }
        Log.d("HUSTLE", "Serie inserita correttamente");
        return true;

    }

    public static synchronized JSONArray getSeasonFromDB(Show s, int seasonNumber) {
        // se nessun DB è stato aperto
        if (instance == null)
            return null;
        JSONArray ja = new JSONArray();

        SQLiteDatabase db = instance.getReadableDatabase();
        Cursor c = db.query(DBHelper.EPISODES_TABLE, null, "seriesid=? AND SeasonNumber=?", new String[]{s.id, ""+seasonNumber}, null, null, null);
        if (c == null || c.getCount() == 0) {
            Log.d("HUSTLE", "cursor non ha elementi...non ho trovato niente nel DB locale");
            c.close();
            return null;
        }
        if (c.moveToFirst()) {
            // itera sui risultati della query
            do {
                JSONObject jo = new JSONObject();
                try {
                    int seen = c.getInt(c.getColumnIndex(DBHelper.SEEN));
                    boolean checked = (seen == 0) ? false : true;

                    jo.put("episodeid", c.getInt(c.getColumnIndex(DBHelper.EPISODEID)));
                    jo.put("seriesid", c.getString(c.getColumnIndex(DBHelper.SERIESID)));
                    jo.put("seasonnumber", c.getString(c.getColumnIndex(DBHelper.SEASON)));
                    jo.put("seen", checked);
                    jo.put("language", c.getString(c.getColumnIndex(DBHelper.LANGUAGE)));
                    jo.put("episodename", c.getString(c.getColumnIndex(DBHelper.EPISODENAME)));
                    jo.put("episodenumber", c.getString(c.getColumnIndex(DBHelper.EPISODENUMBER)));
                    jo.put("overview", c.getString(c.getColumnIndex(DBHelper.OVERVIEW)));
                    jo.put("filename", c.getString(c.getColumnIndex(DBHelper.FILENAME)));
                    Log.d("HUSTLE", "Ricerca da DB OK, aggiungo nell'array l'episodio: " + jo.toString());
                    ja.put(jo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        c.close();

        return ja;
    }

    public static synchronized boolean addSeasonDB(Season season) {
        if (instance == null)
            return false;

        SQLiteDatabase db = instance.getWritableDatabase();
        ArrayList<Episode> episodes = season.episodesList;

        ContentValues cv;
        // Cicla su tutti gli episodi
        for (Episode e : episodes) {
            cv = new ContentValues();
            cv.put(DBHelper.EPISODEID, e.episodeId);
            cv.put(DBHelper.SERIESID, e.seriesID);
            cv.put(DBHelper.SEASON, e.season);
            cv.put(DBHelper.SEEN, e.checked);
            cv.put(DBHelper.EPISODENUMBER, e.episodeNumber);
            cv.put(DBHelper.EPISODENAME, e.title);
            cv.put(DBHelper.OVERVIEW, e.overview);
            cv.put(DBHelper.LANGUAGE, e.language);
            cv.put(DBHelper.FILENAME, e.bmpPath);

            if (db.insert(DBHelper.EPISODES_TABLE, null, cv) == -1) {
                Log.d("HUSTLE", "Non sono riuscito a inserire l'episodio nel DB");
                return false;
            }
        }

        return true;
    }

    public static synchronized boolean updateSeasonDB(Season season) {
        if (instance == null)
            return false;

        SQLiteDatabase db = instance.getWritableDatabase();
        ArrayList<Episode> episodes = season.episodesList;

        ContentValues cv;
        // Cicla su tutti gli episodi
        for (Episode e : episodes) {
            if (!DBHelper.updateEpisode(e.episodeId, e.checked)) {
                return false;
            }
        }

        return true;
    }

    public static synchronized boolean updateEpisode(String episodeid, boolean state) {
        if (instance == null)
            return false;

        SQLiteDatabase db = instance.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.SEEN, state);

        // Ritorna il numero di righe cambiate
        if (db.update(DBHelper.EPISODES_TABLE, cv, "episodeid=?", new String[]{episodeid}) == 1) {
            return true;
        }

        return false;
    }

    public static synchronized boolean addEpisodeToDB(Episode e) {
        Log.d("HUSTLE", "addEpisodeToDB episodio chiamata");
        // prende database
        if (instance == null) {
            return false;
        }
        SQLiteDatabase db = instance.getWritableDatabase();
        // crea oggetto per i valori da inserire nella tabella
        ContentValues cv = new ContentValues();
        // aggiunge i valori
        cv.put(DBHelper.EPISODEID, e.episodeId);
        cv.put(DBHelper.EPISODENUMBER, e.episodeNumber);
        cv.put(DBHelper.LANGUAGE, e.language);
        cv.put(DBHelper.OVERVIEW, e.overview);
        cv.put(DBHelper.SERIESID, e.seriesID);
        cv.put(DBHelper.FILENAME, e.bmpPath);
        cv.put(DBHelper.EPISODENAME, e.title);
        cv.put(DBHelper.SEASON, e.season);

        if (db.insert(DBHelper.EPISODES_TABLE, null, cv) == -1) {
            Log.d("HUSTLE", "Non sono riuscito a inserire l'episodio nel DB");
            return false;
        }
        Log.d("HUSTLE", "Episodio inserito correttamente");
        return true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
