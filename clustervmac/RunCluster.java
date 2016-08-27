package clustervmac;

import java.util.List;

import clustervmac.dataschema.*;
import clustervmac.fetchpacket.*;

public class RunCluster {
	public static void main(String[] args) {
		Cluster cluster = new Cluster();
		PacketFetcher pkFetcher = new CSVPacketFetcher();
		
		//fetch the packet from CSV document
		String [] argv = {"C:/project/smartAP/cluster/20160826_12345678901_virmac.csv",};
		List<Packet> pktList = pkFetcher.fetchPacket(argv);
		
		List<TagGroup> tagGroupList = cluster.clusterBYTag(pktList);
		for(TagGroup tagGroup:tagGroupList){
			tagGroup.printLog();
			System.out.print("\n");
		}
	}
}
