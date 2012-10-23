/**
 * 
 */
package at.ac.tuwien.dslab1.domain;

/**
 * @author klaus
 * 
 */
public class Client {
	private String ip;
	private Integer tcpPort;
	private Integer udpPort;

	/**
	 * @param ip
	 * @param tcpPort
	 * @param udpPort
	 */
	public Client(String ip, Integer tcpPort, Integer udpPort) {
		this.ip = ip;
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getTcpPort() {
		return this.tcpPort;
	}

	public void setTcpPort(Integer tcpPort) {
		this.tcpPort = tcpPort;
	}

	public Integer getUdpPort() {
		return this.udpPort;
	}

	public void setUdpPort(Integer udpPort) {
		this.udpPort = udpPort;
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
