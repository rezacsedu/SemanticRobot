package se.oru.aass.semrob.server;

import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import se.oru.aass.semrob.server.infoProvider.InfoProvider;
import se.oru.aass.semrob.server.infoProvider.NeighborRegionResponse;
import se.oru.aass.semrob.server.infoProvider.NeighborRegionWithRelationResponse;
import se.oru.aass.semrob.server.infoProvider.RegionResponse;


/**
 * 
 * @author marjan
 * This class is responsible to provide an interface needed for the second demo.
 */
public class Server_2 extends Server{
	
	/**
	 * This method initializes the server by loading the ontology and indexing the regions, segments and eventAreas
	 * @param ontoFileName
	 * @return
	 */
	
	private Setting setting;
	public boolean initialize (boolean runningFromJar, CodeSource codeSource) {
		try {
			
			super.initialize(runningFromJar, codeSource);		
			infoProvider.setEventAffectedRegions(InfoProvider.loadEventAreaRegionIDs(ontoCitySPARQL.getAllEventAffectedRegionIDs()));	
			initializationReport();
			setting = new Setting();
			return true;
		} catch (Exception e) {
			e.printStackTrace(); 
			return false;
		}
	}
	
	protected void initializationReport() {
		super.initializationReport();
		System.out.println("Number of EventAffected Regions: " + infoProvider.getEventAffectedRegions().size());
	}
	
	//--------------------------------------------------------------
	// Methods Called by the clients
	//--------------------------------------------------------------
	
	/**
	 * The method getRegionInfo returns the information about the region containing the given point (x, y)
	 * without checking if the region is affected by events
	 * @param x indicates the x-coordinate of the point
	 * @param y indicates the y-coordinate of the point
	 */
	public String getRegionInfo(int x, int y, boolean ignoreEvents) {
		RegionResponse objResponse;
		if (ignoreEvents)
			objResponse	= new RegionResponse(ontoCitySPARQL, x, y);
		else
			objResponse	= new RegionResponse(ontoCitySPARQL, x, y, infoProvider.getEventAffectedRegions());
		
		objResponse.query();
		return objResponse.getResponse();
	}
	
		
	/**
	 * The method getNeighbourRegionInfo returns the information about the neighbors located within a given distance interval from the region containing the given point (x, y)
	 * @param regionsType indicates the desirable types of the neighbors that we are looking for
	 * @param direction indicates the directional relation that the neighbors have with the main region
	 * @param x indicates the x-coordinate of the point
	 * @param y indicates the y-coordinate of the point
	 * @param distance1 indicates the lower bound of the distance interval
	 * @param distance2 indicates the upper bound of the distance interval
	 * @param ignoreEvents indicates if we ignore the eventAffected areas from the result list
	 */
	public String getNeighborRegionInfo(String regionsTypeString, int direction, int x, int y, int distance1, int distance2, double areaSize, int areaSizeSign, boolean ignoreEvents) {
		distance1 = this.setting.convertMeterToPixel(distance1);
		distance2 = this.setting.convertMeterToPixel(distance2);
		areaSize = this.setting.convertSquareMeterToPixel(areaSize);
		
		List<Integer> regionsType = processRegionTypes(regionsTypeString);
		
		NeighborRegionResponse objResponse;
		if (ignoreEvents)
			objResponse = new NeighborRegionResponse(ontoCitySPARQL, regionsType, direction, x, y, distance1, distance2, areaSize, areaSizeSign);
		else
			objResponse = new NeighborRegionResponse(ontoCitySPARQL, regionsType, direction, x, y, distance1, distance2, areaSize, areaSizeSign, infoProvider.getEventAffectedRegions());
		
		objResponse.query();
		return objResponse.getResponse();
	}

