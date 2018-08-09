package se.oru.aass.semrob.motionPlanning;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;

import se.oru.aass.semrob.geometry.GeometryAnalyser;
import se.oru.aass.semrob.motionPlanning.collisionDetection.Box;
import se.oru.aass.semrob.motionPlanning.collisionDetection.CollisionDetector;
import se.oru.aass.semrob.motionPlanning.collisionDetection.CollisionDetector.CollisionInfo;
import se.oru.aass.semrob.motionPlanning.collisionDetection.Line;
import se.oru.aass.semrob.motionPlanning.entity.Configuration;
import se.oru.aass.semrob.motionPlanning.entity.Configuration.Particle;
import se.oru.aass.semrob.motionPlanning.entity.Tree;
import se.oru.aass.semrob.motionPlanning.sampling.RandomConfigurationGenerator;
import se.oru.aass.semrob.motionPlanning.smoothing.PathSmoother;
import se.oru.aass.semrob.ontology.OntoCity;
import se.oru.aass.semrob.server.infoProvider.InfoProvider;
import se.oru.aass.semrob.setting.NumericRange;

public class RRTMotionPlanner {
		
	public static final int OFFSET_ELEVATION_VALUE;
	private static final int SOAR_ELEVATION_VALUE;
	private static final int SOAR_CHECKING_LIMIT;
	private static final int SOAR_XY_BOUNDARY_OFFSET;
	
	public static final int PRIORITY_INDEX_NOCONSTRAINT = 0;
	public static final int PRIORITY_INDEX_WITHOUT_SCHOOL_HOSPITAL = 1;
	public static final int PRIORITY_INDEX_WITHOUT_CITYHALL = 2;
	public static final int PRIORITY_INDEX_WITHOUT_WATER = 3;
	private static final int PATH_CHECKING_LIMIT;
	
	
	private static final int VERY_HIGH_ELEVATION = 10000;
	
	private NumericRange xRange;
	private NumericRange yRange;
	private NumericRange zRange;
	private Tree tree;
	private Random random;
	private int priorityIndex;
	private Coordinate originalSource;
	private Coordinate originalDestination;
	
	static {
		Setting setting = new Setting();
		Properties prop = setting.getConfiguration(Setting.CONFIG_FILE_NAME);
		PATH_CHECKING_LIMIT = Integer.parseInt(prop.getProperty("path.checking.limit"));
		
		OFFSET_ELEVATION_VALUE = Integer.parseInt(prop.getProperty("offset.elevation.value"));
		SOAR_ELEVATION_VALUE  = Integer.parseInt(prop.getProperty("soar.elevation.value"));
		SOAR_CHECKING_LIMIT = Integer.parseInt(prop.getProperty("soar.checking.limit"));
		SOAR_XY_BOUNDARY_OFFSET = Integer.parseInt(prop.getProperty("soar.xy.bounding.offset"));

	}
	public RRTMotionPlanner(Coordinate source, Coordinate destination, NumericRange xRange, NumericRange yRange, NumericRange zRange, int priorityIndex) {
		//this.xRange = xRange;
		//this.yRange = yRange;
		
		this.xRange = new NumericRange(4750, 7500);
		this.yRange = new NumericRange(5750, 8500);
		
		this.zRange = zRange;
		this.priorityIndex = priorityIndex;
		this.originalSource = source;
		this.originalDestination = destination;
		random = new Random();
		random.setSeed(123456789);
	}	 
	private void initializeTree() {
		this.tree = new Tree();
	}
	
