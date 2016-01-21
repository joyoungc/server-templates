package net.joyoungc.server.netty.chat;

import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.CharsetUtil;

public final class ChatServer {

	static final boolean SSL = System.getProperty("ssl") != null;
	static final int PORT = Integer.parseInt(System.getProperty("port", "11207"));

	public void run() throws Exception {
		// SSL 환경설정 (option)
		final SslContext sslCtx;
		
		if (SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
		} else {
			sslCtx = null;
		}

		// server 환경설정
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {

			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 1000)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();

						if (sslCtx != null) {
							pipeline.addLast(sslCtx.newHandler(ch.alloc()));
						}

						pipeline.addLast(new ByteToMessageDecoder() {
							@Override
							protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
								out.add(in.readBytes(in.readableBytes()));
							}
						});
						
						pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
						pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));

						// business logic
						pipeline.addLast(new ChatServerHandler());
					}
					
				});
			
			ChannelFuture f = b.bind(PORT).sync();
			f.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
		
	}

}
