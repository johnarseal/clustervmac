package clustervmac.dataschema;

public class MacGroup {
	private String macAddr;
	private double beginTime;
	private double endTime;
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
	public String getMacAddr() {
		return macAddr;
	}
	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}
	public double getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(double beginTime) {
		this.beginTime = beginTime;
	}
	public double getEndTime() {
		return endTime;
	}
	public void setEndTime(double endTime) {
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
