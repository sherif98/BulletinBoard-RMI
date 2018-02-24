package edu.bulletin.entities;

import lombok.Data;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
RW.server=192.168.1.4
RW.server.port=49053
RW.numberOfReaders= 4
RW.reader0=lab204.1.edu
RW.reader1=lab204.2.edu
RW.reader2=machine3
RW.reader3=machine4
RW.numberOfWriters=4
RW.writer0=lab204.4
RW.writer1=machine5
RW.writer2=lab204.6
RW.writer3=machine7
RW.numberOfAccesses= 3
 */
@Data
public final class ServerConfiguration {
    private static final String CONFIG_FILE_NAME = "system.properties";
    private static final ServerConfiguration serverConfiguration = new ServerConfiguration();

    static {
        Configurations configs = new Configurations();
        try {
            final String configPath =
                    ServerConfiguration.class.getClassLoader().getResource(CONFIG_FILE_NAME).getPath();
            System.out.println(configPath);
            final PropertiesConfiguration properties = configs.properties(new File(configPath));
            serverConfiguration.setIp(properties.getString("RW.server"));
            serverConfiguration.setPort(properties.getInt("RW.server.port"));
            serverConfiguration.setNumOfReaders(properties.getInt("RW.numberOfReaders"));
            for (int i = 0; i < serverConfiguration.numOfReaders; ++i) {
                serverConfiguration.readerNames.add(properties.getString("RW.reader" + i));
            }
            serverConfiguration.setNumOfWriters(properties.getInt("RW.numberOfWriters"));
            for (int i = 0; i < serverConfiguration.numOfWriters; ++i) {
                serverConfiguration.writerNames.add(properties.getString("RW.writer" + i));
            }
            serverConfiguration.setNumOfAccess(properties.getInt("RW.numberOfAccesses"));
        } catch (final Exception ex) {
            System.out.println("Failed to read system.properties");
        }
    }

    private ServerConfiguration() {}

    public static ServerConfiguration getInstance() {
        return serverConfiguration;
    }

    private String ip;
    private int port;
    private int numOfReaders;
    private int numOfWriters;
    private int numOfAccess;
    private List<String> readerNames = new ArrayList<>();
    private List<String> writerNames = new ArrayList<>();
}
