package at.ac.tuwien.dslab3.presentation.biddingClient;

import at.ac.tuwien.dslab3.service.biddingClient.ReplyListener;

class ReplyListenerImpl implements ReplyListener {

	@Override
	public void displayReply(String reply) {
		System.out.println("\n" + reply);
		System.out.print(BiddingClient.getPrompt());
		System.out.flush();
	}

}
