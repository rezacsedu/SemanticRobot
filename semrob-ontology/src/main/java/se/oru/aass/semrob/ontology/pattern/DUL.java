package se.oru.aass.semrob.ontology.pattern;

import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

public enum DUL {
	associatedWith(),
	hasTimeInterval();
	private static PrefixManager prefixManager;
	private static String patternURI;
	
	public static void setPrefixManager (String uri) {
		patternURI = uri;
		prefixManager = new DefaultPrefixManager(patternURI + "#");
	}
	
	public static String getURI() {
		return patternURI;
	}

	public String getEntityURI() {
    	return prefixManager.getDefaultPrefix() + this.toString();
    }
}
