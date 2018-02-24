package edu.bulletin.server;

import edu.bulletin.entities.ServerState;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WorkerThread extends Thread {
    private static final String READER = "reader";
    private static final String WRITER = "writer";

    private final ServerState serverState;
    private final Socket clientSocket;
    private final int seqNum;
    private final LogFileHandler logFileHandler;

    public WorkerThread(final ServerState serverState,
                        final Socket clientSocket,
                        final int seqNum,
                        final LogFileHandler logFileHandler) {
        this.serverState = serverState;
        this.clientSocket = clientSocket;
        this.seqNum = seqNum;
        this.logFileHandler = logFileHandler;
        super.start();
    }

    @Override
    public void run() {
        try {
            final Scanner in = new Scanner(clientSocket.getInputStream());
            final String clientType = in.next();
            final int clientId = in.nextInt();
            switch (clientType) {
                case READER:
                    serverState.getNumOfReaders().incrementAndGet();
                    readerLog(clientId);
                    final PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                    out.println(serverState.getSharedNews().getNewsValue());
                    out.close();
                    break;
                case WRITER:
                    serverState.getNumOfWriters().incrementAndGet();
                    writerLog(clientId);
                    serverState.getSharedNews().setNewsValue(clientId);
                    break;
            }
        } catch (IOException e) {
            System.out.println();
        }
    }

    private void readerLog(int readerId) {
        logFileHandler.writeToReaderFile(this.seqNum,
                serverState.getSharedNews().getNewsValue(), readerId, serverState.getNumOfReaders().intValue());
    }

    private void writerLog(int writerId) {
        logFileHandler.writeToWriterFile(this.seqNum, writerId);
    }
}
