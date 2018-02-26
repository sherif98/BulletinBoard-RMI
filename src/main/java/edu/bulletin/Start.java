package edu.bulletin;

import com.jcraft.jsch.*;
import edu.bulletin.entities.ServerConfiguration;
import edu.bulletin.server.BulletinServer;
import edu.bulletin.server.SSHManager;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class Start {

    private static final String READER = "Reader";
    private static final String WRITER = "Writer";

    public static void main(String[] args) throws JSchException {
        start(args[0]);
    }

    private static void start(String pass) {
        final ServerConfiguration config = ServerConfiguration.getInstance();
        final BulletinServer server = new BulletinServer();
        SSHManager sshManager = new SSHManager();


        int i;
        Optional<String> readerCode = readFile(READER);
        for (i = 0; i < config.getReaderNames().size(); i++) {
            String command =
                    new CommandsBuilder(config.getIp(),
                            config.getPort(),
                            READER,
                            readerCode.get(),
                            i + 1,
                            config.getNumOfAccess())
                            .buildCommands();
            sshManager.executeCommand(config.getReaderNames().get(i), config.getReaderPasswords().get(i), command + "\n");
        }

        Optional<String> writerCode = readFile(WRITER);
        for (int j = 0; j < config.getNumOfWriters(); j++) {
            String command =
                    new CommandsBuilder(config.getIp(),
                            config.getPort(),
                            WRITER,
                            writerCode.get(),
                            ++i,
                            config.getNumOfAccess())
                            .buildCommands();
            sshManager.executeCommand(config.getWriterNames().get(j), config.getWriterPasswords().get(j), command + "\n");
        }


        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static Optional<String> readFile(String fileName) {
        String path = "./src/main/java/" + fileName + ".java";
        try {
            return Optional.ofNullable(Files.lines(Paths.get(path)).collect(Collectors.joining("\n")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}

class CommandsBuilder {

    private String className;
    private String code;
    private String hostName;
    private int portNumber;
    private int clientId;
    private int numberOfAccess;

    public CommandsBuilder(String hostName, int portNumber, String className, String code, int clientId, int numberOfAccess) {
        this.className = className;
        this.code = code;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.clientId = clientId;
        this.numberOfAccess = numberOfAccess;
    }

    public String buildCommands() {
        return Stream.of(
                buildRemoveCommand(),
                buildMakeDirCommand(),
                buildEchoCommand(),
                buildCompileCommand(),
                buildExecuteCommand())
                .collect(Collectors.joining(";"));
    }

    private String buildRemoveCommand() {
        return String.format("rm -rf %s%d || true", className, clientId);
    }

    private String buildMakeDirCommand() {
        return String.format("mkdir %s%d; cd %s%d", className, clientId, className, clientId);
    }

    private String buildEchoCommand() {
        return String.format("echo \'%s\' >> %s.java", code, className);

    }

    private String buildCompileCommand() {
        return String.format("javac %s.java", className);
    }

    private String buildExecuteCommand() {
        return String.format("java %s %s %d %d %d", className, hostName, portNumber, numberOfAccess, clientId);
    }
}