package at.ac.tuwien.dslab2.service.loadTest;

import java.io.Closeable;
import java.io.IOException;

public interface LoadTestService extends Closeable {
    void start() throws IOException;
}
