package it.asg.hustle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
            "  IMDB_ID varchar(25) NOT NULL,\n" +
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
            "  thumb_width smallint(5) DEFAULT NULL\n" +
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

    private static DBHelper instance;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SERIES_TABLE);
        db.execSQL(CREATE_EPISODES_TABLE);
        db.execSQL(CREATE_SEEN_EPISODES_TABLE);
    }

    // synchronized significa che più thread possono chiamare questo metodo senza create
    // eventi di race condition
    public static synchronized DBHelper getInstance(Context c) {
        if (instance == null) {
            instance = new DBHelper(c);
        }
        return instance;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
