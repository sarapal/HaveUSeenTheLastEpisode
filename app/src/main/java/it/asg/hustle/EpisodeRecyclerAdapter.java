package it.asg.hustle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import it.asg.hustle.Info.Episode;
import it.asg.hustle.Info.Season;
import it.asg.hustle.Utils.UpdateEpisodeState;

/**
 * Created by sara on 8/26/15.
 */
public class EpisodeRecyclerAdapter extends RecyclerView.Adapter<EpisodeRecyclerAdapter.ViewHolder> {
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
        int maxImageWidth = dimPX/4;

        //imposto larghezze per farle entrare nella riga
        viewHolder.epImg.setMaxWidth(maxImageWidth);
        viewHolder.mTextView.setWidth(dimPX/2);
        //viewHolder.cb.setWidth(dimPX/4);

        viewHolder.cb.setWidth(dimPX/12);

        // Imposta la TextView con indice episodio: titolo episodio
        viewHolder.mTextView.setText((i+1)+": "+item);
        if (episodes.get(i).watchingFriends.size() != 0) {
            viewHolder.numberFriends.setVisibility(View.VISIBLE);
            viewHolder.numberFriends.setText(episodes.get(i).watchingFriends.size() + "");
        }
        else{
            viewHolder.numberFriends.setText("0");
            viewHolder.numberFriends.setVisibility(View.INVISIBLE);
        }

        final Episode ep = episodes.get(i);
        // Prende l'url dell'immagine dell'episodio
        String imgURL = ep.bmpPath;
        /*
        // Se l'immagine dell'episodio non è stata scaricata
        if (ep.bmp == null) {
            // La scarica su un thread separato, ma solo se l'URL è diverso
            // da quello qui sotto (che significa che l'episodio non ha banner)
            if (ep.bmpPath == null) {
                return;
            }
            if (!ep.bmpPath.equals("http://thetvdb.com/banners/")) {
                at.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ep.bmpPath);
            }
        } else {
            // Se l'immagine dell'episodio è già stata salvata, riusa quella
            viewHolder.epImg.setImageBitmap(ep.bmp);
        }*/

        if (!ep.bmpPath.equals("http://thetvdb.com/banners/")) {
            //new ImageDownloader(context, maxImageWidth, maxImageWidth).download(imgURL, viewHolder.epImg, ep);
            Picasso.with(context).load(imgURL).placeholder(R.drawable.placeholder).resize(maxImageWidth, maxImageWidth / 2).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    viewHolder.epImg.setImageBitmap(bitmap);
                    ep.setThumbnail(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        } else {
            Picasso.with(context).load(R.drawable.placeholder).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).resize(maxImageWidth, maxImageWidth/2).into(viewHolder.epImg);
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
                //Log.d("HUSTLE", "check (In EpisodeRecyclerAdapter)");
                // Invia dati al server esterno e cambia l'oggetto ep sulla base di come è impostata la cb
                boolean newState = viewHolder.cb.isChecked();
                //Log.d("HUSTLE", "Nuovo stato per episodio: " + newState);
                if (!UpdateEpisodeState.changeState(context, ep, viewHolder.cb, newState, null, null)) {
                    // Qui posso cambiare lo stato della cb
                    viewHolder.cb.setChecked(!newState);
                    // avvisa l'utente che non è possibile cambiare lo stato dell'episodio
                    Toast.makeText(context, "Impossibile cambiare lo stato dell'episodio se non si effettua prima il login", Toast.LENGTH_LONG).show();
                }
                //Log.d("HUSTLE", "status ep.checked (EpisodeRecyclerAdapter): " + ep.checked.toString());

            }
        });

        // Cliccando sulla cardview, apre EpisodeActivity
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent(context, EpisodeActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("picture", ep.getThumbnail());
                b.putString("episode", (ep.toJSON()).toString());
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
        private final TextView numberFriends;
        private final ImageView epImg;
        private final CheckBox cb;
        private final CardView cardView;
        private final ImageView friendsIcon;



        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.list_item);
            numberFriends = (TextView) v.findViewById(R.id.friends_number_episode);
            epImg = (ImageView) v.findViewById(R.id.ep_img);
            cb= (CheckBox) v.findViewById(R.id.checkEp);
            cardView = (CardView) v.findViewById(R.id.cardview);
            friendsIcon = (ImageView) v.findViewById(R.id.ic_friends);

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

