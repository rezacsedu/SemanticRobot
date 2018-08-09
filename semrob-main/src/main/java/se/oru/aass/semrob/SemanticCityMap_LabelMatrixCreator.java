package se.oru.aass.semrob;

import java.util.Properties;

import se.oru.aass.semrob.matlabInterface.singleTask.LabelMatrixCreator;

public class SemanticCityMap_LabelMatrixCreator {

	public static void main (String[] args) {
		Setting setting = new Setting();
		Properties prop = setting.getConfiguration(Setting.CONFIG_FILE_NAME);			
			
		
		
		// Given the .mat file, this method is called only once
		LabelMatrixCreator.createLabelMatrix(Integer.parseInt(prop.getProperty("map.size.width")),
	    		Integer.parseInt(prop.getProperty("map.size.heigth")),
	    		Setting.isRunningAsJar(setting.getConfiguration(Setting.CONFIG_FILE_NAME)),
	    		setting.getCodeSource());
		// Given the .mat file, this method is called only once

	
	}
}
