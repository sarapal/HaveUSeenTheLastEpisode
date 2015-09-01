package it.asg.hustle;

public class GridItem {
    private String mName;
    private int mThumbnail;
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

    public int getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.mThumbnail = thumbnail;
    }
}