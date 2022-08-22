package me.coppershark.command;

import me.coppershark.main.Main;
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
		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("coppershark test command"));
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

}