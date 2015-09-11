package it.asg.hustle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.asg.hustle.Info.Episode;
import it.asg.hustle.Info.Season;

/**
 * Created by sara on 8/26/15.
 */
public class EpisodeRecyclerAdapter extends RecyclerView.Adapter<EpisodeRecyclerAdapter.ViewHolder> {

    //private List<String> mItems;
    private ArrayList<Episode> episodes;

    //EpisodeRecyclerAdapter(List<String> items) {
    //    mItems = items;
    //}
    EpisodeRecyclerAdapter(Season season) {
        if (season.episodesList != null) {
            episodes = season.episodesList;
        }
        else{
            episodes = new ArrayList<Episode>();
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        String item = episodes.get(i).title;
        viewHolder.mTextView.setText(item);

        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context= v.getContext();
                context.startActivity(new Intent(context,EpisodeActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;

        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.list_item);
        }

    }
}

