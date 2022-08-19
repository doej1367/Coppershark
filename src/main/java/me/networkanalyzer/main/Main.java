package me.networkanalyzer.main;

import me.networkanalyzer.command.TestCommand;
import me.networkanalyzer.eventhandler.PacketEventHandler;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Main.MODID, version = Main.VERSION)
public class Main {
	public static final String MODID = "networkmonitor";
	public static final String VERSION = "1.0";

	@EventHandler
	public void init(FMLInitializationEvent event) {
		ClientCommandHandler.instance.registerCommand(new TestCommand(this));
		MinecraftForge.EVENT_BUS.register(new PacketEventHandler(this));
		System.out.println("[OK] registered events");
		System.out.println("[OK] init Minecraft Network Monitor");
	}
}
