package se.oru.aass.semrob.server.infoProvider;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import se.oru.aass.semrob.geometry.Region;
import se.oru.aass.semrob.ontology.OntoCity;
import se.oru.aass.semrob.ontology.query.OntoCitySPARQL;


/**
 * This class is used to provide the basis required for all the queries whose reponses are in the form of a set of regions with specific criteria
 * @author marjan
 *
 */


public class RegionBasedResponse implements Response{
	

	
	protected static OntoCitySPARQL ontoSPARQL = null;
	
	
	protected boolean ignoreEvent;
	protected Set<Integer> regionIDs;
	protected int mainRegionID;
	protected HashMap<Integer, Integer> eventAffectedRegionIDs;
	
	public RegionBasedResponse(OntoCitySPARQL ontoSPARQL) {
		this.ontoSPARQL = ontoSPARQL;
		this.regionIDs = new HashSet<Integer>();
		this.eventAffectedRegionIDs = null;
		ignoreEvent = true;
	}
	
	public RegionBasedResponse(OntoCitySPARQL ontoSPARQL, HashMap<Integer, Integer> eventAffectedRegionIDs) {
		this.ontoSPARQL = ontoSPARQL;
		this.regionIDs = new HashSet<Integer>();
		this.eventAffectedRegionIDs = eventAffectedRegionIDs;
		ignoreEvent = false;
	}
	
	
	public void query() {
		regionIDs.clear();
	}
	
	public Set<Integer> getRegionIDs() {
		return regionIDs;
	}
	
	public void printResult () {
		
	}
	
	public String getResponse () {
		String response = "";
		if (mainRegionID != Region.INVALID_REGION_ID) {
			response += generateResponseBlock(mainRegionID) + SPLITTER_SERVER_RESPONSE_INSTANCE;
			
			int regionIndex = 0;
			for (int regionID : regionIDs) {
				
				response += generateResponseBlock(regionID);
				
				if (regionIndex < (regionIDs.size() - 1))
					response += SPLITTER_SERVER_RESPONSE_INSTANCE;
				
				regionIndex ++;
				
				if (regionIndex == 100)
					break;
			}
		}
		
		
		System.out.println("Server is waiting for the next request: ");	
		return response;
	}
	
	protected String generateResponseBlock (int regionID) {
		Polygon region = (Polygon) InfoProvider.regionFeature.getRegionInfos().get(regionID).getGeometry();
		String regionType = OntoCity.getClassName(InfoProvider.regionFeature.getRegionInfos().get(regionID).getLabelCode());
		String regionBoundary = getBoundary(region.getCoordinates());
		double regionPrecision = InfoProvider.regionFeature.getRegionInfos().get(regionID).getPrecision();
		int boundarySize = (int) region.getBoundary().getLength();
		Point centroid = region.getCentroid();
		double cX = centroid.getX();
		double cY = centroid.getY();
		
		String responseBlock = "";
		responseBlock += Integer.toString(regionID) + SPLITTER_SERVER_RESPONSE_INSTANCE_INFO +
				regionType + SPLITTER_SERVER_RESPONSE_INSTANCE_INFO +
				regionBoundary + SPLITTER_SERVER_RESPONSE_INSTANCE_INFO +
				Integer.toString(boundarySize) + SPLITTER_SERVER_RESPONSE_INSTANCE_INFO +
				Double.toString(cX) + SPLITTER_SERVER_RESPONSE_INSTANCE_INFO +
				Double.toString(cY) + SPLITTER_SERVER_RESPONSE_INSTANCE_INFO + 
				regionPrecision + SPLITTER_SERVER_RESPONSE_INSTANCE_INFO;
		
		return responseBlock;		
		
	}
	
	private String getBoundary(Coordinate[] boundary) {
		String boundaryPoints = "";
		for (Coordinate point : boundary) {
			boundaryPoints += (int) point.x + " " + (int) point.y + " "; 
		}
		return boundaryPoints;
	}
	
}
