package probeserver.protocol;

import java.io.Serializable;

public class Packet implements Serializable {

	private static final long serialVersionUID = 8256727503395550802L;

	public static final int HEADER_SIZE = 4;
	
	public static final int VERSION_SIZE = 1;
	public static final int TYPE_SIZE = 1;
	public static final int PAYLOAD_LENGTH_SIZE = 2;
	
	private Version version;
	private Type type;
	private byte[] payload;

	public Packet() {

	}

	public Packet(Version version, Type type, byte[] payload) {
		this.version = version;
		this.type = type;
		this.payload = payload;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

}
