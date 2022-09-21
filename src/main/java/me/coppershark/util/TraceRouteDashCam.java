package me.coppershark.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class TraceRouteDashCam {
	private HashMap<TraceRoute, Long> routeList = new HashMap<TraceRoute, Long>();
	private HashSet<TraceRoute> traceroutes = new HashSet<TraceRoute>();
	private ArrayList<TraceRoute> dashRecords = new ArrayList<TraceRoute>();
	private TraceRoute closestTracert = null;
	private Thread queryThread = null;
	private boolean running = true;
	private boolean stopped = false;
	private int longestRoute = 0;
	private final Object lockCleanup = new Object();
	private final Object lockStopping = new Object();
	private final Object lockIterator = new Object();

	public void startRecording(final String ip) {
		queryThread = new Thread() {
			ArrayList<Thread> threads = new ArrayList<Thread>();

			@Override
			public void run() {
				if (running == false)
					running = true;
				while (running) {
					Thread t = new Thread() {
						public void run() {
							TraceRoute tr = TraceRoute.traceRoute(ip);
							if (tr != null) {
								cleanupDashRecords();
								dashRecords.add(tr);
								traceroutes.add(tr);
								routeList.put(tr, routeList.getOrDefault(tr, 0L) + 1L);
								int l = tr.getRoute().get(tr.getRoute().size() - 1).getHopNumber();
								longestRoute = l > longestRoute ? l : longestRoute;
							}
						};
					};
					threads.add(t);
					t.start();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
					for (Iterator iterator = threads.iterator(); iterator.hasNext();) {
						Thread tmp = (Thread) iterator.next();
						if (!tmp.isAlive())
							iterator.remove();
					}
				}
				for (Thread t : threads)
					try {
						t.join();
					} catch (InterruptedException e) {
					}
			}
		};
		queryThread.start();
	}

	public void stopRecording() {
		synchronized (lockStopping) {
			if (stopped == false) {
				long timestamp = System.currentTimeMillis();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e1) {
				}
				running = false;
				try {
					if (queryThread != null)
						queryThread.join();
				} catch (InterruptedException e) {
				}
				// remove traceroutes that are out of frame
				synchronized (lockIterator) {
					TraceRoute tr_old = null;
					for (Iterator iterator = dashRecords.iterator(); iterator.hasNext();) {
						TraceRoute tr = (TraceRoute) iterator.next();
						if (tr.getTimestampEnd() < timestamp || tr.getTimestampStart() > timestamp || tr.equals(tr_old))
							iterator.remove();
						else
							tr_old = tr;
					}
				}
				// select the tracert hop temporarily closest to the disconnect for each tracert
				// in the timeframe of the disconnect
				IPAddress[] closestTracertParts = new IPAddress[longestRoute];
				for (TraceRoute tr : dashRecords) {
					for (IPAddress ip : tr.getRoute()) {
						int i = ip.getHopNumber() - 1;
						long newDistance = ip.getDistance(timestamp);
						if (newDistance >= 0)
							closestTracertParts[i] = (closestTracertParts[i] == null
									|| closestTracertParts[i].getDistance(timestamp) > newDistance) ? ip
											: closestTracertParts[i];
						else
							closestTracertParts[i] = closestTracertParts[i] == null ? ip : closestTracertParts[i];
					}
				}
				// create return value
				long timestampStart = Long.MAX_VALUE;
				long timestampEnd = -1;
				for (IPAddress ipAddress : closestTracertParts) {
					if (ipAddress == null)
						continue;
					timestampStart = ipAddress.getTimestamp() < timestampStart ? ipAddress.getTimestamp()
							: timestampStart;
					timestampEnd = ipAddress.getTimestamp() > timestampEnd ? ipAddress.getTimestamp() : timestampEnd;
				}
				this.closestTracert = new TraceRoute(
						dashRecords.get(0) == null ? null : dashRecords.get(0).getServerIP(), closestTracertParts,
						timestampStart, timestampEnd);
			}
			stopped = true;
		}

	}

	public TraceRoute getClosestTracert() {
		return closestTracert;
	}

	public TraceRoute getMostUsedRoute() {
		long topScore = 0;
		TraceRoute tr = null;
		for (Entry<TraceRoute, Long> e : routeList.entrySet())
			if (e.getValue() > topScore) {
				tr = e.getKey();
				topScore = e.getValue();
			}
		return tr;
	}

	public ArrayList<TraceRoute> getDashRecord() {
		return dashRecords;
	}

	public HashSet<TraceRoute> getTraceroutes() {
		return traceroutes;
	}

	private void cleanupDashRecords() {
		synchronized (lockCleanup) {
			if (dashRecords.size() < 200)
				return;
			long now = System.currentTimeMillis();
			synchronized (lockIterator) {
				for (Iterator iterator = dashRecords.iterator(); iterator.hasNext();) {
					TraceRoute tr = (TraceRoute) iterator.next();
					if ((now - tr.getTimestampEnd()) > 20 * 1000)
						iterator.remove();
				}
			}
		}
	}

}
