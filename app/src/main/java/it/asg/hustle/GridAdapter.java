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
 * Created by Edwin on 28/02/2015.
 */
public class GridAdapter  extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    List<GridItem> mItems;

    public GridAdapter() {
        super();
        mItems = new ArrayList<GridItem>();
        GridItem i1 = new GridItem();
        i1.setName("Show1");
        i1.setThumbnail(R.drawable.unknown1);
        i1.setShow(new Show("item1"));
        mItems.add(i1);

        GridItem i2 = new GridItem();
        i2.setName("Show2");
        i2.setThumbnail(R.drawable.unknown1);
        mItems.add(i2);

        GridItem i3 = new GridItem();
        i3.setName("Show3");
        i3.setThumbnail(R.drawable.unknown1);
        mItems.add(i3);

        GridItem i4 = new GridItem();
        i4.setName("Show4");
        i4.setThumbnail(R.drawable.unknown1);
        mItems.add(i4);

        GridItem i5 = new GridItem();
        i5.setName("Show5");
        i5.setThumbnail(R.drawable.unknown1);
        mItems.add(i5);

        GridItem i6 = new GridItem();
        i6.setName("Show6");
        i6.setThumbnail(R.drawable.unknown1);
        mItems.add(i6);

        GridItem i7 = new GridItem();
        i7.setName("Show7");
        i7.setThumbnail(R.drawable.unknown1);
        mItems.add(i7);

        GridItem i8 = new GridItem();
        i8.setName("Show8");
        i8.setThumbnail(R.drawable.unknown1);
        mItems.add(i8);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final GridItem item = mItems.get(i);
        viewHolder.tvspecies.setText(item.getName());
        viewHolder.imgThumbnail.setImageResource(item.getThumbnail());

        viewHolder.imgThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context= v.getContext();
                Intent intent = new Intent(context, ShowActivity.class);
                // TODO: add the show to the intent
                // Show s = item. ....
                // intent.putExtra("show", ... );
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgThumbnail;
        public TextView tvspecies;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView)itemView.findViewById(R.id.img_thumbnail);
            tvspecies = (TextView)itemView.findViewById(R.id.tv_species);
        }
    }
}