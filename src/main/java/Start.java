
import com.jcraft.jsch.JSchException;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class Start {

    private static final String READER = "Reader";
    private static final String WRITER = "Writer";
    private static final String ISTORE = "IStore";
    private static final String WRITER_RESULT = "WriterResult";
    private static final String READER_RESULT = "ReaderResult";


    public static void main(String[] args) throws JSchException {

//        String password = args[0];

        final ServerConfiguration config = ServerConfiguration.getInstance();
        SSHManager sshManager = new SSHManager();
        RmiServer server = new RmiServer();


        int i;
        String readerCode = readFile(READER);
        String writerCode = readFile(WRITER);
        String writerResult = readFile(WRITER_RESULT);
        String readerResult = readFile(READER_RESULT);
        String iStore = readFile(ISTORE);

        List<String> classesNames = Arrays.asList(READER, WRITER, ISTORE, WRITER_RESULT, READER_RESULT);
        List<String> codeList = Arrays.asList(readerCode, writerCode, iStore, writerResult, readerResult);

        for (i = 0; i < config.getReaderNames().size(); i++) {
            String command =
                    new CommandsBuilder(config.getIp(),
                            config.getPort(),
                            classesNames,
                            codeList,
                            i + 1,
                            config.getNumOfAccess(),
                            READER).buildCommands();


            sshManager.executeCommand(config.getReaderNames().get(i), config.getReaderPasswords().get(i), command + "\n");
        }


        for (int j = 0; j < config.getNumOfWriters(); j++) {
            String command =
                    new CommandsBuilder(config.getIp(),
                            config.getPort(),
                            classesNames,
                            codeList,
                            ++i,
                            config.getNumOfAccess(),
                            WRITER).buildCommands();
            sshManager.executeCommand(config.getWriterNames().get(j), config.getWriterPasswords().get(j), command + "\n");
        }
    }

    private static String readFile(String fileName) {
        String path = "./src/main/java/" + fileName + ".java";
        try {
            return Files.lines(Paths.get(path)).collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

class CommandsBuilder {

    private List<String> classesNames;
    private List<String> codeList;
    private String runnableClass;
    private String hostName;
    private int portNumber;
    private int clientId;
    private int numberOfAccess;

    public CommandsBuilder(String hostName, int portNumber, List<String> classesNames,
                           List<String> codeList, int clientId, int numberOfAccess, String runnableClass) {
        this.classesNames = classesNames;
        this.codeList = codeList;
        this.runnableClass = runnableClass;
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
        return String.format("rm -rf %s%d || true", runnableClass, clientId);
    }

    private String buildMakeDirCommand() {
        return String.format("mkdir %s%d; cd %s%d", runnableClass, clientId, runnableClass, clientId);
    }

    private String buildEchoCommand() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < classesNames.size() - 1; i++) {
            builder.append(String.format("echo \'%s\' >> %s.java", codeList.get(i), classesNames.get(i))).append(";");
        }
        int idx = classesNames.size() - 1;
        builder.append(String.format("echo \'%s\' >> %s.java", codeList.get(idx), classesNames.get(idx)));
        return builder.toString();
    }

    private String buildCompileCommand() {
        return "javac *.java";
    }

    private String buildExecuteCommand() {
        return String.format("java %s %s %d %d %d", runnableClass, hostName, portNumber, numberOfAccess, clientId);
    }
}