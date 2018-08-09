package se.oru.aass.semrob.setting;

import java.util.Properties;

import se.oru.mpi.setting.GeneralSetting;


public class Setting extends GeneralSetting{

	
	static {
		CONFIG_FILE_NAME = "setting.properties";
	}
	
	//public static int SEGMENT_PIXEL_SIZE = 2000;
	public static int SEGMENT_PIXEL_SIZE = 500;
	//public static int SEGMENT_PIXEL_SIZE = 5;
	public static int REGION_DISTANCE_INTERVAL_SIZE = 100;
	
	public Properties getConfiguration(String configFileName) {
		return super.getConfiguration(configFileName);
	}
	
	
	public static int getNumberOfDistanceInterval() {
		Properties prop = new Setting().getConfiguration(CONFIG_FILE_NAME);
		int mapWidth =  Integer.parseInt(prop.getProperty("map.size.width"));
		int mapHeight =  Integer.parseInt(prop.getProperty("map.size.heigth"));
		int maxSize = mapWidth;
		if (mapHeight > maxSize)
			maxSize = mapHeight;
				
		int numberOfDistanceInterval = (int) Math.ceil(maxSize / REGION_DISTANCE_INTERVAL_SIZE);
		return numberOfDistanceInterval;
	}
}
