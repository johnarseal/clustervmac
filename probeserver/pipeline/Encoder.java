package probeserver.pipeline;

import probeserver.protocol.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class Encoder extends MessageToMessageEncoder<Packet> {

	private static final byte MSG_TYPE_DIR_SERVER_TO_PROBCTRL = 0x10;
	
	public Encoder() {
		
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet pkt, List<Object> out)
			throws Exception {
		if (pkt != null) {
			out.add(encodePacket(pkt));
		}
	}

	public static ByteBuf encodePacket(Packet packet) throws IllegalArgumentException {
		if ((packet.getVersion() == null) || (packet.getVersion() == Version.UNKNOWN)) {
			throw new IllegalArgumentException("Message version cannot be null or UNKNOWN");
		}
		if ((packet.getType() == null) || (packet.getType() == Type.UNKNOWN)) {
			throw new IllegalArgumentException("Message type cannot be null or UNKNOWN");
		}
		if ((packet.getPayload() == null) || (packet.getPayload().length == 0)) {
			throw new IllegalArgumentException("Message payload cannot be null or empty");
		}
		// version(1b) + type(1b) + payload_len(4b) + payload(nb)
		int size = Packet.HEADER_SIZE + packet.getPayload().length;

		ByteBuf buffer = Unpooled.buffer(size);
		buffer.writeByte(packet.getVersion().getByteValue());
		buffer.writeByte(packet.getType().getByteValue() | MSG_TYPE_DIR_SERVER_TO_PROBCTRL);
		buffer.writeShort(packet.getPayload().length);
		buffer.writeBytes(packet.getPayload());
		return buffer;
	}

}
