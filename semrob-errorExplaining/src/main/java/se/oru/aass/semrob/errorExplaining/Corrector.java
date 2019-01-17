package se.oru.aass.semrob.errorExplaining;

import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.query.QuerySolution;
import com.vividsolutions.jts.geom.Polygon;

import arq.update;
import se.oru.aass.semrob.geometry.E_RCC;
import se.oru.aass.semrob.geometry.GeoFeature;
import se.oru.aass.semrob.geometry.GeometryAnalyser;
import se.oru.aass.semrob.geometry.Region;
import se.oru.aass.semrob.matlabInterface.MatlabRegionIndexMatrixCreator;
import se.oru.aass.semrob.matlabInterface.singleTask.MisclassificationExtractor;
import se.oru.aass.semrob.ontology.OntoCity;
import se.oru.aass.semrob.ontology.OntoCityHandler;
import se.oru.aass.semrob.ontology.query.OntoCitySPARQL;


public class Corrector {

	private GeoFeature segmentFeature;
	private OntoCitySPARQL ontoCitySPARQL;
	private List<Region> misclassifiedRegions;
	// keeps all the regions for each segment
	private HashMap<Integer, List<Region>> segmentRegions;
	private static final String MISCLASSIFICATION_INFO_SHADOW = "S";
	private static final String MISCLASSIFICATION_INFO_SUSPICIOUS = "SU";
	private static final String MISCLASSIFICATION_INFO_BRIDGE = "B";
	
	private static final int ELEVATION_THRESHOLD_BUILDING = 8;
	
	private Set<Integer> suspicious = new HashSet<>();
	public Corrector(boolean runningFromJar, CodeSource codeSource) {
		// TODO Auto-generated constructor stub
		
		this.ontoCitySPARQL = new OntoCitySPARQL(runningFromJar, codeSource);
		this.segmentFeature = new GeoFeature(false);
		this.segmentRegions  = new HashMap<Integer, List<Region>>();
		this.misclassifiedRegions = new ArrayList<>();
	}
	
	private void test() {
		for (int segmentID : segmentRegions.keySet()) {
			for (Region region1 : segmentRegions.get(segmentID)) {
				System.out.println(region1.getPolygon().getArea());
				
				/*if (region1.getPredictedLabelCode() == OntoCity.RailRoad.getLabelID() ) {
					for (Region region2 : segmentRegions.get(segmentID)) {
						if (region2.getPredictedLabelCode() == OntoCity.Building.getLabelID()) {
							E_RCC rcc = GeometryAnalyser.getRCCRelation(region1.getPolygon(), region2.getPolygon());
							if (rcc.equals(E_RCC.EC)) {
								//suspicious.add(region2.)
							}
								
						}
					}
				}*/
			}
		}
	}
	
