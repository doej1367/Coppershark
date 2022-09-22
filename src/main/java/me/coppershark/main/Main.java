package me.coppershark.main;

import java.io.IOException;

import me.coppershark.command.CopperSharkCommand;
import me.coppershark.eventhandler.ConnectionExceptionEventHandler;
import me.coppershark.util.DiscordWebhook;
import me.coppershark.util.Settings;
import me.coppershark.util.TraceRoute;
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
	public static final String VERSION = "3.0.7";

	private byte[] tokenBadConnection = { 107, 67, 90, 100, 122, 51, 77, 52, 55, 53, 118, 49, 97, 45, 84, 55, 66, 107,
			83, 85, 56, 120, 117, 102, 107, 81, 117, 90, 87, 110, 78, 118, 86, 88, 85, 99, 90, 76, 53, 110, 121, 80,
			116, 72, 54, 112, 107, 111, 98, 101, 79, 115, 71, 54, 75, 76, 117, 54, 97, 106, 97, 101, 70, 77, 100, 66,
			99, 75 };
	private DiscordWebhook webhookBadConnection = new DiscordWebhook("1019383711176851486", tokenBadConnection);
	private byte[] tokenGoodConnection = { 85, 99, 113, 89, 45, 57, 78, 119, 122, 70, 117, 102, 121, 100, 48, 115, 66,
			45, 116, 77, 119, 80, 121, 74, 98, 54, 89, 115, 113, 69, 103, 109, 120, 54, 71, 85, 51, 84, 77, 107, 54, 77,
			112, 77, 122, 87, 107, 104, 79, 81, 66, 119, 78, 98, 49, 72, 54, 65, 74, 114, 89, 83, 110, 80, 97, 45, 84,
			117 };
	private DiscordWebhook webhookGoodConnection = new DiscordWebhook("1019465411038826570", tokenGoodConnection);

	private long timestamp;
	private String serverIP;
	private String userName;
	private NetworkManager serverState;
	private TraceRouteDashCam trdc;
	private Settings settings;

	public enum Connection {
		GOOD, BAD
	};

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
			String discordHandle = discordID.equalsIgnoreCase("default") ? "" : ("<@" + discordID + ">\n");
			DiscordWebhook webhook = ((type == Connection.BAD) ? webhookBadConnection : webhookGoodConnection);
			webhook.setUsername("Coppershark");
			webhook.setContent((discordHandle + "```\n" + message + "\n```").replaceAll("\n", "\\\\n"));
			webhook.execute();
		} catch (IOException e) {
		}
	}
}
