
import java.io.Serializable;


public class ReaderResult implements Serializable {
    private int news;
    private int sSeq;
    private int rSeq;

    public int getrSeq() {
        return rSeq;
    }

    public void setrSeq(int rSeq) {
        this.rSeq = rSeq;
    }

    public int getNews() {
        return news;
    }

    public void setNews(int news) {
        this.news = news;
    }

    public int getsSeq() {
        return sSeq;
    }

    public void setsSeq(int sSeq) {
        this.sSeq = sSeq;
    }

}
