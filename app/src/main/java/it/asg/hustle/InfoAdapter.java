package it.asg.hustle;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.asg.hustle.Info.Show;

public class InfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> mDataset;
    private Show show;
    public static int DESCRITIONCARD =  0;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextView =(TextView) itemView.findViewById(R.id.info_text);
        }
    }

    public static class DescriptionCard extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView overviewTV;
        public DescriptionCard(View itemView) {
            super(itemView);
            overviewTV =(TextView) itemView.findViewById(R.id.card_description_text);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public InfoAdapter( Show show_in) {
        ArrayList info = new ArrayList<String>();
        info.add("ciao1");
        info.add("ciao 3");
        info.add("ciao 3");
        info.add("ciao 3");
        info.add("ciao 3");
        info.add("ciao 3");
        info.add("ciao 3");
        info.add("ciao 3");
        info.add("ciao 3");
        info.add("ciao 3");
        info.add("ciao 3");
        show = show_in;
        mDataset = info;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        DescriptionCard dc;
        if(viewType == DESCRITIONCARD){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_description, parent, false );
            return (new DescriptionCard(v));
        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_info, parent, false);

            // set the view's size, margins, paddings and layout parameters

            return new ViewHolder(v);

        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(holder.getItemViewType() == DESCRITIONCARD){
            DescriptionCard description = (DescriptionCard) holder;
            description.overviewTV.setText(show.overview);

        }
        else {
            ViewHolder viewholder = (ViewHolder) holder;

            viewholder.mTextView.setText(mDataset.get(position));
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return 0;
        }
        else{
            return 1;
        }
    }

}