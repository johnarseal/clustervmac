package clustervmac.dataschema;

import clustervmac.realtimedb.*;

import java.util.Set;
import java.util.Map;
public class TagGroup {
	private CluPacketTag packetTag;
	//
	// For every tag group,
	// we use the structure 'treeMap' as a dynamic database(key=>value dictionary)
	// to implement CURD(create,update,read,delete)
	// It can be a real-time database, or an AVL tree or red-black tree in Java and C++
	//
	public DBMap<Long,MacGroup> macRecord;		// mac record table
	public DBMap<TimeMacPair,Long> startTsRecord;  // start Timestamp table
	public DBMap<TimeMacPair,Long> endTsRecord;	// end Timestamp table	
	
	public TagGroup(CluPacketTag tag){
		packetTag = tag;
		macRecord = new DBTreeMap<Long,MacGroup>();
		startTsRecord = new DBTreeMap<TimeMacPair,Long>();
		endTsRecord = new DBTreeMap<TimeMacPair,Long>();
	}

	public CluPacketTag getPacketTag() {
		return packetTag;
	}

	public void setPacketTag(CluPacketTag packetTag) {
		this.packetTag = packetTag;
	}
	
	public void printLog(){
		Location loc;
		Set<Map.Entry<Long,MacGroup>> s = macRecord.allMaps();
		for(Map.Entry<Long,MacGroup> ent:s){
			System.out.println("MAC:"+Long.toHexString(ent.getKey()));
			MacGroup curMacGroup = ent.getValue();
			System.out.print("begin:"+curMacGroup.getBeginTime());
			System.out.print(",end:"+curMacGroup.getEndTime());
			
			// log location
			loc = curMacGroup.getBeginLoc();
			System.out.print(",beginLoc:");
			if(loc == null){
				System.out.print("None");
			}
			else{
				System.out.print(loc.getX() + "," + loc.getY());
				System.out.print(",beginLocTime:" + loc.getTs());
			}
			
			loc = curMacGroup.getEndLoc();
			System.out.print(",endLoc:");
			if(loc == null){
				System.out.print("None");
			}
			else{
				System.out.print(loc.getX() + "," + loc.getY());
				System.out.print(",endLocTime:" + loc.getTs());
			}
			System.out.print("\n");
		}
	}
	
}
