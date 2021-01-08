package agprojects.StockAnalyzerAC.api_client;

import agprojects.StockAnalyzerAC.enums.TwsConnectionStatus;
import agprojects.StockAnalyzerAC.enums.TwsErrorCode;
import agprojects.StockAnalyzerAC.monitor.TwsConnectionMonitor;
import com.ib.client.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Data
@AllArgsConstructor
public class EWrapperImpl implements EWrapper, AutoCloseable {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd  HH:mm:ss");

    private final EReaderSignal readerSignal;
    private final EClientSocket clientSocket;
    private final AtomicInteger currentValidOrderId;
    private final TwsConnectionMonitor connectionMonitor;
    private final AtomicInteger requestId;

    private Thread readerSignalThread;
    private EReader reader;

    public EWrapperImpl() {
        this.readerSignal = new EJavaSignal();
        this.clientSocket = new EClientSocket(this, readerSignal);
        this.currentValidOrderId = new AtomicInteger(-1);
        this.connectionMonitor = new TwsConnectionMonitor();
        this.requestId = new AtomicInteger(0);
    }

    public void connect(String host, int port, int clientId) {
        if (connectionMonitor.isConnecting()) {
            log.trace("Already connecting.");
            return;
        }

        if (connectionMonitor.isConnected()) {
            log.trace("Already connected.");
            return;
        }

        readerSignalThread = new Thread(createReaderSignalRunnable());
        readerSignalThread.setUncaughtExceptionHandler((thread, error) -> log.error("Uncaught exception: ", error));
        log.trace("Connecting...");
        connectionMonitor.changeConnectionStatus(TwsConnectionStatus.CONNECTING);
        clientSocket.eConnect(host, port, clientId);
    }

    public void disconnect() {
        if (connectionMonitor.isDisconnected()) {
            log.trace("Already disconnected.");
            return;
        }

        if (connectionMonitor.isDisconnecting()) {
            log.trace("Already disconnecting.");
            return;
        }

        log.trace("Disconnecting...");
        connectionMonitor.changeConnectionStatus(TwsConnectionStatus.DISCONNECTING);
        clientSocket.eDisconnect();

        readerSignalThread.interrupt();
        readerSignalThread = null;
        connectionMonitor.changeConnectionStatus(TwsConnectionStatus.DISCONNECTED);
    }

    public void placeOrder(@NonNull Contract contract, @NonNull Order order) {
        clientSocket.placeOrder(currentValidOrderId.getAndIncrement(), contract, order);
    }

    @Override
    public void close() {
        disconnect();
    }

    @Override
    public void tickPrice(int i, int i1, double v, TickAttrib tickAttrib) {
        log.trace("tickPrice: NOT IMPLEMENTED");
    }

    @Override
    public void tickSize(int i, int i1, int i2) {
        log.trace("tickSize: NOT IMPLEMENTED");
    }

    @Override
    public void tickOptionComputation(int i, int i1, int i2, double v, double v1, double v2,
                                      double v3, double v4, double v5, double v6, double v7) {
        log.trace("tickOptionComputation: NOT IMPLEMENTED");
    }

    @Override
    public void tickGeneric(int i, int i1, double v) {
        log.trace("tickGeneric: NOT IMPLEMENTED");
    }

    @Override
    public void tickString(int i, int i1, String s) {
        log.trace("tickString: NOT IMPLEMENTED");
    }

    @Override
    public void tickEFP(int i, int i1, double v, String s, double v1, int i2, String s1, double v2, double v3) {
        log.trace("tickEFP: NOT IMPLEMENTED");
    }

    @Override
    public void orderStatus(int i, String s, double v, double v1, double v2, int i1, int i2,
                            double v3, int i3, String s1, double v4) {
        log.trace("orderStatus: NOT IMPLEMENTED");
    }

    @Override
    public void openOrder(int i, Contract contract, Order order, OrderState orderState) {
        log.trace("openOrder: NOT IMPLEMENTED");
    }

    @Override
    public void openOrderEnd() {
        log.trace("openOrderEnd: NOT IMPLEMENTED");
    }

    @Override
    public void updateAccountValue(String s, String s1, String s2, String s3) {
        log.trace("updateAccountValue: NOT IMPLEMENTED");
    }

