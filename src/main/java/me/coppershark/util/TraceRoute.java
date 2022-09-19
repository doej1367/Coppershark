package me.coppershark.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.function.Consumer;

import com.google.common.net.InetAddresses;

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
		int address = InetAddresses.coerceToInteger(InetAddresses.forString(ip));
		return (((address >>> 24) & 0xFF) == 10)
				|| ((((address >>> 24) & 0xFF) == 172) && ((address >>> 16) & 0xFF) >= 16
						&& ((address >>> 16) & 0xFF) <= 31)
				|| ((((address >>> 24) & 0xFF) == 192) && (((address >>> 16) & 0xFF) == 168));
	}

	public IPAddress getServerIP() {
		return serverIP;
	}

	public ArrayList<IPAddress> getRoute() {
		return route;
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