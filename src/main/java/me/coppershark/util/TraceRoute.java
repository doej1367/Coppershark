package me.coppershark.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.function.Consumer;

public class TraceRoute {
	private static final String os = System.getProperty("os.name").toLowerCase();

	private long timestamp_start;
	private long timestamp_end;
	private String serverIP;
	private ArrayList<IPAddress> route;

	private TraceRoute(String serverIP, ArrayList<IPAddress> tracert, long timestamp_start) {
		this.serverIP = serverIP;
		this.timestamp_start = timestamp_start;
		this.timestamp_end = System.currentTimeMillis();
		route = tracert;
	}

	public TraceRoute(String serverIP, IPAddress[] tracert, long timestamp_start, long timestamp_end) {
		this.timestamp_start = timestamp_start;
		this.timestamp_end = timestamp_end;
		route = new ArrayList<IPAddress>();
		IPAddress previous = null;
		for (IPAddress ipAddress : tracert)
			if (ipAddress != null && !ipAddress.equals(previous)) {
				route.add(ipAddress);
				previous = ipAddress;
			}
		this.serverIP = serverIP;
	}

	public long getTimestampStart() {
		return timestamp_start;
	}

	public long getTimestampEnd() {
		return timestamp_end;
	}

	public String getServerIP() {
		return serverIP;
	}

	public ArrayList<IPAddress> getRoute() {
		return route;
	}

	public String toStringOneLineIpsOnly() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getRoute().size(); i++)
			sb.append(getRoute().get(i).getIp() + (i == getRoute().size() - 1 ? "" : ";"));
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof TraceRoute))
			return false;
		return toString().equalsIgnoreCase(((TraceRoute) obj).toString());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getRoute().size(); i++)
			sb.append(getRoute().get(i) + (i == getRoute().size() - 1 ? "" : "\n"));
		return sb.toString();
	}

	public static TraceRoute traceRoute(String serverIP) {
		long timestamp_start = System.currentTimeMillis();
		final ArrayList<IPAddress> route = new ArrayList<IPAddress>();
		try {
			String command = os.contains("win") ? "tracert -4 -d -w 200" : "traceroute -n";
			Process traceRt = Runtime.getRuntime().exec(command + " " + serverIP);
			BufferedReader br = new BufferedReader(new InputStreamReader(traceRt.getInputStream()));
			br.lines().forEach(new Consumer<String>() {
				IPAddress previousAddress = null;

				@Override
				public void accept(String l) {
					if (l.matches(" [0-9 ]{2}([0-9 \\*<]{6} (ms|  )){3}  [^ ]*( \\[[^ ]*\\])? ?")) {
						IPAddress address = new IPAddress(l);
						if ((previousAddress == null || !previousAddress.equals(address)) && address.getHopNumber() > 0
								&& !isPrivateV4Address(address.getIp())) {
							route.add(address);
							previousAddress = address;
						}
					}
				}
			});
		} catch (IOException e) {
			return null;
		}
		return new TraceRoute(serverIP, route, timestamp_start);
	}

	private static boolean isPrivateV4Address(String ip) {
		if (!ip.matches("([0-9]{1,3}\\.){3}[0-9]{1,3}"))
			return false;
		String[] tmp = ip.split("\\.");
		return tmp[0].equalsIgnoreCase("10")
				|| (tmp[0].equalsIgnoreCase("172") && Integer.parseInt(tmp[1]) >= 16 && Integer.parseInt(tmp[1]) <= 31)
				|| (tmp[0].equalsIgnoreCase("192") && tmp[1].equalsIgnoreCase("168"));
	}

}