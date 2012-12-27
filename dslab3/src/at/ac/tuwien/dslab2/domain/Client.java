/**
 * 
 */
package at.ac.tuwien.dslab2.domain;

import java.net.InetAddress;

/**
 * @author klaus
 * 
 */
public class Client {
	private final InetAddress ip;
	private final int tcpPort;
	private final int udpPort;

	/**
	 * @param ip
	 * @param tcpPort
	 * @param udpPort
	 */
	public Client(InetAddress ip, int tcpPort, int udpPort) {
		if (ip == null)
			throw new IllegalArgumentException("ip is null");

		this.ip = ip;
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
	}

	public InetAddress getIp() {
		return this.ip;
	}

	public int getTcpPort() {
		return this.tcpPort;
	}

	public int getUdpPort() {
		return this.udpPort;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.ip == null) ? 0 : this.ip.hashCode());
		result = prime * result + this.tcpPort;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Client other = (Client) obj;
		if (this.ip == null) {
			if (other.ip != null)
				return false;
		} else if (!this.ip.equals(other.ip))
			return false;
		if (this.tcpPort != other.tcpPort)
			return false;
		return true;
	}
}
