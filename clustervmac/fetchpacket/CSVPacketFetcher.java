package clustervmac.fetchpacket;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

import clustervmac.dataschema.*;

public class CSVPacketFetcher implements PacketFetcher {

	public List<CluPacket> assemblePacket(String src,List<String []> pktStringList){
		List<CluPacket> rtPacketList = new ArrayList<CluPacket>();
		CluPacket pkt = null;
		for (String[] pktString : pktStringList){
			pkt = new CluPacket(src,pktString);
			rtPacketList.add(pkt);
		}
		return rtPacketList;
	}
	
	public List<CluPacket> fetchPacket(String[] argv){
		String filePath = argv[0];
		CSVReader csvReader = new CSVReader();
		List<String []> rawPacketList = csvReader.readCSV(filePath);
		List<CluPacket> PacketList = assemblePacket("csv",rawPacketList);
		return PacketList;
	}
	
	public List<CluPacket> filterPacket(List<CluPacket> pktList){
		HashMap<BasicCluPacket,Integer> macTagDict = new HashMap<>();
		for(CluPacket pkt:pktList){
			BasicCluPacket bpkt = (BasicCluPacket) pkt;
			Integer curCnt;
			if((curCnt = macTagDict.get(bpkt)) == null){
				macTagDict.put(bpkt, 1);
			}
			else{
				macTagDict.put(bpkt, curCnt + 1);
			}
		}
		Set<Map.Entry<BasicCluPacket,Integer>> macTagSet = macTagDict.entrySet();
		HashMap<Long,CluPacketTag> mac2TagDict = new HashMap<>();
		for(Map.Entry<BasicCluPacket,Integer> ent:macTagSet){
			CluPacketTag oldTag;
			Long curMac = ent.getKey().getMac_address();
			if((oldTag=mac2TagDict.get(curMac)) == null){
				mac2TagDict.put(ent.getKey().getMac_address(),ent.getKey().getPacketTag());
			}
			else{
				Integer curCnt = ent.getValue();
				BasicCluPacket oldPacket = new BasicCluPacket(curMac,oldTag);
				Integer oldCnt = macTagDict.get(oldPacket);
				if(curCnt > oldCnt){
					mac2TagDict.put(curMac,ent.getKey().getPacketTag());
				}
			}
		}
		HashMap<TimeMacPair,Integer> tmRecord = new HashMap<>();
		List<CluPacket> rtPkList = new ArrayList<>();
		for(CluPacket pkt:pktList){
			TimeMacPair curTM = pkt.getTimeMacPair();		
			
			if(tmRecord.containsKey(curTM)){
				continue;
			}
			if(mac2TagDict.get(pkt.getMac_address()).equals(pkt.getPacketTag())){
				tmRecord.put(curTM,1);
				rtPkList.add(pkt);
			}
		}
		return rtPkList;
	}
	
}
