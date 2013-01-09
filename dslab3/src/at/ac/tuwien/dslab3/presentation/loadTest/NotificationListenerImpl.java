package at.ac.tuwien.dslab3.presentation.loadTest;

import at.ac.tuwien.dslab3.domain.Event;
import at.ac.tuwien.dslab3.service.loadTest.TimerNotifications;
import at.ac.tuwien.dslab3.service.managementClient.SubscriptionListener;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

class NotificationListenerImpl implements SubscriptionListener, TimerNotifications {

    private final AtomicLong numAllAuctionEvents;
    private final AtomicLong numBidEvents;
    private final AtomicLong numListingEvents;
    private final AtomicLong numCreationEvents;
    private final int PADDING_SIZE = 20;
    private final String format = "%-" + PADDING_SIZE;


    public NotificationListenerImpl() {
        this.numAllAuctionEvents = new AtomicLong();
        this.numBidEvents = new AtomicLong();
        this.numListingEvents = new AtomicLong();
        this.numCreationEvents = new AtomicLong();

        StringBuilder builder = new StringBuilder();
        builder.append(String.format(format + "s", "AuctionEvent Count"));
        builder.append(String.format(format + "s", "AuctionBid Count"));
        builder.append(String.format(format + "s", "AuctionCreation Count"));
        builder.append(String.format(format + "s", "AuctionListing Count: "));
        builder.append("\n");
        System.out.print(builder.toString());
		System.out.flush();
    }

    @Override
    public void autoPrintEvent(Set<Event> events) {
        this.numAllAuctionEvents.addAndGet(events.size());
        output();
    }

    @Override
    public void newBid(String serverResponse) {
        this.numBidEvents.incrementAndGet();
        output();
    }

    @Override
    public void newListing(String serverResponse) {
        this.numListingEvents.incrementAndGet();
        output();
    }

    @Override
    public void newCreation(String serverResponse) {
        this.numCreationEvents.incrementAndGet();
        output();
    }

    private void output() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("AuctionEvent Count: " + format + "d", this.numAllAuctionEvents.get()));
        builder.append(String.format("AuctionBid Count: " + format + "d", this.numBidEvents.get()));
        builder.append(String.format("AuctionCreation Count: " + format + "d", this.numCreationEvents.get()));
        builder.append(String.format("AuctionListing Count: " + format + "d", this.numListingEvents.get()));
        builder.append("\r");

        System.out.print(builder.toString());
		System.out.flush();
    }
}
