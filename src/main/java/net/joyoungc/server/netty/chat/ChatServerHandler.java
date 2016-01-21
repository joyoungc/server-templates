package net.joyoungc.server.netty.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

@Sharable
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

	static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
/*
	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
		
		System.out.println("Client: "+ ctx.channel().remoteAddress() + " connected....");
		
		// Welcome 메세지 전송하기.
		final ByteBuf welcomMsg = Unpooled.copiedBuffer(Messages.WELCOME.toString().getBytes());
        final ChannelFuture f = ctx.writeAndFlush(welcomMsg);
        
		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (future.isSuccess()) {
					System.out.println("channels.add(ctx.channel())");
					channels.add(ctx.channel());
				}
			}
		});
		
	}
*/
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		// Send the received message to all channels but the current one.
		System.out.println(" Receive Message : " + msg);
		for (Channel c : channels) {
			if (c != ctx.channel()) {
				c.writeAndFlush("[" + ctx.channel().remoteAddress() + "] " + msg + '\n');
			} else {
				// Close the connection if the client has sent 'bye'.
				if (msg.trim().equals("bye")) {
					ctx.close();
				} else {
					c.writeAndFlush("[echo] " + msg + '\n');
				}
			}
		}

	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		for (Channel channel : channels) {
			channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " has joined!\n");
		}
		channels.add(ctx.channel());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		for (Channel channel : channels) {
			channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " has left!\n");
		}
		channels.remove(ctx.channel());
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.out.println("======= exceptionCaught ======= [" + ctx.channel().remoteAddress() + "] ");
		cause.printStackTrace();
		ctx.close();
	}

}