	private void getNewLabel() {
		
		HashMap<Integer, Set<Integer>> updatedLabelList = new HashMap<>();
		int misclassifedRegionCounter = 1;		
		for (Region misclassifiedRegion : misclassifiedRegions) {
			
			  //updatedLabelList.get(misclassifiedRegion.getMatlabIndex());
			
			Set<Integer> possibleLabels = new HashSet();
			
			///////// MIXED SHADOW
			
			if ((misclassifiedRegion.getPredictedLabelCode() == OntoCity.Building.getLabelID()) &&
					(misclassifiedRegion.getActualLabelCode() == OntoCity.Road.getLabelID()) &&
					(misclassifiedRegion.getRelativeElevation() < 3)) {
					//if (region.getPredictedLabelCode() == OntoCity.Building.getLabelID())
						// it will be about buildings that are misclassified as ground.
					
				possibleLabels.add(OntoCity.Road.getLabelID());
				possibleLabels.add(OntoCity.Ground.getLabelID());
			}
			
			if ((misclassifiedRegion.getPredictedLabelCode() == OntoCity.Road.getLabelID()) &&
					(misclassifiedRegion.getActualLabelCode() == OntoCity.Building.getLabelID()) &&
					(misclassifiedRegion.getRelativeElevation() > 2)) {
					//if (region.getPredictedLabelCode() == OntoCity.Building.getLabelID())
						// it will be about buildings that are misclassified as ground.
				possibleLabels.add(OntoCity.Building.getLabelID());
				possibleLabels.add(OntoCity.Ground.getLabelID());
			}
			
			
			if ((misclassifiedRegion.getPredictedLabelCode() == OntoCity.Ground.getLabelID()) &&
					(misclassifiedRegion.getActualLabelCode() == OntoCity.Building.getLabelID()) &&
					(misclassifiedRegion.getRelativeElevation() > 2)) {
				//if (region.getPredictedLabelCode() == OntoCity.Building.getLabelID())
				// it will be about buildings that are misclassified as ground.
				possibleLabels.add(OntoCity.Building.getLabelID());
			}
		
			if ((misclassifiedRegion.getPredictedLabelCode() == OntoCity.Ground.getLabelID()) &&
					(misclassifiedRegion.getActualLabelCode() == OntoCity.Road.getLabelID()) &&
					(misclassifiedRegion.getRelativeElevation() < 2)) {
				//	if (region.getPredictedLabelCode() == OntoCity.Building.getLabelID())
				// it will be about buildings that are misclassified as ground.
				possibleLabels.add(OntoCity.Road.getLabelID());
			}
			
			if ((misclassifiedRegion.getPredictedLabelCode() == OntoCity.Building.getLabelID()) &&
					(misclassifiedRegion.getActualLabelCode() == OntoCity.Building.getLabelID()) &&
					(misclassifiedRegion.getRelativeElevation() > 3)) {
				//	if (region.getPredictedLabelCode() == OntoCity.Building.getLabelID())
				// it will be about buildings that are misclassified as ground.
				possibleLabels.add(OntoCity.Building.getLabelID());
			}
			
			if ((misclassifiedRegion.getPredictedLabelCode() == OntoCity.Road.getLabelID()) &&
					(misclassifiedRegion.getActualLabelCode() == OntoCity.Road.getLabelID()) &&
					(misclassifiedRegion.getRelativeElevation() < 2)) {
				//	if (region.getPredictedLabelCode() == OntoCity.Building.getLabelID())
				// it will be about buildings that are misclassified as ground.
				possibleLabels.add(OntoCity.Road.getLabelID());

			}
			
			if ((misclassifiedRegion.getPredictedLabelCode() == OntoCity.Ground.getLabelID()) &&
					(misclassifiedRegion.getActualLabelCode() == OntoCity.Ground.getLabelID()) &&
					(misclassifiedRegion.getRelativeElevation() < 2)) {
				//	if (region.getPredictedLabelCode() == OntoCity.Building.getLabelID())
				// it will be about buildings that are misclassified as ground.
				possibleLabels.add(OntoCity.Road.getLabelID());
				possibleLabels.add(OntoCity.Ground.getLabelID());
			}
			
			if ((misclassifiedRegion.getPredictedLabelCode() == OntoCity.Building.getLabelID()) &&
					(misclassifiedRegion.getActualLabelCode() == OntoCity.Ground.getLabelID()) &&
					(misclassifiedRegion.getRelativeElevation() > 2)) {
					//if (region.getPredictedLabelCode() == OntoCity.Building.getLabelID())
						// it will be about buildings that are misclassified as ground.
				possibleLabels.add(OntoCity.Ground.getLabelID());
				possibleLabels.add(OntoCity.Building.getLabelID());
			}
			
			if ((misclassifiedRegion.getPredictedLabelCode() == OntoCity.Building.getLabelID()) &&
					(misclassifiedRegion.getActualLabelCode() == OntoCity.Ground.getLabelID()) &&
					(misclassifiedRegion.getRelativeElevation() < 2)) {
					//if (region.getPredictedLabelCode() == OntoCity.Building.getLabelID())
						// it will be about buildings that are misclassified as ground.
				possibleLabels.add(OntoCity.Road.getLabelID());

			}
			
			if ((misclassifiedRegion.getPredictedLabelCode() == OntoCity.Road.getLabelID()) &&
					(misclassifiedRegion.getActualLabelCode() == OntoCity.Ground.getLabelID()) &&
					(misclassifiedRegion.getRelativeElevation() > 2)) {
				//	if (region.getPredictedLabelCode() == OntoCity.Building.getLabelID())
				// it will be about buildings that are misclassified as ground.
				possibleLabels.add(OntoCity.Road.getLabelID());
			}
			
			if ((misclassifiedRegion.getPredictedLabelCode() == OntoCity.Water.getLabelID()) &&
					(misclassifiedRegion.getActualLabelCode() == OntoCity.Building.getLabelID()) &&
					(misclassifiedRegion.getRelativeElevation() > 2)) {
				//	if (region.getPredictedLabelCode() == OntoCity.Building.getLabelID())
				// it will be about buildings that are misclassified as ground.
				possibleLabels.add(OntoCity.Building.getLabelID());
			}
			
			
			updatedLabelList.put((int) misclassifiedRegion.getMatlabIndex(), possibleLabels);
			System.out.println("\n\nmisclassifedRegion:" + misclassifedRegionCounter + " misclassified as: " + OntoCity.getClassName(misclassifiedRegion.getPredictedLabelCode()) + ", ID: " + misclassifiedRegion.getMatlabIndex());
					
			misclassifedRegionCounter++;
			
		}
		
		/*System.out.print("misclassificationIndex = [");
		int i = 0;
		for (Integer index : candidates) {
			System.out.print(index );
			if (i < candidates.size() - 1)
				System.out.print(", ");
			i++;
		}
		System.out.println("];");*/
		
		//String path = "/home/marjan/MyWorx/Projects/SemanticRobot/SemRob/semmap-core/Code/Matlab/files/Neural-Symbolic/ClassificationResults/";
		//String path = "/home/marjan/MyWorx/Projects/SemanticRobot/SemRob/semmap-core/Code/Matlab/files/Neural-Symbolic/SWJ-Results/code/";
		String path = "/home/marjan/MyWorx/Projects/SemanticRobot/SemRob/semmap-core/Code/Matlab/files/Neural-Symbolic/SWJ-Results/new-dataset/code";

		MatlabRegionIndexMatrixCreator.saveRegionIndexIntoMatFile(updatedLabelList, path + "updatedLabels", "updatedLabels");

	}
	
	
	public void correct() {
		System.out.println("loading region...");
		loadRegions();
		System.out.println("extracting misclassifications...");
		this.misclassifiedRegions = MisclassificationExtractor.extract(1);
		
		getNewLabel();
		//getStatistics(spatialRelations);
	}
	
	private void loadRegions () {
		List<QuerySolution> queryResult = ontoCitySPARQL.getAllRegionsWithSegments();
		
		for (QuerySolution solution : queryResult) {
	
			int regionID = getFeatureID(solution.getResource("regionIndivName").toString(), OntoCityHandler.PREFIX_REGION_NAME, 1);
			int segmentID = getFeatureID(solution.getResource("segmentIndivName").toString(), OntoCityHandler.PREFIX_SEGMENT_NAME, 1);
			Polygon polygon = GeometryAnalyser.convertToPolygon(solution.getLiteral("regionBoundary").toString());
			int regionType = OntoCity.getLabelCode(solution.getResource("region").toString().split(OntoCitySPARQL.URI_ENTITY_SPLITTER)[1]);
			Region region = new Region(regionID, polygon, regionType, regionType, -1, -1, -1);
				
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
}
