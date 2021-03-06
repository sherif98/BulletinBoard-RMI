import lombok.extern.log4j.Log4j2;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@Log4j2
public class RmiServer extends Thread {

    public RmiServer() {
        super.start();
    }

    @Override
    public void run() {
        final LogFileHandler logFileHandler = new LogFileHandler();
        try {
            final ServerConfiguration config = ServerConfiguration.getInstance();
            System.setProperty("java.rmi.server.hostname", config.getIp());
            final Registry registry = LocateRegistry.createRegistry(config.getPort());
            final Store store = new Store(logFileHandler);
            final IStore exportedStore = (IStore) UnicastRemoteObject.exportObject(store, config.getPort());
            registry.bind("store", exportedStore);
            log.info("Server is ready.");
            while (true) {

            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
//        logFileHandler.close();
    }
}
