package clustervmac.dataschema;


public class TimeMacPair implements Comparable<TimeMacPair>{
	private Long time;
	private Long macAddr;
	
	public TimeMacPair(Long t,Long m){
		time = t;
		macAddr = m;
	}
	
	//copy constructor
	public TimeMacPair(TimeMacPair tm){
		this(tm.getTime(),tm.getMacAddr());
	}
	
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Long getMacAddr() {
		return macAddr;
	}
	public void setMacAddr(Long macAddr) {
		this.macAddr = macAddr;
	}
	
	public int compareTo(TimeMacPair pair2) {
		if(!this.time.equals(pair2.time)){
			if(this.time > pair2.time){
				return 1;
			}
			else{
				return -1;
			}
		}
		else{
			if(this.macAddr > pair2.macAddr){
				return 1;
			}
			else if(this.macAddr.equals(pair2.macAddr)){
				return 0;
			}
			else{
				return -1;
			}			
		}
	}
}
