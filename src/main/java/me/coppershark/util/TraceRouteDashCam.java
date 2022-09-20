package me.coppershark.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry.AddCallback;

public class TraceRouteDashCam {
	private HashSet<TraceRoute> traceroutes = new HashSet<TraceRoute>();
	private ArrayList<TraceRoute> dashRecords = new ArrayList<TraceRoute>();
	private Thread queryThread = null;
	private boolean running = true;

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
							add(tr);
							traceroutes.add(tr);
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
		long timestamp = System.currentTimeMillis();
		running = false;
		try {
			if (queryThread != null)
				queryThread.join();
		} catch (InterruptedException e) {
		}
		filter(timestamp);
	}

	public ArrayList<TraceRoute> getDashRecord() {
		return dashRecords;
	}

	public HashSet<TraceRoute> getTraceroutes() {
		return traceroutes;
	}

	private boolean add(TraceRoute e) {
		cleanup();
		return dashRecords.add(e);
	}

	private void cleanup() {
		if (dashRecords.size() < 200)
			return;
		long now = System.currentTimeMillis();
		for (Iterator iterator = dashRecords.iterator(); iterator.hasNext();) {
			TraceRoute tr = (TraceRoute) iterator.next();
			if ((now - tr.getTimestampEnd()) > 20 * 1000)
				iterator.remove();
		}
	}

	private void filter(long timestamp) {
		TraceRoute tr_old = null;
		for (Iterator iterator = dashRecords.iterator(); iterator.hasNext();) {
			TraceRoute tr = (TraceRoute) iterator.next();
			if (tr.getTimestampEnd() < timestamp || tr.getTimestampStart() > timestamp || tr.equals(tr_old))
				iterator.remove();
			else
				tr_old = tr;
		}
	}
}
