package net.joyoungc.server.netty.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import net.joyoungc.server.util.Props;

public class ChatClient {

	static final String HOST = System.getProperty("host", Props.TARGET_IP.getValue());
	static final int PORT = Integer.parseInt(System.getProperty("port", "11207"));

	public void run() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		
		try {

			Bootstrap b = new Bootstrap();
			b.group(group)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						
						pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
						pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
						pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
						
						pipeline.addLast(new ChatClientHandler());
					}
				});

			// Start the connection attempt.
			Channel ch = b.connect(HOST, PORT).sync().channel();

			// Read commands from the stdin.
			ChannelFuture lastWriteFuture = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			for(;;) {
				
				String line = in.readLine();
				if (line == null) {
					break;
				}
				
				//lastWriteFuture = ch.writeAndFlush(line + "\r\n");
				lastWriteFuture = ch.writeAndFlush(line);
				
				if ("bye".equals(line.toLowerCase())) {
					ch.closeFuture().sync();
					break;
				}
			}
			
			// Wait until all messages are flushed before closing the channel.
			if (lastWriteFuture != null) {
				lastWriteFuture.sync();
			}
			
		} finally {
			// The connection is closed automatically on shutdown.
			group.shutdownGracefully();
		}
		
	}

}