	public List<Coordinate> generatePath() {
	
		// the z-value of both source and destination are updated to the elevation of their holding regions + an offset value
		Coordinate newSource = new Coordinate(originalSource.x, originalSource.y, RRTMotionPlanner.getElevationOfHoldingRegion((int) originalSource.x, (int) originalSource.y).getRegionAbsoluteElevation() + OFFSET_ELEVATION_VALUE);
		Coordinate newDestination = new Coordinate (originalDestination.x, originalDestination.y, RRTMotionPlanner.getElevationOfHoldingRegion((int) originalDestination.x, (int) originalDestination.y).getRegionAbsoluteElevation() + OFFSET_ELEVATION_VALUE);

		
		if (isWithin2DRange(newSource) && isWithin2DRange(newDestination)) {
			if (hasVaidElevation(newSource) &&	hasVaidElevation(newDestination)) {
				if(hasValidLabelRegion(newSource, priorityIndex) && hasValidLabelRegion(newDestination, priorityIndex)) {
					NumericRange xRange = this.xRange;
					NumericRange yRange = this.yRange;
					
	//				if (priorityIndex == PRIORITY_INDEX_NORMAL){
	//					xRange = getUpdatedRange(this.xRange, newSource.x, newDestination.x); 		
	//					yRange = getUpdatedRange(this.yRange, newSource.y, newDestination.y);
	//				}
	//				else {
	//					xRange = this.xRange;//getUpdatedRange(this.xRange, source.x, destination.x); 		
	//					yRange = this.yRange;//getUpdatedRange(this.yRange, source.y, destination.y);
	//				}
					
					//return generatePath(this.originalSource, this.originalDestination, xRange, yRange);
					
					List<Coordinate> points = generatePath(newSource, newDestination, xRange, yRange);
					if (points != null) {
						points = editSourceDestination(points);
						if (points.size() == 2)
							return points;
						else
							return getSmoothPath(points);
					}
					else {
						//TODO Hacking the planner for situations where the source and destination are very close and there is no path!
						
						
						System.out.println("path is not found!");
						return null;
					}
				
				}
				else {
					System.out.println("check source or destination");
					return null;
				}
			}
			else {
				System.out.println("check source or destination");
				return null;
			}
		}
		else {
			System.out.println("check source or destination");
			return null;
		}
	}
	
	private List<Coordinate>  editSourceDestination(List<Coordinate> points){
		if (points != null) {
			Coordinate sourceInPath = points.get(points.size() - 1);
			Coordinate destinationInPath = points.get(0);
			if (sourceInPath.z != originalSource.z)
				points.add(originalSource);
			if (destinationInPath.z != originalDestination.z)
				points.add(0, originalDestination);
			return points;
		}
		else return null;
	}

	private List<Coordinate> generatePath(Coordinate source, Coordinate destination, NumericRange xRange, NumericRange yRange) {
		List<Coordinate> points = null;
		
		points = generatePath(getConfiguration(source), getConfiguration(destination), xRange, yRange);
		
		// check if there is no path between source and destination try to soar source and destination
		if(points == null) {
					
			// if either source or destination is not connected to their soared coordinate, they are not reachable
			Configuration soaredSource = soar(source);
			if (soaredSource == null)
				return null;
			
			Configuration soaredDestination = soar(destination);
			if (soaredDestination == null)
				return null;
			
			// otherwise we replace the source and destination with their soared coordinates and try to find path between them. At the end, we add the original source and destination to the tree 
			
			points = generatePath(soaredSource, soaredDestination, xRange, yRange);
			if (points != null) { // the path between the soaredSource and soaredDestination has been found.
				if (points.size() >= 2) {
					
					points.add(0, destination); // first item will be the destination
					points.add(source); // last item will be the source
				}
				else return null;
			}
		}
		return points;
	}
		
	
	
