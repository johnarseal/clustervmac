package clustervmac;

import java.util.List;

import clustervmac.dataschema.*;
import clustervmac.fetchpacket.*;

public class Cluster {
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		
		TagCluster tagCluster = new TagCluster();
		
		//fetch the packet from CSV document
		PacketFetcher pkFetcher = new CSVPacketFetcher();
		String [] argv = {"C:/project/smartAP/cluster/virmac_test.csv",};
		List<Packet> pktList = pkFetcher.fetchPacket(argv);
		
		List<TagGroup> tagGroupList = tagCluster.clusterByTag(pktList);
		
		//log and print
		for(TagGroup tagGroup:tagGroupList){
			tagGroup.printLog();
			System.out.print("\n");
		}
		
		EliminateCluster elimCluster = new EliminateCluster();
		elimCluster.eliminate(tagGroupList);
		
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime); 
		System.out.println(duration/1000000);
	}
}
