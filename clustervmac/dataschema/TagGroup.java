package clustervmac.dataschema;

import java.util.*;

public class TagGroup {
	private List<Packet> packetList;
	//
	// For every tag group,
	// we use the structure 'treeMap' as a dynamic database(key=>value dictionary)
	// to implement CURD(create,update,read,delete)
	// It can be a realtime database, or an AVL tree or red-black tree in Java and C++
	//
	private TreeMap<String,MacGroup> macRecord;		// mac record table
	private TreeMap<TimeMacPair,String> startTsRecord;  // start Timestamp table
	private TreeMap<TimeMacPair,String> endTsRecord;	// end Timestamp table	
	
	public List<Packet> getPacketList() {
		return packetList;
	}
	public void setPacketList(List<Packet> packetList) {
		this.packetList = packetList;
	}
	public TreeMap<String, MacGroup> getMacRecord() {
		return macRecord;
	}
	public void setMacRecord(TreeMap<String, MacGroup> macRecord) {
		this.macRecord = macRecord;
	}
	public TreeMap<TimeMacPair, String> getStartTsRecord() {
		return startTsRecord;
	}
	public void setStartTsRecord(TreeMap<TimeMacPair, String> startTsRecord) {
		this.startTsRecord = startTsRecord;
	}
	public TreeMap<TimeMacPair, String> getEndTsRecord() {
		return endTsRecord;
	}
	public void setEndTsRecord(TreeMap<TimeMacPair, String> endTsRecord) {
		this.endTsRecord = endTsRecord;
	}
	
	
}
