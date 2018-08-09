package se.oru.aass.semrob;

import java.util.Properties;

import se.oru.aass.semrob.matlabInterface.singleTask.MapConstructor;


public class SemanticCityMap_MapConstructor {
	public static void main (String[] args) {
		Setting setting = new Setting();
		Properties prop = setting.getConfiguration(Setting.CONFIG_FILE_NAME);			
			
		
		// if we are going to extract regions with their hole info from the classification output the following variable should set to true:
		boolean merging = false;
		
		// if the hole of regions are calculated in the mat file, the following variable should set to true
		boolean withHoleInfo = false;
		
		// Given the .mat file, this method is called only once
	    MapConstructor.construct(Integer.parseInt(prop.getProperty("map.size.width")),
	    		Integer.parseInt(prop.getProperty("map.size.heigth")),
	    		Setting.isRunningAsJar(setting.getConfiguration(Setting.CONFIG_FILE_NAME)),
	    		setting.getCodeSource(), merging, withHoleInfo);
	}
}