	/**
	 * This method tries to find a path between the given source and destination
	 * @param source
	 * @param destination
	 * @param xRange
	 * @param yRange
	 * @return
	 */
	private List<Coordinate> generatePath(Configuration source, Configuration destination, NumericRange xRange, NumericRange yRange) {
		int pathCheckingCounter = 0;
		int destinationNodeIndex = Tree.INVALID_NODE_INDEX;
		
		initializeTree();
		//adding source to the tree and check if it is already connected to the destination or not
		int newConfigNodeIndex = tree.addConfiguration(Tree.TREE_ROOT_PARENT_INDEX, source);
		destinationNodeIndex = checkPathToDestination(newConfigNodeIndex, destination);
		
								
		while (destinationNodeIndex == Tree.INVALID_NODE_INDEX && 
				pathCheckingCounter < PATH_CHECKING_LIMIT) {
			
			Configuration newConfig = RandomConfigurationGenerator.getSampleConfiguration(this.random, xRange, yRange, this.zRange);
			if (newConfig != null) {
				int nearestConfigNodeIndex = tree.getNearestConfigurationIndex(newConfig);
				if (nearestConfigNodeIndex != Tree.INVALID_NODE_INDEX) {
					pathCheckingCounter ++;
					
					if (isConnectible(tree.getConfigurationNode(nearestConfigNodeIndex), newConfig, priorityIndex)) {
						newConfigNodeIndex = tree.addConfiguration(nearestConfigNodeIndex, newConfig);
						destinationNodeIndex = checkPathToDestination(newConfigNodeIndex, destination);
					}
					else {
						System.out.println("collision with the sample detetcted!  checkingCounter = " + pathCheckingCounter);
					}
				}
				else {
					System.out.println("Repeatitive sample! checkingCounter = " + pathCheckingCounter);
				}
			}
			else {
				System.out.println("Invalid sample! checkingCounter = " + pathCheckingCounter);
			}
		}
		
		if (destinationNodeIndex != Tree.INVALID_NODE_INDEX) {
			return tree.getPath(destinationNodeIndex);
		}
		else {
			return null;
		}
	}
	
	/**
	 * This method tries to find a configuration above the given configuration located within the biggest bounding box holding the coordinate
	 * @param configuration
	 * @return
	 */
	private Configuration soar(Coordinate coordinate) {
		
//		Configuration configuration = getConfiguration(new Coordinate(coordinate.x, coordinate.y, coordinate.z + SOAR_ELEVATION_VALUE));
//		return configuration;
		
		Configuration configuration = getConfiguration(coordinate);
		Envelope envelope = getLarggestEnvelopeHoldingCoordinate(coordinate);
		envelope = GeometryAnalyser.extendEnvelope(envelope, SOAR_XY_BOUNDARY_OFFSET).getEnvelopeInternal();
		envelope = setBoundary(envelope, this.xRange, this.yRange);
			
		int soarSamplingCounter = 0;
		while(soarSamplingCounter < SOAR_CHECKING_LIMIT) {
			Configuration newConfig;
			// for the first time, just check and see if it is possible to only increase the z-value 
			if (soarSamplingCounter == 0) {
				newConfig = getConfiguration(new Coordinate(coordinate.x, coordinate.y, coordinate.z + SOAR_ELEVATION_VALUE));
			}
			else {
				newConfig = RandomConfigurationGenerator.getSampleConfiguration(this.random, 
						new NumericRange(envelope.getMinX(), envelope.getMaxX()), 
						new NumericRange(envelope.getMinY(), envelope.getMaxY()),
						new NumericRange(coordinate.z + SOAR_ELEVATION_VALUE, coordinate.z + SOAR_ELEVATION_VALUE));
			}
			
			if (newConfig != null) {
				if (isConnectible(newConfig, configuration, this.priorityIndex)) {
					return newConfig;
				}
			}
			soarSamplingCounter ++;
		}
		return null;
	}
	
	private static Envelope getLarggestEnvelopeHoldingCoordinate(Coordinate coordinate) {
		Envelope envelope = new Envelope((int) coordinate.x, (int) coordinate.x, (int) coordinate.y, (int) coordinate.y);
		List<Integer> segmentIDs = InfoProvider.segmentFeature.getBoundingBoxHolder().getBoundingBox((int) coordinate.x, (int) coordinate.y);	
		Set<Integer> regionIDs = InfoProvider.getRegionIDsContainingPoint(segmentIDs, (int) coordinate.x, (int) coordinate.y);
		if (regionIDs.size() > 0) {
			for (int regionID : regionIDs) {
				Envelope currentEnvelope = InfoProvider.regionFeature.getRegionInfos().get(regionID).getGeometry().getEnvelopeInternal();
				if (currentEnvelope.getArea() > envelope.getArea())
					envelope = currentEnvelope;
			}
		}
		return envelope;
	}
	
