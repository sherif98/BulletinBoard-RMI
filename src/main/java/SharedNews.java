public class SharedNews {

    private int readers = 0;
    private int writers = 0;
    private int writeRequests = 0;
    private int newsValue = -1;

    public synchronized void lockRead() throws InterruptedException {
        while (writers > 0 || writeRequests > 0) {
            wait();
        }
        readers++;
    }

    public int getNewsValue() {
        return this.newsValue;
    }

    public void setNewsValue(final int newsValue) {
        this.newsValue = newsValue;
    }

    public synchronized void unlockRead() {
        readers--;
        notifyAll();
    }

    public synchronized void lockWrite() throws InterruptedException {
        writeRequests++;

        while (readers > 0 || writers > 0) {
            wait();
        }
        writeRequests--;
        writers++;
    }

    public synchronized void unlockWrite() throws InterruptedException {
        writers--;
        notifyAll();
    }
}
