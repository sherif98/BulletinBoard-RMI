import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

public class Reader {

    interface IStore extends Remote {
        ReaderResult readNews(int clientId) throws RemoteException;
        WriterResult write(int newsValue) throws RemoteException;
    }

    class ReaderResult implements Serializable {
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

    private static final String CLIENT_TYPE = "reader";

    private String hostName;
    private int portNumber;
    private int clientId;
    private static FileWriter fileWriter;

    public Reader(String hostName, int portNumber, int clientId) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.clientId = clientId;
    }

    /**
     * @param args args[0] represents the host name of the server
     *             args[1] reprensets the port number of the server
     *             args[2] represents the number of accesses
     *             args[3] represents id of the client
     */
    public static void main(String[] args) {

        try {
            fileWriter = new FileWriter(new File("log" + args[3] + ".txt"));
            fileWriter.write("rSeq\tsSeq\toVal\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < Integer.valueOf(args[2]); i++) {
            new Reader(args[0], Integer.valueOf(args[1]), Integer.valueOf(args[3])).run();
            try {
                Thread.sleep(new Random().nextInt(10_000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            final Registry registry = LocateRegistry.getRegistry(hostName, portNumber);
            final IStore store = (IStore) registry.lookup("store");
            final ReaderResult result = store.readNews(clientId);
            writeToReaderFile(result);
        } catch (final Exception e) {
            System.out.println("error while creating client socket");
        }
    }

    private static void writeToReaderFile(final ReaderResult result) {
        try {
            fileWriter.write(String.format("%4d\t%4d\t%4d\n", result.rSeq, result.sSeq, result.news));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




