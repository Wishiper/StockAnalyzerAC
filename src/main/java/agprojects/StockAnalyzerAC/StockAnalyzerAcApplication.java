package agprojects.StockAnalyzerAC;

import agprojects.StockAnalyzerAC.api_client.TwsClient;
import agprojects.StockAnalyzerAC.enums.TwsConnectionStatus;
import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;

@SpringBootApplication
@Slf4j
public class StockAnalyzerAcApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockAnalyzerAcApplication.class, args);
		System.out.println("StockAnalyzer started successfully");

		TwsClient twsClient = new TwsClient();

		twsClient.connect("127.0.0.1", 7497, 0);

//		twsClient.connectionStatus()
//				.subscribe(bool -> System.out.println("Connected ? - " + bool));
//		twsClient.connect("127.0.0.1", 7497, 0)
//				.block(Duration.ofSeconds(30));

		while (twsClient.getConnectionStatus() != TwsConnectionStatus.CONNECTED) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


		log.debug(">>>>> connected.");

		Contract contract = new Contract();
		contract.symbol("SWED.A");
		contract.currency("SEK");
		contract.exchange("SFB");
		contract.secType("STK");

		Order order = new Order();
		order.orderType(OrderType.MKT);
		order.totalQuantity(1);


		twsClient.placeOrder(contract, order);
	}

}
