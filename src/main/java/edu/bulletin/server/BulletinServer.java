package edu.bulletin.server;

import edu.bulletin.entities.ServerConfiguration;
import edu.bulletin.entities.ServerState;
import lombok.extern.log4j.Log4j2;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class BulletinServer extends Thread {
    private final ServerState serverState = new ServerState();
    private final AtomicInteger numOfClients = new AtomicInteger(0);
    private final LogFileHandler logFileHandler = new LogFileHandler();

    public BulletinServer() {
        super.start();
    }

    @Override
    public void run() {
        final ServerConfiguration config = ServerConfiguration.getInstance();
        final int clientsCount = config.getNumOfAccess() * (config.getNumOfReaders() + config.getNumOfWriters());
        final List<WorkerThread> workerThreads = new ArrayList<>();
        try {
            final ServerSocket serverSocket = new ServerSocket(config.getPort());
            for (int i = 0; i < clientsCount; ++i) {
                final Socket clientSocket = serverSocket.accept();
                log.info("client connected spawning worker thread");
                workerThreads.add(new WorkerThread(serverState, clientSocket, numOfClients.incrementAndGet(), logFileHandler));
            }

            workerThreads.forEach(workerThread -> {
                try {
                    workerThread.join();
                } catch (InterruptedException e) {
                    log.error("interrupted while waiting for worker thread");
                }
            });

        } catch (final Exception e) {
            log.error("failed to create server socket", e);
        }
        logFileHandler.close();
    }
}
