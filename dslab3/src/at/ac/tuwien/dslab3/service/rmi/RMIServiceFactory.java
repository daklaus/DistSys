package at.ac.tuwien.dslab3.service.rmi;

import java.rmi.RemoteException;

public abstract class RMIServiceFactory {

	public static RMIClientService newRMIClientService(String host, int port)
			throws RemoteException {
		return new RMIClientServiceImpl(host, port);
	}

	public static RMIServerService newRMIServerService(int port)
			throws RemoteException {
		return new RMIServerServiceImpl(port);
	}
}
