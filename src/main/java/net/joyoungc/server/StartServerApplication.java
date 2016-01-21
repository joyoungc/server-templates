package net.joyoungc.server;

import net.joyoungc.server.netty.chat.ChatClient;
import net.joyoungc.server.netty.chat.ChatServer;
import net.joyoungc.server.netty.echo.EchoServer;

public class StartServerApplication {

	public static void main(String[] args) throws Exception {
		
		if ("echo_server".equalsIgnoreCase(args[0])) {
			new EchoServer().run();
		} else if ("chat_server".equalsIgnoreCase(args[0])) {
			new ChatServer().run();
		} else if ("chat_client".equalsIgnoreCase(args[0])) {
			new ChatClient().run();
		}
		
	}
	
}
