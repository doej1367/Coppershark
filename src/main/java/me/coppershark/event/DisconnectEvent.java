package me.coppershark.event;

import me.coppershark.main.Main;
import me.coppershark.main.Main.Connection;
import me.coppershark.util.TraceRoute;
import me.coppershark.util.TraceRouteDashCam;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class DisconnectEvent extends Event {

	public DisconnectEvent(final Main main, final ClientDisconnectionFromServerEvent event) {
		new Thread() {
			@Override
			public void run() {
				if (event.manager == null || event.manager.getExitMessage() == null
						|| !event.manager.getExitMessage().getUnformattedText().matches("Quitting"))
					return;
				TraceRouteDashCam traceroute = main.getTraceRouteDashCam();
				traceroute.stopRecording();
				String version = "Mod Version: " + Main.VERSION + "\n";
				String name = "User: " + main.getUserName() + "\n";
				String ip = "IP: " + main.getServerIP() + "\n";
				if (main.getUptimeMinutes() < 40)
					return;
				String uptime = "Uptime: " + main.getUptimeMinutes() + " min\n";
				String reason = "Ended: " + event.manager.getExitMessage().getUnformattedText() + "\n";
				if (traceroute == null || traceroute.getTraceroutes() == null || traceroute.getTraceroutes().size() < 1)
					return;

				TraceRoute selectedTraceroute = traceroute.getMostUsedRoute();

				String message = version + name + ip + uptime + selectedTraceroute + "\nTrace Route Count: "
						+ traceroute.getTraceroutes().size();
				System.out.println("[Coppershark]\n" + message);
				main.sendToWebhook(message, Connection.GOOD);
			}
		}.start();
	}

}