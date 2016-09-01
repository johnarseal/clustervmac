package probeserver.pipeline.handler;


import hk.ust.mtrec.apbl.ctrlsrv.msg.buffer.MessageQueue;
import hk.ust.mtrec.apbl.ctrlsrv.msg.msg.DataMessage;
import hk.ust.mtrec.apbl.ctrlsrv.msg.msg.helper.MessageHelper;

import probeserver.ProbeServer;
import probeserver.bean.Probe;
import probeserver.protocol.*;

import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataPktHandler implements PktHandler {
	
	private ProbeServer probeSrv;
	private MessageQueue msgQueue;

	private final Logger logger = LoggerFactory.getLogger(DataPktHandler.class);

	
	public DataPktHandler(ProbeServer probeSrv, MessageQueue msgQueue) {
		this.probeSrv = probeSrv;
		this.msgQueue = msgQueue;
	}

	@Override
	public void handle(Packet pkt, SocketChannel ch) {
		DataMessage dataMsg = MessageHelper.wrapDataMessage(pkt.getPayload());
		Probe probe = probeSrv.getProbeByChannel(ch);
		if (probe != null) {
			probe.updateRecentDataArrivedTime();
			/*if (probe.getDataMacAddr() == null) {
				// We assume that only one probe connects to a probe-controller.
				List<RssData> l = dataMsg.getData();
				if (l != null && l.size() > 0)
					probe.updateDataMacAddr(l.get(0).getRxAddrStr());
			}*/
		}
		logger.trace("Handling DataMessage {}.", dataMsg);
		try {
			this.msgQueue.put(dataMsg);
		} catch (InterruptedException e) {
			logger.info("Thread is interrupted.");
		}
	}

}
