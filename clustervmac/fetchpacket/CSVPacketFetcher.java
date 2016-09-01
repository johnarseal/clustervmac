package clustervmac.fetchpacket;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

import clustervmac.dataschema.*;

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
	}
	
	public List<Packet> filterPacket(List<Packet> pktList){
		HashMap<BasicPacket,Integer> macTagDict = new HashMap<>();
		for(Packet pkt:pktList){
			BasicPacket bpkt = (BasicPacket) pkt;
			Integer curCnt;
			if((curCnt = macTagDict.get(bpkt)) == null){
				macTagDict.put(bpkt, 1);
			}
			else{
				macTagDict.put(bpkt, curCnt + 1);
			}
		}
		Set<Map.Entry<BasicPacket,Integer>> macTagSet = macTagDict.entrySet();
		HashMap<Long,PacketTag> mac2TagDict = new HashMap<>();
		for(Map.Entry<BasicPacket,Integer> ent:macTagSet){
			PacketTag oldTag;
			Long curMac = ent.getKey().getMac_address();
			if((oldTag=mac2TagDict.get(curMac)) == null){
				mac2TagDict.put(ent.getKey().getMac_address(),ent.getKey().getPacketTag());
			}
			else{
				Integer curCnt = ent.getValue();
				BasicPacket oldPacket = new BasicPacket(curMac,oldTag);
				Integer oldCnt = macTagDict.get(oldPacket);
				if(curCnt > oldCnt){
					mac2TagDict.put(curMac,ent.getKey().getPacketTag());
				}
			}
		}
		HashMap<TimeMacPair,Integer> tmRecord = new HashMap<>();
		List<Packet> rtPkList = new ArrayList<>();
		for(Packet pkt:pktList){
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
