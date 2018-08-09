package se.oru.aass.semrob.ontology;


import java.security.CodeSource;
import java.util.List;
import java.util.Properties;


import se.oru.mpi.ontology.Ontology;
import se.oru.aass.semrob.ontology.pattern.DUL;
import se.oru.aass.semrob.ontology.pattern.OntoCityEventPattern;
import se.oru.aass.semrob.ontology.pattern.OpenGisGEOPattern;
import se.oru.aass.semrob.ontology.pattern.OpenGisGMLPattern;

public class OntoCityHandler extends Ontology {
	private static Properties prop;
	

	
	public static final String INDIV_NAME_SPLITTER = "_";
	public static final String OBJ_PROPERTY_NAME_SPLITTER = "_";
	public static final String PREFIX_REGION_NAME = "r" + INDIV_NAME_SPLITTER;
	private static final String PREFIX_POLYGON_NAME = "p" + INDIV_NAME_SPLITTER;
	private static final String PREFIX_LINESTRING_NAME = "ls" + INDIV_NAME_SPLITTER;
	private static final String PREFIX_EVENT_NAME = "e" + INDIV_NAME_SPLITTER;
	public static final String PREFIX_EVENT_AREA_NAME = "a" + INDIV_NAME_SPLITTER;
	public static final String PREFIX_SEGMENT_NAME = "s" + INDIV_NAME_SPLITTER;
	public static final String PREFIX_RECTANGLE_NAME = "rec" + INDIV_NAME_SPLITTER;
	public static final String PREFIX_ENRICHED_BUILDING_NAME = "eb" + INDIV_NAME_SPLITTER;

		
	
	static {
		prop = new Setting().getConfiguration(Setting.CONFIG_FILE_NAME);
		OntoCity.setPrefixManager(prop.getProperty("ontology.uri.ontocity"));
		DUL.setPrefixManager(prop.getProperty("ontology.uri.dul"));
		OntoCityEventPattern.setPrefixManager(prop.getProperty("ontology.uri.ontocity_event"));
		OpenGisGEOPattern.setPrefixManager(prop.getProperty("ontology.uri.opengis.geo"));
		OpenGisGMLPattern.setPrefixManager(prop.getProperty("ontology.uri.opengis.gml"));
	}
	
	public OntoCityHandler (String ontologyPath, boolean runningFromJar, CodeSource codeSource)  {
		super(ontologyPath, runningFromJar, codeSource);
	}
	
	public OntoCityHandler (boolean runningFromJar, CodeSource codeSource)  {
		super(prop.getProperty("ontology.ontocity.file.path"), runningFromJar, codeSource);
	}
	
	

	public void addSegment (int segmentID, String segmentBoundary) {
		String segmentIndivName = generateIndividualName(PREFIX_SEGMENT_NAME, segmentID);
		addIndividual(OntoCity.Segment.getEntityURI(), 
				OntoCity.getEntityURI(segmentIndivName));
		
		String rectangleIndivName = generateIndividualName(PREFIX_RECTANGLE_NAME, segmentID);
		addIndividual(OpenGisGMLPattern.Rectangle.getEntityURI(), 
				OntoCity.getEntityURI(rectangleIndivName));
		
		addDataPropertyAxiom(OntoCity.getEntityURI(rectangleIndivName), 
				OpenGisGEOPattern.asWKT.getEntityURI(), 
				segmentBoundary);
		
		addObjectPropertyAxiom(OntoCity.getEntityURI(segmentIndivName), 
				OntoCity.getEntityURI(rectangleIndivName), 
				OpenGisGEOPattern.hasGeometry.getEntityURI());	
	}
	
	public void addRegion (int regionID, int predictedLabelCode, double precision, double absoluteElevation, double relativeElevation, 
			String regionBoundary, List<Integer> segments) {

		String className;
		className = OntoCity.getClassName(predictedLabelCode);
		
		if (className != null) {
		
			// adding the region individual
			String regionIndivName = generateIndividualName(PREFIX_REGION_NAME, regionID);	
			
			
			addIndividual(OntoCity.getEntityURI(className), 
						OntoCity.getEntityURI(regionIndivName));
			
			addDataPropertyAxiom(OntoCity.getEntityURI(regionIndivName), 
					OntoCity.hasPrecision.getEntityURI(), 
					precision);
			
			addDataPropertyAxiom(OntoCity.getEntityURI(regionIndivName), 
					OntoCity.hasAbsoluteElevationValue.getEntityURI(), 
					absoluteElevation);
			
			addDataPropertyAxiom(OntoCity.getEntityURI(regionIndivName), 
					OntoCity.hasRelativeElevationValue.getEntityURI(), 
					relativeElevation);
			
			// adding the polygon (geometry) individual
			String polygonIndivName = generateIndividualName(PREFIX_POLYGON_NAME, regionID);	
			addIndividual(OpenGisGMLPattern.Polygon.getEntityURI(), 
					OntoCity.getEntityURI(polygonIndivName));
			
			
			addDataPropertyAxiom(OntoCity.getEntityURI(polygonIndivName), 
					OpenGisGEOPattern.asWKT.getEntityURI(), 
					regionBoundary);
			
			//relating the added region to the added polygon
			addObjectPropertyAxiom(OntoCity.getEntityURI(regionIndivName), 
					OntoCity.getEntityURI(polygonIndivName),
					OpenGisGEOPattern.hasGeometry.getEntityURI());
			
			addFeatureSegments (regionIndivName, segments);
			
			
		}
	}
	
