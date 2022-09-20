package me.coppershark.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.function.Consumer;

public class TraceRoute {
	private static final String os = System.getProperty("os.name").toLowerCase();

	private IPAddress serverIP;
	private ArrayList<IPAddress> route;

	public TraceRoute() {
		route = new ArrayList<IPAddress>();
		serverIP = null;
	}

	public TraceRoute(ArrayList<String> tracertOutput) {
		route = new ArrayList<IPAddress>();
		for (String line : tracertOutput)
			if (line.matches(" [0-9 ]{2}([0-9 \\*<]{6} (ms|  )){3}  [^ ]*( \\[[^ ]*\\])? ?")) {
				IPAddress address = new IPAddress(line);
				if (address.getHopNumber() > 1 && !isPrivateV4Address(address.getIp()))
					route.add(address);
			}
		serverIP = route.size() > 0 ? route.get(route.size() - 1) : null;
	}

	private boolean isPrivateV4Address(String ip) {
		if (!ip.matches("([0-9]{1,3}\\.){3}[0-9]{1,3}"))
			return false;
		String[] tmp = ip.split("\\.");
		return tmp[0].equalsIgnoreCase("10")
				|| (tmp[0].equalsIgnoreCase("172") && Integer.parseInt(tmp[1]) >= 16 && Integer.parseInt(tmp[1]) <= 31)
				|| (tmp[0].equalsIgnoreCase("192") && tmp[1].equalsIgnoreCase("168"));
	}

	public IPAddress getServerIP() {
		return serverIP;
	}

	public ArrayList<IPAddress> getRoute() {
		return route;
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
		final ArrayList<String> route = new ArrayList<String>();
		try {
			Process traceRt;
			String command = os.contains("win") ? "tracert -4 -d -w 100" : "traceroute -n";
			traceRt = Runtime.getRuntime().exec(command + " " + serverIP);
			BufferedReader reader = new BufferedReader(new InputStreamReader(traceRt.getInputStream()));
			reader.lines().forEach(new Consumer<String>() {
				@Override
				public void accept(String l) {
					route.add(l);
				}
			});
		} catch (IOException e) {
			return new TraceRoute();
		}
		return new TraceRoute(route);
	}
}