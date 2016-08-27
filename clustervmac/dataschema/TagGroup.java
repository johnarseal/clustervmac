package clustervmac.dataschema;

import clustervmac.realtimedb.*;
import java.util.Set;
import java.util.Map;
public class TagGroup {
	private PacketTag packetTag;
	//
	// For every tag group,
	// we use the structure 'treeMap' as a dynamic database(key=>value dictionary)
	// to implement CURD(create,update,read,delete)
	// It can be a realtime database, or an AVL tree or red-black tree in Java and C++
	//
	public DBMap<Long,MacGroup> macRecord;		// mac record table
	public DBMap<TimeMacPair,Long> startTsRecord;  // start Timestamp table
	public DBMap<TimeMacPair,Long> endTsRecord;	// end Timestamp table	
	
	public TagGroup(PacketTag tag){
		packetTag = tag;
	}

	public PacketTag getPacketTag() {
		return packetTag;
	}

	public void setPacketTag(PacketTag packetTag) {
		this.packetTag = packetTag;
	}
	
	public void printLog(){
		Set<Map.Entry<Long,MacGroup>> s = macRecord.allMaps();
		for(Map.Entry<Long,MacGroup> ent:s){
			System.out.println("MAC:"+ent.getKey());
			System.out.print("begin:"+ent.getValue().getBeginTime());
			System.out.print(",end:"+ent.getValue().getEndTime());
			System.out.print("\n");
		}
	}
	
}
