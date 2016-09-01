package probeserver.pipeline;

import java.util.List;

import probeserver.protocol.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class Decoder extends ReplayingDecoder<Decoder.DecodingState> {

	private Packet packet;

	public Decoder() {
		this.reset();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
			throws Exception {
		switch (state()) {
		case VERSION:
			this.packet.setVersion(Version.fromByte(in.readByte()));
			checkpoint(DecodingState.TYPE);
		case TYPE:
			this.packet.setType(Type.fromByte((byte) (in.readByte() & 0x0F)));
			checkpoint(DecodingState.PAYLOAD_LENGTH);
		case PAYLOAD_LENGTH:
			int size = in.readShort();
			if (size < 0)
				throw new Exception("Invalid content size");
			byte[] payload = new byte[size];
			this.packet.setPayload(payload);
			checkpoint(DecodingState.PAYLOAD);
		case PAYLOAD:
			in.readBytes(this.packet.getPayload(), 0, this.packet.getPayload().length);
			try {
				out.add(packet);
				return;
			} finally {
				this.reset();
			}
		default:
			throw new Exception("unknown decoding state: " + state());
		}
	}

	private void reset() {
		checkpoint(DecodingState.VERSION);
		this.packet = new Packet();
	}

	public enum DecodingState {
		VERSION,
		TYPE,
		PAYLOAD_LENGTH,
		PAYLOAD;
	}
}

