package clustervmac;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import clustervmac.dataschema.*;

public class EliminateCluster {
	
	private double maxSpeed;			//unit: cm/s
	private Long maxExistTime;
	
	// the largest gap between a MAC address and it's next
	private Long maxSilentTime;
	// the smallest gap between a MAC address and it's next
	private Long minGapTime;
	public EliminateCluster(){
		maxSilentTime = 100L;
		minGapTime = 1L;
		maxExistTime = 500L;		//in the unit of second
		// max speed is 5m/s
		maxSpeed = 500;
		
	}
	
	
	public void eliminate(List<TagGroup> tagGroupList){
		Collection<Map.Entry<TimeMacPair, Long>> candidates;
		// used as boundary for finding submap
		TimeMacPair CeilBondPair;
		TimeMacPair floorBondPair;
		TimeMacPair curPair = null;
		TimeMacPair delPair = null;
		long curTS = System.currentTimeMillis() / 1000L;
		for(TagGroup tagGroup:tagGroupList){
			
			if(tagGroup.endTsRecord.firstEnt() == null){
				continue;
			}

			while(tagGroup.endTsRecord.firstEnt() != null){
				//current mac/pair that is seeking next
				curPair = tagGroup.endTsRecord.firstEnt().getKey();
				if((curTS - curPair.getTime()) < maxExistTime){
					// if the smallest MAC pair in endTSRecord is not far before enough
					// we are breaking from the whole taggroup
					break;
				}
				MacGroup curMacGroup = tagGroup.macRecord.get(curPair.getMacAddr());
				if(curMacGroup == null){
					// this should not be reached
					System.out.println("TagGroup not coherenced0");
					break;
				}
				
				// the boundary for finding submap
				floorBondPair = tagGroup.endTsRecord.firstEnt().getKey();
				floorBondPair.setTime(floorBondPair.getTime() + minGapTime);
				CeilBondPair = new TimeMacPair(floorBondPair);
				CeilBondPair.setTime(floorBondPair.getTime() - minGapTime + maxSilentTime);
				
				
				//the candidates of the next MAC of the cur MAC
				candidates = tagGroup.startTsRecord.subMap(floorBondPair, CeilBondPair);
				boolean find = false;
				for(Map.Entry<TimeMacPair, Long> candid:candidates){
					Long candMac = candid.getKey().getMacAddr();
					MacGroup candMacGroup = tagGroup.macRecord.get(candMac);
					if(candMacGroup == null){
						// this should not be reached
						System.out.println("TagGroup not coherenced1");
					}
					else{
						double speed = candMacGroup.getBeginLoc().speed(curMacGroup.getEndLoc());
						if(speed > maxSpeed){
							//DEBUG
							System.out.println("a candidate is eliminated because speed:" + speed + " is too big");
							continue;
						}
						//if reached here, the candidate is not eliminated, so find
						System.out.println("the next of MAC " + Long.toHexString(curPair.getMacAddr()) + " is " + Long.toHexString(candMac));
						find = true;
						delPair = candid.getKey();
						break;
					}
				}
				if(find){
					//delete the next from starTS record
					tagGroup.startTsRecord.remove(delPair);
				}
				else{
					System.out.println("the next of MAC " + Long.toHexString(curPair.getMacAddr()) + " is NULL");
				}
				//delete the current pair because it has found its next
				tagGroup.macRecord.remove(curPair.getMacAddr());
				tagGroup.startTsRecord.remove(curPair);
				tagGroup.endTsRecord.remove(curPair);
			}
		}
	}
}
