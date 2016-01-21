package net.joyoungc.server.util;

import java.util.Properties;

public enum Props {

	TARGET_IP("targetIp"), 
	VERTX_PORT("vertxPort");

	private static Properties properties;

	static {
		properties = new Properties();
		try {
			properties.load(Props.class.getClassLoader().getResourceAsStream("prop.properties"));
		} catch (Exception e) {
			throw new RuntimeException("Error when loading prop file", e);
		}
	}

	private String key;

	private Props(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return properties.getProperty(key);
	}

	public Integer getIntValue() {
		return Integer.parseInt(properties.getProperty(key));
	}
}
