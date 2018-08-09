package se.oru.aass.semrob;

import se.oru.aass.semrob.server.Demo;
import se.oru.aass.semrob.server.Server_2;
import se.oru.aass.semrob.Setting;

public class SemanticCityMap_Demo2 {
	public static void main(String[] args) throws Exception   {
		Setting setting = new Setting();
		Server_2 server = new Server_2();
		new Demo().runDemo(Setting.isRunningAsJar(setting.getConfiguration(Setting.CONFIG_FILE_NAME)), 
				setting.getCodeSource(), 
				server);
	}
}
