package me.coppershark.event;

import me.coppershark.main.Main;
import me.coppershark.main.Main.Connection;
import me.coppershark.util.TraceRoute;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class DisconnectEvent extends Event {

	public DisconnectEvent(Main main, ClientDisconnectionFromServerEvent event) {
		String version = "Mod Version: " + main.VERSION + "\n";
		String name = "User: " + main.getUserName() + "\n";
		String ip = "IP: " + main.getServerIP() + "\n";
		if (main.getUptimeMinutes() < 40)
			return;
		String uptime = "Uptime: " + main.getUptimeMinutes() + " min\n";
		String reason = "Ended: " + event.manager.getExitMessage().getUnformattedText() + "\n";
		TraceRoute traceroute = main.getTraceroute();
		if (traceroute == null || traceroute.getRoute().size() < 2)
			return;
		String message = version + name + ip + uptime + traceroute;
		System.out.println("[Coppershark]\n" + ip + uptime + reason + traceroute);
		if (event.manager.getExitMessage().getUnformattedText().matches("Quitting"))
			main.sendToWebhook(message, Connection.GOOD);
	}

}