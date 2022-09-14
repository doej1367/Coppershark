package me.coppershark.event;

import me.coppershark.main.Main;
import me.coppershark.util.TraceRoute;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ConnectionExceptionEvent extends Event {

	public ConnectionExceptionEvent(Main main, Throwable exception) {
		String ip = "IP: " + main.getServerIP() + "\n";
		String uptime = "Uptime: " + main.getUptimeMinutes() + " min\n";
		String error = "Error: " + exception.getClass().getName() + "\n";
		TraceRoute traceroute = main.getTraceroute();
		if (traceroute == null)
			return;
		String message = ip + uptime + error + traceroute;
		System.out.println("[Coppershark]\n" + message);
		main.sendToWebhook(message);
	}

}
