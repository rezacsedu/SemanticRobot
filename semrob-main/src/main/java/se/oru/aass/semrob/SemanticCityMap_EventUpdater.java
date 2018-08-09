package se.oru.aass.semrob;

import se.oru.aass.semrob.matlabInterface.singleTask.EventExtractor;

public class SemanticCityMap_EventUpdater {
	public static void main (String[] args) {
		Setting setting = new Setting();
		
		EventExtractor.extractEvents(Setting.isRunningAsJar(setting.getConfiguration(Setting.CONFIG_FILE_NAME)), 
				setting.getCodeSource());
		
	    System.out.println(":D");

	}
}
