package hk.ust.mtrec.apbl.ctrlsrv.probe.bean;

/**
 * The status of a probe connection.
 * @author tanjiajie
 *
 */
public class ProbeStatus {
	
	private Probe probe;
	private long recentRegTime;
	private long recentAliveTime;
	private long recentDataArrivedTime;
	
	ProbeStatus(Probe probe) {
		this.probe = probe;
	}
	
	public String getId() {
		return probe.getId();
	}
	
	public String getAlias() {
		return probe.getAlias();
	}

	public long getRecentRegTime() {
		return recentRegTime;
	}
	
	public void setRecentRegTime(long recentRegTime) {
		this.recentRegTime = recentRegTime;
	}

	public long getRecentAliveTime() {
		return recentAliveTime;
	}

	public void setRecentAliveTime(long recentAliveTime) {
		this.recentAliveTime = recentAliveTime;
	}
	
	public long getRecentDataArrivedTime() {
		return recentDataArrivedTime;
	}
	
	public void setRecentDataArrivedTime(long recentDataArrivedTime) {
		this.recentDataArrivedTime = recentDataArrivedTime;
	}
	
}
