package se.oru.aass.semrob;

import se.oru.aass.semrob.errorExplaining.Corrector;

public class SemanticCityMap_ErrorCorrector {
	public static void main(String[] args) {
		//MisclassificationExtractor.extract();

		Setting setting = new Setting();
		Corrector corrector = new Corrector(Setting.isRunningAsJar(setting.getConfiguration(Setting.CONFIG_FILE_NAME)),
	    		setting.getCodeSource());
		corrector.correct();
	}
}
