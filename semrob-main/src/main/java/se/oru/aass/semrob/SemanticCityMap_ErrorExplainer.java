package se.oru.aass.semrob;

import se.oru.aass.semrob.errorExplaining.Explainer;

public class SemanticCityMap_ErrorExplainer {
	
	public static void main(String[] args) {
		//MisclassificationExtractor.extract();
		
		Setting setting = new Setting();
		Explainer explainer = new Explainer(Setting.isRunningAsJar(setting.getConfiguration(Setting.CONFIG_FILE_NAME)),
	    		setting.getCodeSource());
		explainer.explain();
	}
}
