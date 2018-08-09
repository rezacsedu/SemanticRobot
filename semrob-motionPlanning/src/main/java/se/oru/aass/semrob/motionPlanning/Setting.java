package se.oru.aass.semrob.motionPlanning;


import java.util.Properties;

import se.oru.mpi.setting.GeneralSetting;

public class Setting extends GeneralSetting{
	
	static {
		CONFIG_FILE_NAME = "motionPlanning.properties";
	}
	
	public Properties getConfiguration(String configFileName) {
		return super.getConfiguration(configFileName);
	}
}