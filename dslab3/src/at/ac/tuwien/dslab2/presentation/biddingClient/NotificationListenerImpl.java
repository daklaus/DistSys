package at.ac.tuwien.dslab2.presentation.biddingClient;

import at.ac.tuwien.dslab2.service.biddingClient.NotificationListener;

class NotificationListenerImpl implements NotificationListener {

	@Override
	public void newBid(String description) {
		System.out.println("\nYou have been overbid on '" + description + "'");
		System.out.print(BiddingClient.getPrompt());
	}

	@Override
	public void auctionEnded(String winner, double amount, String description) {
		System.out.print("\nThe auction '" + description + "' has ended. ");

		String user = BiddingClient.getUserName();
		if (user != null && user.equals(winner)) {
			System.out.print("You ");
		} else {
			System.out.print(winner + " ");
		}

		System.out.println("won with " + String.format("%.2f", amount) + "!");
		System.out.print(BiddingClient.getPrompt());
	}

}
