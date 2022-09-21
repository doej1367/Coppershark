package me.coppershark.event;

import java.util.ArrayList;

import me.coppershark.main.Main;
import me.coppershark.main.Main.Connection;
import me.coppershark.util.TraceRoute;
import me.coppershark.util.TraceRouteDashCam;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ConnectionExceptionEvent extends Event {

	public ConnectionExceptionEvent(final Main main, final Throwable exception) {
		new Thread() {
			@Override
			public void run() {
				TraceRouteDashCam traceroute = main.getTraceRouteDashCam();
				traceroute.stopRecording();
				String version = "Mod Version: " + main.VERSION + "\n";
				String name = "User: " + main.getUserName() + "\n";
				String ip = "IP: " + main.getServerIP() + "\n";
				String uptime = "Uptime: " + main.getUptimeMinutes() + " min\n";
				String error = "Error: " + exception.getClass().getName() + "\n";
				if (traceroute == null || traceroute.getDashRecord() == null || traceroute.getDashRecord().size() < 1)
					return;

				ArrayList<TraceRoute> trList = new ArrayList<TraceRoute>(traceroute.getDashRecord());
				TraceRoute bestTraceroute = traceroute.getClosestTracert();

				String message = version + name + ip + uptime + error + bestTraceroute + "\nTrace Route Count: "
						+ trList.size();
				System.out.println("[Coppershark]\n" + message);
				main.sendToWebhook(message, Connection.BAD);
			};
		}.start();
	}

}