    @Override
    public void updatePortfolio(Contract contract, double v, double v1, double v2, double v3,
                                double v4, double v5, String s) {
        log.trace("updatePortfolio: NOT IMPLEMENTED");
    }

    @Override
    public void updateAccountTime(String s) {
        log.trace("updateAccountTime: NOT IMPLEMENTED");
    }

    @Override
    public void accountDownloadEnd(String s) {
        log.trace("accountDownloadEnd: NOT IMPLEMENTED");
    }

    @Override
    public void nextValidId(int orderId) {
        log.trace("Received current order id which is {}", orderId);
        currentValidOrderId.set(orderId);
        connectionMonitor.receivedValidOrderId();
    }

    @Override
    public void contractDetails(int i, ContractDetails contractDetails) {
        log.trace("contractDetails: NOT IMPLEMENTED");
    }

    @Override
    public void bondContractDetails(int i, ContractDetails contractDetails) {
        log.trace("bondContractDetails: NOT IMPLEMENTED");
    }

    @Override
    public void contractDetailsEnd(int i) {
        log.trace("contractDetailsEnd: NOT IMPLEMENTED");
    }

    @Override
    public void execDetails(int i, Contract contract, Execution execution) {
        log.trace("execDetails: NOT IMPLEMENTED");
    }

    @Override
    public void execDetailsEnd(int i) {
        log.trace("execDetailsEnd: NOT IMPLEMENTED");
    }

    @Override
    public void updateMktDepth(int i, int i1, int i2, int i3, double v, int i4) {
        log.trace("updateMktDepth: NOT IMPLEMENTED");
    }

    @Override
    public void updateMktDepthL2(int i, int i1, String s, int i2, int i3, double v, int i4, boolean b) {
        log.trace("updateMktDepthL2: NOT IMPLEMENTED");
    }

    @Override
    public void updateNewsBulletin(int i, int i1, String s, String s1) {
        log.trace("updateNewsBulletin: NOT IMPLEMENTED");
    }

    @Override
    public void managedAccounts(String accountList) {
        log.trace("managedAccounts: {}", accountList);
        connectionMonitor.receivedManagedAccounts();
    }

    @Override
    public void receiveFA(int i, String s) {
        log.trace("receiveFA: NOT IMPLEMENTED");
    }

    @Override
    public void historicalData(int reqId, Bar bar) {
        log.trace("historicalData: NOT IMPLEMENTED");
    }

    @Override
    public void scannerParameters(String s) {
        log.trace("scannerParameters: NOT IMPLEMENTED");
    }

    @Override
    public void scannerData(int i, int i1, ContractDetails contractDetails,
                            String s, String s1, String s2, String s3) {
        log.trace("scannerData: NOT IMPLEMENTED");
    }

    @Override
    public void scannerDataEnd(int i) {
        log.trace("scannerDataEnd: NOT IMPLEMENTED");
    }

    @Override
    public void realtimeBar(int i, long l, double v, double v1, double v2, double v3,
                            long l1, double v4, int i1) {
        log.trace("realtimeBar: NOT IMPLEMENTED");
    }

    @Override
    public void currentTime(long l) {
        log.trace("currentTime: NOT IMPLEMENTED");
    }

    @Override
    public void fundamentalData(int i, String s) {
        log.trace("fundamentalData: NOT IMPLEMENTED");
    }

    @Override
    public void deltaNeutralValidation(int i, DeltaNeutralContract deltaNeutralContract) {
        log.trace("deltaNeutralValidation: NOT IMPLEMENTED");
    }

    @Override
    public void tickSnapshotEnd(int i) {
        log.trace("tickSnapshotEnd: NOT IMPLEMENTED");
    }

    @Override
    public void marketDataType(int i, int i1) {
        log.trace("marketDataType: NOT IMPLEMENTED");
    }

    @Override
    public void commissionReport(CommissionReport commissionReport) {
        log.trace("commissionReport: NOT IMPLEMENTED");
    }

    @Override
    public void position(String s, Contract contract, double v, double v1) {
        log.trace("position: NOT IMPLEMENTED");
    }

    @Override
    public void positionEnd() {
        log.trace("positionEnd: NOT IMPLEMENTED");
    }

