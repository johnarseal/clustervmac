package clustervmac;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import clustervmac.dataschema.*;
import clustervmac.storer.ClusterStorer;

public class EliminateCluster {
	
	private final double maxSpeed;			//unit: cm/s
	private final Long maxExistTime;
	
	// the largest gap between a MAC address and it's next
	private final Long maxSilentTime;
	// the smallest gap between a MAC address and it's next
	private final Long minGapTime;
	// the time gap of a flip
	private final Long flipGapTime;
	
	public EliminateCluster(){
		maxSilentTime = 200L;
		minGapTime = 1L;
		maxExistTime = 500L;		//in the unit of second
		// max speed is 5m/s
		maxSpeed = 500;
		flipGapTime = 2L;
	}
	
	
	public void eliminate(List<TagGroup> tagGroupList){
		Collection<Map.Entry<TimeMacPair, Long>> candidates;
		// used as boundary for finding submap
		TimeMacPair CeilBondPair;
		TimeMacPair floorBondPair;
		TimeMacPair curPair = null;
		TimeMacPair delPair = null;
		long curTS = System.currentTimeMillis() / 1000L;
		// used to store cluster result
		ClusterStorer cluStorer = new ClusterStorer("egz209.ust.hk:7023/rsa_dev", "root", "v1mZo48q2VdjqSn");
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
						// the speed condition
						if(speed > maxSpeed){
							//DEBUG
							System.out.println("a candidate is eliminated because speed:" + speed + " is too big");
							continue;
						}
						// the fast flip condition (MAC address changes 3 times in 2 * flipGapTime seconds)
						if((curMacGroup.getPrevGap() != null) && (curMacGroup.getPrevGap() < flipGapTime) && (candMacGroup.getBeginTime() - curMacGroup.getEndTime() < flipGapTime)){
							//DEBUG
							System.out.println("a candidate is eliminated because MAC flip too fast");
							continue;							
						}
						//if reached here, the candidate is not eliminated, so find
						//System.out.println("the next of MAC " + Long.toHexString(curPair.getMacAddr()) + " is " + Long.toHexString(candMac));
						find = true;
						delPair = candid.getKey();
						break;
					}
				}
				if(find){
					//update next's leadMac
					MacGroup delMacG = tagGroup.macRecord.get(delPair.getMacAddr());
					if(curMacGroup.getLeadMac() == null){
						delMacG.setLeadMac(curPair.getMacAddr());
					}
					else{
						delMacG.setLeadMac(curMacGroup.getLeadMac());
					}
					//update next's prevGap
					delMacG.setPrevGap(delMacG.getBeginTime() - curMacGroup.getEndTime());
					//delete the next from starTS record
					tagGroup.startTsRecord.remove(delPair);
				}
				//debugging
				//cluStorer.storeCluster(curMacGroup);
				
				//delete the current pair because it has found its next
				tagGroup.macRecord.remove(curPair.getMacAddr());
				tagGroup.startTsRecord.remove(curPair);
				tagGroup.endTsRecord.remove(curPair);
			}
		}
	}
}