	public void addEnrichedBuilding (String buildingType, int regionID, String regionBoundary) {

		// adding the region individual
		String regionIndivName = generateIndividualName(PREFIX_ENRICHED_BUILDING_NAME, regionID);	
		
		
		addIndividual(OntoCity.getEntityURI(buildingType), 
				OntoCity.getEntityURI(regionIndivName));
		
		// adding the polygon (geometry) individual
		String polygonIndivName = generateIndividualName(PREFIX_POLYGON_NAME, regionID);	
			addIndividual(OpenGisGMLPattern.Polygon.getEntityURI(), 
				OntoCity.getEntityURI(polygonIndivName));
			
			
		addDataPropertyAxiom(OntoCity.getEntityURI(polygonIndivName), 
				OpenGisGEOPattern.asWKT.getEntityURI(), 
				regionBoundary);
		
		//relating the added region to the added polygon
		addObjectPropertyAxiom(OntoCity.getEntityURI(regionIndivName), 
				OntoCity.getEntityURI(polygonIndivName),
				OpenGisGEOPattern.hasGeometry.getEntityURI());
		
	}		
	private void addFeatureSegments (String featureIndivName, List<Integer> segments) {
		for (Integer segment : segments) {
			String segmentIndivName = generateIndividualName(PREFIX_SEGMENT_NAME, segment);
			addObjectPropertyAxiom(OntoCity.getEntityURI(featureIndivName),
					OntoCity.getEntityURI(segmentIndivName),
					OpenGisGEOPattern.sfOverlaps.getEntityURI());
		}
	}
		
	public void addEventLineArea (int eventID, int areaID, String areaBoundary) {
		String eventIndivName = generateIndividualEventName (eventID, OntoCity.Flood.toString());
		String areaIndivName = generateIndividualName(PREFIX_EVENT_AREA_NAME, eventID, areaID);
		String lineStringIndivName = generateIndividualName(PREFIX_LINESTRING_NAME, eventID, areaID);
		
		addIndividual(OntoCityEventPattern.Area.getEntityURI(),
				OntoCity.getEntityURI(areaIndivName));
		
		addIndividual(OpenGisGMLPattern.LineString.getEntityURI(), 
				OntoCity.getEntityURI(lineStringIndivName));
				
		addDataPropertyAxiom(OntoCity.getEntityURI(lineStringIndivName), 
				OpenGisGEOPattern.asWKT.getEntityURI(),
				areaBoundary);
		
		addObjectPropertyAxiom(OntoCity.getEntityURI(areaIndivName),
				OntoCity.getEntityURI(lineStringIndivName),
				OpenGisGEOPattern.hasGeometry.getEntityURI());
		
		addObjectPropertyAxiom(OntoCity.getEntityURI(eventIndivName),
				OntoCity.getEntityURI(areaIndivName),
				DUL.associatedWith.getEntityURI());
		
	}
	
	public void addEventArea (int eventID, int areaID, String areaBoundary) {
		String eventIndivName = generateIndividualEventName (eventID, OntoCity.Flood.toString());
		String areaIndivName = generateIndividualName(PREFIX_EVENT_AREA_NAME, eventID, areaID);
		String polygonIndivName = generateIndividualName(PREFIX_POLYGON_NAME, eventID, areaID);
		
		addIndividual(OntoCityEventPattern.Area.getEntityURI(), 
				OntoCity.getEntityURI(areaIndivName));
		
		addIndividual(OpenGisGMLPattern.Polygon.getEntityURI(), 
				OntoCity.getEntityURI(polygonIndivName));
				
		addDataPropertyAxiom(OntoCity.getEntityURI(polygonIndivName),
				OpenGisGEOPattern.asWKT.getEntityURI(),
				areaBoundary);
		
		addObjectPropertyAxiom(OntoCity.getEntityURI(areaIndivName),
				OntoCity.getEntityURI(polygonIndivName),
				OpenGisGEOPattern.hasGeometry.getEntityURI());
		
		addObjectPropertyAxiom(OntoCity.getEntityURI(eventIndivName), 
				OntoCity.getEntityURI(areaIndivName),
				DUL.associatedWith.getEntityURI());
	}
	
	public void addEventAreaRegion(int regionID, int eventID, int areaID) {
		String areaIndivName = generateIndividualName(PREFIX_EVENT_AREA_NAME, eventID, areaID);
		String regionIndivName = generateIndividualName(PREFIX_REGION_NAME, regionID);
		addObjectPropertyAxiom(OntoCity.getEntityURI(areaIndivName),
				OntoCity.getEntityURI(regionIndivName),
				OpenGisGEOPattern.sfOverlaps.getEntityURI());
	}
	
	public void addFloodEvent (int eventID) {
		String eventIndivName = generateIndividualEventName (eventID, OntoCity.Flood.toString());
		addIndividual(OntoCity.Flood.getEntityURI(),
				OntoCity.getEntityURI(eventIndivName));
	}
	
	
	public void save() {
		savetoFile();
	}

	public static String generateIndividualName (String prefix, int ID) {
		return prefix + ID;
	}
	
	public static String generateIndividualName (String prefix, int ID, int ID1) {
		return prefix + ID + INDIV_NAME_SPLITTER + ID1;
	}
	
	private String generateIndividualEventName (int eventID, String eventClassName) {
		return PREFIX_EVENT_NAME + eventClassName.toLowerCase() + eventID;
	}
}
