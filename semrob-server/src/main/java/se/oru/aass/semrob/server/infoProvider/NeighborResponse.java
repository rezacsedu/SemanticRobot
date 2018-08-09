package se.oru.aass.semrob.server.infoProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.oru.aass.semrob.geometry.Direction;
import se.oru.aass.semrob.geometry.Region;
import se.oru.aass.semrob.ontology.query.OntoCitySPARQL;


public class NeighborResponse extends RegionBasedResponse{
	
	protected int x;
	protected int y;
	protected int limit = 10;
	protected int lowerBoundDistance;
	protected int upperBoundDistance;
	protected boolean ignoreEvents = false;
	protected int direction = Direction.AROUND.getLabelID();
	protected List<Integer> regionsType;
	protected double areaSize;
	protected int areaSizeSign;
	

	public NeighborResponse(OntoCitySPARQL ontoSPARQL, List<Integer> regionsType, int direction, int x, int y, int distance1, int distance2, double areaSize, int areaSizeSign, HashMap<Integer, Integer> eventAffectedRegions) {
		super(ontoSPARQL, eventAffectedRegions);
		this.regionsType = regionsType;
		this.direction = direction;
		this.x = x;
		this.y = y;
		this.areaSize = areaSize;
		this.areaSizeSign = areaSizeSign;
		setDistanceInterval(distance1, distance2);
	}
	
	public NeighborResponse(OntoCitySPARQL ontoSPARQL, List<Integer> regionsType, int direction, int x, int y, int distance1, int distance2, double areaSize, int areaSizeSign) {
		super(ontoSPARQL);
		this.regionsType = regionsType;
		this.direction = direction;
		this.x = x;
		this.y = y;
		this.areaSize = areaSize;
		this.areaSizeSign = areaSizeSign;
		setDistanceInterval(distance1, distance2);
	}
	
	public void setDistanceInterval (int distance1, int distance2) {
		if (distance1 < distance2) {
			this.lowerBoundDistance = distance1;
			this.upperBoundDistance = distance2;
		}
		else {
			this.lowerBoundDistance = distance2;
			this.upperBoundDistance = distance1;
		}
	}
	
	/**
	 * this method returns the id of the region neighbors of which are retrieved by this class
	 * @return
	 */
	protected int getMainRegionID(HashMap<Integer, Integer> eventAffectedRegions) {
		RegionResponse regionInfo = new RegionResponse(ontoSPARQL, x, y, eventAffectedRegions);
		regionInfo.query();
		List<Integer> theRegionID = new ArrayList<>(regionInfo.getRegionIDs());
		if (theRegionID.size() == 1)
			return theRegionID.get(0);
		else return Region.INVALID_REGION_ID;
		
	}
	
	protected int getMainRegionID() {
		RegionResponse regionInfo = new RegionResponse(ontoSPARQL, x, y);
		regionInfo.query();
		List<Integer> theRegionID = new ArrayList<>(regionInfo.getRegionIDs());
		if (theRegionID.size() == 1)
			return theRegionID.get(0);
		else return Region.INVALID_REGION_ID;
		
	}
	
	@Override
	public void printResult() {
		
		super.printResult();
	}
}
