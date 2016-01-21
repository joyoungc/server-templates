package net.joyoungc.server.vertx.echo;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.streams.Pump;
import net.joyoungc.server.util.Props;

public class EchoServer extends AbstractVerticle {

	private static final String CORE_EXAMPLES_DIR = "";
	private static final String CORE_EXAMPLES_JAVA_DIR = CORE_EXAMPLES_DIR + "/src/main/java/";
	
	public static void main(String[] args) {
		runExample(EchoServer.class);
	}

	@Override
	public void start() throws Exception {

		vertx.createNetServer().connectHandler(sock -> {
			Pump.pump(sock, sock).start();
			// Create a pump
//			sock.handler(buffer -> {
//				System.out.println("I received some bytes: " + buffer.toString());
//			});

		}).listen(Props.VERTX_PORT.getIntValue());

		System.out.println("Echo server is now listening");

	}

	public static void runExample(Class clazz) {
		runExample(CORE_EXAMPLES_JAVA_DIR, clazz, new VertxOptions().setClustered(false), null);
	}

	public static void runExample(String exampleDir, Class clazz, VertxOptions options,
			DeploymentOptions deploymentOptions) {
		runExample(exampleDir + clazz.getPackage().getName().replace(".", "/"), clazz.getName(), options,
				deploymentOptions);
	}

	public static void runExample(String exampleDir, String verticleID, VertxOptions options,
			DeploymentOptions deploymentOptions) {
		if (options == null) {
			// Default parameter
			options = new VertxOptions();
		}
		// Smart cwd detection

		// Based on the current directory (.) and the desired directory
		// (exampleDir), we try to compute the vertx.cwd
		// directory:
		try {
			// We need to use the canonical file. Without the file name is .
			File current = new File(".").getCanonicalFile();
			if (exampleDir.startsWith(current.getName()) && !exampleDir.equals(current.getName())) {
				exampleDir = exampleDir.substring(current.getName().length() + 1);
			}
		} catch (IOException e) {
			// Ignore it.
		}

		System.setProperty("vertx.cwd", exampleDir);
		Consumer<Vertx> runner = vertx -> {
			try {
				if (deploymentOptions != null) {
					vertx.deployVerticle(verticleID, deploymentOptions);
				} else {
					vertx.deployVerticle(verticleID);
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		};
		if (options.isClustered()) {
			Vertx.clusteredVertx(options, res -> {
				if (res.succeeded()) {
					Vertx vertx = res.result();
					runner.accept(vertx);
				} else {
					res.cause().printStackTrace();
				}
			});
		} else {
			Vertx vertx = Vertx.vertx(options);
			runner.accept(vertx);
		}
	}

}
