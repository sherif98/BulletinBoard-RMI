package edu.bulletin.server;

import edu.bulletin.entities.ServerState;

import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicInteger;

public class Store implements IStore {
    private final ServerState serverState = new ServerState();
    private final LogFileHandler logFileHandler;
    private final AtomicInteger numOfClients = new AtomicInteger(0);

    public Store(final LogFileHandler logFileHandler) {
        this.logFileHandler = logFileHandler;
    }

    @Override
    public ReaderResult readNews(int clientId) throws RemoteException {
        try {
            final ReaderResult result = new ReaderResult();
            serverState.getNumOfReaders().incrementAndGet();
            serverState.getSharedNews().lockRead();
            result.setNews(serverState.getSharedNews().getNewsValue()); //oVal
            int sSeq = serverState.getSequenceNumber().incrementAndGet();
            result.setsSeq(sSeq); //sSeq
            result.setrSeq(numOfClients.incrementAndGet());
            readerLog(clientId, sSeq);
            serverState.getSharedNews().unlockRead();

            serverState.getNumOfReaders().decrementAndGet();
            return result;
        } catch (final Exception e) {
            throw new RemoteException("Error happened");
        }
    }

    // TODO Thread.sleep if results are bad.
    @Override
    public WriterResult write(int clientId) throws RemoteException {
        try {
            WriterResult result = new WriterResult();
            serverState.getNumOfWriters().incrementAndGet();
            serverState.getSharedNews().lockWrite();
            serverState.getSharedNews().setNewsValue(clientId);
            int writeSSeq = serverState.getSequenceNumber().incrementAndGet();
            result.setsSeq(writeSSeq); //sSeq
            result.setrSeq(numOfClients.incrementAndGet());
            writerLog(clientId, writeSSeq);
            serverState.getSharedNews().unlockWrite();
            serverState.getNumOfWriters().decrementAndGet();
            return result;
        } catch (final Exception e) {
            throw new RemoteException("Error happend");
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
