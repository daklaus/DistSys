package at.ac.tuwien.dslab1.presentation.client;

import at.ac.tuwien.dslab1.service.client.AuctionClientService;
import at.ac.tuwien.dslab1.service.client.NotificationListener;
import at.ac.tuwien.dslab1.service.client.ServiceFactory;

public class NotificationListenerImpl implements NotificationListener {

	@Override
	public void newBid(String description) {
		System.out.println("\nYou have been overbid on '" + description + "'");
		System.out.print(Client.getPrompt());
	}

	@Override
	public void auctionEnded(String winner, double amount, String description) {
		AuctionClientService acs = ServiceFactory.getAuctionClientService();

		System.out.print("\nThe auction '" + description + "' has ended. ");

		String user = acs.getUserName();
		if (user != null && user.equals(winner)) {
			System.out.print("You ");
		} else {
			System.out.print(winner + " ");
		}

		System.out.println("won with " + String.format("%.2f", amount) + "!");
		System.out.print(Client.getPrompt());
	}

}
