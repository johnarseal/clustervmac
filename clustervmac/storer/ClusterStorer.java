package clustervmac.storer;

import clustervmac.dataschema.MacGroup;
import clustervmac.dbconnection.DBConnector;

public class ClusterStorer {
	private DBConnector conn;
	private static int sqlNum = 0;
	public ClusterStorer(String addr, String usr, String pwd){
		conn = new DBConnector("PostgreSQL");
		conn.connect(addr, usr, pwd);		
	}
	
	public void storeCluster(MacGroup macGroup){
		String leadMac;
		if(macGroup.getLeadMac() == null){
			leadMac = "NULL";
		}
		else{
			leadMac = macGroup.getLeadMac().toString();
		}
		String sql = "INSERT INTO cluster_results (areaid,mac,leadmac,startts,endts)" + 
		"VALUES('hkust_1002'," + macGroup.getMacAddr() + "," + leadMac + 
		"," + macGroup.getBeginTime() + "," + macGroup.getEndTime() + ")";
		sqlNum += 1;
		System.out.println("executing:" + sqlNum + "query");
		conn.update(sql);
	}
}
