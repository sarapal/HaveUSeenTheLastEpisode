package it.asg.hustle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sara on 8/26/15.
 */
public class SearchShowRecyclerAdapter extends RecyclerView.Adapter<SearchShowRecyclerAdapter.ViewHolder> {

    private ArrayList<Show> shows;

    SearchShowRecyclerAdapter(ArrayList<Show> shows) {
        this.shows = shows;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_show_row, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Show item = shows.get(i);

        viewHolder.mTextView.setText(item.title + " (" + item.language + ")");
        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context= v.getContext();
                // TODO: lancia ShowActivity passandogli l'id della serie nell'intent
                //context.startActivity(new Intent(context,EpisodeActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return shows.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;
        private final ImageView mImageView;

        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.title);
            mImageView = (ImageView) v.findViewById(R.id.poster);
        }

    }
}