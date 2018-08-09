package se.oru.aass.semrob.ontology.query;

import java.util.Vector;

public final class SPARQLParser {
	
	private SPARQLParser() {
		
	}
	
	public static int getNumericVaue(String expression) {
		try {
		String tokens[] = expression.split("\\\\\"");
		return Integer.parseInt(tokens[1]);
		} catch(Exception e) {
			return -1;
		}
	}
	
	
	public static Vector<Integer> getNumericValues(String expression) {
		Vector<Integer> v = new Vector<Integer>();
		String[] results = expression.split(",");
		for (String s : results) {
			v.addElement(getNumericVaue(s));
		}
		return v;
	}
	
}