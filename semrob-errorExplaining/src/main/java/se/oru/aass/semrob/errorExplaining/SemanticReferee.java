package se.oru.aass.semrob.errorExplaining;

import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.omg.PortableServer.THREAD_POLICY_ID;

import com.hp.hpl.jena.query.QuerySolution;
import com.vividsolutions.jts.geom.Polygon;

import se.oru.aass.semrob.errorExplaining.Corrector.RegionRCC;
import se.oru.aass.semrob.geometry.Direction;
import se.oru.aass.semrob.geometry.E_RCC;
import se.oru.aass.semrob.geometry.GeoFeature;
import se.oru.aass.semrob.geometry.GeometryAnalyser;
import se.oru.aass.semrob.geometry.Region;
import se.oru.aass.semrob.matlabInterface.MatlabRegionIndexMatrixCreator;
import se.oru.aass.semrob.matlabInterface.singleTask.MisclassificationExtractor;
import se.oru.aass.semrob.ontology.OntoCity;
import se.oru.aass.semrob.ontology.OntoCityHandler;
import se.oru.aass.semrob.ontology.query.OntoCitySPARQL;

public class SemanticReferee {
	//private static final String PATH_INDEX_MATH_FILE = "/home/marjan/MyWorx/Projects/SemanticRobot/SemRob/semmap-core/Code/Matlab/files/Neural-Symbolic/SWJ-Results/code/";
	private static final String PATH_INDEX_MATH_FILE = "/home/marjan/MyWorx/Projects/SemanticRobot/SemRob/semmap-core/Code/Matlab/files/Neural-Symbolic/SWJ-Results/new-dataset/code/";
	//private static final String PATH_INDEX_MATH_FILE = "reasoningOutputs/";

	private static final int REGION_AREA_THRESHOLD = 4000;
	private static final int ELEVATION_DIFFERENCE_TO_CAST_SHADOW = 1;
	private static final int ELEVATION_THRESHOLD_BUILDING = 6;
	private static final int ELEVATION_THRESHOLD_VEGETATION = 4;
	private static final int ELEVATION_THRESHOLD_WATER = 2;
	private static final int ELEVATION_THRESHOLD_RAILROAD = 2;
	private static final int ELEVATION_THRESHOLD_ROAD = 2;
	
	private GeoFeature segmentFeature;
	private OntoCitySPARQL ontoCitySPARQL;
	private List<Region> misclassifiedRegions;
	
	// keeps all the regions for each segment
	private HashMap<Integer, List<Region>> segmentRegions;
	
	
	private Set<Integer> suspicious = new HashSet<>();
	private Set<Integer> non_suspicious = new HashSet<>();
	private Set<Integer> shadow = new HashSet<>();
	private Set<Integer> non_shadow = new HashSet<>();
	private Set<Integer> elevation_building = new HashSet<>();
	private Set<Integer> elevation_pavement = new HashSet<>();
	private Set<Integer> elevation_water = new HashSet<>();
	private Set<Integer> elevation_vehicle = new HashSet<>();
	
	
	private void loadRegions () {
		List<QuerySolution> queryResult = ontoCitySPARQL.getAllRegionsWithSegments();
		
		for (QuerySolution solution : queryResult) {
	
			int regionID = getFeatureID(solution.getResource("regionIndivName").toString(), OntoCityHandler.PREFIX_REGION_NAME, 1);
			int segmentID = getFeatureID(solution.getResource("segmentIndivName").toString(), OntoCityHandler.PREFIX_SEGMENT_NAME, 1);
			Polygon polygon = GeometryAnalyser.convertToPolygon(solution.getLiteral("regionBoundary").toString());
			int regionType = OntoCity.getLabelCode(solution.getResource("region").toString().split(OntoCitySPARQL.URI_ENTITY_SPLITTER)[1]);
			double relativeElevation = OntoCitySPARQL.getNumbericValue(solution, "relativeElevation");
			
			Region region = new Region(regionID, polygon, regionType, regionType, -1, -1, relativeElevation, regionID);
				
			if (!(this.segmentFeature.getGeometries().containsKey(segmentID))) {	
				this.segmentFeature = GeoFeature.updateGeoFeatureInfo (this.segmentFeature, segmentID, solution.getLiteral("segmentBoundary").toString(), OntoCityHandler.PREFIX_SEGMENT_NAME);
			}	
			
			List<Region> regionsInSegment;
			if(this.segmentRegions.containsKey(segmentID)) {
				regionsInSegment = this.segmentRegions.get(segmentID);
				regionsInSegment.add(region);
				this.segmentRegions.remove(segmentID);
				this.segmentRegions.put(segmentID, regionsInSegment);
			}
			else {
				regionsInSegment = new ArrayList<>();
				regionsInSegment.add(region);
				this.segmentRegions.put(segmentID, regionsInSegment);
			}
		}
		
	}
	
