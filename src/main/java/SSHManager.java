import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;

@Log4j2
public class SSHManager {


    public void executeCommand(String hostName, String password, String command) {

        try {
            JSch jsch = new JSch();

            Session session = jsch.getSession(hostName);
            session.setPassword(password);

            UserInfo ui = new MyUserInfo(password);
            session.setUserInfo(ui);

            session.connect(30000);   // making a connection with timeout.

            Channel channel = session.openChannel("shell");

            channel.setInputStream(new ByteArrayInputStream(command.getBytes()));

            channel.setOutputStream(System.out);
            channel.connect(3 * 1000);

        } catch (Exception e) {
            log.error("Cant connect to host with host name {}", hostName, e);
        }
    }
}