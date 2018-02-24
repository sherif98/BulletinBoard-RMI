package edu.bulletin.server;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LoggerTest {
    public void log() {
        log.warn("hello world");
    }

    public static void main(String[] args) {
        final LoggerTest test = new LoggerTest();
        test.log();
    }
}
