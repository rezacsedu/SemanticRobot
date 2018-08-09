package se.oru.aass.semrob;

import se.oru.aass.semrob.server.Demo;
import se.oru.aass.semrob.server.Server_3;
import se.oru.aass.semrob.Setting;


public class SemanticCityMap_Demo3{
	public static void main(String[] args) throws Exception   {
		Setting setting = new Setting();
		

		
		Server_3 server = new Server_3();
		new Demo().runDemo(Setting.isRunningAsJar(setting.getConfiguration(Setting.CONFIG_FILE_NAME)), 
				setting.getCodeSource(), 
				server,
				Integer.parseInt(setting.getConfiguration(Setting.CONFIG_FILE_NAME).getProperty("map.size.width")),
				Integer.parseInt(setting.getConfiguration(Setting.CONFIG_FILE_NAME).getProperty("map.size.heigth")),
				Integer.parseInt(setting.getConfiguration(Setting.CONFIG_FILE_NAME).getProperty("map.size.elevation")));

		
		
//		Setting setting = new Setting();
//		Properties prop = setting.getConfiguration(Setting.CONFIG_FILE_NAME);			
//		Server_3 server = new Server_3();
//		server.initialize(Setting.isRunningAsJar(setting.getConfiguration(Setting.CONFIG_FILE_NAME)), 
//				setting.getCodeSource(),
//				Integer.parseInt(prop.getProperty("map.size.width")),
//				Integer.parseInt(prop.getProperty("map.size.heigth")),
//				Integer.parseInt(prop.getProperty("map.size.elevation")));
//		
//
//		Coordinate source = new Coordinate(1, 1, 100);
//		Coordinate destination = new Coordinate(100, 15000, 100);
//		server.getPathInfo((int) source.x, (int) source.y, (int) source.z, (int) destination.x, (int) destination.y, (int) destination.z);
		
	}
}
