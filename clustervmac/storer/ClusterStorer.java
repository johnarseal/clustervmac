package clustervmac.storer;

import clustervmac.dataschema.MacGroup;
import clustervmac.dbconnection.DBConnector;

public class ClusterStorer {
	public void storeCluster(MacGroup macGroup){
		DBConnector conn = new DBConnector("PostgreSQL");
		conn.connect("egz209.ust.hk:7023/rsa_dev", "root", "v1mZo48q2VdjqSn");
		/*
		String sql = "INSERT INTO mtrec_test WHERE areaid = 'mtrec_1'"
				+ " AND ts >= " + startTS*1000 + " AND ts <= " + endTS*1000
				+ " AND (did::BIT(48) & x'020000000000'::BIT(48)) != 0::BIT(48)";*/
	}
}
