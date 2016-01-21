package net.joyoungc.server.netty.echo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf in = (ByteBuf) msg;
		String recvMsg = in.toString(CharsetUtil.UTF_8).trim();
		System.out.println("[CLIENT] - " + ctx.channel().remoteAddress() + " channelRead : " + recvMsg);
		ctx.write(msg);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// exception이 발생했을 시 connection을 close한다.
		cause.printStackTrace();
		ctx.close();
	}

	// This method executes when a client connects
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Client: "+ ctx.channel().remoteAddress() + " connected....");
	}

	// connection이 close 됐을 시 실행된다.
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Client: "+ ctx.channel().remoteAddress() + " disconnected....");		
	}

}
