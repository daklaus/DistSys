package at.ac.tuwien.dslab2.presentation.biddingClient;

import at.ac.tuwien.dslab2.service.biddingClient.ReplyListener;

class ReplyListenerImpl implements ReplyListener {

	@Override
	public void displayReply(String reply) {
		System.out.println("\n" + reply);
		// Don't display prompt after reply messages because of ugly output at
		// login
		// System.out.print(BiddingClient.getPrompt());
		// System.out.flush();
	}

}
