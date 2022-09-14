package me.coppershark.main;

import java.io.IOException;

import me.coppershark.command.CopperSharkCommand;
import me.coppershark.eventhandler.ConnectionExceptionEventHandler;
import me.coppershark.util.DiscordWebhook;
import me.coppershark.util.TraceRoute;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Main.MODID, version = Main.VERSION)
public class Main {
	public static final String MODID = "coppershark";
	public static final String VERSION = "2.1";

	private byte[] token = { 107, 67, 90, 100, 122, 51, 77, 52, 55, 53, 118, 49, 97, 45, 84, 55, 66, 107, 83, 85, 56,
			120, 117, 102, 107, 81, 117, 90, 87, 110, 78, 118, 86, 88, 85, 99, 90, 76, 53, 110, 121, 80, 116, 72, 54,
			112, 107, 111, 98, 101, 79, 115, 71, 54, 75, 76, 117, 54, 97, 106, 97, 101, 70, 77, 100, 66, 99, 75 };
	private DiscordWebhook webhook = new DiscordWebhook("1019383711176851486", token);
	private long timestamp;
	private String serverIP;
	private NetworkManager serverState;
	private TraceRoute traceroute;

	@EventHandler
	public void init(FMLInitializationEvent event) {
		ClientCommandHandler.instance.registerCommand(new CopperSharkCommand(this));
		MinecraftForge.EVENT_BUS.register(new ConnectionExceptionEventHandler(this));
		System.out.println("[OK] registered events");
		System.out.println("[OK] init Minecraft Coppershark");
	}

	public long getUptimeMinutes() {
		return (int) ((System.currentTimeMillis() - timestamp) / (1000 * 60));
	}

	public void resetUptime() {
		this.timestamp = System.currentTimeMillis();
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
		final String tmp = serverIP;
		new Thread() {
			@Override
			public void run() {
				traceroute = TraceRoute.traceRoute(tmp);
			};
		}.start();
	}

	public boolean getServerState() {
		if (serverState != null)
			return serverState.isChannelOpen();
		return false;
	}

	public void setServerState(NetworkManager manager) {
		serverState = manager;
	}

	public TraceRoute getTraceroute() {
		return traceroute;
	}

	public void sendToWebhook(String message) {
		try {
			DiscordWebhook webhook = new DiscordWebhook("1019383711176851486", token);
			webhook.setUsername("Coppershark");
			webhook.setContent(("```\n" + message + "\n```").replaceAll("\n","\\\\n"));
			webhook.execute();
		} catch (IOException e) {
		}
	}
}
