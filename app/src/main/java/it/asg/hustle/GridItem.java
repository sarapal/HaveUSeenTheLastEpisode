package it.asg.hustle;

import android.graphics.Bitmap;

import it.asg.hustle.Info.Show;
import it.asg.hustle.Interfaces.ThumbnailViewer;

public class GridItem implements ThumbnailViewer,Comparable<GridItem> {
    private String mName;
    private Bitmap mThumbnail;
    private Show mShow;
    private int friends;

    @Override
    public boolean equals(Object o) {
        GridItem gridItem = (GridItem) o;
        if(gridItem.getName().compareTo(this.getName())==0){return true;}
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;



        if (!mName.equals(gridItem.mName)) return false;
        if (mThumbnail != null ? !mThumbnail.equals(gridItem.mThumbnail) : gridItem.mThumbnail != null)
            return false;
        return mShow.equals(gridItem.mShow);

    }

    public void addFriend(){
        this.friends++;
    }

    public int getFriends() {return this.friends;}

    public GridItem() {
        this.friends=0;
    }

    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + (mThumbnail != null ? mThumbnail.hashCode() : 0);
        result = 31 * result + mShow.hashCode();
        return result;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setShow(Show show) {
        this.mShow = show;
    }

    public Show getShow()
    {
        return this.mShow;
    }

    public Bitmap getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.mThumbnail = thumbnail;
    }

    @Override
    public int compareTo(GridItem o) {
        return  o.friends-this.friends ;
    }


}