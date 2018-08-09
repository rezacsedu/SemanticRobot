package se.oru.aass.semrob.server.infoProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.oru.aass.semrob.geometry.Region;
import se.oru.aass.semrob.ontology.query.OntoCitySPARQL;


public class RegionResponse extends RegionBasedResponse {

	private int x;
	private int y;
	

	public RegionResponse(OntoCitySPARQL objSPARQL, int x, int y, HashMap<Integer, Integer> eventAffectedRegionIDs) {
		super(objSPARQL);
		
		this.x = x;
		this.y = y;
		this.eventAffectedRegionIDs = eventAffectedRegionIDs;
		this.ignoreEvent = false;
	}
	
	public RegionResponse(OntoCitySPARQL objSPARQL, int x, int y) {
		super(objSPARQL);
		
		this.x = x;
		this.y = y;
		this.eventAffectedRegionIDs = null;
		this.ignoreEvent = true;
	}
	
	@Override
	public void query() {
		super.query();
		
		List<Integer> segmentIDs = InfoProvider.segmentFeature.getBoundingBoxHolder().getBoundingBox(x, y);
		regionIDs = InfoProvider.getRegionIDsContainingPoint(segmentIDs, x, y);
		
		if (regionIDs.size() == 1) {
			mainRegionID = new ArrayList<Integer>(regionIDs).get(0);
			if (!ignoreEvent) {
				if (eventAffectedRegionIDs.containsKey(mainRegionID)) {
					regionIDs.clear();
					mainRegionID = Region.INVALID_REGION_ID;
				}
			}
		}
		else { // We must have only one region or nothing as the result...
			regionIDs.clear();
			mainRegionID = Region.INVALID_REGION_ID;
		}
	}

	
	@Override
	public void printResult() {
		super.printResult();
		
	}
	
	@Override
	public String getResponse() {
		String response = "";
		if (mainRegionID != Region.INVALID_REGION_ID) {
			response += generateResponseBlock(mainRegionID) + SPLITTER_SERVER_RESPONSE_INSTANCE;
		}
		
		System.out.println("Server is waiting for the next request: ");	
		return response;
	}
}
