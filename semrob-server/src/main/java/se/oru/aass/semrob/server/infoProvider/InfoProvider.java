package se.oru.aass.semrob.server.infoProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.query.QuerySolution;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import se.oru.mpi.rtree.RTree;
import se.oru.aass.semrob.geometry.GeoFeature;
import se.oru.aass.semrob.geometry.GeometryAnalyser;
import se.oru.aass.semrob.geometry.Orientation;
import se.oru.aass.semrob.geometry.Region;
import se.oru.aass.semrob.geometry.RegionInfo;
import se.oru.aass.semrob.ontology.OntoCity;
import se.oru.aass.semrob.ontology.OntoCityHandler;
import se.oru.aass.semrob.ontology.query.OntoCitySPARQL;
import se.oru.aass.semrob.setting.NumericRelation;
import se.oru.aass.semrob.setting.Setting;



public class InfoProvider {
	public static GeoFeature regionFeature;
	//private GeoFeature eventAreaFeature;
	public static GeoFeature segmentFeature;
	
	// segmentRegions is a hashmap of hashmaps. For each segment it contains a hashmap that includes a set of hashmaps each contains a boundingboxholder for all the regions of a specific type (e.g., building) that exist in the segment
	// For the sake of convenience, each hashmap of a segment, also include a rtree (boundingboxHolder) for all the regions in the segment regardless of their type.
	public static HashMap<Integer, HashMap<Integer, RTree>> segmentRegions;
	
	//eventAreaRegions is a hashmap that holds for each eventAreaID the list of regionIDs overlapping
	private HashMap<Integer, Integer> eventAffectedRegions;
	
	
	public static HashMap<Integer, HashMap<Integer, Set<Integer>>> regionDistanceHolder;
	
	public InfoProvider() {
		
	}
		
	public HashMap<Integer, Integer> getEventAffectedRegions() {
		return eventAffectedRegions;
	}
	
	public void setEventAffectedRegions(HashMap<Integer, Integer> eventAreaRegions) {
		this.eventAffectedRegions = eventAreaRegions;
	}
	
	/**
	 * This method loads all the given geoFeatures retrieved from the ontology
	 * into the GeoFeature structure containing a boundingbox and the hashmap of the geometries
	 */
	public static GeoFeature loadSegments (List<QuerySolution> queryResult, String featureIndivName, String featureBoundary, String spliter, int splitIndex, boolean isAlwaysPolygon) {
		GeoFeature geoFeature = new GeoFeature(false);
		
		for (QuerySolution solution : queryResult) {
			
			int geoFeatureID = getFeatureID(solution.getResource(featureIndivName).toString(), spliter, splitIndex);
			
						
			String boundary = solution.getLiteral(featureBoundary).toString();
			Geometry geometry;
			if (isAlwaysPolygon)
				geometry = GeometryAnalyser.convertToPolygon(boundary);
			else
				geometry = GeometryAnalyser.checkGeometryType(boundary);
				
			Envelope envelope  = geometry.getEnvelopeInternal();
			geoFeature.getBoundingBoxHolder().add(geoFeatureID, (int) envelope.getMinX(), (int) envelope.getMinY(), (int) envelope.getMaxX(), (int) envelope.getMaxY());
			geoFeature.getGeometries().put(geoFeatureID, geometry);
		}
		
		return geoFeature;
	}
	
	/**
	 * This method returns a hashmap holding the list of the IDs of all the eventAffectedRegions
	 * @param queryResult
	 * @return
	 */
	public static HashMap<Integer, Integer> loadEventAreaRegionIDs (List<QuerySolution> queryResult) {
		
		
		HashMap<Integer, Integer> eventAffectedRegions = new HashMap<>();
		for (QuerySolution solution : queryResult) {
				
			int regionID = getFeatureID(solution.getResource("regionIndivName").toString(), OntoCityHandler.PREFIX_REGION_NAME, 1);
			eventAffectedRegions.put(regionID, regionID);
		}
	
		return eventAffectedRegions;
	}
	