	/**
	 * This method returns all the regions with specific set of types and specific area size whose neighbors located within a specific distance also have specific type
	 * @param regionsType indicates the desirable types of the regions that we are looking for
	 * @param areaSize indicates the area size
	 * @param areaSizeSign indicates the numeric relation of regions with the given area size
	 * @param distanceRelational indicates the desirable distance limit the regions have with their neighbors with specific types 
	 * @param regionTypeRelational indicates the desired type for the neighbors of regions
	 */
	public String getRegionsWithRelationInfo(List<Integer> regionsType, double areaSize, int areaSizeSign, int distanceRelational, int regionTypeRelational) {
//		areaSize = Setting.convertSquareMeterToPixel(areaSize);
//		distanceRelational = Setting.convertMeterToPixel(distanceRelational);
//		
//		
//		RegionWithRelationResponse objResponse = new RegionWithRelationResponse(ontoCitySPARQL, regionsType, areaSize, areaSizeSign, distanceRelational, regionTypeRelational);
//		objResponse.query();
//		return objResponse.getResponse();
		
		return "";
	}
	
	
	/**
	 * This method returns all the neighbors located within a given distance interval from the region containing the given point (x, y). The neighbors that we are looking for also have 
	 * other features such as the area size and the type of regions in their vicinities
	 * @param regionsTypeString indicates the desirable types of the regions that we are looking for (it is a concatenated string containing regionType IDs)
	 * @param direction indicates the directional relation that the neighbors have with the main region
	 * @param x indicates the x-coordinate of the point
	 * @param y indicates the y-coordinate of the point
	 * @param distance1 indicates the lower bound of the distance interval
	 * @param distance2 indicates the upper bound of the distance interval
	  * @param areaSize indicates the area size
	 * @param areaSizeSign indicates the numeric relation of regions with the given area size
	 * @param distanceRelational indicates the desirable distance limit the regions have with their neighbors with specific types 
	 * @param regionTypeRelational indicates the desired type for the neighbors of regions
	 * @param ignoreEvents indicates if we ignore the eventAffected areas from the result list
	 */
	public String getNeighborRegionWithRelationInfo(String regionsTypeString, int direction, int x, int y,  int distance1, int distance2, double areaSize, int areaSizeSign, int distanceRelational, int regionTypeRelational, boolean ignoreEvents) {
		
				
		distance1 = this.setting.convertMeterToPixel(distance1);
		distance2 = this.setting.convertMeterToPixel(distance2);
		areaSize = this.setting.convertSquareMeterToPixel(areaSize);
		distanceRelational = this.setting.convertMeterToPixel(distanceRelational);
		
		List<Integer> regionsType = processRegionTypes(regionsTypeString);
		
		NeighborRegionWithRelationResponse objResponse;
		
		if (ignoreEvents)
			objResponse = new NeighborRegionWithRelationResponse(ontoCitySPARQL, regionsType, direction, x, y, distance1, distance2, areaSize, areaSizeSign, distanceRelational, regionTypeRelational);
		else
			objResponse = new NeighborRegionWithRelationResponse(ontoCitySPARQL, regionsType, direction, x, y, distance1, distance2, areaSize, areaSizeSign, distanceRelational, regionTypeRelational, infoProvider.getEventAffectedRegions());
		
				
		objResponse.query();
		return objResponse.getResponse();
	}
	
	private List<Integer> processRegionTypes (String regionsTypeString) {
		List<Integer> regionTypes = new ArrayList<>();
		String[] array = regionsTypeString.split("_");
		for (String type : array) {
			regionTypes.add(Integer.parseInt(type));
		}
		return regionTypes;
	}
	
//	public void getRegionNeighborInfo(int x, int y, int distance, int limit, int regionType, boolean ignoreEvents)  {
//		
//		List<Integer> regionIDs = InfoProvider.regionFeature.getBoundinhBoxHolder().getBoundingBox(x, y);
//		
//		if (regionIDs.size() > 0) {
//			RegionResponse regionInfo;
//			if (ignoreEvents)
//				regionInfo = new RegionResponse(ontoCitySPARQL, x, y, regionIDs, true); 
//			else
//				regionInfo = new RegionResponse(ontoCitySPARQL, x, y, regionIDs, false);
//			
//			regionInfo.query();
//						
//			List<QuerySolution> regionResult = regionInfo.getQueryResult();
//			
//			// we expect that the point belongs to only one region, otherwise it shows that some regions (mistakenly) overlap each other 
//			if (regionResult.size() == 1) {
//							
//				try {
//					//get the boundary of region
//					String regionBoundary = OntoCitySPARQL.getWKTValue(regionResult.get(0), "boundary");
//					
//					//get the boundingbox of the region
//					Polygon polygon = (Polygon) new WKTReader().read(regionBoundary);
//												
//					
//					//----------------------------------------
//					
//					
//					
//					RegionNeighbourResponse objRegionNeighbourInfo;
//					if (ignoreEvents)
//						objRegionNeighbourInfo = new RegionNeighbourResponse(ontoCitySPARQL, regionType, limit);
//					else
//						objRegionNeighbourInfo = new RegionNeighbourResponse(ontoCitySPARQL, regionType, limit, infoProvider.getEventAreaFeature().getBoundinhBoxHolder());
//					
//					//objRegionNeighbourInfo.query();
//					//objRegionNeighbourInfo.printResult();
//					
//
//					//objRegionNeighbourInfo.query1(regionFeature, segmentFeature, eventAreaFeature, envelope, distance);
//					
//					// to measure everything from the clicked piont
//					
//					
//					System.out.println("Within:");
//					double t;
//					t = System.currentTimeMillis();
//					objRegionNeighbourInfo.query2(distance, polygon, x, y);
//					System.out.println("query2 takes:" + (System.currentTimeMillis()-t)/1000);
//					
//					System.out.println("-------------------");
//					
//					System.out.println("Out of:");
//					
//					t = System.currentTimeMillis();
//					objRegionNeighbourInfo.query3(distance, polygon, x, y);
//					System.out.println("query3 takes:" + (System.currentTimeMillis()-t)/1000);
//					
//					t = System.currentTimeMillis();
//					objRegionNeighbourInfo.query4(distance, polygon, x, y);
//					System.out.println("query4 takes:" + (System.currentTimeMillis()-t)/1000);
//				}
//				catch (ParseException e) {
//					
//				}
//			}
//			else {
//				System.out.println("The region is an event_affected area...");
//			}
//		}
//	}
}
