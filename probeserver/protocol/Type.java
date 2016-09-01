package probeserver.protocol;

public enum Type {

//	// probe to server
//	P2S_REGISTER((byte) 0x11),
//	P2S_KEEPALIVE((byte) 0x12),
//	P2S_DATA((byte) 0x13),
//	
//	// server to probe
//	S2P_REGACK((byte) 0x01),
//	S2P_INTERNAL_OPERATION((byte) 0x02),
//	S2P_EXTERNAL_OPERATION((byte) 0x03),
	SYSTEM((byte) 0x01),
	DATA((byte) 0x02),
	BUILTINOPER((byte) 0x03),
	EXTERNELOPER((byte) 0x04),
	FILE((byte) 0x05),
	
	UNKNOWN((byte) 0x00);

	private final byte b;

	private Type(byte b) {
		this.b = b;
	}

	public static Type fromByte(byte b) {
		for (Type code : values()) {
			if (code.b == b)
				return code;
		}
		return UNKNOWN;
	}

	public byte getByteValue() {
		return b;
	}

}
