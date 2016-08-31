package clustervmac.dataschema;

public class MacGroup {
	private Long macAddr;
	private Long beginTime;
	private Long endTime;
	private Location beginLoc;	//begin location (actually x,y two variables)
	private Location endLoc;		//end location
	private Long prevGap;		//the previous time gap of this mac
	private Long leadMac;		//the first mac addr of this MAC series (e.g. A->B->C, C's leadMac is A)
	
	public MacGroup(Packet packet) {
		macAddr = packet.getMac_address();
		beginTime = packet.getTime();
		endTime = packet.getTime();
		beginLoc = packet.getLocation();
		endLoc = packet.getLocation();
		leadMac = null;
		prevGap = null;
	}
	public Long getPrevGap() {
		return prevGap;
	}
	public void setPrevGap(Long prevGap) {
		this.prevGap = prevGap;
	}
	public Long getLeadMac() {
		return leadMac;
	}
	public void setLeadMac(Long leadMac) {
		this.leadMac = leadMac;
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
