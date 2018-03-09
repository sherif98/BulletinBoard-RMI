import lombok.Value;

import java.util.concurrent.atomic.AtomicInteger;

@Value
public class ServerState {
    private final SharedNews sharedNews = new SharedNews();
    private final AtomicInteger numOfReaders = new AtomicInteger(0);
    private final AtomicInteger numOfWriters = new AtomicInteger(0);
    private final AtomicInteger sequenceNumber = new AtomicInteger(0);
}