	private List<Coordinate> getSmoothPath(List<Coordinate> points) {
		PathSmoother pathSmoother = new PathSmoother(points, this.random, priorityIndex);
		return pathSmoother.getSmoothPath();
		
	}
	
	private boolean isWithin2DRange(Coordinate coordinate) {
		if (coordinate.x >= xRange.getMinValue() &&
				coordinate.x <= xRange.getMaxValue() &&
				coordinate.y >= yRange.getMinValue() &&
				coordinate.y <= yRange.getMaxValue())
			return true;
		else return false;
	}
	
	
	private static NumericRange getUpdatedRange(NumericRange originalRange, double minValue, double maxValue) {
		if (maxValue < minValue) {
			double temp = maxValue;
			maxValue = minValue;
			minValue = temp;
		}
		double distance = maxValue - minValue;
		int offset;
		if (0 <= distance && distance <= 3000)
			offset = 3000;
		if (3000 < distance && distance <= 6000)
			offset = 2000;
		else 
			offset = 1000;
		
		
		minValue = minValue - offset;
		if (minValue < originalRange.getMinValue())
			minValue = originalRange.getMinValue();
			
		maxValue = maxValue + offset;
		if (maxValue > originalRange.getMaxValue())
			maxValue = originalRange.getMaxValue();
		
		
		return new NumericRange(minValue, maxValue);
	}
	
	private static boolean isConnectible (Configuration configuration1, Configuration configuration2, int priorityIndex) {
		return isConnectible(configuration1.getParticles().get(0).getCoordinate(), configuration2.getParticles().get(0).getCoordinate(), priorityIndex);
	}
	

	public static boolean isConnectible (Coordinate coordinate1, Coordinate coordinate2, int priorityIndex) {
		boolean isConnectible = true;
		//Configuration configuration1 = tree.getConfigurationNode(configurationNodeIndex);
		CollisionDetector obj = new CollisionDetector();
		Line line = new Line(coordinate1, coordinate2);
		
		Envelope envelope = new Envelope();
		envelope = getCoveringEnvelope(envelope, coordinate1);
		envelope = getCoveringEnvelope(envelope,  coordinate2);
		
		// ge all the regions intersecting with the envelope
		List<Integer> regionIDs = InfoProvider.regionFeature.getBoundingBoxHolder().findIntersectingBoundingBox((int) envelope.getMinX(), (int) envelope.getMinY(), (int) envelope.getMaxX(), (int) envelope.getMaxY());
		for (int regionID : regionIDs) {
							
			int regionLabelCode = InfoProvider.regionFeature.getRegionInfos().get(regionID).getLabelCode();
			int regionElevation = (int) InfoProvider.regionFeature.getRegionInfos().get(regionID).getAbsoluteElevation();
			if (priorityIndex == PRIORITY_INDEX_WITHOUT_CITYHALL) {
				if (regionLabelCode == OntoCity.CityHall.getLabelID()) {
					regionElevation = VERY_HIGH_ELEVATION;
				}
				
			}
			else if (priorityIndex == PRIORITY_INDEX_WITHOUT_SCHOOL_HOSPITAL) {
				if (regionLabelCode == OntoCity.Hospital.getLabelID() ||
						regionLabelCode == OntoCity.School.getLabelID()) {
					regionElevation = VERY_HIGH_ELEVATION;
				}
			}
			else if (priorityIndex == PRIORITY_INDEX_WITHOUT_WATER) {
				if (regionLabelCode == OntoCity.Water.getLabelID()) {
					regionElevation = VERY_HIGH_ELEVATION;
				}
			}
						
			Envelope envelopeRegion = InfoProvider.regionFeature.getRegionInfos().get(regionID).getGeometry().getEnvelopeInternal();
			Box box = new Box((int) envelopeRegion.getMinX(), (int) envelopeRegion.getMinY(), (int) envelopeRegion.getMaxX(), (int) envelopeRegion.getMaxY(), regionElevation);
			
			CollisionInfo collisionInfo = obj.hasCollision(line, box);
			boolean hasCollision = collisionInfo.getCollisionStatus();
			
			if(hasCollision) {
				isConnectible = false;
				break;
			}	
		}
		
		return isConnectible;
	}
	
