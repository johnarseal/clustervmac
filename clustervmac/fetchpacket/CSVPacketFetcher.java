package clustervmac.fetchpacket;

import java.util.ArrayList;
import java.util.List;

import clustervmac.dataschema.Packet;

public class CSVPacketFetcher implements PacketFetcher {

	public List<Packet> assemblePacket(String src,List<String []> pktStringList){
		List<Packet> rtPacketList = new ArrayList<Packet>();
		Packet pkt = null;
		for (String[] pktString : pktStringList){
			pkt = new Packet(src,pktString);
			rtPacketList.add(pkt);
		}
		return rtPacketList;
	}
	public List<Packet> fetchPacket(String[] argv){
		String filePath = argv[0];
		CSVReader csvReader = new CSVReader();
		List<String []> rawPacketList = csvReader.readCSV(filePath);
		List<Packet> PacketList = assemblePacket("csv",rawPacketList);
		return PacketList;
		//DBConnector dbCon = new DBConnector("PostgreSQL");
		//dbCon.connect("egz209.ust.hk:7023/rsa_dev", "root", "v1mZo48q2VdjqSn");
		//sql = "SELECT * FROM "
		//ResultSet rs = dbCon.execute(sql)
	}	
	
}
