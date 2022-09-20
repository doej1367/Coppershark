package me.coppershark.util;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry.AddCallback;

public class TraceRouteDashCam {
	private static TraceRouteDashCam record;
	private static TraceRouteDashCam recordSaved;

	private ArrayList<TraceRoute> cache = new ArrayList<TraceRoute>();
	private Thread queryThread = null;
	private boolean running = true;

	public static void startRecording(final String ip) {
		record = new TraceRouteDashCam();
		record.queryThread = new Thread() {
			ArrayList<Thread> threads = new ArrayList<Thread>();

			@Override
			public void run() {
				if (record.running == false)
					record.running = true;
				while (record.running) {
					Thread t = new Thread() {
						public void run() {
							TraceRoute tr = TraceRoute.traceRoute(ip);
							record.add(tr);
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
		record.queryThread.start();
	}

	public static ArrayList<TraceRoute> stopAndReturnRecording() {
		long timestamp = System.currentTimeMillis();
		recordSaved = record;
		recordSaved.running = false;
		try {
			record.queryThread.join();
		} catch (InterruptedException e) {
		}
		recordSaved.filter(timestamp);
		return recordSaved.cache;
	}

	private boolean add(TraceRoute e) {
		cleanup();
		return cache.add(e);
	}

	private void cleanup() {
		if (cache.size() < 200)
			return;
		long now = System.currentTimeMillis();
		for (Iterator iterator = cache.iterator(); iterator.hasNext();) {
			TraceRoute tr = (TraceRoute) iterator.next();
			if ((now - tr.getTimestampEnd()) > 20 * 1000)
				iterator.remove();
		}
	}

	private void filter(long timestamp) {
		TraceRoute tr_old = null;
		for (Iterator iterator = cache.iterator(); iterator.hasNext();) {
			TraceRoute tr = (TraceRoute) iterator.next();
			if (tr.getTimestampEnd() < timestamp || tr.getTimestampStart() > timestamp || tr.equals(tr_old))
				iterator.remove();
			else
				tr_old = tr;
		}
	}
}
