package se.oru.aass.semrob;

import se.oru.aass.semrob.enrichedLabelRetrieving.EnrichedLabelExtractor;

public class SemanticCityMap_EnrichedLabelExtractor {
	public static void main (String[] args) {
		Setting setting = new Setting();
	
		// instead of running this method, we decided to handle the enrichment process of the labels in matlab.
		EnrichedLabelExtractor.extract(Setting.isRunningAsJar(setting.getConfiguration(Setting.CONFIG_FILE_NAME)),
	    		setting.getCodeSource());

	}
}
