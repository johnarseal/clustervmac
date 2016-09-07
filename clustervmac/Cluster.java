package clustervmac;

import java.util.Iterator;
import java.util.List;

import clustervmac.dataschema.*;
import clustervmac.fetchpacket.*;
import hk.ust.mtrec.apbl.ctrlsrv.vmacsrv.cluster.dataschema.CluPacket;
import hk.ust.mtrec.apbl.ctrlsrv.vmacsrv.conn.buf.BufferFactory;

/**
 * Main function of clustering
 * @author johnzz
 *
 */

public class Cluster {
	private List<CluPacket> pktList;
	private TagCluster tagCluster;
	public Cluster(){
		TagCluster tagCluster = new TagCluster();
	}
	public void runCluster(){
		long startTime = System.nanoTime();
		
		//fetch the packet from CSV document
		//PacketFetcher pkFetcher = new CSVPacketFetcher();
		//String [] argv = {"C:/project/smartAP/cluster/groundtruthdata/20160901_mint_virmac(10minmod).csv",};
		//pktList = pkFetcher.fetchPacket(argv);*/
		
		//since it's possible that a same mac will have different tags
		//we have to clean and filter it first
		//List<CluPacket> cluPktList = pkFetcher.filterPacket(pktList);
		List<CluPacket> cPktList = BufferFactory.getBuffer().fetch();
		
		
		List<TagGroup> tagGroupList = tagCluster.clusterByTag(cPktList);
		
		EliminateCluster elimCluster = new EliminateCluster();
		elimCluster.eliminate(tagGroupList);
		
		Iterator<TagGroup> iter = tagGroupList.iterator();
		while(iter.hasNext()){
		    if(iter.next().free()){
		        iter.remove();
		    }
		}
		
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime); 
		System.out.println("run a cluster for " + duration/1000000 + " seconds");		
	}
}
