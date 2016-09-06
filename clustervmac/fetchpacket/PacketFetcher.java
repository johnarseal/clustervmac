package clustervmac.fetchpacket;
//import java.sql.ResultSet;
//import java.util.ArrayList;
import java.util.List;
import clustervmac.dataschema.CluPacket;

public interface PacketFetcher {

	public List<CluPacket> fetchPacket(String[] argv);
	public List<CluPacket> filterPacket(List<CluPacket> pktList);
}
