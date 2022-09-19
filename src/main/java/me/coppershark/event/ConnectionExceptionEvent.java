package me.coppershark.event;

import me.coppershark.main.Main;
import me.coppershark.main.Main.Connection;
import me.coppershark.util.TraceRoute;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ConnectionExceptionEvent extends Event {

	public ConnectionExceptionEvent(Main main, Throwable exception) {
		String version = "Mod Version: " + main.VERSION + "\n";
		String name = "User: " + main.getUserName() + "\n";
		String ip = "IP: " + main.getServerIP() + "\n";
		String uptime = "Uptime: " + main.getUptimeMinutes() + " min\n";
		String error = "Error: " + exception.getClass().getName() + "\n";
		TraceRoute traceroute = main.getTraceroute();
		if (traceroute == null || traceroute.getRoute().size() < 1)
			return;
		String message = version + name + ip + uptime + error + traceroute;
		System.out.println("[Coppershark]\n" + message);
		main.sendToWebhook(message, Connection.BAD);
	}

}