	/**
	 * This method load the regions with preliminary label set
	 * @param queryResult
	 */
	public static void loadRegions (List<QuerySolution> queryResult) {
		
		regionFeature = new GeoFeature(true);
		segmentFeature = new GeoFeature(false);
				
		segmentRegions  = new HashMap<Integer, HashMap<Integer, RTree>>();
				
		for (QuerySolution solution : queryResult) {
	
			int regionID = getFeatureID(solution.getResource("regionIndivName").toString(), OntoCityHandler.PREFIX_REGION_NAME, 1);
			int segmentID = getFeatureID(solution.getResource("segmentIndivName").toString(), OntoCityHandler.PREFIX_SEGMENT_NAME, 1);
			double regionPrecision = getPrecisionVaue(solution.getLiteral("precision").toString());
			double regionAbsoluteElevation = getElevationVaue(solution.getLiteral("absoluteElevation").toString());
			double regionRelativeElevation = getElevationVaue(solution.getLiteral("relativeElevation").toString());
			String boundary = solution.getLiteral("regionBoundary").toString();
			Geometry polygon = GeometryAnalyser.convertToPolygon(boundary);
			String regionTypeName = solution.getResource("region").toString().split(OntoCitySPARQL.URI_ENTITY_SPLITTER)[1];
			int regionType = OntoCity.getLabelCode(regionTypeName);
	
//			if (regionType == OntoCity.Unknown.getLabelID()) { 
//				System.out.println("Unknown region, ID = " + regionID + " at segment: " + segmentID);
//			}
			
			if (!(regionFeature.getRegionInfos().containsKey(regionID)))
				regionFeature = GeoFeature.updateGeoFeatureInfo (regionFeature, regionID, polygon, OntoCityHandler.PREFIX_REGION_NAME, regionType, regionPrecision, regionAbsoluteElevation, regionRelativeElevation);
				
			if (!(segmentFeature.getGeometries().containsKey(segmentID))) {	
				segmentFeature = GeoFeature.updateGeoFeatureInfo (segmentFeature, segmentID, solution.getLiteral("segmentBoundary").toString(), OntoCityHandler.PREFIX_SEGMENT_NAME);
			}	

			segmentRegions.put(segmentID, getSegmentRegionsWithType(segmentID, regionType, regionID));
			segmentRegions.put(segmentID, getSegmentRegionsWithType(segmentID, Region.INVALID_REGION_ID, regionID));
		}
		
		//segmentRegions.get(17).get(OntoCity.Unknown.getLabelID())
	}
	
//	/**
//	 * This method load the regions with enriched label set
//	 * @param queryResult
//	 * @param enrichedLabelQueryResult
//	 */
//	public static void loadRegions (List<QuerySolution> queryResult, List<QuerySolution> enrichedLabelQueryResult) {
//		GeoFeature enrichedBuildings;
//		
//		regionFeature = new GeoFeature(true);
//		segmentFeature = new GeoFeature(false);
//				
//		segmentRegions  = new HashMap<Integer, HashMap<Integer, RTree>>();
//		
//		enrichedBuildings = getEnrichedPolygons(enrichedLabelQueryResult);
//		
//		for (QuerySolution solution : queryResult) {
//	
//			int regionID = getFeatureID(solution.getResource("regionIndivName").toString(), OntoCityHandler.PREFIX_REGION_NAME, 1);
//			int segmentID = getFeatureID(solution.getResource("segmentIndivName").toString(), OntoCityHandler.PREFIX_SEGMENT_NAME, 1);
//			double regionPrecision = getPrecisionVaue(solution.getLiteral("precision").toString());
//			double regionAbsoluteElevation = getElevationVaue(solution.getLiteral("absoluteElevation").toString());
//			double regionRelativeElevation = getElevationVaue(solution.getLiteral("relativeElevation").toString());
//			String boundary = solution.getLiteral("regionBoundary").toString();
//			Geometry polygon = GeometryAnalyser.convertToPolygon(boundary);
//			int regionType;
//			
//			List<Integer> enrichedBuildingIDs = enrichedBuildings.getBoundingBoxHolder().findIntersectingBoundingBox(polygon);
//			if (enrichedBuildingIDs.size() == 1) {
//				regionType = enrichedBuildings.getRegionInfos().get(enrichedBuildingIDs.get(0)).getLabelCode();
//			}
//			else {
//				String regionTypeName = solution.getResource("region").toString().split(OntoCitySPARQL.URI_ENTITY_SPLITTER)[1];
//				regionType = OntoCity.getLabelCode(regionTypeName);
//			}
//	
//			if (!(regionFeature.getRegionInfos().containsKey(regionID)))
//				regionFeature = updateGeoFeatureInfo (regionFeature, regionID, polygon, OntoCityHandler.PREFIX_REGION_NAME, regionType, regionPrecision, regionAbsoluteElevation, regionRelativeElevation);
//				
//			if (!(segmentFeature.getGeometries().containsKey(segmentID))) {	
//				segmentFeature = updateGeoFeatureInfo (segmentFeature, segmentID, solution, "segmentBoundary", OntoCityHandler.PREFIX_SEGMENT_NAME);
//			}	
//			
//			
//
//			segmentRegions.put(segmentID, getSegmentRegionsWithType(segmentID, regionType, regionID));
//			segmentRegions.put(segmentID, getSegmentRegionsWithType(segmentID, Region.INVALID_REGION_ID, regionID));
//			
//		}
//	}
//	
//	private static GeoFeature getEnrichedPolygons(List<QuerySolution> enrichedLabelQueryResult) {
//		
//		
//		GeoFeature enrichedBuildings = new GeoFeature(true);
//		
//		for (QuerySolution solution : enrichedLabelQueryResult) {
//			int enrichedBuildingID = getFeatureID(solution.getResource("enrichedBuildingID").toString(), OntoCityHandler.PREFIX_ENRICHED_BUILDING_NAME, 1);
//			String boundary = solution.getLiteral("boundary").toString();
//			Geometry polygon = GeometryAnalyser.convertToPolygon(boundary);
//			Envelope envelope  = polygon.getEnvelopeInternal();
//			enrichedBuildings.getBoundingBoxHolder().add(enrichedBuildingID, (int) envelope.getMinX(), (int) envelope.getMinY(), (int) envelope.getMaxX(), (int) envelope.getMaxY());
//			
//			
//			String regionTypeName = solution.getResource("enrichedBuilding").toString().split(OntoCitySPARQL.URI_ENTITY_SPLITTER)[1];
//			int regionType = OntoCity.getLabelCode(regionTypeName);
//			RegionInfo enrichedBuildingInfo = new RegionInfo(polygon, regionType, 0, 0, 0);
//			enrichedBuildings.getRegionInfos().put(enrichedBuildingID, enrichedBuildingInfo);
//		}
//		return enrichedBuildings;
//	}
	private static double getPrecisionVaue (String value) {
		String s = getNumericValue(value);
		if (s.length() > 0)
			return Double.parseDouble(s) * 100;
		else return 100;
			
	}
	
