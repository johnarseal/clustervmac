package clustervmac.dataschema;

public class CluPacketTag{
/*	private String sup_rate ;
	private String ext_sup_rate;
	private String HT_cap;
	private String ext_cap;
	private String interworking;
	private String vendor1;
	private String vendor2;
	private String vendor_EPI_HT;*/
	
	private final String tagPresent;
	private final String[] tags;
	private final int tagNum;
			
	public CluPacketTag(String tagP,String[] strArr){
		tagPresent = tagP;
		tags = strArr;
		tagNum = strArr.length;
	}	
	
	@Override
	public boolean equals(Object o){
	    if(o == null)	return false;		
	    if(this == o)	return true;
	    CluPacketTag pktO = (CluPacketTag) o;
	    // first check tag present, then match tags
	    if(!this.tagPresent.equals(pktO.tagPresent))	return false;
	    if(this.tagNum != pktO.tagNum)	return false;
	    else{
	    	for(int i = 0; i < tagNum;i++){
	    		if (!tags[i].equals(pktO.tags[i])){
	    			return false;
	    		}
	    	}
	    	return true;
	    }
	}
	
	@Override
	public int hashCode(){
		int result = 0;
		// we are assuming the string here is byte hex, so the length is a
		int strLen;
		strLen = tagPresent.length();
		for(int i = 0; i < strLen; i+=2){
			result += Integer.parseInt(tagPresent.substring(i, i+2),16);
		}
		for(int i = 0; i < tagNum; i++){
			strLen = tags[i].length();
			//if the string is too long, we only want the front 8 and rear 8
			if (strLen <= 16){
				for(int j = 0; j < strLen; j += 2){
					result += Integer.parseInt(tags[i].substring(j, j+2),16);
				}
			}
			else{
				for(int j = 0; j < 8; j += 2){
					result += Integer.parseInt(tags[i].substring(j, j+2),16);
				}
				for(int j = strLen; j > strLen-8; j -= 2){
					result += Integer.parseInt(tags[i].substring(j-2, j),16);
				}
			}
		}
		return result;
	}
	
	public void printLog(){
		System.out.print(tagPresent);
    	for(int i = 0; i < tagNum;i++){
    		System.out.print(" "+tags[i]);
    	}		
		System.out.print("\n");
	}
}
