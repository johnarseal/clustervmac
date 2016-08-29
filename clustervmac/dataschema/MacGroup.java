package clustervmac.dataschema;

public class MacGroup {
	private Long macAddr;
	private Long beginTime;
	private Long endTime;
	private Location beginLoc;	//begin location (actually x,y two variables)
	private Location endLoc;		//end location
	
	public MacGroup(Packet packet) {
		this.macAddr = packet.getMac_address();
		this.beginTime = packet.getTime();
		this.endTime = packet.getTime();
		this.beginLoc = packet.getLocation();
		this.endLoc = packet.getLocation();
	}
	public MacGroup() {
		// TODO Auto-generated constructor stub
	}
	public Long getMacAddr() {
		return macAddr;
	}
	public void setMacAddr(Long macAddr) {
		this.macAddr = macAddr;
	}
	public Long getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Long beginTime) {
		this.beginTime = beginTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public Location getBeginLoc() {
		return beginLoc;
	}
	public void setBeginLoc(Location beginLoc) {
		this.beginLoc = beginLoc;
	}
	public Location getEndLoc() {
		return endLoc;
	}
	public void setEndLoc(Location endLoc) {
		this.endLoc = endLoc;
	}
}
