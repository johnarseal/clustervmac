package hk.ust.mtrec.apbl.ctrlsrv.probe.bean;

import hk.ust.mtrec.apbl.ctrlsrv.probe.protocol.Packet;
import hk.ust.mtrec.apbl.ctrlsrv.probe.protocol.Type;
import hk.ust.mtrec.apbl.ctrlsrv.probe.protocol.Version;
import io.netty.channel.socket.SocketChannel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Probe object
 * @author tanjiajie
 *
 */
public class Probe {

	private static final int FILE_READ_BUFFER_LEN = 1024;
	private static final String DEFAULT_ALIAS = "untitled";
	
	private String id;
	private String alias;
	private SocketChannel channel;
	private ProbeStatus status;

	Probe(String id) {
		this(id, DEFAULT_ALIAS, null);
	}
	
	Probe(String id, String alias) {
		this(id, alias, null);
	}

	Probe(String id, String alias, SocketChannel channel) {
		this.id = id;
		this.alias = alias;
		this.channel = channel;
		this.status = new ProbeStatus(this);
	}

	public String getId() {
		return id;
	}
	
	public String getAlias() {
		return alias;
	}

	public SocketChannel getChannel() {
		return channel;
	}

	public synchronized void setChannel(SocketChannel channel) {
		this.channel = channel;
	}

	public ProbeStatus getStatus() {
		return status;
	}

	public String getHostname() {
		return channel.remoteAddress().getHostName();
	}

	public String getIp() {
		return channel.remoteAddress().getAddress().getHostAddress();
	}

	public int getPort() {
		return channel.remoteAddress().getPort();
	}
	
	public void updateRecentRegTime() {
		status.setRecentRegTime(System.currentTimeMillis());
	}
	
	public void updateRecentAliveTime() {
		status.setRecentAliveTime(System.currentTimeMillis());
	}
	
	public void updateRecentDataArrivedTime() {
		status.setRecentDataArrivedTime(System.currentTimeMillis());
	}

	/**
	 * Send file to probe. This method is only for testing. 
	 * @param file
	 * @param rcvPath
	 * @param action
	 * @return
	 */
	public boolean sendFile(File file, String rcvPath, byte action) {
		BufferedInputStream bis = null;
		try {
			try {
		        bis = new BufferedInputStream(new FileInputStream(file));
				boolean isFirst = true;
		        while (true) {
			        byte[] bytes = new byte[FILE_READ_BUFFER_LEN];
					int readNum = bis.read(bytes, 0, FILE_READ_BUFFER_LEN);
					if (readNum == -1)
						break;
					Packet packet = new Packet();
					packet.setVersion(Version.VERSION_2);
					packet.setType(Type.FILE);
					ByteBuffer byteBuf = ByteBuffer.allocate(168 + readNum);
					byteBuf.order(ByteOrder.BIG_ENDIAN);
					if (isFirst)
						byteBuf.put((byte) 0x01);
					else
						byteBuf.put((byte) 0x02);
					byteBuf.position(4);
					byteBuf.putInt(readNum);
					byteBuf.position(8);
					byteBuf.put(file.getName().getBytes(), 0, 8);
					byteBuf.position(40);
					byteBuf.put(rcvPath.getBytes(), 0, 16);
					byteBuf.position(168);
					byteBuf.put(bytes, 0, readNum);
					packet.setPayload(byteBuf.array());
					channel.writeAndFlush(packet);
					isFirst = false;
		        }
		        return true;
			} finally {
				if (bis != null)
					bis.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	} 

}
