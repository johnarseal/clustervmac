package clustervmac.dataschema;

// the packet only contains mac address and tag
public class BasicPacket {
	public Long mac_address;
	public PacketTag packetTag;
	
	public Long getMac_address() {
		return mac_address;
	}
	public void setMac_address(Long mac_address) {
		this.mac_address = mac_address;
	}
	
	public PacketTag getPacketTag()
	{
		return packetTag;
	}
	public void setPacketTag(PacketTag packetTag)
	{
		this.packetTag = packetTag;
	}
	
	public BasicPacket(){
		mac_address = null;
		packetTag = null;
	}
	
	public BasicPacket(Long mac,PacketTag tag){
		mac_address = mac;
		packetTag = tag;
	}
	
	@Override
	public boolean equals(Object o){
	    if(o == null)	return false;		
	    if(this == o)	return true;
	    BasicPacket pktO = (BasicPacket) o;
	    // first check tag present, then match tags
	    if(!this.getMac_address().equals(pktO.getMac_address())) return false;
	    
	    return this.getPacketTag().equals(pktO.getPacketTag());	
	}
	
	@Override
	public int hashCode(){
		int result = 0;
		
		result += this.getMac_address();
		result += this.getPacketTag().hashCode();
		return result;
	}	
}
