package at.ac.tuwien.dslab2.presentation.biddingClient;

import at.ac.tuwien.dslab2.service.biddingClient.ReplyListener;

public class ReplyListenerImpl implements ReplyListener {

	@Override
	public void displayReply(String reply) {
		System.out.println("\n" + reply);
		System.out.print(Client.getPrompt());
	}

}