	private static int getFeatureID (String solution, String spliter, int indexSplit) {
		return Integer.parseInt(solution.split(OntoCitySPARQL.URI_ENTITY_SPLITTER)[1].split(spliter)[indexSplit]);
	}
	
	/**
	 * kepps the pair of a regionType (a classified region) and its RCC relation (with a misclassification area)
	 * @author marjan
	 *
	 */
	class RegionRCC {
		private E_RCC rcc;
		private Region region;
			
		public RegionRCC(Region region, E_RCC rcc) {
			this.rcc = rcc;
			this.region = region;
		}
		public E_RCC getRcc() {
			return rcc;
		}
		public Region getRegion() {
			return region;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((rcc == null) ? 0 : rcc.hashCode());
			result = prime * result + ((region == null) ? 0 : region.getPredictedLabelCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			RegionRCC other = (RegionRCC) obj;
			if (rcc != other.rcc)
				return false;
			if (region == null) {
				if (other.region != null)
					return false;
			} else if (region.getPredictedLabelCode() != other.region.getPredictedLabelCode())
				return false;
			return true;
		}
	}
	
	public SemanticReferee(boolean runningFromJar, CodeSource codeSource) {
		this.ontoCitySPARQL = new OntoCitySPARQL(runningFromJar, codeSource);
		this.segmentFeature = new GeoFeature(false);
		this.segmentRegions  = new HashMap<Integer, List<Region>>();
		this.misclassifiedRegions = new ArrayList<>();
	}
	
