package at.ac.tuwien.dslab3.service;

public abstract class PropertiesServiceFactory {

	public static PropertiesService getPropertiesService() {
		return PropertiesServiceImpl.getInstance();
	}
}