	private static double getElevationVaue (String value) {
		String s = getNumericValue(value);
		if (s.length() > 0)
			return Double.parseDouble(s);
		else return 0;
	}
	
	private static String getNumericValue(String value) {
		try {
			String[] s = value.split("\\^");
			if (s.length > 0)
				return s[0];
			else return "";
		}
		catch (Exception e) {
			return "";
		}
	}
	
	private static HashMap<Integer, RTree> getSegmentRegionsWithType(int segmentID, int regionType, int regionID) {
		
		HashMap<Integer, RTree> segmentRegionsWithType;
		RTree segmentRegionsBoundingBox;
		if(segmentRegions.containsKey(segmentID)) {
			segmentRegionsWithType = segmentRegions.get(segmentID);
			
			if(segmentRegionsWithType.containsKey(regionType))
				segmentRegionsBoundingBox = segmentRegionsWithType.get(regionType);
			else
				segmentRegionsBoundingBox = new RTree();
		}
		else {
			segmentRegionsWithType = new HashMap<Integer, RTree>();
			segmentRegionsBoundingBox = new RTree();
		}
		
		segmentRegionsBoundingBox.add(regionID, regionFeature.getRegionInfos().get(regionID).getGeometry());
		segmentRegionsWithType.put(regionType, segmentRegionsBoundingBox);
		return segmentRegionsWithType;
		
	}
	
