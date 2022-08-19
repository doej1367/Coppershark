package me.networkanalyzer.command;

import me.networkanalyzer.main.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class TestCommand extends CommandBase {
	private Main main;

	public TestCommand(Main main) {
		this.main = main;
	}

	@Override
	public String getCommandName() {
		return "test";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "hyblockrnganalyzer test command";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		// TODO (placeholder for testing something)
		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("TestCommand"));
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

}