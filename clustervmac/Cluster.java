package clustervmac;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import clustervmac.dataschema.MacGroup;
import clustervmac.dataschema.Packet;
import clustervmac.dataschema.PacketTag;
import clustervmac.dataschema.TagGroup;
import clustervmac.dataschema.TimeMacPair;

public class Cluster {
	private List<Packet> rawList;
	private Map<PacketTag,TagGroup> tagRecord;
	private double maxSpeed;
	private double maxExistTime;
	private double threshold;
	
	public Cluster(){
		tagRecord = new HashMap<PacketTag,TagGroup>();
	}
	
	public List<TagGroup> clusterBYTag(List<Packet> rawList)
	{
		List<TagGroup> tagGroupList = new ArrayList<TagGroup>();// to be returned
		TagGroup tmpTagGroup;
		int cluNum = 0;
		for(Packet packet:rawList)
		{
			PacketTag tmpTag = packet.getPacketTag();
			tmpTagGroup = tagRecord.get(tmpTag);
			if(tmpTagGroup == null)
			{
				cluNum += 1;
				System.out.print("New Tag:");
				tmpTag.printLog();
				//System.out.println("new MAC:"+Long.toHexString(packet.getMac_address()));
				tmpTagGroup = new TagGroup(tmpTag);
				tagGroupList.add(tmpTagGroup);
			}
			updateTagGroup(tmpTagGroup,packet);
		}
		System.out.println(cluNum);
		return tagGroupList;
	}
	
	public void updateTagGroup(TagGroup tagGroup, Packet packet)
	{		
		TimeMacPair timeMacPair;
		
		//we are getting a reference here,
		//so we don't need to update macRecord if macGroup is already in
		MacGroup macGroup = tagGroup.macRecord.get(packet.getMac_address());
		
		// if the MAC never appeared
		if(macGroup == null)
		{
			macGroup = new MacGroup(packet);
			tagGroup.macRecord.insert(packet.getMac_address(),macGroup);
			timeMacPair = new TimeMacPair(packet.getTime(),packet.getMac_address());
			tagGroup.startTsRecord.insert(timeMacPair, packet.getMac_address());
			timeMacPair = new TimeMacPair(packet.getTime(),packet.getMac_address());
			tagGroup.endTsRecord.insert(timeMacPair, packet.getMac_address());
		}
		else{
			if(packet.getTime() < macGroup.getBeginTime())
			{
				// update startTsRecord
				timeMacPair = new TimeMacPair(macGroup.getBeginTime(),packet.getMac_address());
				tagGroup.startTsRecord.remove(timeMacPair);
				timeMacPair.setTime(packet.getTime());
				tagGroup.startTsRecord.insert(timeMacPair, packet.getMac_address());
				// update macRecord
				macGroup.setBeginTime(packet.getTime());
				tagGroup.macRecord.update(packet.getMac_address(),macGroup);
			}
			if(packet.getTime() > macGroup.getBeginTime())
			{
				// update endTsRecord
				timeMacPair = new TimeMacPair(macGroup.getEndTime(),packet.getMac_address());
				tagGroup.endTsRecord.remove(timeMacPair);
				timeMacPair.setTime(packet.getTime());
				tagGroup.endTsRecord.insert(timeMacPair, packet.getMac_address());
				// update macRecord
				macGroup.setEndTime(packet.getTime());
				tagGroup.macRecord.update(packet.getMac_address(),macGroup);
			}
		}
	}
	/*
	public void clusterByElimination(TagGroup tagGroup)
	{
		TimeMacPair curTimeMacPair = new TimeMacPair();
		TimeMacPair timeMacPair = new TimeMacPair();
		String macAddr;
		MacGroup currentMacGroup = new MacGroup();
		List<MacGroup> candidates = new ArrayList<MacGroup>();
		MacGroup candidate;
		int flag;
		double timeGap;
		double dist;
		
		while(tagGroup.getEndTsRecord().size() != 0)
		{
			// select the mac address that hasn't appear for the longest time
			// as the current mac addres that seeking next
			curTimeMacPair = tagGroup.getEndTsRecord().firstKey();
			macAddr = tagGroup.getEndTsRecord().get(curTimeMacPair);
			currentMacGroup = tagGroup.getMacRecord().get(macAddr);
			// if endTime of this mac address is long enough before
			// assume it has changed and will never appear again, 
			//so let's find it's next
			if((System.currentTimeMillis() - curTimeMacPair.getTime()) > maxExistTime)
			{
				// When choosing candidates for the next mac address of the current mac
				// the next's start is larger than current end, but shouldn't be too big
				
				/cantidates = tagGroup.getStartTsRecord().find(time >
				//curTimeMacPair.time and time < curTimeMacPair.time + maxSilent);
				flag = 0;
				// iterate through candidates
				for(int i = 0; i < candidates.size(); i++)
				{
					candidate = candidates.get(i);
					dist = candidate.getEndLoc().dist(currentMacGroup.getBeginLoc());
					timeGap = candidate.getBeginTime() - curTimeMacPair.getTime();
					// if it is a fast moving, eliminate
					if((dist / timeGap) > maxSpeed)
						continue;
					// if it is a frequent mac address change, eliminate
					
					//if (curTimeMacPair.macAddr.prevGap < threshold) and (timeGap < threshold):
					//	continue
					
					// when reach here, the candidate is valid
					// the record method can be either writing to database or to file
					System.out.println("curTimeMacPair.macAddr.next is candidate.macAddr");
					//candidate.macAddr.prevGap = timeGap
					timeMacPair.setMacAddr(candidate.getMacAddr());
					timeMacPair.setTime(candidate.getBeginTime());
					tagGroup.getStartTsRecord().remove(timeMacPair);
					flag = 1;
					break;
				}
				if(flag == 0)
				{
					System.out.println("curTimeMacPair.macAddr.next is NULL");
				}
				tagGroup.getStartTsRecord().remove(currentMacGroup);
				tagGroup.getEndTsRecord().remove(currentMacGroup);
			}
		}
	}*/
}