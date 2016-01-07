package it.asg.hustle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it.asg.hustle.Info.Friend;

/**
 * Created by andrea on 15/09/15.
 */

public class FriendsAdapter extends RecyclerView
        .Adapter<FriendsAdapter
        .FriendHolder> {
    private Context context;
    private ArrayList<Friend> friends;


    public static class FriendHolder extends RecyclerView.ViewHolder{
        TextView friend_name;
        com.facebook.login.widget.ProfilePictureView friend_photo;

        public FriendHolder(View itemView) {
            super(itemView);
            friend_name = (TextView) itemView.findViewById(R.id.friend_name);
            friend_photo = (com.facebook.login.widget.ProfilePictureView) itemView.findViewById(R.id.friend_id_picture);
            friend_photo.setProfileId(null);
        }
    }

    public FriendsAdapter(ArrayList<Friend> myDataset, Context context) {
        friends = myDataset;

        this.context=context;
    }

    @Override
    public FriendHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_friend_item, parent, false);


        FriendHolder dataObjectHolder = new FriendHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final FriendHolder holder, final int position) {
        holder.friend_name.setText(friends.get(position).getName());
        holder.friend_photo.setProfileId(friends.get(position).getId());

        holder.friend_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,FriendActivity.class);
                i.putExtra("id", friends.get(position).getId());
                i.putExtra("name", friends.get(position).getName());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //Log.d("HUSTLE","HAI CLICCATO SU UN AMICO");
                context.startActivity(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return friends.size();
    }

}