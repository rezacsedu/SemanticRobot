package se.oru.aass.semrob.client.infoRequester;

import java.util.Vector;

import se.oru.aass.semrob.client.Setting;

public class InfoRequester {
	private static String Server_IP;
	private static int Server_PORT;
	private static String Server_CLASS_NICKNAME;
	public static final String XML_RESPONSE_HEADING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	
	public static final String SPLITTER_SERVER_RESPONSE_INSTANCE = "#";
	public static final String SPLITTER_SERVER_RESPONSE_INSTANCE_INFO = "&";
	
	
	
	static {
		Setting setting = new Setting();
		Server_IP = setting.getConfiguration(Setting.CONFIG_FILE_NAME).getProperty("server.IP");
		Server_PORT = Integer.parseInt(setting.getConfiguration(Setting.CONFIG_FILE_NAME).getProperty("server.port")); 
		Server_CLASS_NICKNAME = setting.getConfiguration(Setting.CONFIG_FILE_NAME).getProperty("server.class.nickname");
	}
	
	
	public void printInfo(String response) {
	}
	
	public static String requestInfo(String methodName, Vector<Object> params) {
		return ServerConnector.getServerResponse(Server_IP,
   			 		Server_PORT, 
   					Server_CLASS_NICKNAME,
   					methodName,
   					params);
	}
	
}
	
