package edu.bulletin.server;

import edu.bulletin.entities.ServerState;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

@Log4j2
public class WorkerThread extends Thread {
    private static final String READER = "reader";
    private static final String WRITER = "writer";

    private final ServerState serverState;
    private final Socket clientSocket;
    private final int rSeq;
    private final LogFileHandler logFileHandler;

    public WorkerThread(final ServerState serverState,
                        final Socket clientSocket,
                        final int rSeq,
                        final LogFileHandler logFileHandler) {
        this.serverState = serverState;
        this.clientSocket = clientSocket;
        this.rSeq = rSeq;
        this.logFileHandler = logFileHandler;
        super.start();
    }

    @Override
    public void run() {
        try {
            final Scanner in = new Scanner(clientSocket.getInputStream());
            final String clientType = in.next();
            final int clientId = in.nextInt();
            log.info("client type is {}, client id is {}", clientType, clientId);

            /*
                data sent to the client are the following:
                1 - request sequence number
                2 - news value in case of reader client
                3 - serve sequence number
             */
            final PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            out.println(this.rSeq);
            Thread.sleep(new Random().nextInt(10_000));
            switch (clientType) {
                case READER:
                    serverState.getNumOfReaders().incrementAndGet();


                    serverState.getSharedNews().lockRead();
                    out.println(serverState.getSharedNews().getNewsValue()); //oVal
                    int sSeq = serverState.getSequenceNumber().incrementAndGet();
                    out.println(sSeq); //sSeq
                    readerLog(clientId, sSeq);
                    serverState.getSharedNews().unlockRead();

                    out.close();
                    serverState.getNumOfReaders().decrementAndGet();
                    break;
                case WRITER:
                    serverState.getNumOfWriters().incrementAndGet();

                    serverState.getSharedNews().lockWrite();
                    serverState.getSharedNews().setNewsValue(clientId);
                    int writeSSeq = serverState.getSequenceNumber().incrementAndGet();
                    out.println(writeSSeq); //sSeq
                    writerLog(clientId, writeSSeq);
                    serverState.getSharedNews().unlockWrite();

                    out.close();
                    serverState.getNumOfWriters().decrementAndGet();
                    break;
            }
        } catch (IOException e) {
            System.out.println();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void readerLog(int readerId, int sSeq) {
        logFileHandler.writeToReaderFile(sSeq,
                serverState.getSharedNews().getNewsValue(), readerId, serverState.getNumOfReaders().intValue());
    }

    private void writerLog(int writerId, int sSeq) {
        logFileHandler.writeToWriterFile(sSeq, writerId);
    }
}
