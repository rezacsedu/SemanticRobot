package se.oru.mpi.setting;

import java.io.File;
import java.io.IOException;
import java.security.CodeSource;
import java.util.Properties;

public class GeneralSetting {
	
	public static String CONFIG_FILE_NAME;
	public Properties getConfiguration(String configFileName) {
				
		Properties prop = new Properties();
		try {
			
			prop.load(this.getClass().getClassLoader().getResourceAsStream(configFileName));
			
		} catch (IOException e) {
			
			e.printStackTrace();
			prop = null;
			
		}
		return prop;
	}
		
	public static boolean isRunningAsJar(String propIsRunningAsJar) {
		return (propIsRunningAsJar.trim().toLowerCase().equals("false")) ? false : true;
	}
	
	public CodeSource getCodeSource() {
		return this.getClass().getProtectionDomain().getCodeSource();
	}
	
	// it is assumed that the path is referring to a path located in a folder out of the main project's folder
	public static String getFullPath(String path, boolean runningFromJar, CodeSource codeSource) {

		try {
			File mainFile = new File(codeSource.getLocation().toURI().getPath());
			String mainDir = mainFile.getParentFile().getPath();
			
			String fullPath;
			if (!runningFromJar) {
				String[] dirsInPath = mainDir.split("/");
				String projectName = dirsInPath[dirsInPath.length - 1];
				mainDir = mainDir.split(projectName)[0];
				fullPath = mainDir + path;
			}
			else {
				fullPath = mainDir + "/" + path;
				
			}
			
			return fullPath;
			
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