	/**
	 * This method expands the given envelope to also covers all the regions holding the given point
	 * @param configuration
	 * @return
	 */
	private static Envelope getCoveringEnvelope(Envelope envelope, Coordinate coordination) {
		int x = (int) coordination.x;
		int y = (int) coordination.y;
		//making sure if the envelope covers the point (in case there is no region covering the point)
		envelope = GeometryAnalyser.getCoveringBouningBox(envelope, new GeometryFactory().createPoint(coordination));
		
		Set<Integer> regionIDs = InfoProvider.getRegionIDsContainingPoint(InfoProvider.segmentFeature.getBoundingBoxHolder().getBoundingBox(x, y), x, y);
		for (int regionID : regionIDs) {
			envelope = GeometryAnalyser.getCoveringBouningBox(envelope, InfoProvider.regionFeature.getRegionInfos().get(regionID).getGeometry());
		}
		return envelope;
	}
	
	/**
	 * This method checks to see if there is a path to the destination from the given nodeIndex. 
	 * And if there is, adds the destination to the tree and returns back its index
	 * @param nodeIndex
	 * @param destination
	 * @return
	 */
	private int checkPathToDestination (int nodeIndex, Configuration destination) {
		if(isConnectible(tree.getConfigurationNode(nodeIndex), destination, priorityIndex))
			return tree.addConfiguration(nodeIndex, destination);
			
		else return Tree.INVALID_NODE_INDEX;
	}
	
	
	/**
	 * This method retrieves the maximum elevation values of regions holding the given point. It should normally be only one region that 
	 * holds the point. However, in case of exceptions, we check and return the maximum elevation value.
	 * @param x
	 * @param y
	 * @return
	 */
	public static RegionElevationValue getElevationOfHoldingRegion(int x, int y) {
		//List<Integer> segmentIDs = InfoProvider.segmentFeature.getBoundingBoxHolder().getBoundingBox(x, y);	
		//Set<Integer> regionIDs = InfoProvider.getRegionIDsContainingPoint(segmentIDs, x, y);
		
		// instead of regions we check all the bounding boxes of regions that holding the point
		List<Integer> regionIDs = InfoProvider.regionFeature.getBoundingBoxHolder().getBoundingBox(x, y);
		
		int selectedRegionID = -1;
		
		if (regionIDs.size() > 0) {
			double elevation = -1;
			selectedRegionID = -1;
			for (int regionID : regionIDs) {
				//double value =  InfoProvider.regionFeature.getRegionInfos().get(regionID).getAbsoluteElevation();
				//double value =  InfoProvider.regionFeature.getRegionInfos().get(regionID).getRelativeElevation();
				double value =  InfoProvider.regionFeature.getRegionInfos().get(regionID).getAbsoluteElevation();
				if (value > elevation) {
					elevation = value;
					selectedRegionID = regionID;
				}
			}
		}
		else {
			// TODO Hacking the situation where there is no region!
			//selectedRegionID = -1;
			return new RegionElevationValue(36, 20);
		}
		
		if (selectedRegionID == -1)
			return null;
		else {
			return new RegionElevationValue(InfoProvider.regionFeature.getRegionInfos().get(selectedRegionID).getAbsoluteElevation(), 
					InfoProvider.regionFeature.getRegionInfos().get(selectedRegionID).getRelativeElevation());
		}
	}
	
