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
		// TODO (placeholder for testing something)
		new Thread() {
			@Override
			public void run() {
				String ip = "IP: " + main.getServerIP() + "\n";
				String uptime = "Uptime: " + main.getUptimeMinutes() + " min\n";
				TraceRouteDashCam tracert = TraceRouteDashCam.stopAndReturnRecording();
				System.out.println("[Coppershark] debug command\n" + ip + uptime);
				for (TraceRoute tr : tracert.getDashRecord())
					System.out.println("[Coppershark] " + tr.toStringOneLineIpsOnly());
			};
		}.start();
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

}