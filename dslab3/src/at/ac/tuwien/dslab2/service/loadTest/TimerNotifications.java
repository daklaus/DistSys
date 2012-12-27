package at.ac.tuwien.dslab2.service.loadTest;

public interface TimerNotifications {
    void newBid(String serverResponse);

    void newCreation(String serverResponse);

    void newListing(String serverResponse);
}
