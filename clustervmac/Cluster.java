package clustervmac;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import clustervmac.dataschema.MacGroup;
import clustervmac.dataschema.Packet;
import clustervmac.dataschema.PacketTag;
import clustervmac.dataschema.TagGroup;
import clustervmac.dataschema.TimeMacPair;

public class Cluster {
	private List<Packet> rawList;
	private TreeMap<PacketTag,TagGroup> tagRecord;
	private double maxSpeed;
	private double maxExistTime;
	private double threshold;
	
	public void init() // init all data
	{

	}
	
	public List<TagGroup> clusterBYTag(List<Packet> rawList)
	{
		List<TagGroup> tagGroupList = new ArrayList<TagGroup>();// to be returned
		Packet packet;// = new Packet();
		TagGroup tmpTagGroup;
		int size = rawList.size();
		for(int i = 0; i < size; i++)
		{
			packet = rawList.get(i);
			if(!tagRecord.containsKey(packet))
			{
				tmpTagGroup = new TagGroup();
				tagGroupList.add(tmpTagGroup);
			}
			else
			{
				tmpTagGroup = tagRecord.get(packet.getPacketTag());
			}
			updateTagGroup(tmpTagGroup,packet);
		}
		return tagGroupList;
	}
	
	public void updateTagGroup(TagGroup tagGroup, Packet packet)
	{
		MacGroup macGroup;
		TimeMacPair timeMacPair = new TimeMacPair();
		if(!tagGroup.getMacRecord().containsKey(packet.getMac_address()))
		{
			macGroup = new MacGroup(packet);
		}
		else
		{
			macGroup = tagGroup.getMacRecord().get(packet.getMac_address());
			// update startTsRecord
			if(macGroup.getBeginTime() > packet.getTime())
			{
				timeMacPair.setTime(macGroup.getBeginTime());
				timeMacPair.setMacAddr(packet.getMac_address());
				
				tagGroup.getStartTsRecord().remove(timeMacPair);
				timeMacPair.setTime(packet.getTime());
				
				tagGroup.getStartTsRecord().put(timeMacPair, packet.getMac_address());
				
				macGroup.setBeginTime(packet.getTime());
			}
			else if(macGroup.getBeginTime() < packet.getTime())
			{
				timeMacPair.setTime(macGroup.getEndTime());
				timeMacPair.setMacAddr(packet.getMac_address());
				
				tagGroup.getEndTsRecord().remove(timeMacPair);
				timeMacPair.setTime(packet.getTime());
				
				tagGroup.getEndTsRecord().put(timeMacPair, packet.getMac_address());
				
				macGroup.setEndTime(packet.getTime());
			}
		}
		tagGroup.getMacRecord().replace(packet.getMac_address(), macGroup);// replace by key
	}
	
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
				
				/*cantidates = tagGroup.getStartTsRecord().find(time >
				curTimeMacPair.time and time < curTimeMacPair.time + maxSilent);*/
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
					
					/*if (curTimeMacPair.macAddr.prevGap < threshold) and (timeGap < threshold):
						continue*/
					
					// when reach here, the candidate is valid
					// the record method can be either writing to database or to file
					System.out.println("curTimeMacPair.macAddr.next is candidate.macAddr");
					/*candidate.macAddr.prevGap = timeGap*/
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
	}
}
