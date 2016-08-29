package clustervmac.dataschema;
import java.util.*;

public class Packet {
	private Long time;
	private Long mac_address;
	private Location location;
	private PacketTag packetTag;
	
	public Packet(String src,String[] strArr){
		if(src == "csv"){
			this.time = Long.parseLong(strArr[0]);
			this.mac_address = Long.decode("0x"+strArr[2]);
			packetTag = new PacketTag(strArr[4],Arrays.copyOfRange(strArr,5,12));
			location = null;
		}
	}
	
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	
	public Long getMac_address() {
		return mac_address;
	}
	public void setMac_address(Long mac_address) {
		this.mac_address = mac_address;
	}
	
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public PacketTag getPacketTag()
	{
		return packetTag;
	}
	public void setPacketTag(PacketTag packetTag)
	{
		this.packetTag = packetTag;
	}
	
}