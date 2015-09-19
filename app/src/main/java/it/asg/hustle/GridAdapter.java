package it.asg.hustle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.asg.hustle.Info.Show;
import it.asg.hustle.Utils.BitmapHelper;
import it.asg.hustle.Utils.ImageDownloader;

/**
 * Created by sara on 17/09/2015.
 */
public class GridAdapter  extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    ArrayList<GridItem> mItems;
    Context ctx;

    public GridAdapter(Context ctx) {
        super();
        mItems = new ArrayList<GridItem>();
        this.ctx = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void reset() {
        this.mItems.clear();
        this.mItems = new ArrayList<GridItem>();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final GridItem item = mItems.get(i);
        viewHolder.title.setText(item.getName());

        final int reqWidth = (int) ctx.getResources().getDimension(R.dimen.grid_item_ImageView_width);
        final int reqHeight = (int) ctx.getResources().getDimension(R.dimen.grid_item_ImageView_height);

        if (item.getThumbnail() != null) {
            Log.d("HUSTLE", "La bitmap già c'è");
            viewHolder.thumbnail.setImageBitmap(item.getThumbnail());
        } else {
            if (!item.getShow().poster.equals("http://thetvdb.com/banners/")) {
                new ImageDownloader(ctx, reqWidth, reqHeight).download(item.getShow().poster, viewHolder.thumbnail, item);
            }

        }
        viewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ShowActivity.class);
                intent.putExtra("show", item.getShow().source.toString());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView thumbnail;
        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
            title = (TextView)itemView.findViewById(R.id.title);
        }
    }
}