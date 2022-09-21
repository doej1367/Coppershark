package me.coppershark.command;

import me.coppershark.main.Main;
import me.coppershark.util.TraceRoute;
import me.coppershark.util.TraceRouteDashCam;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CopperSharkCommand extends CommandBase {
	private Main main;

	public CopperSharkCommand(Main main) {
		this.main = main;
	}

	@Override
	public String getCommandName() {
		return "coppershark";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "coppershark test command";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length <= 0)
			return;
		else if (args.length > 1 && args[0].equalsIgnoreCase("setDiscordId") && args[1].matches("[0-9]{18}")) {
			main.getSettings().putSetting("discordUserID", args[1]);
		} else if (args[0].equalsIgnoreCase("debug")) {
			final TraceRouteDashCam traceroute = main.getTraceRouteDashCam();
			new Thread() {
				@Override
				public void run() {
					traceroute.stopRecording();
					String ip = "IP: " + main.getServerIP();
					String uptime = ", Uptime: " + main.getUptimeMinutes() + " min";
					System.out.println("[Coppershark] debug command " + ip + uptime);
					if (traceroute != null) {
						System.out.println("[Coppershark] \n" + traceroute.getClosestTracert());
						System.out.println("[Coppershark] \n" + traceroute.getMostUsedRoute());
					}
				};
			}.start();
			main.restartTraceRouteDashCam();
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

}