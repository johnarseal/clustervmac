package probeserver.protocol;

public enum Version {
	
	VERSION_1((byte) 0x01),
	VERSION_2((byte) 0x02),
	VERSION_3((byte) 0x03),
	UNKNOWN((byte) 0x00);
	
	private final byte b;
	
	private Version(byte b) {
		this.b = b;
	}
	
	public static Version fromByte(byte b) {
		for (Version code : values()) {
			if (code.b == b)
				return code;
		}
		return UNKNOWN;
	}
	
	public byte getByteValue() {
		return b;
	}

}
