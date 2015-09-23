package it.asg.hustle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import it.asg.hustle.Info.Episode;
import it.asg.hustle.Info.Friend;
import it.asg.hustle.Info.Season;
import it.asg.hustle.Info.Show;
import it.asg.hustle.Utils.CheckConnection;
import it.asg.hustle.Utils.DBHelper;
import it.asg.hustle.Utils.ImageDownloader;
import it.asg.hustle.Utils.UpdateEpisodeState;

public class ShowActivity extends AppCompatActivity {
    private ImageView posterImageView;
    private JSONObject showJSON = null;
    private JSONArray seasonsJSON = null;
    private Bitmap posterBitmap = null;
    private TextView card_description;
    public static Show show;
    private SeasonsAdapter a;
    public static ViewPager viewPager;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    static private ArrayList<EpisodeRecyclerAdapter> adapterList;
    static private FriendsAdapter adapter_friends;
    private ArrayList<Friend> show_friends = null;
    private ArrayList<Friend> all_friends = null;
    private boolean updateFromServer = false;



    private ArrayList<String> info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        adapterList = new ArrayList<EpisodeRecyclerAdapter>();

        //caso in cui l'activity è stata stoppata o messa in pausa, ricrea i dati dai savedInstanceState
        if (savedInstanceState != null) posterBitmap = savedInstanceState.getParcelable("poster"); //ripristina l'immagine salvata poster
        if (savedInstanceState != null) {try {
            //ricrea gli oggetti java show stagioni e episodi
            showJSON = new JSONObject(savedInstanceState.getString("show"));
            } catch (JSONException e1) {
            e1.printStackTrace();
            }
            show.fillSeasonsList(seasonsJSON);
        }

        posterImageView = (ImageView) findViewById(R.id.show_activity_poster);