	private static boolean hasValidLabelRegion(Coordinate coordinate, int priorityIndex) {
		if (priorityIndex == PRIORITY_INDEX_NOCONSTRAINT)
			return true;
		else {
			int x = (int) coordinate.x;
			int y = (int) coordinate.y;
			List<Integer> segmentIDs = InfoProvider.segmentFeature.getBoundingBoxHolder().getBoundingBox(x, y);	
			Set<Integer> regionIDs = InfoProvider.getRegionIDsContainingPoint(segmentIDs, x, y);
			if (regionIDs.size() > 0) {
				for (int regionID : regionIDs) {
					int regionLabelCode = InfoProvider.regionFeature.getRegionInfos().get(regionID).getLabelCode();
					if (priorityIndex == PRIORITY_INDEX_WITHOUT_CITYHALL) {
						if (regionLabelCode == OntoCity.CityHall.getLabelID())
							return false;
					}
					else if (priorityIndex == PRIORITY_INDEX_WITHOUT_SCHOOL_HOSPITAL) {
						if (regionLabelCode == OntoCity.Hospital.getLabelID() || regionLabelCode == OntoCity.School.getLabelID())
							return false;
					}	
					else if (priorityIndex == PRIORITY_INDEX_WITHOUT_WATER) {
						if (regionLabelCode == OntoCity.Water.getLabelID())
							return false;
					}
				}
				return true;
			}
			return true;
		}
	}
	
	/**
	 * This method checks the z-value of the given coordinate to see if it is valid and also just above the height of the region holding the point or not
	 * @param coordinate
	 * @param zRange
	 * @return
	 */
	private boolean hasVaidElevation (Coordinate coordinate) {
//		/*if (isWithinLegalElevationRange((int) coordinate.z, zRange) && isLocatedAboveRegion(coordinate))
//			return true;
//		else return false;*/
//		
//		
//		/*The given coordinate has the z-value measured from the sea-floor basis line. In order to see if it is located within the legal elevation range, we have to
//		 * find the relative and absolute elevation values of the region holding the point in 2-D. Then calculate the elevation value of the coordinate from the ground.
//		 */
//		RegionElevationValue regionElevationValue = getElevationOfHoldingRegion((int) coordinate.x, (int) coordinate.y);
//		double difference = regionElevationValue.getRegionAbsoluteElevation() - regionElevationValue.getRegionRelativeElevation();
//		
//		if (isWithinLegalElevationRange(coordinate.z, zRange.addValueToRange(difference)) && 
//				coordinate.z + OFFSET_ELEVATION_VALUE >= regionElevationValue.getRegionAbsoluteElevation()  // checking if the coordinate has located above the region
//			)
//			return true;
//		else return false;
		
		return true;
	}
	
	public static double getDistance (Coordinate coord1, Coordinate coord2) {
		return Math.sqrt( Math.pow((coord1.x-coord2.x), 2) + Math.pow((coord1.y-coord2.y), 2) + Math.pow((coord1.z-coord2.z), 2));
	}

	
	public static class RegionElevationValue {
		private double regionAbsoluteElevation;
		private double regionRelativeElevation;
		public RegionElevationValue(double regionAbsoluteElevation, double regionRelativeElevation) {
			this.regionAbsoluteElevation = regionAbsoluteElevation;
			this.regionRelativeElevation = regionRelativeElevation;
		}
		public double getRegionAbsoluteElevation() {
			return regionAbsoluteElevation;
		}
		public double getRegionRelativeElevation() {
			return regionRelativeElevation;
		}
	}
	
	public static Configuration getConfiguration(Coordinate coordinate) {
		List<Particle> particles = new ArrayList<>();
		particles.add(new Particle(coordinate));
		return new Configuration(particles);
	}
	
	private static Envelope setBoundary(Envelope envelope, NumericRange xRange, NumericRange yRange) {
		double minX = envelope.getMinX();
		double minY = envelope.getMinY();
		double maxX = envelope.getMaxX();
		double maxY = envelope.getMaxY();
		if (minX < xRange.getMinValue())
			minX = xRange.getMinValue();
		if (maxX > xRange.getMaxValue())
			maxX = xRange.getMaxValue();
		if (minY < yRange.getMinValue())
			minY = yRange.getMinValue();
		if (maxY > yRange.getMaxValue())
			maxY = yRange.getMaxValue();
		
		return new Envelope(minX, maxX, minY, maxY);
	}
}