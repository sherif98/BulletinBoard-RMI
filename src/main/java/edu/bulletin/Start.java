package edu.bulletin;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import edu.bulletin.entities.ServerConfiguration;
import edu.bulletin.server.BulletinServer;
import edu.bulletin.server.SSHManager;

public class Start {
    public static void main(String[] args) throws JSchException {
        final ServerConfiguration config = ServerConfiguration.getInstance();
        final BulletinServer server = new BulletinServer();
        final JSch jsch = new JSch();
        for (String host : config.getReaderNames()) {
            final Session session = jsch.getSession(host);
        }
    }

    private static void configureSshHost(final Session session) {
        System.out.println("sendCommand");

        /**
         * YOU MUST CHANGE THE FOLLOWING
         * FILE_NAME: A FILE IN THE DIRECTORY
         * USER: LOGIN USER NAME
         * PASSWORD: PASSWORD FOR THAT USER
         * HOST: IP ADDRESS OF THE SSH SERVER
         **/
        String command = "ls";
        String userName = "USER";
        String password = "PASSWORD";
        String connectionIP = "HOST";
        SSHManager instance = new SSHManager(userName, password, connectionIP, "");
        String errorMessage = instance.connect();

        if (errorMessage != null) {
            System.out.println(errorMessage);
        }

        String expResult = "FILE_NAME\n";
        // call sendCommand for each command and the output
        //(without prompts) is returned
        String result = instance.sendCommand(command);
        // close only after all commands are sent
        instance.close();
        System.out.println(result);
    }
}
