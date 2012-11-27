package at.ac.tuwien.dslab2.service;

public abstract class PropertiesServiceFactory {

	public static PropertiesService getPropertiesService() {
		return PropertiesServiceImpl.getInstance();
	}
}