	public static int getFeatureID (String solution, String spliter, int indexSplit) {
		
		return Integer.parseInt(solution.split(OntoCitySPARQL.URI_ENTITY_SPLITTER)[1].split(spliter)[indexSplit]);
	}
	

	
	

	
	// -------------------------   INDEXED QUERIES  -------------------------------  
	

	/**
	 * This method retrieves all the regionIDs that containing the given point
	 * @param x indicates the x-coordinate of the point
	 * @param y indicates the y-coordinate of the point
	 * @return list of the retrieved regionIDs
	 */
	public static Set<Integer> getRegionIDsContainingPoint(Collection<Integer> segmentIDs, int x, int y) {
		Set<Integer> regionIDs = new HashSet<>();
		Coordinate point = new Coordinate(x, y);
		
		for (int segmentID : segmentIDs) {
			//List<Integer> regionIDsContainingPointInSegment = segmentRegions.get(segmentID).get(OntoCityRegionPattern.Region.getLabelID()).getBoundingBox(x, y);
			List<Integer> regionIDsContainingPointInSegment = segmentRegions.get(segmentID).get(Region.INVALID_REGION_ID).getBoundingBox(x, y);
			for (int regionID : regionIDsContainingPointInSegment) {
				Polygon regionPolygon = (Polygon) regionFeature.getRegionInfos().get(regionID).getGeometry();
				if (GeometryAnalyser.contains(regionPolygon, point))
					regionIDs.add(regionID);
			}
		}
		return regionIDs;
	}	

	/**
	 * The method getSegmentIDsAroundPoint retrieves all the segmentIDs located with a directional relation with the given point considering the distance interval
	 * @param x
	 * @param y
	 * @param lowerBoundDistance
	 * @param upperBoundDistance
	 * @param direction
	 * @return
	 */
	public static List<Integer> getSegmentIDsInRelationWithPoint(int x, int y, int lowerBoundDistance, int upperBoundDistance, int direction) {
		
		List<Integer> segmentIDs = new ArrayList<>();
		
		lowerBoundDistance = getSafeLowerBoundDistance(lowerBoundDistance);
		
		Polygon smallerEnvelope =  GeometryAnalyser.getEnvelopeAroundPoint(x, y, lowerBoundDistance, direction);
		Polygon biggerEnvelope =  GeometryAnalyser.getEnvelopeAroundPoint(x, y, upperBoundDistance, direction);
		
		segmentIDs = segmentFeature.getBoundingBoxHolder().findIntersectingBoundingBox(biggerEnvelope);
		List<Integer> segmentIDsWithinLowerBoundDistance = segmentFeature.getBoundingBoxHolder().findIntersectingBoundingBox(smallerEnvelope);
		
		if (lowerBoundDistance != 0)
			segmentIDs.removeAll(segmentIDsWithinLowerBoundDistance);
		
		return segmentIDs;
	}
	
	public static int getSafeLowerBoundDistance (int lowerBoundDistance) {
		if (lowerBoundDistance >= Setting.SEGMENT_PIXEL_SIZE)
			lowerBoundDistance = lowerBoundDistance - Setting.SEGMENT_PIXEL_SIZE;
		else
			lowerBoundDistance = 0;
		return lowerBoundDistance;
	}

