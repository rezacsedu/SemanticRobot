package se.oru.aass.semrob.client;


import java.util.Properties;

import se.oru.mpi.setting.GeneralSetting;

public class Setting extends GeneralSetting{
	
	static {
		CONFIG_FILE_NAME = "semrob-client.properties";
	}
	
	public Properties getConfiguration(String configFileName) {
		return super.getConfiguration(configFileName);
	}
}