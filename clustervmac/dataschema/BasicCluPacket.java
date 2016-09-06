package clustervmac.dataschema;

// the packet only contains mac address and tag
public class BasicCluPacket {
	public Long mac_address;
	public CluPacketTag packetTag;
	
	public Long getMac_address() {
		return mac_address;
	}
	public void setMac_address(Long mac_address) {
		this.mac_address = mac_address;
	}
	
	public CluPacketTag getPacketTag()
	{
		return packetTag;
	}
	public void setPacketTag(CluPacketTag packetTag)
	{
		this.packetTag = packetTag;
	}
	
	public BasicCluPacket(){
		mac_address = null;
		packetTag = null;
	}
	
	public BasicCluPacket(Long mac,CluPacketTag tag){
		mac_address = mac;
		packetTag = tag;
	}
	
	@Override
	public boolean equals(Object o){
	    if(o == null)	return false;		
	    if(this == o)	return true;
	    BasicCluPacket pktO = (BasicCluPacket) o;
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
