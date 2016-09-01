package probeserver.pipeline.handler;

import hk.ust.mtrec.apbl.common.utils.ByteUtils;
import hk.ust.mtrec.apbl.common.utils.ConvertUtils;
import hk.ust.mtrec.apbl.ctrlsrv.msg.buffer.MessageQueue;
import hk.ust.mtrec.apbl.ctrlsrv.msg.msg.helper.MessageHelper;
import hk.ust.mtrec.apbl.ctrlsrv.msg.msg.sys.in.StatusReportMessage;
import hk.ust.mtrec.apbl.ctrlsrv.probe.ProbeServer;
import hk.ust.mtrec.apbl.ctrlsrv.probe.bean.Probe;
import hk.ust.mtrec.apbl.ctrlsrv.probe.protocol.Packet;

import probeserver.protocol.*;
import probeserver.ProbeServer;
import probeserver.bean.*;

import io.netty.channel.socket.SocketChannel;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemPktHandler implements PktHandler {

	public static final byte SUBTYPE_KEEPALIVE = 0x00;
	public static final byte SUBTYPE_REGISTER = 0x01;
	public static final byte SUBTYPE_REGISTER_ACCEPT = 0x02;
	public static final byte SUBTYPE_REGISTER_REFUSE = 0x03;
	public static final byte SUBTYPE_STATUSREPORT = 0x04;
	
	private ProbeServer probeSrv;
	private MessageQueue msgQueue;
	
	private final Logger logger = LoggerFactory.getLogger(SystemPktHandler.class);
	private final Logger probeStatusLogger = LoggerFactory.getLogger("ProbeStatus");
			
	public SystemPktHandler(ProbeServer probeSrv, MessageQueue msgQueue) {
		this.probeSrv = probeSrv;
		this.msgQueue = msgQueue;
	}
	
	@Override
	public void handle(Packet pkt, SocketChannel ch) {
		byte[] payload = pkt.getPayload();
		
		switch (payload[0]) {
		case SUBTYPE_REGISTER:
			handleRegisterPkt(payload, ch);
			break;
		case SUBTYPE_KEEPALIVE:
			handleKeepalivePkt(payload, ch);
			break;
		case SUBTYPE_STATUSREPORT:
			handleStatusreportPkt(payload, ch);
			break;
		}
	}
	
	void handleRegisterPkt(byte[] payload, SocketChannel ch) {
		byte formatType = payload[1];
		String id, alias;
		switch (formatType) {
		case 0x01:
			long idLong = ByteUtils.bytesToLong(payload, 2, 6);
			id = ConvertUtils.longToMacStr(idLong, "");
			alias = new String(Arrays.copyOfRange(payload, 8, payload.length-1)).trim();
			break;
		default:
		case 0x00:
			id = new String(Arrays.copyOfRange(payload, 2, payload.length-1)).trim();
			alias = id;
		}
		
		/* handle new probe */
/*		Probe probe = probeSrv.getProbeById(id);
		if (probe == null) {
			probe = probeSrv.newProbe(id, alias, ch);
			probeStatusLogger.info("A new Probe {} (id={}, ipaddr={}) registers to the server.", alias, id, ch.remoteAddress());
		} else {
			if (ch != probe.getChannel()) {
				probeSrv.updateChannel(probe.getChannel(), ch);
				probeStatusLogger.info("The channel of Probe {} (id={}, ipaddr={}) is updated.", alias, id, ch.remoteAddress());
			}
		}*/
		
		// handle new probe
		Probe probe = probeSrv.getProbeById(id);
		if (probe == null) {	// handle new probe
			probe = probeSrv.getProbeVerifier().verifyAndNewProbe(id, alias, ch);
			if (probe != null) {
				probeStatusLogger.info("Probe {} (id={}, ipaddr={}) registers to the server for the first time.", alias, id, ch.remoteAddress());
			} else {
				ch.close();
				probeStatusLogger.warn("Probe {} (id={}, ipaddr={}) cannot register to the server. It may be an invalid device.", alias, id, ch.remoteAddress());
				return;
			}
		} else if (ch != probe.getChannel()) {	// handle existing probe
			probeSrv.updateChannel(probe.getChannel(), ch);
			probeStatusLogger.info("Probe {} (id={}, ipaddr={}) reconnects to the server.", alias, id, ch.remoteAddress());
		}
		
		// update information of probe
		probe.updateRecentRegTime();		
	}
	
	void handleKeepalivePkt(byte[] payload, SocketChannel ch) {
		logger.trace("Handling Keepalive message.");
		Probe probe = probeSrv.getProbeByChannel(ch);
		if (probe != null) {
			probe.updateRecentAliveTime();
			probeStatusLogger.trace("Probe {} (id={}, ipaddr={}) is alive.", probe.getAlias(), probe.getId(), ch.remoteAddress());
		}
	}
	
	void handleStatusreportPkt(byte[] payload, SocketChannel ch) {
		StatusReportMessage reportMsg = MessageHelper.wrapStatusReportMessage(payload);
		logger.trace("Received status report: {}", reportMsg);
		try {
			this.msgQueue.put(reportMsg);
		} catch (InterruptedException e) {
			logger.info("Thread is interrupted.");
		}
	}

}
