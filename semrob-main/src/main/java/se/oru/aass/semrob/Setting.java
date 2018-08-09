package se.oru.aass.semrob;


import java.security.CodeSource;
import java.util.Properties;

import se.oru.mpi.setting.GeneralSetting;

public class Setting extends GeneralSetting{
	
	static {
		CONFIG_FILE_NAME = "semrob.properties";
	}
	
	public Properties getConfiguration(String configFileName) {
		return super.getConfiguration(configFileName);
	}
	
	public static boolean isRunningAsJar(Properties prop) {
		String propIsRunningAsJar = prop.getProperty("server.running.as.jar");
		return isRunningAsJar(propIsRunningAsJar);
	}
	
	public CodeSource getCodeSource() {
		return super.getCodeSource();
	}
}