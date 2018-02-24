package edu.bulletin.entities;

public class SharedNews {
    private int newsValue;

    public SharedNews(final int newsValue) {
        this.newsValue = newsValue;
    }

    public int getNewsValue() {
        return this.newsValue;
    }

    synchronized public void setNewsValue(final int newsValue) {
        this.newsValue = newsValue;
    }
}
