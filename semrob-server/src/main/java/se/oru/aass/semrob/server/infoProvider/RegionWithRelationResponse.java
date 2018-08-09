package se.oru.aass.semrob.server.infoProvider;


import java.util.List;
import java.util.Set;

import se.oru.aass.semrob.geometry.Direction;
import se.oru.aass.semrob.ontology.query.OntoCitySPARQL;



public class RegionWithRelationResponse  extends RegionBasedResponse {

	
	protected List<Integer> regionsType;
	protected double areaSize;
	protected int areaSizeSign;
	protected int distanceRelational;
	protected int directionRelational;
	protected int regionTypeRelational;
	
	
	public RegionWithRelationResponse(OntoCitySPARQL objSPARQL, List<Integer> regionsType, double areaSize, int areaSizeSign, int distanceRelational, int regionTypeRelational) {
		super(objSPARQL);
		this.regionsType = regionsType;
		this.areaSize = areaSize;
		this.areaSizeSign = areaSizeSign;
		this.distanceRelational = distanceRelational;
		this.directionRelational = Direction.AROUND.getLabelID();
		this.regionTypeRelational = regionTypeRelational;
	}
	
	public void query() {
		super.query();
		
		regionIDs = InfoProvider.getAllRegionIDsInSegmentsWithType(InfoProvider.segmentRegions.keySet(), regionsType);
		
		Set<Integer> regionIDsWithDistanceRelation = InfoProvider.getRegionIDsWithDistanceRelation (regionIDs, distanceRelational, regionTypeRelational);
		regionIDs.retainAll(regionIDsWithDistanceRelation);
		
//		List<Integer> regionTypesRelation = new ArrayList<>();
//		regionTypesRelation.add(regionTypeRelational);
//		regionIDs = InfoProvider.getAllRegionIDsWithSuitableDistance(regionIDs, distanceRelational, regionTypesRelation);
		
		
		if (regionIDs.size() > 0) {
			Set<Integer> regionIDsWithSuitableAreaSize = InfoProvider.getRegionIDsWithSuitableAreaSize(regionIDs, areaSize, areaSizeSign);
			regionIDs.retainAll(regionIDsWithSuitableAreaSize);
		}
		
	}
	
	
	
	
	
	@Override
	public void printResult() {
		super.printResult();
	}

}
