package it.asg.hustle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout myDrawerLayout;    //imposta NavigationDrawer
    private FloatingActionButton fab;
    private NavigationView navigationView;

    private MenuItem SearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    com.facebook.login.widget.ProfilePictureView profilePictureInvisible = null;
    de.hdodenhof.circleimageview.CircleImageView circleImageView = null;
    TextView account_name_facebook_tv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        // imposto ActionBar sulla Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //prendo DrawerLayout
        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //click elementi su NavigationDrawer
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                myDrawerLayout.closeDrawers();
                Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                if(menuItem.getTitle().equals(getResources().getString(R.string.nav_item_login))==true){
                    // accesso facebook
                    Intent intentactivityfacebook = new Intent(MainActivity.this, FacebookActivity.class);
                    startActivity(intentactivityfacebook);

                }
                return true;
            }
        });
        //click del FAB
        fab = (FloatingActionButton) findViewById(R.id.fab_plus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("asg", "FAB was pressed");
                // Launch activity for searching a TV show
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            }
        });

        // Crea un TvShowAdapter
        TvShowAdapter adapter = new TvShowAdapter(getSupportFragmentManager());
        // Prende il ViewPager e imposta come adapter il TvShowAdapter: in base alla tab
        // selezionata, mostra il fragment relativo
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        // Prende il TabLayout e imposta il ViewPager
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        circleImageView = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.circleView);
        profilePictureInvisible = (com.facebook.login.widget.ProfilePictureView)findViewById(R.id.profilePictureInvisible);
        account_name_facebook_tv = (TextView) findViewById(R.id.account_name_facebook);
        updateCircleProfile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                myDrawerLayout.openDrawer(GravityCompat.START);
                updateCircleProfile();
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                handleMenuSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void handleMenuSearch(){
        ActionBar action = getSupportActionBar(); //get the actionbar

        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            //add the search icon in the action bar
            SearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText)action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        String searchingTitle = v.getText().toString();
                        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                        Bundle b = new Bundle();
                        b.putString("SearchTitle", searchingTitle);
                        i.putExtras(b);
                        startActivity(i);
                        return true;
                    }
                    return false;
                }
            });

            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            SearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close));
            isSearchOpened = true;

        }
    }

    //
    @Override
    public void onBackPressed() {
        if(isSearchOpened) {
            handleMenuSearch();
            return;
        }
        super.onBackPressed();
    }

    // for serching button (on Toolbar)
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    // sottoclasse per gestire i fragment della pagina inziale
    public static class TvShowFragment extends Fragment {
        private static final String TAB_POSITION = "tab_position";

        public TvShowFragment() {

        }

        public static TvShowFragment newInstance(int tabPosition) {
            TvShowFragment fragment = new TvShowFragment();
            Bundle args = new Bundle();
            args.putInt(TAB_POSITION, tabPosition);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            int tabPosition = args.getInt(TAB_POSITION);
            /*Log.d("asg","tabPosition "+tabPosition); */
            ArrayList<String> items = new ArrayList<String>();
            for(int i=0 ; i < 50 ; i++){
                items.add("TV-Show "+i);
            }
            View v = inflater.inflate(R.layout.fragment_list_view, container, false);
            RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new ShowRecyclerAdapter(items));

            /*switch (tabPosition){
                case 0:

                    break;
                case 1:
                    break;
                case 2:

                    break;
            }*/
            return v;
        }
    }

    //sottoclasse per l'adapter per i fragment e i titoli (delle varie tab)
    class TvShowAdapter extends FragmentStatePagerAdapter {
        private int number_of_tabs=3;

        public TvShowAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TvShowFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return number_of_tabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getResources().getString(R.string.tab_myshow);
                // break;
                case 1:
                    return getResources().getString(R.string.tab_friends);
                // break;
                case 2:
                    return getResources().getString(R.string.tab_mostviewed);
                // break;
            }
            return "";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCircleProfile();
    }

    void updateCircleProfile(){
        SharedPreferences options = getSharedPreferences("id_facebook", Context.MODE_PRIVATE);
        String id = options.getString("id_facebook", null);

        Log.d("HUSTLE", "Sto per aggiornare, profileID: " + profilePictureInvisible.getProfileId() + " id delle preferenze: " +id);

        if (id != null && (profilePictureInvisible.getProfileId() != id)){
            Log.d("HUSTLE", "GETTING id facebook: " + id);
            profilePictureInvisible.setProfileId(id);
            Log.d("HUSTLE", "Prendo il profile id da invisible blabla: " + profilePictureInvisible.getProfileId());
        }
        else{
            profilePictureInvisible.setProfileId(id);
        }

        Log.d("HUSTLE", "update circle profile");
        //ImageView profileImageView = ((ImageView)profilePictureInvisible.getChildAt(0));
        Bitmap bitmap  = ((BitmapDrawable)((ImageView)profilePictureInvisible.getChildAt(0)).getDrawable()).getBitmap();
        circleImageView.setImageBitmap(bitmap);


        //aggiornamento textview nome
        options = getSharedPreferences("name_facebook", Context.MODE_PRIVATE);
        String name = options.getString("name_facebook", null);

        if (name != null){
            account_name_facebook_tv.setText(name);
        }
        else{
            account_name_facebook_tv.setText(getResources().getString(R.string.account_default_name));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
