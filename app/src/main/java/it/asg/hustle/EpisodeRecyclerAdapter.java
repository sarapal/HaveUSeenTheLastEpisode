package it.asg.hustle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import it.asg.hustle.Info.Episode;
import it.asg.hustle.Info.Season;
import it.asg.hustle.Utils.UpdateEpisodeState;

/**
 * Created by sara on 8/26/15.
 */
public class EpisodeRecyclerAdapter extends RecyclerView.Adapter<EpisodeRecyclerAdapter.ViewHolder> {

    private Bitmap posterBitmap = null;
    private ImageView posterImageView;
    private ArrayList<Episode> episodes;

    private Context context = null;
    private Activity activity = null;

    private Display display;
    public static final int EP_CHANGED = 1;

    public EpisodeRecyclerAdapter(Context c, Season season) {
        this.context = c;
        if (season.episodesList != null) {
            episodes = season.episodesList;
        }
        else{
            episodes = new ArrayList<Episode>();
        }
    }

    public EpisodeRecyclerAdapter(Context c, Activity a, Season season) {
        this.context = c;
        this.activity = a;
        if (season.episodesList != null) {
            episodes = season.episodesList;
        }
        else{
            episodes = new ArrayList<Episode>();
        }
    }

    public EpisodeRecyclerAdapter(Season season) {
        if (season.episodesList != null) {
            episodes = season.episodesList;
        }
        else{
            episodes = new ArrayList<Episode>();
        }

    }

    public ArrayList<Episode> getEpisodes() {
        return this.episodes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        Bitmap b = episodes.get(i).bmp;
        final String item = episodes.get(i).title;

        //disposizione elementi dell'episodio in base alla dimensione dello schermo
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        int dimPX = getDisplayDimensionsPX(display);

        viewHolder.epImg.setMaxWidth(dimPX/4);
        viewHolder.mTextView.setWidth(dimPX/2);
        //viewHolder.cb.setWidth(dimPX/4);

        // Imposta la TextView con indice episodio: titolo episodio
        viewHolder.mTextView.setText((i+1)+": "+item);

        final Episode ep = episodes.get(i);
        // Prende l'url dell'immagine dell'episodio
        String imgURL = ep.bmpPath;
        // AsyncTask per scaricare l'immagine dell'episodio
        AsyncTask<String, Void, Bitmap> at = new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bm = null;
                InputStream in = null;
                try {
                    in = new java.net.URL(params[0]).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bm = BitmapFactory.decodeStream(in);
                return bm;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                // Imposta l'immagine nella ImageView
                viewHolder.epImg.setImageBitmap(bitmap);
                // salva l'oggetto BitMap nell'oggetto Episode
                ep.bmp = bitmap;
                // Imposta la TextView con indice episodio: titolo episodio
                viewHolder.mTextView.setText((i+1)+": "+item);
                // Imposta l'altezza della TextView con il titolo dell'episodio alla stessa altezza
                // dell'immagine (cosi il testo viene centrato verticalmente)
                //viewHolder.mTextView.setHeight(viewHolder.epImg.getHeight());
                // Lo fa anche per la checkbox
                //viewHolder.cb.setHeight(viewHolder.epImg.getHeight());

            }
        };

        // Se l'immagine dell'episodio non è stata scaricata
        if (ep.bmp == null) {
            Log.d("HUSTLE", "Downloading image: " + ep.bmpPath);
            // La scarica su un thread separato, ma solo se l'URL è diverso
            // da quello qui sotto (che significa che l'episodio non ha banner)
            if (ep.bmpPath == null) {
                return;
            }
            if (!ep.bmpPath.equals("http://thetvdb.com/banners/"))
                at.execute(ep.bmpPath);
        } else {
            // Se l'immagine dell'episodio è già stata salvata, riusa quella
            viewHolder.epImg.setImageBitmap(ep.bmp);
            //viewHolder.mTextView.setHeight(viewHolder.epImg.getHeight());
            //viewHolder.cb.setHeight(viewHolder.epImg.getHeight());
        }
        // mette check o uncheck per l'episodio a seconda del valore che ha l'oggetto Episode
        // se i dati erano scaricati dal server quando l'user era loggato, ogni episodio
        // ha nel JSON il campo "seen" impostato a true o false se l'utente l'ha visto oppure no
        viewHolder.cb.setChecked(ep.checked);

        // Cliccando sulla checkbox si invia al server una richiesta per cambiare lo stato
        // dell'episodio da visto a non visto e viceversa
        viewHolder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HUSTLE", "check (In EpisodeRecyclerAdapter)");
                // Invia dati al server esterno e cambia l'oggetto ep sulla base di come è impostata la cb
                boolean newState = viewHolder.cb.isChecked();
                Log.d("HUSTLE", "Nuovo stato per episodio: " + newState);
                if (!UpdateEpisodeState.changeState(context, ep, viewHolder.cb, newState, null)) {
                    // Qui posso cambiare lo stato della cb
                    viewHolder.cb.setChecked(!newState);
                    // avvisa l'utente che non è possibile cambiare lo stato dell'episodio
                    Toast.makeText(context, "Impossibile cambiare lo stato dell'episodio se non si effettua prima il login", Toast.LENGTH_LONG).show();
                }
                Log.d("HUSTLE", "status ep.checked (EpisodeRecyclerAdapter): " + ep.checked.toString());

            }
        });

        // Se clicchi sull'img dell'episodio apre EpisodeActivity
        viewHolder.epImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent(context, EpisodeActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("picture", ep.bmp);
                b.putString("episode", ep.source.toString());
                i.putExtras(b);
                activity.startActivityForResult(i, EP_CHANGED);
            }
        });
        // Se clicchi sul testo dell'episodio apre EpisodeActivity
        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent(context, EpisodeActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("picture", ep.bmp);
                b.putString("episode", ep.source.toString());
                i.putExtras(b);
                activity.startActivityForResult(i, EP_CHANGED);
            }
        });

    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;
        private final ImageView epImg;
        private final CheckBox cb;


        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.list_item);
            epImg = (ImageView) v.findViewById(R.id.ep_img);
            cb= (CheckBox) v.findViewById(R.id.checkEp);

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            display = wm.getDefaultDisplay();
            int dimPX = getDisplayDimensionsPX(display);

            mTextView.setWidth(dimPX/2);
            //epImg.setMaxWidth(20);
            //cb.setWidth(dimPX / 4);

        }
    }


    public int getDisplayDimensionsPX(Display d){
        Point size = new Point();
        d.getSize(size);
        int widthPX = size.x;

        return widthPX;
    }

}

