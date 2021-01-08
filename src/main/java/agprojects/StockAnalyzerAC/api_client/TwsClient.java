package agprojects.StockAnalyzerAC.api_client;

import agprojects.StockAnalyzerAC.enums.TwsConnectionStatus;
import com.ib.client.Contract;
import com.ib.client.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@Service
public class TwsClient implements AutoCloseable {

    public static final int REQUEST_TIMEOUT_IN_SEC = 30;

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_CLIENT_ID = 0;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EWrapperImpl wrapperImpl;

    public TwsClient() {
        this.wrapperImpl = new EWrapperImpl();
    }

    public void connect(int port) {
        connect(DEFAULT_HOST, port, DEFAULT_CLIENT_ID);
    }

    public void connect(String host, int port, int clientId) {
        wrapperImpl.connect(host, port, clientId);
    }

    public void disconnect() {
        wrapperImpl.disconnect();
    }

    public TwsConnectionStatus getConnectionStatus() {
        return wrapperImpl.getConnectionMonitor().getConnectionStatus();
    }

    @Override
    public void close() {
        wrapperImpl.close();
    }

    public void placeOrder(@NonNull Contract contract, @NonNull Order order) {
        wrapperImpl.placeOrder(contract, order);
    }
}
