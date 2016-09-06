package clustervmac;

import java.util.List;

import clustervmac.dataschema.*;
import clustervmac.fetchpacket.*;


/**
 * Main function of clustering
 * @author johnzz
 *
 */

public class Cluster {
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		
		TagCluster tagCluster = new TagCluster();
		
		//fetch the packet from CSV document
		PacketFetcher pkFetcher = new CSVPacketFetcher();
		String [] argv = {"C:/project/smartAP/cluster/groundtruthdata/20160901_mint_virmac(10minmod).csv",};
		List<CluPacket> pktList = pkFetcher.fetchPacket(argv);
		
		//since it's possible that a same mac will have different tags
		//we have to clean and filter it first
		List<CluPacket> cluPktList = pkFetcher.filterPacket(pktList);
		
		System.out.println(cluPktList.size());
		
		List<TagGroup> tagGroupList = tagCluster.clusterByTag(cluPktList);
		
		//log and print
		/*
		System.out.println("log in main");
		int tagCnt = 0;
		for(TagGroup tagGroup:tagGroupList){
			System.out.print("Tag Group " + tagCnt++);
			tagGroup.printLog();
			System.out.print("\n");
		}*/
		
		EliminateCluster elimCluster = new EliminateCluster();
		elimCluster.eliminate(tagGroupList);
		
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime); 
		System.out.println(duration/1000000);
	}
}
