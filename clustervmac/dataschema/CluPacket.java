package clustervmac.dataschema;
import java.util.*;


/**
 * CluPacket stands for Cluster Packet: the packet schema used for cluster
 * @author johnzz
 *
 */

public class CluPacket extends BasicCluPacket{
	private Long time;
	private Location location;
	
	public CluPacket(String src,String[] strArr){
		if(src == "csv"){
			this.time = Long.parseLong(strArr[0]);
			this.mac_address = Long.decode("0x"+strArr[2]);
			packetTag = new CluPacketTag(strArr[4],Arrays.copyOfRange(strArr,5,12));
			location = null;
		}
	}
	
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public TimeMacPair getTimeMacPair(){
		TimeMacPair tmP = new TimeMacPair(this.time,this.mac_address);
		return tmP;
	}
	public void printTagLog(){
		System.out.print("mac:"+Long.toHexString(this.mac_address)+" tag:");
		this.packetTag.printLog();
	}
}