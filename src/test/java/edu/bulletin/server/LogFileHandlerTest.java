package edu.bulletin.server;

public class LogFileHandlerTest {
    public static void main(String[] args) {
        final LogFileHandler logFileHandler = new LogFileHandler();
        logFileHandler.writeToWriterFile(1, 10);
        logFileHandler.writeToReaderFile(1, 10, 20, 100);
        logFileHandler.close();
    }
}
