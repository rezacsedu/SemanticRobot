package se.oru.aass.semrob.server;

import se.oru.aass.semrob.server.infoProvider.InfoProvider;

import java.security.CodeSource;

import se.oru.aass.semrob.ontology.query.OntoCitySPARQL;

public class Server {
	protected static OntoCitySPARQL ontoCitySPARQL;
	protected InfoProvider infoProvider;
	double startTime;
	/**
	 * This method initializes the server by loading the ontology and indexing the regions, segments
	 * @param ontoFileName
	 * @return
	 */
	public boolean initialize (boolean runningFromJar, CodeSource codeSource) {
		try {
			
			System.out.println("Initializing the server...");
			startTime = System.currentTimeMillis();
			infoProvider = new InfoProvider();
			ontoCitySPARQL = new OntoCitySPARQL(runningFromJar, codeSource);
			InfoProvider.loadRegions(ontoCitySPARQL.getAllRegionsWithSegments());
			return true;
		} catch (Exception e) {
			e.printStackTrace(); 
			return false;
		}
	}
	
	protected void initializationReport() {
		System.out.println("Server initialization process took: " + (System.currentTimeMillis() - startTime) / 1000 + " seconds...");
		System.out.println("Number of Regions: " + InfoProvider.regionFeature.getRegionInfos().size());
		System.out.println("Number of Segments: " + InfoProvider.segmentFeature.getGeometries().size());
		
	}
}
