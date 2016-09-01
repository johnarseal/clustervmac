package clustervmac.dataschema;

public class Location {
	//x,y is directly fetched from database, so in the unit of pixel
	private double x;		
	private double y;
	//the time stamp of this location
	private Long ts;
	
	//how many centimeters one pixel equals to
	private static final double pixScale = 1.1;
	
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
	
	//in the unit of cm/s
	public double speed(Location loc)
	{
		double pixDist = Math.sqrt(Math.pow(this.x-loc.getX(),2) + Math.pow(this.y-loc.getY(),2));
		return (pixDist * pixScale) / (this.getTs() - loc.getTs());
	}
}
