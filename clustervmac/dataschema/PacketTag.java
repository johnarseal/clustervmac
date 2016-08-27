package clustervmac.dataschema;

public class PacketTag implements Comparable<PacketTag>{
	private String sup_rate ;
	private String ext_sup_rate;
	private String HT_cap;
	private String ext_cap;
	private String interworking;
	private String vendor1;
	private String vendor2;
	private String vendor_EPI_HT;

	public PacketTag(String[] strArr){
		sup_rate = strArr[0];
		ext_sup_rate = strArr[1];
		HT_cap = strArr[2];
		ext_cap = strArr[3];
		//interworking = strArr[4];
		vendor1 = strArr[4];
		vendor2 = strArr[5];
		vendor_EPI_HT = strArr[6];
	}	
	
	public int compareTo(PacketTag tag2) {
		return 0;
	}	
	
	public String getSup_rate() {
		return sup_rate;
	}
	public void setSup_rate(String sup_rate) {
		this.sup_rate = sup_rate;
	}
	
	public String getExt_sup_rate() {
		return ext_sup_rate;
	}
	public void setExt_sup_rate(String ext_sup_rate) {
		this.ext_sup_rate = ext_sup_rate;
	}
	
	public String getHT_cap() {
		return HT_cap;
	}
	public void setHT_cap(String hT_cap) {
		HT_cap = hT_cap;
	}
	
	public String getExt_cap() {
		return ext_cap;
	}
	public void setExt_cap(String ext_cap) {
		this.ext_cap = ext_cap;
	}
	
	public String getInterworking() {
		return interworking;
	}
	public void setInterworking(String interworking) {
		this.interworking = interworking;
	}
	
	public String getVendor1() {
		return vendor1;
	}
	public void setVendor1(String vendor1) {
		this.vendor1 = vendor1;
	}
	
	public String getVendor2() {
		return vendor2;
	}
	public void setVendor2(String vendor2) {
		this.vendor2 = vendor2;
	}
	
	public String getVendor_EPI_HT() {
		return vendor_EPI_HT;
	}
	public void setVendor_EPI_HT(String vendor_EPI_HT) {
		this.vendor_EPI_HT = vendor_EPI_HT;
	}

}
