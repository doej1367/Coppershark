package me.coppershark.event;

import java.util.ArrayList;

import me.coppershark.main.Main;
import me.coppershark.main.Main.Connection;
import me.coppershark.util.TraceRoute;
import me.coppershark.util.TraceRouteDashCam;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ConnectionExceptionEvent extends Event {

	public ConnectionExceptionEvent(Main main, Throwable exception) {
		TraceRouteDashCam traceroute = main.stopAndReturnRecording();
		String version = "Mod Version: " + main.VERSION + "\n";
		String name = "User: " + main.getUserName() + "\n";
		String ip = "IP: " + main.getServerIP() + "\n";
		String uptime = "Uptime: " + main.getUptimeMinutes() + " min\n";
		String error = "Error: " + exception.getClass().getName() + "\n";
		if (traceroute == null || traceroute.getDashRecord() == null || traceroute.getDashRecord().size() < 1)
			return;

		// TODO analyze traceroutes
		ArrayList<TraceRoute> trList = new ArrayList<TraceRoute>(traceroute.getDashRecord());
		TraceRoute firstTraceroute = trList.get(0);

		String message = version + name + ip + uptime + error + firstTraceroute + "\nTrace Route Count: "
				+ trList.size();
		System.out.println("[Coppershark]\n" + message);
		main.sendToWebhook(message, Connection.BAD);
	}

}
