

import lombok.extern.log4j.Log4j2;

import java.rmi.RemoteException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class Store implements IStore {
    private final ServerState serverState = new ServerState();
    private final LogFileHandler logFileHandler;
    private final AtomicInteger numOfClients = new AtomicInteger(0);

    public Store(final LogFileHandler logFileHandler) {
        this.logFileHandler = logFileHandler;
    }

    @Override
    public ReaderResult readNews(int clientId) throws RemoteException {
        log.info("reading news client id {}", clientId);
        try {
            final ReaderResult result = new ReaderResult();
            serverState.getNumOfReaders().incrementAndGet();
            result.setrSeq(numOfClients.incrementAndGet());
            Thread.sleep(new Random().nextInt(10_000));
            serverState.getSharedNews().lockRead();
            result.setNews(serverState.getSharedNews().getNewsValue()); //oVal
            int sSeq = serverState.getSequenceNumber().incrementAndGet();
            result.setsSeq(sSeq); //sSeq
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
        log.info("writing news client id {}", clientId);
        try {
            WriterResult result = new WriterResult();
            serverState.getNumOfWriters().incrementAndGet();
            result.setrSeq(numOfClients.incrementAndGet());
            Thread.sleep(new Random().nextInt(10_000));
            serverState.getSharedNews().lockWrite();
            serverState.getSharedNews().setNewsValue(clientId);
            int writeSSeq = serverState.getSequenceNumber().incrementAndGet();
            result.setsSeq(writeSSeq); //sSeq
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
