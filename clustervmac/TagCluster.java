package clustervmac;

import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;

import clustervmac.dataschema.*;
import clustervmac.dbconnection.DBConnector;
import clustervmac.realtimedb.*;


public class TagCluster {
	private Map<PacketTag,TagGroup> tagRecord;
	private List<TagGroup> tagGroupList;
	
	
	public TagCluster(){
		tagRecord = new HashMap<>();
		tagGroupList = new ArrayList<>();// to be returned		
	}
	
	public List<TagGroup> clusterByTag(List<Packet> rawList)
	{
		TagGroup tmpTagGroup;
		//Every time after clustering by tags, renew these two vars
		//in order to fetch locations from database 
		Long minFetchLocTime = Long.MAX_VALUE;
		Long maxFetchLocTime = Long.MIN_VALUE;		
		
		
		//debug use
		int cluNum = 0;
		for(Packet packet:rawList)
		{
			PacketTag tmpTag = packet.getPacketTag();
			
			//renew loc fetch time
			if(packet.getTime() < minFetchLocTime){
				minFetchLocTime = packet.getTime();
			}
			if(packet.getTime() > maxFetchLocTime){
				maxFetchLocTime = packet.getTime();
			}
			
			//renew tag group
			tmpTagGroup = tagRecord.get(tmpTag);
			if(tmpTagGroup == null)
			{
				cluNum += 1;
				tmpTagGroup = new TagGroup(tmpTag);
				tagRecord.put(tmpTag,tmpTagGroup);
				tagGroupList.add(tmpTagGroup);
			}
			updateTagGroup(tmpTagGroup,packet);
		}
		System.out.println("tag cluster num:"+cluNum);
		mergeLoc(minFetchLocTime,maxFetchLocTime);
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
				//System.out.println("here0");
				// update startTsRecord
				timeMacPair = new TimeMacPair(macGroup.getBeginTime(),packet.getMac_address());
				tagGroup.startTsRecord.remove(timeMacPair);
				timeMacPair.setTime(packet.getTime());
				tagGroup.startTsRecord.insert(timeMacPair, packet.getMac_address());
				// update macRecord
				macGroup.setBeginTime(packet.getTime());
				tagGroup.macRecord.update(packet.getMac_address(),macGroup);
			}
			if(packet.getTime() > macGroup.getEndTime())
			{
				//System.out.println("here1");
				// update endTsRecord
				timeMacPair = new TimeMacPair(macGroup.getEndTime(),packet.getMac_address());
				if(tagGroup.endTsRecord.remove(timeMacPair) == null){
					System.out.println("why");
				}
				timeMacPair.setTime(packet.getTime());
				tagGroup.endTsRecord.insert(timeMacPair, packet.getMac_address());
				// update macRecord
				macGroup.setEndTime(packet.getTime());
				tagGroup.macRecord.update(packet.getMac_address(),macGroup);
			}
		}
	}
	
	// get closest Location Entry
	public Map.Entry<Long, Location> getClosestLocEnt(DBMap<Long,Location> ts2Loc,Long ts){
		Map.Entry<Long, Location> ent1 = ts2Loc.ceilingEntry(ts);
		Map.Entry<Long, Location> ent2 = ts2Loc.floorEntry(ts);
		
		if(ent1 == null && ent2 == null)	return null;
		else if(ent1 == null)	return ent2;
		else if(ent2 == null)	return ent1;
		else{
			if(Math.abs(ent1.getKey() - ts) < Math.abs(ent2.getKey() - ts))	return ent1;
			else	return ent2;
		}
	}
	
	public void deleteMacInfo(TagGroup tagGroup, Long mac){
		MacGroup macGroup = tagGroup.macRecord.get(mac);
		Long beginTS = macGroup.getBeginTime();
		Long endTS = macGroup.getEndTime();
		//remove from startTsRecord
		TimeMacPair tmPair = new TimeMacPair(beginTS,mac);
		tagGroup.startTsRecord.remove(tmPair);
		//remove from endTsRecord
		tmPair.setTime(endTS);
		tagGroup.endTsRecord.remove(tmPair);
		//remove from macRecord
		tagGroup.macRecord.remove(mac);
	}
	
	
	// because the raw packet we get from routers doesn't have locations info, we need to
	// fetch them from the server database and merge it into packet
	public void mergeLoc(long startTS, long endTS){
		// a temporary dict to host the data retrieved from database
		// it is actually HashMap<mac,DBMap<ts,location>>
		HashMap<Long,DBMap<Long,Location>> tmpDict= new HashMap<Long,DBMap<Long,Location>>();
		
		Long ts;
		Long mac;
		DBMap<Long,Location> dbMacRec;
		
		// connect to db and fetch data
		DBConnector conn = new DBConnector("PostgreSQL");
		conn.connect("egz209.ust.hk:7023/rsa_dev", "root", "v1mZo48q2VdjqSn");
<<<<<<< HEAD
		String sql = "SELECT * FROM location_results WHERE areaid = 'hkust_1002'"
=======
		String sql = "SELECT * FROM mtrec_test WHERE areaid = 'mtrec_1'"
>>>>>>> 4e843a0b423d766a35d4fd8bc7a176b35a917c96
				+ " AND ts >= " + startTS*1000 + " AND ts <= " + endTS*1000
				+ " AND (did::BIT(48) & x'020000000000'::BIT(48)) != 0::BIT(48)";
		ResultSet raw = conn.execute(sql);
		try {
			while (raw.next()){
				mac = raw.getLong("did");
				ts = raw.getLong("ts") / 1000;			//the ts in database is in usec
				Location loc = new Location(raw.getDouble("x"),raw.getDouble("y"),ts);
				dbMacRec = tmpDict.get(mac);
				if(dbMacRec == null){
					dbMacRec = new DBTreeMap<Long,Location>();
					tmpDict.put(mac, dbMacRec);
				}
				dbMacRec.insert(ts, loc);
			}
		} catch (SQLException e ) {
			e.printStackTrace();
		}
		
		// merge the location info into the Tag Group
		List<Map.Entry<Long, TagGroup>> delPairList = new ArrayList<Map.Entry<Long, TagGroup>>();
		for(TagGroup curTagGroup:tagGroupList){
			Set<Map.Entry<Long,MacGroup>> macMap = curTagGroup.macRecord.allMaps();
			for(Map.Entry<Long,MacGroup> ent:macMap){
				mac = ent.getKey();
				MacGroup tagMacGroup = ent.getValue();
				dbMacRec = tmpDict.get(mac);
				if(dbMacRec == null){
					Map.Entry<Long, TagGroup> tmpEnt = new AbstractMap.SimpleEntry<Long, TagGroup>(mac, curTagGroup);
					delPairList.add(tmpEnt);
				}
				else{
					Map.Entry<Long, Location> beginLocEnt = getClosestLocEnt(dbMacRec,tagMacGroup.getBeginTime());
					Map.Entry<Long, Location> endLocEnt = getClosestLocEnt(dbMacRec,tagMacGroup.getEndTime());
					if(beginLocEnt != null && endLocEnt != null){
						tagMacGroup.setBeginLoc(beginLocEnt.getValue());
						tagMacGroup.setEndLoc(endLocEnt.getValue());
					}
					else{
						Map.Entry<Long, TagGroup> tmpEnt = new AbstractMap.SimpleEntry<Long, TagGroup>(mac, curTagGroup);
						delPairList.add(tmpEnt);
					}
				}
			}
		}
		for(Map.Entry<Long, TagGroup> pair:delPairList){
			deleteMacInfo(pair.getValue(),pair.getKey());
		}
		conn.close();
	}
	
}