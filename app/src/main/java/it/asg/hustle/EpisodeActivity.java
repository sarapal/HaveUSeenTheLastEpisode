package it.asg.hustle;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import it.asg.hustle.Info.Episode;
import it.asg.hustle.Utils.UpdateEpisodeState;

public class EpisodeActivity extends AppCompatActivity {
    private String LOG_TAG = "ActivityFacebook";
    private Episode ep ;
    private FloatingActionButton fabCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        Bundle b = getIntent().getExtras();
        try {
            JSONObject jo = new JSONObject(b.getString("episode"));
            ep = new Episode(jo);
            ep.bmp = b.getParcelable("picture");

            ImageView iv_epImg = (ImageView) findViewById(R.id.episode_image);

            collapsingToolbar.setTitle(ep.title);
            if(ep.bmp!=null){
                iv_epImg.setImageBitmap(ep.bmp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // aggiornare stato del checkbox al click del FAB
        fabCheck = (FloatingActionButton) findViewById(R.id.fab_check);
        if (ep.checked){
            fabCheck.setImageResource(R.drawable.ic_close);
        }else{
            fabCheck.setImageResource(R.drawable.ic_done);
        }
        fabCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HUSTLE", "FAB (in EpisodeActivity) was pressed");
                if(!UpdateEpisodeState.changeState(getApplicationContext(),ep,null,!ep.checked,fabCheck)){
                    Toast.makeText(getApplicationContext(),"Impossibile selezionare l'elemento. Effettuare il login",Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        Intent i = getIntent();
        i.putExtra("status",ep.checked);
        i.putExtra("episode_num",ep.episodeNumber);
        i.putExtra("season",ep.season);

        //Log.d("HUSTLE", "back! " + ep.checked);
        setResult(Activity.RESULT_OK, i);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_episode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
