package net.joyoungc.server;

public class StartServerApplication {

	public static void main(String[] args) throws Exception {

		if ("echo_server".equalsIgnoreCase(args[0])) {
			new net.joyoungc.server.netty.echo.EchoServer().run();
		} else if ("chat_server".equalsIgnoreCase(args[0])) {
			new net.joyoungc.server.netty.chat.ChatServer().run();
		} else if ("chat_client".equalsIgnoreCase(args[0])) {
			new net.joyoungc.server.netty.chat.ChatClient().run();
		} else if ("vertx_echo_server".equalsIgnoreCase(args[0])) {
			net.joyoungc.server.vertx.echo.EchoServer.runExample(net.joyoungc.server.vertx.echo.EchoServer.class);
		}

	}

}
