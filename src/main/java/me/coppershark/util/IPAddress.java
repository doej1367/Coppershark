package me.coppershark.util;

import java.util.ArrayList;

public class IPAddress {
	private long timestamp;
	private int hopNumber;
	private String ip;
	private String name;
	private int rtt1;
	private int rtt2;
	private int rtt3;

	public IPAddress(String tracertLine) {
		// TODO 'traceroute' on Linux has a slightly different layout
		this.timestamp = System.currentTimeMillis();
		this.hopNumber = Integer.parseInt(tracertLine.substring(0, 3).trim());
		this.rtt1 = parseRTT(tracertLine.substring(3, 9));
		this.rtt2 = parseRTT(tracertLine.substring(12, 18));
		this.rtt3 = parseRTT(tracertLine.substring(21, 27));
		String[] tmp = tracertLine.substring(32).split(" ");
		ArrayList<String> addressList = new ArrayList<String>();
		for (String ip : tmp)
			if (tmp.length > 0)
				addressList.add(ip);
		if (addressList.size() < 1) {
			this.ip = "";
			this.name = "";
		} else if (addressList.size() < 2) {
			this.name = "";
			this.ip = addressList.get(0);
		} else {
			this.name = addressList.get(0);
			this.ip = addressList.get(1).replaceAll("[^0-9\\.:]", "");
		}
	}

	private int parseRTT(String rtt) {
		String tmp = rtt.trim();
		if (tmp.contains("*"))
			return -1;
		if (tmp.contains("<1"))
			return 0;
		return Integer.parseInt(tmp);
	}

	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Gets the time distance from the disconnectTime to this tracert IP address
	 * from the past. Records from the future (POV disconnectTime) return a negative
	 * value
	 * 
	 * @param disconnectTime - time the disconnect happened
	 * @return
	 */
	public long getDistance(long disconnectTime) {
		return disconnectTime - timestamp;
	}

	public int getHopNumber() {
		return hopNumber;
	}

	public String getIp() {
		return ip;
	}

	public String getName() {
		return name;
	}

	public int getRttAverage() {
		int count = (rtt1 >= 0 ? 1 : 0) + (rtt2 >= 0 ? 1 : 0) + (rtt3 >= 0 ? 1 : 0);
		if (count <= 0)
			return -1;
		int rttSum = (rtt1 >= 0 ? rtt1 : 0) + (rtt2 >= 0 ? rtt2 : 0) + (rtt3 >= 0 ? rtt3 : 0);
		return rttSum / count;
	}

	public int getRtt1() {
		return rtt1;
	}

	public int getRtt2() {
		return rtt2;
	}

	public int getRtt3() {
		return rtt3;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof IPAddress))
			return false;
		return getIp().equalsIgnoreCase(((IPAddress) obj).getIp());
	}

	@Override
	public String toString() {
		return formatNumberAndIP();
	}

	private String formatNumberAndIP() {
		return leadingSpaces("" + hopNumber, 3) + "  " + leadingSpaces(ip, 15);
	}

	private String leadingSpaces(String text, int leadingSpaces) {
		return (leadingSpaces - text.length() > 0
				? new String(new char[leadingSpaces - text.length()]).replace("\0", " ")
				: "") + text;
	}

}