/**
 * 
 */
package at.ac.tuwien.dslab2.service.billingServer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import at.ac.tuwien.dslab2.domain.Bill;
import at.ac.tuwien.dslab2.domain.PriceSteps;

/**
 * @author klaus
 * 
 */
public class BillingServerSecureImpl implements BillingServerSecure {
	private final Map<String, Bill> bills;

	// Private constructor prevents instantiation from other classes
	private BillingServerSecureImpl() {
		bills = new ConcurrentHashMap<String, Bill>();
	}

	private static class BillingServerSecureHolder {
		public static final BillingServerSecure INSTANCE = new BillingServerSecureImpl();
	}

	public static BillingServerSecure getInstance() {
		return BillingServerSecureHolder.INSTANCE;
	}

	@Override
	public PriceSteps getPriceSteps() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createPriceStep(double startPrice, double endPrice,
			double fixedPrice, double variablePricePercent)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deletePriceStep(double startPrice, double endPrice)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void billAuction(String user, long auctionID, double price)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public Bill getBill(String user) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

}
