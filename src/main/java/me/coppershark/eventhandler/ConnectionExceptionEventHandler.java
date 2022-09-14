package me.coppershark.eventhandler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import me.coppershark.event.ConnectEvent;
import me.coppershark.event.ConnectionExceptionEvent;
import me.coppershark.event.DisconnectEvent;
import me.coppershark.main.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Sharable
public class ConnectionExceptionEventHandler extends ChannelInboundHandlerAdapter {
	private Main main;

	public ConnectionExceptionEventHandler(Main main) {
		this.main = main;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void connect(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
		ChannelPipeline pipeline = event.manager.channel().pipeline();
		pipeline.addBefore("packet_handler", this.getClass().getName(), this);
		final IThreadListener mainThread = Minecraft.getMinecraft();
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				MinecraftForge.EVENT_BUS.post(new ConnectEvent(main, event));
			}
		});
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void connect(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
		final IThreadListener mainThread = Minecraft.getMinecraft();
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				MinecraftForge.EVENT_BUS.post(new DisconnectEvent(main, event));
			}
		});
	}

	// TODO can this be replaced by ClientDisconnectionFromServerEvent?
	@SideOnly(Side.CLIENT)
	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable exception) throws Exception {
		super.exceptionCaught(context, exception);
		MinecraftForge.EVENT_BUS.post(new ConnectionExceptionEvent(main, exception));
	}
}