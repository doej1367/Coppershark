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
			if (line.matches(" [0-9 ]{2}([0-9 \\*<]{6} (ms|  )){3}  [^ ]*( \\[[^ ]*\\])? ?"))
				route.add(new IPAddress(line));
		serverIP = route.size() > 0 ? route.get(route.size() - 1) : null;
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