        //caso in cui l'activity viene generata dalla ricerca
        if(savedInstanceState == null){
            Bundle b = getIntent().getExtras();

            if (b != null) {
                String s = b.getString("show");
                try {
                    showJSON = new JSONObject(s);
                    show = new Show(showJSON);

                    int dimPX = getDisplayDimensionsPX();
                    if (!new ImageDownloader(this, dimPX, dimPX).download(show.fanart, posterImageView, show)) {
                        Log.d("HUSTLE", "Gli arriva il NULL");
                    }
                    doGetInfo(show);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //il primo adapter è per le info
        adapterList.add(new EpisodeRecyclerAdapter(getApplicationContext(), ShowActivity.this, new Season()));
        for(int i=1; i<= show.seasonNumber; i++) {
            adapterList.add(new EpisodeRecyclerAdapter(getApplicationContext(), ShowActivity.this, show.seasonsList.get(i - 1)));

        }

        // get toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        try {
            collapsingToolbar.setTitle(showJSON.getString("seriesname"));
            //collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.title_background));
            collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.expandedtitle_background));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //scarico la lista amici che vedono la serie
        show_friends = new ArrayList<Friend>();
        adapter_friends = new FriendsAdapter(show_friends);
        all_friends = getFriendList();
        downloadFriendShows(all_friends, show_friends, show.id);

        //adapter per stagioni e info
        a = new SeasonsAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(a);


        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        //get poster image
        if(posterBitmap!=null){
            Log.d("HUSTLE", "prendo poster da savedInstanceState");
            posterImageView.setImageBitmap(posterBitmap);
        }

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270){
            Log.d("HUSTLE", "landscape mode!");
        }
    }

    public int getDisplayDimensionsPX() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widthPX = size.x;

        return widthPX;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("HUSTLE", "onActivityResult di ShowActivity");
        if (requestCode == EpisodeRecyclerAdapter.EP_CHANGED){
            if (resultCode == Activity.RESULT_OK){

                Bundle b = data.getExtras();
                Boolean status = b.getBoolean("status");
                int ep_num = b.getInt("episode_num");
                int season = b.getInt("season");

                Log.d("HUSTLE", "Episodio n " + ep_num + " stagione " + season + " stato " + status);

                EpisodeRecyclerAdapter era = adapterList.get(season);
                // prende l'episodio
                Episode e = era.getEpisodes().get(ep_num-1);
                // gli cambia stato
                e.checked = status;
                // Avvisa l'adapter che i dati sono cambiati
                era.notifyDataSetChanged();
                // Ora cambia anche il json dell'episodio (per essere consistenti)
                try {
                    e.source.put("seen",status);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            } else {
                Log.d("HUSTLE", "result: "+resultCode+" OK è " + Activity.RESULT_OK);
            }
        } else {
            Log.d("HUSTLE", ""+requestCode);
        }
    }

    private void doGetInfoSeason(final Show showInfo, final int seasonNumber){
        final String id = getSharedPreferences("id_facebook", Context.MODE_PRIVATE).getString("id_facebook", null);
        String name = getSharedPreferences("name_facebook", Context.MODE_PRIVATE).getString("name_facebook", null);
        final boolean logged = getSharedPreferences("logged", Context.MODE_PRIVATE).getBoolean("logged", false);

        // se c'è connessione a internet
        if (CheckConnection.isConnected(this)) {
            // se l'utente è loggato
            if (id != null && name != null && logged) {
                // prendi i dati dal server esterno e aggiorna il DB
                updateFromServer = true;
            }
        }
        // Se non c'è necessità di aggiornare dal server, prendi i dati dal DB locale se ci sono
        if (!updateFromServer) {
            JSONArray season = DBHelper.getSeasonFromDB(showInfo, seasonNumber);
            if (season != null) {
                Log.d("HUSTLE", "Trovata la stagione nel DB");
                show.seasonsList.get(seasonNumber - 1).fromJson(season);
                show.seasonsList.get(seasonNumber - 1).seasonNumber = seasonNumber;
                return;
            }
        }

        AsyncTask<String, Void, Season> st = new AsyncTask<String, Void, Season>() {
            @Override
            protected Season doInBackground(String... params) {
                ArrayList<Episode> seasonList = new ArrayList<Episode>();
                String s=null;
                JSONArray seasonJSON = null;
                // Se l'utente è loggato tramite facebook e sul server esterno, aggiunge il suo id alla richiesta
                // in modo che la risposta del server conterrà gli episodi già visti (campo "seen" del json object)
                String x = "";
                if (id != null && logged)
                    x = ""+id;
                //richiesta dati episodi della stagione
                while (seasonJSON == null) {
                    try {
                        Uri builtUri = Uri.parse("http://hustle.altervista.org/getEpisodes.php?").
                                buildUpon().
                                appendQueryParameter("seriesid", params[0]).
                                appendQueryParameter("season", params[1]).
                                appendQueryParameter("user_id", x).
                                appendQueryParameter("language", Locale.getDefault().getLanguage()).
                                build();
                        String u = builtUri.toString();
                        Log.d("SEASON", "requesting: " + u);
                        URL url = new URL(u);
                        //URL url = new URL("http://hustle.altervista.org/getEpisodes.php?seriesid=" + params[0] + "&season=" + params[1] + x);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        InputStream in = new BufferedInputStream(conn.getInputStream());
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        s = br.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //creazione array dalla risposta
                    try {
                        seasonJSON = new JSONArray(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //creazione elemento java da arraylist
                int number = Integer.parseInt(params[1]);
                show.seasonsList.get(number-1).fromJson(seasonJSON);
                show.seasonsList.get(number-1).seasonNumber = number;

                return show.seasonsList.get(number-1);
            }

            @Override
            protected void onPostExecute(Season season) {
                super.onPostExecute(season);
                adapterList.get(season.seasonNumber).notifyDataSetChanged();

                // se updateFromServer è false, significa che la stagione non si trova
                // nel DB e devo aggiungerla
                if (!updateFromServer) {
                    Log.d("HUSTLE", "Aggiungo la stagione al DB");
                    DBHelper.addSeasonDB(show.seasonsList.get(season.seasonNumber - 1));
                } else {
                    Log.d("HUSTLE", "Devo aggiornare la serie dal server");
                    if (DBHelper.getSeasonFromDB(show, season.seasonNumber) == null) {
                        Log.d("HUSTLE", "Non c'è, la aggiungo");
                        DBHelper.addSeasonDB(season);
                    } else {
                        Log.d("HUSTLE", "c'è già, la aggiorno");
                        DBHelper.updateSeasonDB(show.seasonsList.get(season.seasonNumber - 1));
                    }
                }
            }
        };
        if (CheckConnection.isConnected(this)) {
            st.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, showInfo.id, "" + seasonNumber);
        }
    }


    private void doGetInfo(final Show showInfo) {
        int i;
        for(i=1; i<=showInfo.seasonNumber; i++){
            doGetInfoSeason(showInfo, i);
        }
        //Log.d("HUSTLE", "creato java file di show, " + showInfo.seasonsList.get(0) + "  " + showInfo.seasonsList.get(1));
    }


    private void doGetInfoFriendAll(ArrayList<Friend> friends, String series_id){
        Friend actual=null;
        for (int i=0; i<friends.size();i++){
            actual = friends.get(i);
            doGetInfoFriend(actual);
        }
    }

    private void doGetInfoFriend(final Friend friend){
        final String friend_id = friend.id;
        AsyncTask<String, Void, Void> friend_asynctask = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                String series_id = show.id;
                int series_number = show.seasonNumber;
                String s = null;
                JSONArray episodesFriend = null;
                Show friendShow = new Show(show.title);
                while(s == null) {
                    //richiesta dati episodi dell'amico
                    try {
                        Uri builtUri = Uri.parse("http://hustle.altervista.org/getEpisodes.php?").
                                buildUpon().
                                appendQueryParameter("seriesid", series_id).
                                appendQueryParameter("season", "all").
                                appendQueryParameter("user_id", friend_id).
                                appendQueryParameter("short", "true").
                                appendQueryParameter("language", Locale.getDefault().getLanguage()).
                                build();
                        String u = builtUri.toString();
                        Log.d("SEASON", "requesting: " + u);
                        URL url = new URL(u);
                        //URL url = new URL("http://hustle.altervista.org/getEpisodes.php?seriesid=" + series_id + "&season=all&user_id=" + friend_id + "&short=true");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        InputStream in = new BufferedInputStream(conn.getInputStream());
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        s = br.readLine();
                        Log.d("HUSTLE", "puntate viste da " + friend.name + ": " + s);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //creazione array dalla risposta
                try {
                    episodesFriend = new JSONArray(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (episodesFriend == null)
                    return null;

                for (int i = 0; i < episodesFriend.length(); i++){
                    try {
                        JSONObject friendEpisode = episodesFriend.getJSONObject(i);
                        Episode episode = new Episode(friendEpisode);
                        Episode original = show.seasonsList.get(episode.season-1).episodesList.get(episode.episodeNumber - 1);
                        if (!original.watchingFriends.contains(friend) && episode.checked){
                            original.watchingFriends.add(friend);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void n) {
                super.onPostExecute(n);
                for (int i = 0; i<adapterList.size(); i++) {
                    adapterList.get(i).notifyDataSetChanged();
                }
            }
        };
        if (CheckConnection.isConnected(this)) {
            friend_asynctask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }



    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putParcelable("poster", show.getThumbnail());
        savedInstanceState.putString("show", showJSON.toString());
        seasonsJSON = show.toSeasonsJSON();

        if(seasonsJSON != null){savedInstanceState.putString("seasons", seasonsJSON.toString());}
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    // sottoclasse per gestire i fragment della pagina inziale
    public static class SeasonsFragment extends Fragment {
        private static final String TAB_POSITION = "tab_position";
        private FloatingActionButton checkall;

        public SeasonsFragment() {

        }

        public static SeasonsFragment newInstance(int tabPosition) {
            SeasonsFragment fragment = new SeasonsFragment();
            Bundle args = new Bundle();
            args.putInt(TAB_POSITION, tabPosition);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onResume() {
            Bundle args = getArguments();
            int tabPosition = args.getInt(TAB_POSITION);

            if (ShowActivity.viewPager.getCurrentItem() == 0) {
                ((FloatingActionButton) getActivity().findViewById(R.id.fab_checkAll)).setVisibility(View.INVISIBLE);
            }
            else{
                ((FloatingActionButton) getActivity().findViewById(R.id.fab_checkAll)).setVisibility(View.VISIBLE);
            }
            super.onResume();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            final int tabPosition = args.getInt(TAB_POSITION);

            View v;

            if (tabPosition == 0){
                v = inflater.inflate(R.layout.cardview_info_scrollview, container,false);
                TextView card_description = (TextView) v.findViewById(R.id.card_description_text);

                /*TextView card_series_id = (TextView) v.findViewById(R.id.id_serie_info);
                card_series_id.setText(show.id);*/

                //description card
                card_description.setText(show.overview);
                //card friend
                RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recyclerview_friends_card);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(adapter_friends);
                //rating bar
                RatingBar ratingBar = (RatingBar) v.findViewById(R.id.ratingBar_show);
                ratingBar.setIsIndicator(true);
                Drawable progress = ratingBar.getProgressDrawable();
                DrawableCompat.setTint(progress, Color.RED);
                Drawable indet = ratingBar.getIndeterminateDrawable();
                DrawableCompat.setTint(indet, Color.LTGRAY);
                ratingBar.setMax(5);
                ratingBar.setNumStars(5);
                ratingBar.setRating((float) (show.rating / 2));
                //actor e genre
                TextView card_actors = (TextView) v.findViewById(R.id.card_actors_text);
                TextView card_genre = (TextView) v.findViewById(R.id.card_genre_text);
                card_actors.setText(show.actors);
                card_genre.setText(show.genre);
                checkall = (FloatingActionButton) getActivity().findViewById(R.id.fab_checkAll);

            }
            else {
                v = inflater.inflate(R.layout.fragment_episodes_view, container, false);
                checkall = (FloatingActionButton) getActivity().findViewById(R.id.fab_checkAll);

                RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapterList.get(tabPosition));

                checkall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = ShowActivity.viewPager.getCurrentItem();
                        Log.d("HUSTLE", "FAB-checkall was pressed tabPosition " + pos);
                        if (pos != 0)
                            checkAll(adapterList.get(pos));
                    }
                });
            }

            if (ShowActivity.viewPager.getCurrentItem() == 0) {
                checkall.setVisibility(View.INVISIBLE);
            }
            else{
                checkall.setVisibility(View.VISIBLE);
            }

            return v;

        }


        private void checkAll(EpisodeRecyclerAdapter episodeRecyclerAdapter) {
            ArrayList<Episode> episodes = episodeRecyclerAdapter.getEpisodes();
            boolean status = true;     //true=tutti check false=almeno un non-check

            for (Episode i : episodes) {
                if (i.checked==false) {
                    status = false;
                }
            }

            boolean new_status = (status == false) ? true : false;
            for (Episode i : episodes) {
                if (i.checked != new_status){
                    if (!UpdateEpisodeState.changeState(getActivity(), i, null, new_status, null, episodeRecyclerAdapter)){
                        Toast.makeText(getActivity(),"Impossibile effettuare il check-all",Toast.LENGTH_SHORT);
                        Log.d("HUSTLE", "Impossibile effettuare il check-all");
                    }
                }

            }


        }
    }

    //sottoclasse per l'adapter per i fragment (delle varie tab)
    class SeasonsAdapter extends FragmentStatePagerAdapter {

        private int number_of_tabs=show.seasonNumber+1;

        public SeasonsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return SeasonsFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return number_of_tabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if (position == 0) {
                return getResources().getString(R.string.tab_show_info);
            }
            int i;
                // break;
            for (i=1; i<=number_of_tabs; i++) {
                if (position == i) {
                    return getResources().getString(R.string.tab_season) + " " + i;
                    // break;
                }
            }
            return "";
        }
    }


    ArrayList<Friend> getFriendList(){
        SharedPreferences options = getSharedPreferences("friend_list", Context.MODE_PRIVATE);
        String friend_list_json_string = options.getString("friend_list", null);
        Log.d("HUSTLE", "lista amici totale: " + friend_list_json_string);
        ArrayList<Friend> return_list = new ArrayList<Friend>();
        if(friend_list_json_string != null){
            try {
                JSONArray friend_list_json = new JSONArray(friend_list_json_string);
                for (int i= 0; i< friend_list_json.length(); i++) {
                    return_list.add(new Friend(friend_list_json.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return return_list;
    }

    void downloadFriendShows(ArrayList<Friend> all_friends, final ArrayList<Friend> friends_list_adapter, final String series_id){
//async task, prende come parametro la lista amici totale e la lista amici vuota


        AsyncTask<ArrayList<Friend>, Void, ArrayList<Friend>> friend_shows_download = new AsyncTask<ArrayList<Friend>, Void, ArrayList<Friend>>() {
            @Override
            protected ArrayList<Friend> doInBackground(ArrayList<Friend> ...params) {
                String s=null;
                JSONArray friendshowsJSON = null;
                ArrayList<Friend> all_friends = params[0];
                ArrayList<Friend> show_friends = params[1];
                Friend actual = null;
                String user_id = null;


                for (int i= 0; i < all_friends.size(); i++){
                    actual = all_friends.get(i);
                    user_id = actual.id;

                    try {
                        Uri builtUri = Uri.parse("http://hustle.altervista.org/getSeries_bis.php?").
                                buildUpon().
                                appendQueryParameter("user_id_short", user_id).
                                appendQueryParameter("seriesid_short", series_id).
                                appendQueryParameter("language", Locale.getDefault().getLanguage()).
                                build();
                        String u = builtUri.toString();
                        Log.d("FRIEND", "requesting: " + u);
                        URL url = new URL(u);
                        //URL url = new URL("http://hustle.altervista.org/getSeries_bis.php?user_id_short=" + user_id + "&seriesid_short=" + series_id);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        InputStream in = new BufferedInputStream(conn.getInputStream());
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        s = br.readLine();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    //creazione array dalla risposta
                    try {
                        friendshowsJSON= new JSONArray(s);
                        if(friendshowsJSON.length() >= 1){
                            Log.d("HUSTLE", "utente " + actual.name + " segue la serie");
                            show_friends.add(actual);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                return show_friends;

            }


            @Override
            protected void onPostExecute(ArrayList<Friend> show_friends) {
                super.onPostExecute(show_friends);

                adapter_friends.notifyDataSetChanged();
                doGetInfoFriendAll(show_friends, show.title);

            }
        };

        if (CheckConnection.isConnected(this)) {
            friend_shows_download.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, all_friends, friends_list_adapter);
        }
    }


}
