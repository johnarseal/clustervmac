package probeserver.pipeline;

import probeserver.pipeline.handler.PktHandler;
import probeserver.protocol.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PktProcessor extends ChannelInboundHandlerAdapter {

	private static final Version CURRENT_VERSION = Version.VERSION_2;
	
	private Map<Type, PktHandler> handlers;
	
	private final Logger logger = LoggerFactory.getLogger(PktProcessor.class);
	
	public PktProcessor(Map<Type, PktHandler> handlers) {
		this.handlers = handlers;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object pkt) {
		SocketChannel ch = (SocketChannel) ctx.channel();
		logger.trace("A packet is received from {}.", ch.remoteAddress());
		if (pkt instanceof Packet) {
			Packet rPacket = (Packet) pkt;
			if (rPacket.getVersion() != CURRENT_VERSION) {
				logger.warn("Unsupported version of packet from {}. (packet version = {}, expected version = {})",
						ch.remoteAddress(), rPacket.getVersion(), CURRENT_VERSION);
				return;
			}
			for (Type type : handlers.keySet()) {
				if (rPacket.getType() == type) {
					handlers.get(type).handle(rPacket, ch);
					break;
				}
			}
		} else {
			logger.warn("Unknown message from {}.", ch.remoteAddress());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("Error occurs.", cause);
		// Close the connection when an exception is raised.
		ctx.close();
	}
	
}
