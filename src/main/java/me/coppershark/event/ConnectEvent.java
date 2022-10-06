package me.coppershark.event;

import me.coppershark.main.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class ConnectEvent extends Event {

	public ConnectEvent(Main main, ClientConnectedToServerEvent event) {
		final ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
		String serverIP = event.isLocal ? "localhost" : serverData != null ? serverData.serverIP : null;
		main.resetUptime();
		main.setServerIP(serverIP);
		main.restartTraceRouteDashCam();
		main.setServerState(event.manager);
		String message = "[Coppershark] Connected to " + serverIP;
		System.out.println(message);
	}

}
