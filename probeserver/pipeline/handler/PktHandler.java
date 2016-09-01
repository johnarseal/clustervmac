package probeserver.pipeline.handler;

import probeserver.protocol.*;
import io.netty.channel.socket.SocketChannel;

public interface PktHandler {
	
	public void handle(Packet pkt, SocketChannel ch);

}
