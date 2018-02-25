
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Reader {

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
            Socket clientSocket = new Socket(hostName, portNumber);
            PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream());
            printWriter.println(CLIENT_TYPE);
            printWriter.println(this.clientId);
            printWriter.flush();

            Scanner in = new Scanner(clientSocket.getInputStream());
            int rSeq = in.nextInt();
            int oVal = in.nextInt();
            int sSeq = in.nextInt();
            writeToReaderFile(rSeq, oVal, sSeq);

            printWriter.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("error while creating client socket");
        }
    }

    private static void writeToReaderFile(int rSeq, int oVal, int sSeq) {
        try {
            fileWriter.write(String.format("%4d\t%4d\t%4d\n", rSeq, sSeq, oVal));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
