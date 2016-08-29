package clustervmac;
import java.util.List;

import clustervmac.dataschema.*;

public class EliminateCluster {
	
	private double maxSpeed;
	private double maxExistTime;
	private double threshold;
	
	public void eliminate(List<TagGroup> tagGroupList){		
		/*
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
			}*/		
	}
}
