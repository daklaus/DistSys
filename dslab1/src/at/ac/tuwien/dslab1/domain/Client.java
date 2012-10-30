/**
 * 
 */
package at.ac.tuwien.dslab1.domain;

import java.net.InetAddress;

/**
 * @author klaus
 * 
 */
public class Client {
	private InetAddress ip;
	private Integer tcpPort;
	private Integer udpPort;

	/**
	 * @param ip
	 * @param tcpPort
	 * @param udpPort
	 */
	public Client(InetAddress ip, Integer tcpPort, Integer udpPort) {
		if (ip == null)
			throw new IllegalArgumentException("ip is null");
		if (tcpPort == null)
			throw new IllegalArgumentException("tcpPort is null");
		if (udpPort == null)
			throw new IllegalArgumentException("udpPort is null");

		this.ip = ip;
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
	}

	public InetAddress getIp() {
		return this.ip;
	}

	public Integer getTcpPort() {
		return this.tcpPort;
	}

	public Integer getUdpPort() {
		return this.udpPort;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.ip == null) ? 0 : this.ip.hashCode());
		result = prime * result
				+ ((this.tcpPort == null) ? 0 : this.tcpPort.hashCode());
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
		if (this.tcpPort == null) {
			if (other.tcpPort != null)
				return false;
		} else if (!this.tcpPort.equals(other.tcpPort))
			return false;
		return true;
	}
}