	public static Set<Integer> getAllRegionIDsInSegmentsWithType(Collection<Integer> segmentIDs, List<Integer> regionsType, Geometry envelope) {
		Set<Integer> regionIDs = new HashSet<>();
		HashMap<Integer, RTree> segmentRegionsWithType;
		
		for (int segmentID : segmentIDs)  {
			segmentRegionsWithType = InfoProvider.segmentRegions.get(segmentID);
			for (int regionType : regionsType) {
				if(segmentRegionsWithType.containsKey(regionType)) {
					RTree segmentRegionsBoundingBox = segmentRegionsWithType.get(regionType);
					if(segmentRegionsBoundingBox != null) {
						List<Integer> regionIDsWithinDistance = segmentRegionsBoundingBox.findIntersectingBoundingBox(envelope);
						regionIDs.addAll(regionIDsWithinDistance);
					}
				}
			}
		}
		
		return regionIDs;
	}
	
	
	public static Set<Integer> getAllRegionIDsInSegmentsWithType(Collection<Integer> segmentIDs, List<Integer> regionsType) {
		
		Set<Integer> regionIDs = new HashSet<>();
		HashMap<Integer, RTree> segmentRegionsWithType;
		for (int segmentID : segmentIDs) {
			segmentRegionsWithType = InfoProvider.segmentRegions.get(segmentID);
			for (int regionType : regionsType) {
				if(segmentRegionsWithType.containsKey(regionType)) {
					RTree segmentRegionsBoundingBox = segmentRegionsWithType.get(regionType);
					regionIDs.addAll(segmentRegionsBoundingBox.findIntersectingBoundingBox(segmentFeature.getGeometries().get(segmentID)));
				}
			}
		}
		
		return regionIDs;
	}	
	public static Set<Integer> getRegionIDsWithDistanceRelation (Set<Integer> regionIDs, int distanceRelational, int regionTypeRelational) {
		Set<Integer> regionIDsWithDistanceRelation = new HashSet<>();
		
		List<Integer> regionTypes = new ArrayList<>();
		regionTypes.add(regionTypeRelational);
		for (int regionID : regionIDs)  {

			
			//Geometry region = InfoProvider.regionFeature.getRegionInfos().get(regionID).getGeometry();
			Envelope envelope = InfoProvider.regionFeature.getRegionInfos().get(regionID).getGeometry().getEnvelopeInternal();
			
			Geometry extendedEnvelope	= GeometryAnalyser.extendEnvelope(envelope, distanceRelational, Orientation.AROUND.getLabelID());
			List<Integer> segmentIDs = InfoProvider.segmentFeature.getBoundingBoxHolder().findIntersectingBoundingBox(extendedEnvelope);
			
			Set<Integer> regionIDsWithinEnvelope = getAllRegionIDsInSegmentsWithType(segmentIDs, regionTypes, extendedEnvelope);
			regionIDsWithDistanceRelation.addAll(regionIDsWithinEnvelope);
		}
		
		return regionIDsWithDistanceRelation;
	}
	
	public static Set<Integer> getRegionIDsWithSuitableAreaSize (Set<Integer> regionIDs, double areaSize, int areaSizeSign) {
		Set<Integer> regionIDsWithSuitableAreaSize = new HashSet<>();
		for (int regionID : regionIDs) {
			double currentRegionAreaSize = InfoProvider.regionFeature.getRegionInfos().get(regionID).getGeometry().getArea();
			if (areaSizeSign == NumericRelation.BIGGER_THAN.getCode()) {
				if (currentRegionAreaSize > areaSize) {
					regionIDsWithSuitableAreaSize.add(regionID);
				}
			}
			else if (areaSizeSign == NumericRelation.SMALLER_THAN.getCode()) {
				if (currentRegionAreaSize < areaSize) {
					regionIDsWithSuitableAreaSize.add(regionID);
				}
			}
			else if (areaSizeSign == NumericRelation.BIGGER_THAN.getCode()) {
				if (currentRegionAreaSize == areaSize) {
					regionIDsWithSuitableAreaSize.add(regionID);
				}
			}
		}
		return regionIDsWithSuitableAreaSize;
	}
	

	
	public static Set<Integer> checkGeometryDistanceWithOthers (Collection<Integer> regionIDs, Geometry geometry, int lowerBoundDistance, int upperBoundDistance) {
		Set<Integer> resultedRegionIDs = new HashSet<>();
		for (int regionID : regionIDs) {
			double currentDistance = regionFeature.getRegionInfos().get(regionID).getGeometry().distance(geometry);
			if (currentDistance <= upperBoundDistance && currentDistance >= lowerBoundDistance) {
				resultedRegionIDs.add(regionID);
			}
		}
		return resultedRegionIDs;
	}
}
