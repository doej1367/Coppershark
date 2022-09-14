package me.coppershark.util;

import java.util.ArrayList;

class IPAddress {
	private int hopNumber;
	private String ip;
	private String name;
	private int rtt1;
	private int rtt2;
	private int rtt3;

	public IPAddress(String tracertLine) {
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

	public int getHopNumber() {
		return hopNumber;
	}

	public String getIp() {
		return ip;
	}

	public String getName() {
		return name;
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