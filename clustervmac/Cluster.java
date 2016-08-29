package clustervmac;

import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;

import clustervmac.dataschema.*;
import clustervmac.realtimedb.*;
import clustervmac.fetchpacket.DBConnector;


public class Cluster {
	private Map<PacketTag,TagGroup> tagRecord;
	private double maxSpeed;
	private double maxExistTime;
	private double threshold;
	private List<TagGroup> tagGroupList;
	
	
	public Cluster(){
		tagRecord = new HashMap<PacketTag,TagGroup>();
		tagGroupList = new ArrayList<TagGroup>();// to be returned		
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
		System.out.println(cluNum);
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
		String sql = "SELECT * FROM mtrec_test WHERE areaid = 'mtrec_1'"
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
		for(TagGroup curTagGroup:tagGroupList){
			Set<Map.Entry<Long,MacGroup>> macMap = curTagGroup.macRecord.allMaps();
			for(Map.Entry<Long,MacGroup> ent:macMap){
				mac = ent.getKey();
				MacGroup tagMacGroup = ent.getValue();
				dbMacRec = tmpDict.get(mac);
				if(dbMacRec == null){
					// TODO delete record from taggroup
				}
				else{
					Map.Entry<Long, Location> beginLocEnt = getClosestLocEnt(dbMacRec,tagMacGroup.getBeginTime());
					Map.Entry<Long, Location> endLocEnt = getClosestLocEnt(dbMacRec,tagMacGroup.getEndTime());
					if(beginLocEnt != null && endLocEnt != null){
						tagMacGroup.setBeginLoc(beginLocEnt.getValue());
						tagMacGroup.setEndLoc(endLocEnt.getValue());
					}
					else{
						// TODO delete record from taggroup
					}
				}
			}
		}
		conn.close();
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