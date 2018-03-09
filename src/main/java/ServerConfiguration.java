import lombok.Data;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
            serverConfiguration.setPort(properties.getInt("RW.rmiregistry.port"));
            serverConfiguration.setNumOfReaders(properties.getInt("RW.numberOfReaders"));
            for (int i = 0; i < serverConfiguration.numOfReaders; ++i) {
                serverConfiguration.readerNames.add(properties.getString("RW.reader" + i));
                serverConfiguration.readerPasswords.add(properties.getString("RW.reader" + i + ".pass"));

            }
            serverConfiguration.setNumOfWriters(properties.getInt("RW.numberOfWriters"));
            for (int i = 0; i < serverConfiguration.numOfWriters; ++i) {
                serverConfiguration.writerNames.add(properties.getString("RW.writer" + i));
                serverConfiguration.writerPasswords.add(properties.getString("RW.writer" + i + ".pass"));
            }
            serverConfiguration.setNumOfAccess(properties.getInt("RW.numberOfAccesses"));
        } catch (final Exception ex) {
            System.out.println("Failed to read system.properties");
        }
    }

    private ServerConfiguration() {
    }

    public static ServerConfiguration getInstance() {
        return serverConfiguration;
    }

    private int port;
    private int numOfReaders;
    private int numOfWriters;
    private int numOfAccess;
    private String ip;

    private List<String> readerNames = new ArrayList<>();
    private List<String> writerNames = new ArrayList<>();
    private List<String> readerPasswords = new ArrayList<>();
    private List<String> writerPasswords = new ArrayList<>();
}