	public void check(int matFileIndex) {
		
		loadData(matFileIndex);
		

		
		// check if two regions intersecting each other and have different elevation values?
		for (int segmentID : segmentRegions.keySet()) {
			for (Region region1 : segmentRegions.get(segmentID)) {
				for (Region region2 : segmentRegions.get(segmentID)) {				
					if (region1.getID() != region2.getID()) {
							if (region1.getPolygon().intersects(region2.getPolygon())) {
								
								if (region1.getPredictedLabelCode() == OntoCity.AirPlane.getLabelID()) {
									if (region2.getActualLabelCode() == OntoCity.PavedArea.getLabelID()) {
										suspicious.add(region2.getID());
										suspicious.add(region1.getID());
									}
									else if (region2.getActualLabelCode() == OntoCity.Water.getLabelID()) {
										suspicious.add(region2.getID());
										suspicious.add(region1.getID());
									}
									else if (region2.getActualLabelCode() == OntoCity.Car.getLabelID()) {
										suspicious.add(region2.getID());
										suspicious.add(region1.getID());
									}
									else if (region2.getActualLabelCode() == OntoCity.Ship.getLabelID()) {
										suspicious.add(region2.getID());
										suspicious.add(region1.getID());
									}
									else if (region2.getActualLabelCode() == OntoCity.Vegetation.getLabelID()) {
										suspicious.add(region2.getID());
										suspicious.add(region1.getID());
									}
									else if (region2.getActualLabelCode() == OntoCity.AirPlane.getLabelID()) {
										suspicious.add(region2.getID());
										suspicious.add(region1.getID());
									}
								}
								else if (region1.getPredictedLabelCode() == OntoCity.Car.getLabelID()) {
									if (region2.getActualLabelCode() == OntoCity.Water.getLabelID()) {
										suspicious.add(region2.getID());
										suspicious.add(region1.getID());
									}
									else if (region2.getActualLabelCode() == OntoCity.AirPlane.getLabelID()) {
										suspicious.add(region2.getID());
										suspicious.add(region1.getID());
									}
									else if (region2.getActualLabelCode() == OntoCity.Ship.getLabelID()) {
										suspicious.add(region2.getID());
										suspicious.add(region1.getID());
									}
									else if (region2.getActualLabelCode() == OntoCity.Vegetation.getLabelID()) {
										suspicious.add(region2.getID());
										suspicious.add(region1.getID());
									}
								}
								else if (region1.getPredictedLabelCode() == OntoCity.Ship.getLabelID()) {
									if (region2.getActualLabelCode() == OntoCity.AirPlane.getLabelID()) {
										suspicious.add(region2.getID());
										suspicious.add(region1.getID());
									}
									else if (region2.getActualLabelCode() == OntoCity.Ship.getLabelID()) {
										suspicious.add(region2.getID());
										suspicious.add(region1.getID());
									}
									else if (region2.getActualLabelCode() == OntoCity.Building.getLabelID()) {
										suspicious.add(region2.getID());
										suspicious.add(region1.getID());
									}
									else if (region2.getActualLabelCode() == OntoCity.PavedArea.getLabelID()) {
										suspicious.add(region2.getID());
										suspicious.add(region1.getID());
									}
								}
								
							}	
					}
				}
			}
		}
		
		
		for (Region misclassifiedRegion : misclassifiedRegions) {
			if ((misclassifiedRegion.getPredictedLabelCode() == OntoCity.Car.getLabelID()) &&
					misclassifiedRegion.getPolygon().getArea() > 100){
				suspicious.add(misclassifiedRegion.getID());
			}
			
			
			if ((misclassifiedRegion.getPredictedLabelCode() == OntoCity.AirPlane.getLabelID()) &&
					misclassifiedRegion.getPolygon().getArea() < 100){
				suspicious.add(misclassifiedRegion.getID());
			}
			
			if ((misclassifiedRegion.getPredictedLabelCode() == OntoCity.Ship.getLabelID())) {
				boolean isValidShip = false;
				for (int segmentID : segmentRegions.keySet()) {
					for (Region region1 : segmentRegions.get(segmentID)) {
						
						if (region1.getPolygon().intersects(misclassifiedRegion.getPolygon()) && 
								region1.getActualLabelCode() == OntoCity.Water.getLabelID()) {
							isValidShip = true;
							break;
						}
					}
					if(!isValidShip)
						suspicious.add(misclassifiedRegion.getID());
				}
			}
			
			if (misclassifiedRegion.getPredictedLabelCode() == OntoCity.Ship.getLabelID()) {
				for (int segmentID : segmentRegions.keySet()) {
					for (Region region1 : segmentRegions.get(segmentID)) {
						
						if (region1.getPolygon().intersects(misclassifiedRegion.getPolygon())) {
							if (region1.getActualLabelCode() == OntoCity.AirPlane.getLabelID() ||
									region1.getActualLabelCode() == OntoCity.Building.getLabelID() ||
									region1.getActualLabelCode() == OntoCity.Car.getLabelID() ||
									region1.getActualLabelCode() == OntoCity.PavedArea.getLabelID() ) {
								
							
								suspicious.add(region1.getID());
								suspicious.add(misclassifiedRegion.getID());
							}
						}
					}
				}
			}
			
			if (misclassifiedRegion.getPredictedLabelCode() == OntoCity.Car.getLabelID()) {
				for (int segmentID : segmentRegions.keySet()) {
					for (Region region1 : segmentRegions.get(segmentID)) {
						
						if (region1.getPolygon().intersects(misclassifiedRegion.getPolygon())) {
							if (region1.getActualLabelCode() == OntoCity.AirPlane.getLabelID() ||
									region1.getActualLabelCode() == OntoCity.Water.getLabelID() ||
									region1.getActualLabelCode() == OntoCity.Ship.getLabelID() ||
									region1.getActualLabelCode() == OntoCity.Vegetation.getLabelID() ) {
								
							
								suspicious.add(region1.getID());
								suspicious.add(misclassifiedRegion.getID());
							}
						}
					}
				}
			}
			
			if (misclassifiedRegion.getPredictedLabelCode() == OntoCity.AirPlane.getLabelID()) {
				for (int segmentID : segmentRegions.keySet()) {
					for (Region region1 : segmentRegions.get(segmentID)) {
						
						if (region1.getPolygon().intersects(misclassifiedRegion.getPolygon())) {
							if (region1.getActualLabelCode() == OntoCity.PavedArea.getLabelID() ||
									region1.getActualLabelCode() == OntoCity.Water.getLabelID() ||
									region1.getActualLabelCode() == OntoCity.Ship.getLabelID() ||
									region1.getActualLabelCode() == OntoCity.Vegetation.getLabelID() ) {
								
							
								suspicious.add(region1.getID());
								suspicious.add(misclassifiedRegion.getID());
							}
						}
					}
				}
			}
			
			if (misclassifiedRegion.getPredictedLabelCode() == OntoCity.Vegetation.getLabelID() &&
					misclassifiedRegion.getActualLabelCode() != misclassifiedRegion.getPredictedLabelCode()) {
				suspicious.add(misclassifiedRegion.getID());
			}
			
			if (misclassifiedRegion.getActualLabelCode() == OntoCity.Vegetation.getLabelID() &&
					misclassifiedRegion.getActualLabelCode() != misclassifiedRegion.getPredictedLabelCode()) {
				suspicious.add(misclassifiedRegion.getID());
			}
			
			if (misclassifiedRegion.getPredictedLabelCode() == OntoCity.Water.getLabelID() &&
					misclassifiedRegion.getActualLabelCode() != misclassifiedRegion.getPredictedLabelCode()) {
				suspicious.add(misclassifiedRegion.getID());
			}
			
			if (misclassifiedRegion.getActualLabelCode() == OntoCity.Water.getLabelID() &&
					misclassifiedRegion.getActualLabelCode() != misclassifiedRegion.getPredictedLabelCode()) {
				suspicious.add(misclassifiedRegion.getID());
			}
			
			if (misclassifiedRegion.getActualLabelCode() == OntoCity.Building.getLabelID() &&
					misclassifiedRegion.getActualLabelCode() == misclassifiedRegion.getPredictedLabelCode()) {
				non_suspicious.add(misclassifiedRegion.getID());
			}
			
			if (misclassifiedRegion.getActualLabelCode() == OntoCity.Car.getLabelID() &&
					misclassifiedRegion.getActualLabelCode() == misclassifiedRegion.getPredictedLabelCode()) {
				non_suspicious.add(misclassifiedRegion.getID());
			}
			
			if (misclassifiedRegion.getActualLabelCode() == OntoCity.AirPlane.getLabelID() &&
					misclassifiedRegion.getActualLabelCode() == misclassifiedRegion.getPredictedLabelCode()) {
				non_suspicious.add(misclassifiedRegion.getID());
			}
			
			if (misclassifiedRegion.getActualLabelCode() == OntoCity.Ship.getLabelID() &&
					misclassifiedRegion.getActualLabelCode() == misclassifiedRegion.getPredictedLabelCode()) {
				non_suspicious.add(misclassifiedRegion.getID());
			}
			
			if (misclassifiedRegion.getActualLabelCode() == OntoCity.AirPlane.getLabelID()) {
				elevation_vehicle.add(misclassifiedRegion.getID());
			}
			if (misclassifiedRegion.getActualLabelCode() == OntoCity.Car.getLabelID()) {
				elevation_vehicle.add(misclassifiedRegion.getID());
			}
			
			if (misclassifiedRegion.getActualLabelCode() == OntoCity.Building.getLabelID()) {
				elevation_building.add(misclassifiedRegion.getID());
			}
			
			if (misclassifiedRegion.getActualLabelCode() == OntoCity.PavedArea.getLabelID()) {
				elevation_pavement.add(misclassifiedRegion.getID());
			}
			if (misclassifiedRegion.getActualLabelCode() == OntoCity.Water.getLabelID()) {
				elevation_water.add(misclassifiedRegion.getID());
			}
			
		}
		
		
		
		for (int segmentID : segmentRegions.keySet()) {
			for (Region region1 : segmentRegions.get(segmentID)) {
				for (Region region2 : segmentRegions.get(segmentID)) {				
					if (region1.getID() != region2.getID()) {
							//if (region1.getPolygon().intersects(region2.getPolygon())) {
								
								if (region1.getActualLabelCode() == OntoCity.Building.getLabelID()) {
									if (GeometryAnalyser.getDirection(region1.getPolygon(), region2.getPolygon().getCentroid()) == Direction.NORTH_OF) {
										if (region2.getActualLabelCode() == OntoCity.PavedArea.getLabelID() ||
												region2.getActualLabelCode() == OntoCity.Ground.getLabelID() ||
												region2.getActualLabelCode() == OntoCity.Water.getLabelID() ||
												region2.getActualLabelCode() == OntoCity.Car.getLabelID() ||
												region2.getActualLabelCode() == OntoCity.AirPlane.getLabelID()) {
											shadow.add(region2.getID());
										}
										else if (region2.getActualLabelCode() == OntoCity.Building.getLabelID()){
											non_shadow.add(region2.getID());
										}
									}
									else {
										non_shadow.add(region2.getID());
									}
										
								}
							//}
					}
				}
			}
		}							
		
		save(matFileIndex);
		
	}
	private void save(int matFileIndex) {
		List<List<Integer>> indexLists = new ArrayList<>();
		indexLists.add(new ArrayList(shadow));
		indexLists.add(new ArrayList(non_shadow));
		indexLists.add(new ArrayList(suspicious));
		indexLists.add(new ArrayList(non_suspicious));
		indexLists.add(new ArrayList(elevation_building));
		indexLists.add(new ArrayList(elevation_vehicle));
		indexLists.add(new ArrayList(elevation_pavement));
		indexLists.add(new ArrayList(elevation_water));

		List<String> variableNames = new ArrayList<>();
		variableNames.add("Shadow");
		variableNames.add("Non_Shadow");
		variableNames.add("Suspicious");
		variableNames.add("Non_Suspicious");
		variableNames.add("Elev_Building");
		variableNames.add("Elev_Vehicle");
		variableNames.add("Elev_Pavement");
		variableNames.add("Elev_Water");
	
		MatlabRegionIndexMatrixCreator.saveRegionIndexIntoMatFile(indexLists, PATH_INDEX_MATH_FILE + "reasoning_output_" + Integer.toString(matFileIndex), variableNames);

	}
	
	/**
	 * Since ActualLabel has not been recorded in OntoCity we need to retrieve the actual label for each region by checking if the region is in the misclassified list or not
	 * @param region
	 * @return
	 */
	private int getActualLabel(Region region) {
		for (Region misclassifiedRegion : misclassifiedRegions) {
			if (region.getID() == misclassifiedRegion.getID())
				return misclassifiedRegion.getActualLabelCode();
		}
		
		// the region is not misclassified and therefore its actual label is equal to its predicted label
		return region.getPredictedLabelCode();
	}
	private void loadData(int matFileIndex) {
		System.out.println("loading region...");
		loadRegions();
		System.out.println("extracting misclassifications...");
		this.misclassifiedRegions = MisclassificationExtractor.extract(matFileIndex);
	}
}
