package me.coppershark.eventhandler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import me.coppershark.main.Main;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Sharable
public class PacketEventHandler extends SimpleChannelInboundHandler<Packet> {
	private Main main;

	public PacketEventHandler(Main main) {
		super(false);
		this.main = main;
	}

	@SubscribeEvent
	public void connect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		ChannelPipeline pipeline = event.manager.channel().pipeline();
		pipeline.addBefore("packet_handler", this.getClass().getName(), this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
		try {
			if (msg instanceof S00PacketKeepAlive) {
				System.out.println("KeepAlive: key = " + ((S00PacketKeepAlive) msg).func_149134_c());
			}
		} catch (Exception e) {
		} finally {
			ctx.fireChannelRead(msg);
		}
	}
}