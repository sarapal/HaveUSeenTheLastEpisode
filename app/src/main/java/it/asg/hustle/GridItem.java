package it.asg.hustle;

import android.graphics.Bitmap;

import it.asg.hustle.Info.Show;

public class GridItem {
    private String mName;
    private Bitmap mThumbnail;
    private Show mShow;

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
}