    @Override
    public void accountSummary(int i, String s, String s1, String s2, String s3) {
        log.trace("accountSummary: NOT IMPLEMENTED");
    }

    @Override
    public void accountSummaryEnd(int i) {
        log.trace("accountSummaryEnd: NOT IMPLEMENTED");
    }

    @Override
    public void verifyMessageAPI(String s) {
        log.trace("verifyMessageAPI: NOT IMPLEMENTED");
    }

    @Override
    public void verifyCompleted(boolean b, String s) {
        log.trace("verifyCompleted: NOT IMPLEMENTED");
    }

    @Override
    public void verifyAndAuthMessageAPI(String s, String s1) {
        log.trace("verifyAndAuthMessageAPI: NOT IMPLEMENTED");
    }

    @Override
    public void verifyAndAuthCompleted(boolean b, String s) {
        log.trace("verifyAndAuthCompleted: NOT IMPLEMENTED");
    }

    @Override
    public void displayGroupList(int i, String s) {
        log.trace("displayGroupList: NOT IMPLEMENTED");
    }

    @Override
    public void displayGroupUpdated(int i, String s) {
        log.trace("displayGroupUpdated: NOT IMPLEMENTED");
    }

    @Override
    public void error(Exception error) {
        log.error("Error: {}", error.getMessage(), error);
    }

    @Override
    public void error(String errorMsg) {
        log.error("Error: {}", errorMsg);
    }

    @Override
    public void error(int id, int errorCode, String errorMsg) {
        log.error("Error: id={}, errorCode={}, {}", id, errorCode, errorMsg);

        Optional<TwsErrorCode> twsErrorCodeOptional = TwsErrorCode.valueOfCode(errorCode);
        if (twsErrorCodeOptional.isPresent()
                && twsErrorCodeOptional.get() == TwsErrorCode.CONNECTION_TO_TWS_NOT_ESTABLISHED) {
            disconnect();
        }
    }

    @Override
    public void connectionClosed() {
        log.trace("connectionClosed: NOT IMPLEMENTED");
    }

    @Override
    public void connectAck() {
        reader = new EReader(clientSocket, readerSignal);
        reader.start();

        readerSignalThread.start();
    }

    @Override
    public void positionMulti(int i, String s, String s1, Contract contract, double v, double v1) {
        log.trace("positionMulti: NOT IMPLEMENTED");
    }

    @Override
    public void positionMultiEnd(int i) {
        log.trace("positionMultiEnd: NOT IMPLEMENTED");
    }

    @Override
    public void accountUpdateMulti(int i, String s, String s1, String s2, String s3, String s4) {
        log.trace("accountUpdateMulti: NOT IMPLEMENTED");
    }

    @Override
    public void accountUpdateMultiEnd(int i) {
        log.trace("accountUpdateMultiEnd: NOT IMPLEMENTED");
    }

    @Override
    public void securityDefinitionOptionalParameter(int i, String s, int i1, String s1, String s2,
                                                    Set<String> set, Set<Double> set1) {
        log.trace("securityDefinitionOptionalParameter: NOT IMPLEMENTED");
    }

    @Override
    public void securityDefinitionOptionalParameterEnd(int i) {
        log.trace("securityDefinitionOptionalParameterEnd: NOT IMPLEMENTED");
    }

    @Override
    public void softDollarTiers(int i, SoftDollarTier[] softDollarTiers) {
        log.trace("softDollarTiers: NOT IMPLEMENTED");
    }

    @Override
    public void familyCodes(FamilyCode[] familyCodes) {
        log.trace("familyCodes: NOT IMPLEMENTED");
    }

    @Override
    public void symbolSamples(int i, ContractDescription[] contractDescriptions) {
        log.trace("symbolSamples: NOT IMPLEMENTED");
    }

    @Override
    public void historicalDataEnd(int reqId, String startDateTime, String endDateTime) {
        log.trace("historicalDataEnd: NOT IMPLEMENTED");
    }

