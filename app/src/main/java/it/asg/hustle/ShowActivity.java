package it.asg.hustle;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import android.widget.TextView;

import java.util.ArrayList;

public class ShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        // get toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("TV Show (Title)");

        SeasonsAdapter a = new SeasonsAdapter(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(a);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
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

        switch (id) {
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // sottoclasse per gestire i fragment della pagina inziale
    public static class SeasonsFragment extends Fragment {
        private static final String TAB_POSITION = "tab_position";

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
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            int tabPosition = args.getInt(TAB_POSITION);
            // TODO: modifica questo fragment in modo da mostrare le info sulla serie TV
            ArrayList<String> items = new ArrayList<String>();
            for(int i=0 ; i < 20 ; i++){
                items.add("Element "+i);
            }

            View v = inflater.inflate(R.layout.fragment_episodes_view, container, false);
            RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new EpisodeRecyclerAdapter(items));

            return v;
        }
    }

    //sottoclasse per l'adapter per i fragment (delle varie tab)
    class SeasonsAdapter extends FragmentStatePagerAdapter {

        private int number_of_tabs=2;

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

            switch (position){
                case 0:
                    return getResources().getString(R.string.tab_show_info);
                    // break;
                case 1:
                    return getResources().getString(R.string.tab_season);
                    // break;
            }
            return "";
        }
    }




}
