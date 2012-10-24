package at.ac.tuwien.dslab1.presentation.client;

import at.ac.tuwien.dslab1.service.client.ReplyListener;

public class ReplyListenerImpl implements ReplyListener {

	@Override
	public void displayReply(String reply) {
		System.out.println("\n" + reply);
		System.out.print(Client.getPrompt());
	}

}