    @Override
    public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions) {
        log.trace("mktDepthExchanges: NOT IMPLEMENTED");
    }

    @Override
    public void tickNews(int i, long l, String s, String s1, String s2, String s3) {
        log.trace("tickNews: NOT IMPLEMENTED");
    }

    @Override
    public void smartComponents(int i, Map<Integer, Map.Entry<String, Character>> map) {
        log.trace("smartComponents: NOT IMPLEMENTED");
    }

    @Override
    public void tickReqParams(int i, double v, String s, int i1) {
        log.trace("tickReqParams: NOT IMPLEMENTED");
    }

    @Override
    public void newsProviders(NewsProvider[] newsProviders) {
        log.trace("newsProviders: NOT IMPLEMENTED");
    }

    @Override
    public void newsArticle(int i, int i1, String s) {
        log.trace("newsArticle: NOT IMPLEMENTED");
    }

    @Override
    public void historicalNews(int i, String s, String s1, String s2, String s3) {
        log.trace("historicalNews: NOT IMPLEMENTED");
    }

    @Override
    public void historicalNewsEnd(int i, boolean b) {
        log.trace("historicalNewsEnd: NOT IMPLEMENTED");
    }

    @Override
    public void headTimestamp(int i, String s) {
        log.trace("headTimestamp: NOT IMPLEMENTED");
    }

    @Override
    public void histogramData(int i, List<HistogramEntry> list) {
        log.trace("histogramData: NOT IMPLEMENTED");
    }

    @Override
    public void historicalDataUpdate(int i, Bar bar) {
        log.trace("historicalDataUpdate: NOT IMPLEMENTED");
    }

    @Override
    public void rerouteMktDataReq(int i, int i1, String s) {
        log.trace("rerouteMktDataReq: NOT IMPLEMENTED");
    }

    @Override
    public void rerouteMktDepthReq(int i, int i1, String s) {
        log.trace("rerouteMktDepthReq: NOT IMPLEMENTED");
    }

    @Override
    public void marketRule(int i, PriceIncrement[] priceIncrements) {
        log.trace("marketRule: NOT IMPLEMENTED");
    }

    @Override
    public void pnl(int i, double v, double v1, double v2) {
        log.trace("pnl: NOT IMPLEMENTED");
    }

    @Override
    public void pnlSingle(int i, int i1, double v, double v1, double v2, double v3) {
        log.trace("pnlSingle: NOT IMPLEMENTED");
    }

    @Override
    public void historicalTicks(int i, List<HistoricalTick> list, boolean b) {
        log.trace("historicalTicks: NOT IMPLEMENTED");
    }

    @Override
    public void historicalTicksBidAsk(int i, List<HistoricalTickBidAsk> list, boolean b) {
        log.trace("historicalTicksBidAsk: NOT IMPLEMENTED");
    }

    @Override
    public void historicalTicksLast(int i, List<HistoricalTickLast> list, boolean b) {
        log.trace("historicalTicksLast: NOT IMPLEMENTED");
    }

    @Override
    public void tickByTickAllLast(int i, int i1, long l, double v, int i2,
                                  TickAttribLast tickAttribLast, String s, String s1) {
        log.trace("tickByTickAllLast: NOT IMPLEMENTED");
    }

    @Override
    public void tickByTickBidAsk(int i, long l, double v, double v1, int i1, int i2,
                                 TickAttribBidAsk tickAttribBidAsk) {
        log.trace("tickByTickBidAsk: NOT IMPLEMENTED");
    }

    @Override
    public void tickByTickMidPoint(int i, long l, double v) {
        log.trace("tickByTickMidPoint: NOT IMPLEMENTED");
    }

    @Override
    public void orderBound(long l, int i, int i1) {
        log.trace("orderBound: NOT IMPLEMENTED");
    }

    @Override
    public void completedOrder(Contract contract, Order order, OrderState orderState) {
        log.trace("completedOrder: NOT IMPLEMENTED");
    }

    @Override
    public void completedOrdersEnd() {
        log.trace("completedOrdersEnd: NOT IMPLEMENTED");
    }

    private Runnable createReaderSignalRunnable() {
        return () -> {
            while (clientSocket.isConnected()) {
                readerSignal.waitForSignal();
                try {
                    reader.processMsgs();
                } catch (Exception e) {
                    log.error("Exception: {}", e.getMessage(), e);
                }
            }
        };
    }
}
