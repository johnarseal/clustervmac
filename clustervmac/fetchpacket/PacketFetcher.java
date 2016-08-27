package clustervmac.fetchpacket;
//import java.sql.ResultSet;
//import java.util.ArrayList;
import java.util.List;
import clustervmac.dataschema.Packet;

public interface PacketFetcher {

	public List<Packet> fetchPacket(String[] argv);
}
