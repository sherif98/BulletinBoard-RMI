package edu.bulletin.server;

import lombok.extern.log4j.Log4j2;

import java.io.FileWriter;
import java.io.IOException;

@Log4j2
public class LogFileHandler {
    private static final String READER_CLIENTS_FILE_PATH = "server-log/readers.txt";
    private static final String WRITER_CLIENTS_FILE_PATH = "server-log/writers.txt";

    private FileWriter readerClientsFile;
    private FileWriter writerClientFile;

    public LogFileHandler() {
        try {
            readerClientsFile = new FileWriter(READER_CLIENTS_FILE_PATH, true);
            writerClientFile = new FileWriter(WRITER_CLIENTS_FILE_PATH, true);
            readerClientsFile.write("sSeq\toVal\trID     rNum\n");
            writerClientFile.write("sSeq\toVal\twID\n");
        } catch (IOException e) {
            log.warn("failed to create server log files");
        }
    }

    synchronized void writeToReaderFile(final int seqNum,
                                        final int sharedNewsValue,
                                        final int readerId,
                                        final int numOfReaders) {
        try {
            readerClientsFile.write(String.format("%4d\t%4d\t%4d\t%4d\n", seqNum, sharedNewsValue, readerId, numOfReaders));
        } catch (IOException e) {
            log.warn("failed to write to readers file");
        }
    }

    synchronized void writeToWriterFile(final int seqNum, final int sharedNewsValue) {
        try {
            writerClientFile.write(String.format("%4d\t%4d\t%3d\n", seqNum, sharedNewsValue, sharedNewsValue));
        } catch (IOException e) {
            log.warn("failed to write to writers file");
        }
    }

    public void close() {
        try {
            this.readerClientsFile.close();
            this.writerClientFile.close();
        } catch (IOException e) {
            log.warn("failed to close server-log files");
        }
    }
}
