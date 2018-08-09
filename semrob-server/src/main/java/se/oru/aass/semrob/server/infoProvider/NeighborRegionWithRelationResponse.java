package se.oru.aass.semrob.server.infoProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import se.oru.aass.semrob.geometry.Direction;
import se.oru.aass.semrob.geometry.Region;
import se.oru.aass.semrob.ontology.query.OntoCitySPARQL;


public class NeighborRegionWithRelationResponse  extends NeighborRegionResponse {


	protected int distanceRelational;
	protected int directionRelational;
	protected int regionTypeRelational;
	
	
	public NeighborRegionWithRelationResponse(OntoCitySPARQL objSPARQL, List<Integer> regionsType, int direction, int x, int y, int distance1, int distance2,
		double areaSize, int areaSizeSign, int distanceRelational, int regionTypeRelational, HashMap<Integer, Integer> eventAffectedRegions) {
	
		super(objSPARQL, regionsType, direction, x, y, distance1, distance2, areaSize, areaSizeSign, eventAffectedRegions);
		this.distanceRelational = distanceRelational;
		this.directionRelational = Direction.AROUND.getLabelID();
		this.regionTypeRelational = regionTypeRelational;
	}

	public NeighborRegionWithRelationResponse(OntoCitySPARQL objSPARQL, List<Integer> regionsType, int direction, int x, int y, int distance1, int distance2, 
		double areaSize, int areaSizeSign, int distanceRelational, int regionTypeRelational) {
		super(objSPARQL, regionsType, direction, x, y, distance1, distance2, areaSize, areaSizeSign);
		this.distanceRelational = distanceRelational;
		this.directionRelational = Direction.AROUND.getLabelID();
		this.regionTypeRelational = regionTypeRelational;
	}
	
	public void query() {
		super.query();
		
	
		if (mainRegionID != Region.INVALID_REGION_ID) {
			
			Set<Integer> regionIDsWithDistanceRelation = InfoProvider.getRegionIDsWithDistanceRelation (regionIDs, distanceRelational, regionTypeRelational);
			if (regionIDsWithDistanceRelation.size() == 0)
				regionIDs.clear();

		}
	}
	
	@Override
	public void printResult() {
		
		super.printResult();
	}
	
	@Override
	public String getResponse() {
		return super.getResponse();
	}
}
