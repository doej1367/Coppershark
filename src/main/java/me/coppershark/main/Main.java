package me.coppershark.main;

import java.io.IOException;

import me.coppershark.command.CopperSharkCommand;
import me.coppershark.eventhandler.ConnectionExceptionEventHandler;
import me.coppershark.util.DiscordWebhook;
import me.coppershark.util.Settings;
import me.coppershark.util.TraceRouteDashCam;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Main.MODID, version = Main.VERSION)
public class Main {
	public static final String MODID = "coppershark";
	public static final String VERSION = "3.0.11";

	private byte[] tokenBadConnection = { 71, 107, 98, 52, 55, 99, 97, 72, 69, 78, 81, 107, 72, 56, 110, 51, 102, 78,
			107, 113, 114, 112, 83, 81, 116, 114, 68, 89, 50, 120, 85, 115, 115, 98, 121, 122, 122, 100, 101, 68, 99,
			113, 89, 53, 76, 89, 89, 70, 77, 100, 45, 53, 85, 113, 104, 50, 68, 71, 120, 109, 50, 111, 45, 98, 119, 51,
			105, 67 };
	private DiscordWebhook webhookBadConnection = new DiscordWebhook("1024424392408703026", tokenBadConnection);
	private byte[] tokenGoodConnection = { 108, 66, 113, 87, 76, 87, 79, 109, 118, 106, 52, 95, 88, 80, 101, 95, 53, 81,
			121, 82, 80, 70, 119, 115, 65, 89, 73, 114, 106, 79, 103, 114, 109, 100, 49, 102, 70, 89, 48, 49, 117, 111,
			103, 68, 101, 66, 67, 95, 53, 88, 119, 69, 57, 99, 48, 117, 103, 122, 65, 81, 69, 66, 104, 53, 57, 71, 70,
			108 };
	private DiscordWebhook webhookGoodConnection = new DiscordWebhook("1024424709468737576", tokenGoodConnection);

	private long timestamp;
	private String serverIP;
	private String userName;
	private NetworkManager serverState;
	private TraceRouteDashCam trdc;
	private Settings settings;

	public enum Connection {
		GOOD, BAD
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		settings = new Settings(this);
		settings.setFolder(event);
		settings.createSettingsFolderAndFile();
		System.out.println("[OK] preInit Minecraft Coppershark");
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		ClientCommandHandler.instance.registerCommand(new CopperSharkCommand(this));
		MinecraftForge.EVENT_BUS.register(new ConnectionExceptionEventHandler(this));
		this.userName = Minecraft.getMinecraft().getSession().getUsername();
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
	}

	public String getUserName() {
		return userName;
	}

	public boolean getServerState() {
		if (serverState != null)
			return serverState.isChannelOpen();
		return false;
	}

	public void setServerState(NetworkManager manager) {
		serverState = manager;
	}

	public TraceRouteDashCam getTraceRouteDashCam() {
		return trdc;
	}

	public void restartTraceRouteDashCam() {
		this.trdc = new TraceRouteDashCam();
		trdc.startRecording(serverIP);
	}

	public Settings getSettings() {
		return settings;
	}

	public void sendToWebhook(String message, Connection type) {
		try {
			String discordID = settings.getSetting("discordUserID");
			if (!(discordID.matches("[0-9]{18}") || discordID.equalsIgnoreCase("default")))
				return;
			String discordHandle = discordID.equalsIgnoreCase("default") ? "" : ("<@" + discordID + ">\n");
			DiscordWebhook webhook = ((type == Connection.BAD) ? webhookBadConnection : webhookGoodConnection);
			webhook.setUsername("Coppershark");
			webhook.setContent((discordHandle + "```\n" + message + "\n```").replaceAll("\n", "\\\\n"));
			webhook.execute();
		} catch (IOException e) {
		}
	}
}
