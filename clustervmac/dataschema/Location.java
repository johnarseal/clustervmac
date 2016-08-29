package clustervmac.dataschema;

public class Location {
	private double x;
	private double y;
	//the time stamp of this location
	private Long ts;
	
	public Location(double tx,double ty,Long t_ts){
		x = tx;
		y = ty;
		ts = t_ts;
	}
	
	public Long getTs() {
		return ts;
	}

	public void setTs(Long ts) {
		this.ts = ts;
	}

	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double dist(Location loc)
	{
		return 0;
	}
}
