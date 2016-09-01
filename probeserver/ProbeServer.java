package probeserver;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedList;

import probeserver.protocol.*; 
import probeserver.pipeline.handler.*;
import probeserver.pipeline.*;
import probeserver.bean.*;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Server for probe-controllers.
 * @author johnzz
 *
 */
public class ProbeServer {

	private static final long DEFAULT_PROBE_INVALID_INTEVAL = 1 * 3600 * 1000;	// 1 hr
	
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private Channel channel;
	
	//private ProbeVerifier probeVerifier;
	
	private Map<Type, PktHandler> msgHandlers = new HashMap<>();
	
	/**
	 * This map is used to get a probe quickly by its channel.
	 * Since all channels are long connections, we can find a corresponding Probe by the channel object.
	 */
	private Map<SocketChannel, Probe> probeMap = new ConcurrentHashMap<>();
	
	private Collection<ProbeStatus> probeStatusCollection = new LinkedList<>();
	
	private final Logger logger = LoggerFactory.getLogger(ProbeServer.class);
	
	public ProbeServer(ProbeSrvConf conf, MessageQueue msgQueue) {
		//this.conf = conf;
		this.msgHandlers.put(Type.SYSTEM, new SystemPktHandler(this, msgQueue));
		this.msgHandlers.put(Type.DATA, new DataPktHandler(this, msgQueue));
		//this.probeVerifier = new NativeProbeVerifier(this, conf.getWhitelistPath());
	}
	
	/**
	 * Start the ProbeServer.
	 */
	public void start() {
		if (isStarted()) {
			logger.warn("The instance has already started before.");
			return;
		}
		this.bossGroup = new NioEventLoopGroup();
		this.workerGroup = new NioEventLoopGroup();
		
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel channel)
							throws Exception {
					//	logger.info("Accept new connection from {}:{}.",
					//			channel.remoteAddress().getAddress(), 
					//			channel.remoteAddress().getPort());
						channel.pipeline()
							.addLast("encoder", new Encoder())
							.addLast("decoder", new Decoder())
							.addLast("processor", new PktProcessor(msgHandlers));
					}
				})
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
		try {
			//TODO should use conf here
			ChannelFuture channelFuture = bootstrap.bind(conf.getPort()).sync();
			channel = channelFuture.channel();
		} catch (InterruptedException e) {
			logger.warn("Binding on ProbeSrv is interrupted.");
		}
	}

	/**
	 * Shutdown the ProbeServer.
	 */
	public synchronized void close() {
		try {
			logger.debug("Closing ProbeServer ...");
			if (channel != null)
				channel.close().sync();
			if (workerGroup != null)
				workerGroup.shutdownGracefully().sync();
			if (bossGroup != null)
				bossGroup.shutdownGracefully().sync();
			logger.debug("ProbeServer is closed.");
		} catch (InterruptedException e) {
			logger.warn("Shutting down the ProbeSrv is interrupted.");
		}
	}
	
	/**
	 * Return whether the ProbeServer is running.
	 * @return
	 */
	public synchronized boolean isStarted() {
		return channel != null && channel.isActive();
	}
	
}