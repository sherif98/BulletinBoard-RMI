package edu.bulletin.server;

import java.io.Serializable;

public class WriterResult implements Serializable {
    private int sSeq;
    private int rSeq;

    public int getsSeq() {
        return sSeq;
    }

    public void setsSeq(int sSeq) {
        this.sSeq = sSeq;
    }

    public int getrSeq() {
        return rSeq;
    }

    public void setrSeq(int rSeq) {
        this.rSeq = rSeq;
    }
}
