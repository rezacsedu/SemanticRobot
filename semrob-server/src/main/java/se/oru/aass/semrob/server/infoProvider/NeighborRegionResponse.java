package se.oru.aass.semrob.server.infoProvider;


import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import se.oru.aass.semrob.geometry.GeometryAnalyser;
import se.oru.aass.semrob.geometry.Orientation;
import se.oru.aass.semrob.geometry.Region;
import se.oru.aass.semrob.ontology.query.OntoCitySPARQL;
import se.oru.aass.semrob.setting.Setting;



public class NeighborRegionResponse  extends NeighborResponse {

	
	
	public NeighborRegionResponse(OntoCitySPARQL objSPARQL, List<Integer> regionsType, int direction, int x, int y, int distance1, int distance2, double areaSize, int areaSizeSign, HashMap<Integer, Integer> eventAffectedRegions) {
		super(objSPARQL, regionsType, direction, x, y, distance1, distance2, areaSize, areaSizeSign, eventAffectedRegions);
	}
	
	public NeighborRegionResponse(OntoCitySPARQL objSPARQL, List<Integer> regionsType, int direction, int x, int y, int distance1, int distance2, double areaSize, int areaSizeSign) {
		super(objSPARQL, regionsType, direction, x, y, distance1, distance2, areaSize, areaSizeSign);
	}
		
	public void query() {
		super.query();
		
		List<Integer> segmentIDs = InfoProvider.getSegmentIDsInRelationWithPoint(x, y, lowerBoundDistance, upperBoundDistance, direction);
		if (ignoreEvent)
			mainRegionID = getMainRegionID();
		else
			mainRegionID = getMainRegionID(this.eventAffectedRegionIDs);
			
		if (mainRegionID != Region.INVALID_REGION_ID) {
			//regionIDs = InfoProvider.getAllRegionIDsInSegmentsWithType(segmentIDs, regionsType, x, y, lowerBoundDistance, upperBoundDistance);
			Geometry region = InfoProvider.regionFeature.getRegionInfos().get(mainRegionID).getGeometry();
			Envelope envelope = region.getEnvelopeInternal();
					
			// we chose around direction, because the direction has already applied before to choose the list of segments
			int boundDistance = upperBoundDistance;
			if (upperBoundDistance == 0)
				boundDistance = Setting.SEGMENT_PIXEL_SIZE;
			Geometry upperBoundEnvelope	= GeometryAnalyser.extendEnvelope(envelope, boundDistance, Orientation.AROUND.getLabelID());	
			Set<Integer> regionIDsWithinUpperBound = InfoProvider.getAllRegionIDsInSegmentsWithType(segmentIDs, regionsType, upperBoundEnvelope);
			regionIDs.addAll(regionIDsWithinUpperBound);
			
			
			if (InfoProvider.getSafeLowerBoundDistance(lowerBoundDistance) != 0) {
				// we chose around direction, because the direction has already applied before to choose the list of segments
				Geometry lowerBoundEnvelope	= GeometryAnalyser.extendEnvelope(envelope, lowerBoundDistance, Orientation.AROUND.getLabelID());	
				Set<Integer> regionIDsWithinLowerBound = InfoProvider.getAllRegionIDsInSegmentsWithType(segmentIDs, regionsType, lowerBoundEnvelope);
				regionIDs.removeAll(regionIDsWithinLowerBound);
			}
			
			// removing the main region which contains the point x, y from the list ore regionIDs
			regionIDs.remove(mainRegionID);
			
			regionIDs.retainAll(InfoProvider.checkGeometryDistanceWithOthers(regionIDs, region, lowerBoundDistance, upperBoundDistance));
			
	
			
			if (regionIDs.size() > 0) {
				Set<Integer> regionIDsWithSuitableAreaSize = InfoProvider.getRegionIDsWithSuitableAreaSize(regionIDs, areaSize, areaSizeSign);
				regionIDs.retainAll(regionIDsWithSuitableAreaSize);
			}
			
			
			if (!ignoreEvent) {
				regionIDs.removeAll(eventAffectedRegionIDs.keySet());
			}
		}
	}

	@Override
	public void printResult() {
		super.printResult();
	}
	

}
