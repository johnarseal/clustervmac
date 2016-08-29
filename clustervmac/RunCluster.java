package clustervmac;

import java.util.List;

import clustervmac.dataschema.*;
import clustervmac.fetchpacket.*;

public class RunCluster {
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		
		Cluster cluster = new Cluster();
		
		//fetch the packet from CSV document
		PacketFetcher pkFetcher = new CSVPacketFetcher();
		String [] argv = {"C:/project/smartAP/cluster/virmac_test.csv",};
		List<Packet> pktList = pkFetcher.fetchPacket(argv);
		
		List<TagGroup> tagGroupList = cluster.clusterByTag(pktList);
		
		for(TagGroup tagGroup:tagGroupList){
			tagGroup.printLog();
			System.out.print("\n");
		}
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime); 
		System.out.println(duration/1000000);
}
}
