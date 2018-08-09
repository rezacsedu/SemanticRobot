package se.oru.aass.semrob.server;


import java.util.Properties;

import se.oru.mpi.setting.GeneralSetting;

public class Setting extends GeneralSetting{
	
	static {
		CONFIG_FILE_NAME = "server.properties";
	}
	
	
	
	public Properties getConfiguration(String configFileName) {
		return super.getConfiguration(configFileName);
	}
	
	public double convertSquareMeterToPixel(double squareMeterValue) {
		//Properties prop = getConfiguration(Setting.CONFIG_FILE_NAME);
		Properties prop = getConfiguration("server.properties");
		return squareMeterValue * Integer.parseInt(prop.getProperty("map.pixel.per.meter")) * Integer.parseInt(prop.getProperty("map.pixel.per.meter"));
	}
	
	public int convertMeterToPixel(int meterValue) {
		//Properties prop = getConfiguration(Setting.CONFIG_FILE_NAME);
		Properties prop = getConfiguration("server.properties");
		return (meterValue * Integer.parseInt(prop.getProperty("map.pixel.per.meter")));
	}
}