package agprojects.StockAnalyzerAC.monitor;

import agprojects.StockAnalyzerAC.enums.TwsConnectionStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class TwsConnectionMonitor {

    private final AtomicReference<TwsConnectionStatus> connectionStatusAtomic;
    private final AtomicBoolean validOrderIdReceived;
    private final AtomicBoolean managedAccountsReceived;

    public TwsConnectionMonitor() {
        this.connectionStatusAtomic = new AtomicReference<>(TwsConnectionStatus.DISCONNECTED);
        this.validOrderIdReceived = new AtomicBoolean(false);
        this.managedAccountsReceived = new AtomicBoolean(false);
    }

    public TwsConnectionStatus getConnectionStatus() {
        return connectionStatusAtomic.get();
    }

    public boolean isConnected() {
        return connectionStatusAtomic.get() == TwsConnectionStatus.CONNECTED;
    }

    public boolean isConnecting() {
        return connectionStatusAtomic.get() == TwsConnectionStatus.CONNECTING;
    }

    public boolean isDisconnected() {
        return connectionStatusAtomic.get() == TwsConnectionStatus.DISCONNECTED;
    }

    public boolean isDisconnecting() {
        return connectionStatusAtomic.get() == TwsConnectionStatus.DISCONNECTING;
    }

    public void receivedValidOrderId() {
        if (isConnecting()) {
            validOrderIdReceived.set(true);
            changeConnectionStatusIfHandshakeCompleted();
        }
    }

    public void receivedManagedAccounts() {
        if (isConnecting()) {
            managedAccountsReceived.set(true);
            changeConnectionStatusIfHandshakeCompleted();
        }
    }

    public synchronized void changeConnectionStatus(TwsConnectionStatus status) {
        if (connectionStatusAtomic.get() == status) {
            return;
        }

        TwsConnectionStatus oldStatus = connectionStatusAtomic.getAndSet(status);
        log.debug("Connection status changed: {} => {}", oldStatus, status);
        resetReceivedForHandshakeDataStatuses();
    }

    private void changeConnectionStatusIfHandshakeCompleted() {
        if (validOrderIdReceived.get()
                && managedAccountsReceived.get()
                && connectionStatusAtomic.get() == TwsConnectionStatus.CONNECTING) {
            changeConnectionStatus(TwsConnectionStatus.CONNECTED);
        }
    }

    private void resetReceivedForHandshakeDataStatuses() {
        validOrderIdReceived.set(false);
        managedAccountsReceived.set(false);
    }
}
