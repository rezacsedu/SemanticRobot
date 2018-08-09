package se.oru.aass.semrob.ontology.query;




import java.security.CodeSource;
import java.util.List;
import java.util.Properties;


import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import se.oru.aass.semrob.ontology.OntoCity;
import se.oru.aass.semrob.ontology.OntoCityHandler;
import se.oru.aass.semrob.ontology.Setting;
import se.oru.aass.semrob.ontology.pattern.DUL;
import se.oru.aass.semrob.ontology.pattern.OntoCityEventPattern;
import se.oru.aass.semrob.ontology.pattern.OpenGisGEOPattern;
import se.oru.aass.semrob.ontology.pattern.OpenGisGMLPattern;




public class OntoCitySPARQL {
	
	private static Properties prop;
	private static String FUNCTION_URI;
	public static String RDF_URI;
	public static String OWL_URI;
	public static String XSD_URI;
	public static String RDFS_URI;
	private static String SPATIAL_RELATION_URI;
	
	public final static String URI_ENTITY_SPLITTER = "#";
	public final static String URI_STARTING_CHAR = "<";
	public final static String URI_ENDING_CHAR = ">";
	public final static String LITERAL_ENTITY_SPLITTER = "^^";
	
	public final static String PREFIX_RDF = "rdf";
	public final static String PREFIX_OWL = "owl";
	public final static String PREFIX_RDFS = "rdfs";
	public final static String PREFIX_XSD = "xsd";
	public final static String PREFIX_ONTOCITY = "oncy";
	private final static String PREFIX_DUL = "dul";
	private final static String PREFIX_GEO = "geo";
	private final static String PREFIX_GML = "gml";
	private final static String PREFIX_ONTOCITY_EVENT = "evn";
	public final static String PREFIX_SPATIAL_FUNCTION = "cap";
	
	private OntoCityHandler ontoCity;
	
	static {
		prop = new Setting().getConfiguration(Setting.CONFIG_FILE_NAME);
		SPATIAL_RELATION_URI = prop.getProperty("ontology.uri.spatial.relation");
		FUNCTION_URI = prop.getProperty("function.uri");
		
		RDF_URI = prop.getProperty("ontology.uri.rdf");
		OWL_URI = prop.getProperty("ontology.uri.owl");
		XSD_URI = prop.getProperty("ontology.uri.xsd");
		RDFS_URI = prop.getProperty("ontology.uri.rdfs");
	}
	

//	public OntoCitySPARQL (String ontologyPath,	boolean runningFromJar, CodeSource codeSource) {
//		ontoCity = new OntoCityHandler(ontologyPath, runningFromJar, codeSource);
//	}
	
	public OntoCitySPARQL (boolean runningFromJar, CodeSource codeSource) {
		ontoCity = new OntoCityHandler(prop.getProperty("ontology.ontocity.file.path"), runningFromJar, codeSource);
	}
	
	public OntoCitySPARQL (OntoCityHandler ontoCity) {
		this.ontoCity = ontoCity; 
	}

	private static String getBasePrefix() {
		String header =  "PREFIX " + PREFIX_RDF +": <" +  RDF_URI + URI_ENTITY_SPLITTER + "> \n" +
		"PREFIX " + PREFIX_OWL + ": <" +  OWL_URI + URI_ENTITY_SPLITTER + "> \n" +
		"PREFIX " + PREFIX_RDFS + ": <" +  RDFS_URI + URI_ENTITY_SPLITTER +  "> \n" +
		"PREFIX " + PREFIX_XSD + ": <" +  XSD_URI + URI_ENTITY_SPLITTER +  "> \n" +
		"PREFIX " + PREFIX_ONTOCITY + ": <" + OntoCity.getURI() +  URI_ENTITY_SPLITTER + "> \n" +
		"PREFIX " + PREFIX_ONTOCITY_EVENT + ": <" + OntoCityEventPattern.getURI() +  URI_ENTITY_SPLITTER + "> \n" +
		"PREFIX " + PREFIX_GML + ": <" + OpenGisGMLPattern.getURI() +  URI_ENTITY_SPLITTER + "> \n" +
		"PREFIX " + PREFIX_GEO + ": <" + OpenGisGEOPattern.getURI() +  URI_ENTITY_SPLITTER + "> \n" +
		"PREFIX " + PREFIX_DUL + ": <" + DUL.getURI() +  URI_ENTITY_SPLITTER + "> \n";// +
		//"PREFIX " + PREFIX_SPATIAL_FUNCTION + ": <java:" + SPATIAL_RELATION_URI + ".> \n";
		
		return header;

	}
	
	
	public static String getStringName (QuerySolution solution, String variableName) {
		return solution.getResource(variableName).toString().split(URI_ENTITY_SPLITTER)[1];
	}
	
	public static String getWKTValue (QuerySolution solution, String variableName) {
		String s = solution.getLiteral(variableName).toString();
		return s.substring(0, s.indexOf(LITERAL_ENTITY_SPLITTER));
	}
	
	public static double getNumbericValue (QuerySolution solution, String variableName) {
		try {
			String s = solution.getLiteral(variableName).toString();
			int indexSplitter = s.indexOf(LITERAL_ENTITY_SPLITTER);
			if (indexSplitter > 0) {
				int indexScientificNotation = s.indexOf("e");
				if (indexScientificNotation > 0) {
					if (indexScientificNotation < indexSplitter)
						return Double.parseDouble(s.substring(0, indexScientificNotation - 1).toString());
					else
						return Double.parseDouble(s.substring(0, indexSplitter - 1).toString());
				}
				else
					return Double.parseDouble(s.substring(0, indexSplitter - 1).toString());
			}
			else
				return 0;
		}
		catch (Exception e) {
			return 0;
		}
	}
	
	private static String getIntegerSPARQLPhrase (int value) {
		return "\"" + value + "\"" + LITERAL_ENTITY_SPLITTER + PREFIX_XSD + ":integer";
	}
	
//	private static String getWKTSPARQLPhrase (String value) {
//		return "\"" + value + "\"" + LITERAL_ENTITY_SPLITTER + PREFIX_GEO + ":wktLiteral";
//	}

	private List <QuerySolution> getQueryResultSet(String queryString) {
		
		List <QuerySolution> resultList;
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, ontoCity.getOntologyModel());
				
		try {	
			ResultSet resultSet = qexec.execSelect();	
			resultList = ResultSetFormatter.toList(resultSet);
		}
		catch(Exception e) {
			resultList = null;
		}
		finally{
			qexec.close();
		}
		
		return resultList;
	}
	
	/**
	 * Given the id of a region, this method returns back the individual name assigned to the region when it was added to the ontology. 
	 * @param ID indicates the ID of the region.
	 * @return
	 */
	private static String getRegionIndividualName(int ID) {
		return PREFIX_ONTOCITY + ":" + OntoCityHandler.generateIndividualName(OntoCityHandler.PREFIX_REGION_NAME, ID);
	}
	
	public static String getSegmentIndividualName(int ID) {
		return PREFIX_ONTOCITY + ":" + OntoCityHandler.generateIndividualName(OntoCityHandler.PREFIX_SEGMENT_NAME, ID);
	}
	
	private static String getEventAreaIndividualName(int ID) {
		return PREFIX_ONTOCITY + ":" + OntoCityHandler.generateIndividualName(OntoCityHandler.PREFIX_EVENT_AREA_NAME + "0" + OntoCityHandler.INDIV_NAME_SPLITTER , ID);
	}
	
	
	public List <QuerySolution> getAllRegionsWithSegments () {
		String queryString =
				
				getBasePrefix() + 
				"\n" +
				"SELECT ?regionIndivName ?regionBoundary ?segmentIndivName ?segmentBoundary ?region ?precision ?absoluteElevation ?relativeElevation " +
						"\n" +
				  		
				"WHERE \n" +
				"{\n"  +
						"?regionIndivName " + PREFIX_RDF + ":type ?region. \n" +
						"?region " + PREFIX_RDFS + ":subClassOf* " + PREFIX_ONTOCITY + ":" + OntoCity.Region.toString() + ". \n" +
						"?regionIndivName " + PREFIX_GEO + ":" + OpenGisGEOPattern.hasGeometry.toString() + " ?regionGeometry. \n" +
						"?regionIndivName  " + PREFIX_ONTOCITY + ":" + OntoCity.hasPrecision.toString() + " ?precision. \n" +
						"?regionIndivName  " + PREFIX_ONTOCITY + ":" + OntoCity.hasAbsoluteElevationValue.toString() + " ?absoluteElevation. \n" +
						"?regionIndivName  " + PREFIX_ONTOCITY + ":" + OntoCity.hasRelativeElevationValue.toString() + " ?relativeElevation. \n" +
						"?regionGeometry " + PREFIX_GEO +  ":" + OpenGisGEOPattern.asWKT.toString() + "  ?regionBoundary. \n" +
						
						"?regionIndivName  " + PREFIX_GEO + ":" + OpenGisGEOPattern.sfOverlaps.toString() + " ?segmentIndivName. \n" +
						"?segmentIndivName  " + PREFIX_RDF + ":type " + PREFIX_ONTOCITY + ":" + OntoCity.Segment.toString() + ". \n" +
						"?segmentIndivName  " + PREFIX_GEO + ":" + OpenGisGEOPattern.hasGeometry.toString() + " ?segmentGeometry. \n" +
						"?segmentGeometry " + PREFIX_GEO +  ":" + OpenGisGEOPattern.asWKT.toString() + " ?segmentBoundary. \n" +
				
				
				"} \n" + 
				"ORDER BY ?segmentIndivName ?regionIndivName";

		return getQueryResultSet(queryString);
	}
	
	
	public List <QuerySolution> getEnrichedBuildings () {
		String queryString =
				
				getBasePrefix() + 
				"\n" +
				"SELECT ?enrichedBuildingIndivName ?enrichedBuildingBoundary ?enrichedBuilding  " +
						"\n" +
				  		
				"WHERE \n" +
				"{\n"  +
						"?enrichedBuildingIndivName " + PREFIX_RDF + ":type ?enrichedBuilding. \n" +
						"?enrichedBuilding " + PREFIX_RDFS + ":subClassOf* " + PREFIX_ONTOCITY + ":" + OntoCity.EnrichedBuilding.toString() + ". \n" +
						"?enrichedBuildingIndivName " + PREFIX_GEO + ":" + OpenGisGEOPattern.hasGeometry.toString() + " ?enrichedBuildingGeometry. \n" +
						"?enrichedBuildingGeometry " + PREFIX_GEO +  ":" + OpenGisGEOPattern.asWKT.toString() + "  ?enrichedBuildingBoundary. \n" +			
				
				"} \n";// + 
				//"ORDER BY ?segmentIndivName ?regionIndivName";

		return getQueryResultSet(queryString);
	}
	
	public List <QuerySolution> getAllEventAreas () {
		String queryString =
				
				getBasePrefix() + 
				"\n" +
				"SELECT ?eventAreaIndivName ?eventAreaBoundary" +
						"\n" +
				  		
				"WHERE \n" +
				"{\n"  +
						"?event " + PREFIX_RDFS + ":subClassOf* " + PREFIX_ONTOCITY_EVENT + ":" + OntoCityEventPattern.EventCity.toString() + ". \n" +
						"?eventIndivName  " + PREFIX_DUL + ":" + DUL.associatedWith.toString() + " ?eventAreaIndivName. \n" +
						"?eventAreaIndivName  " + PREFIX_GEO + ":" + OpenGisGEOPattern.hasGeometry.toString()  + " ?geometry. \n" +
						"?geometry " + PREFIX_GEO +  ":" + OpenGisGEOPattern.asWKT.toString() + " ?eventAreaBoundary. \n" +
				"} ";

		return getQueryResultSet(queryString);
	}
	
	public List <QuerySolution> getAllSegments () {
		String queryString = 
				
				getBasePrefix() + 
				"\n" +
				"SELECT ?segmentIndivName ?segmentBoundary " +
						"\n" +
				  		
				"WHERE \n" +
				"{\n"  +
						"?segmentIndivName  " + PREFIX_RDF + ":type " + PREFIX_ONTOCITY + ":" + OntoCity.Segment.toString() + ". \n" +
						"?segmentIndivName  " + PREFIX_GEO + ":" + OpenGisGEOPattern.hasGeometry.toString() + " ?geometry. \n" +
						"?geometry " + PREFIX_GEO +  ":" + OpenGisGEOPattern.asWKT.toString() + " ?segmentBoundary. \n" +
				"} ";

		return getQueryResultSet(queryString);
	}
	
	
	/**
	 * This method retrieves all the regions belonging to the given segment
	 * @param segmentID
	 * @return
	 */
	public List <QuerySolution> getAllRegionsInSegment(int segmentID) {
		String segmentIndivName = getSegmentIndividualName(segmentID);
		String queryString = 
				
				getBasePrefix() + 
				"\n" +
				"SELECT ?regionIndivName ?regionBoundary " +
						"\n" +
				  		
				"WHERE \n" +
				"{\n"  +
				"?regionIndivName " + PREFIX_RDF + ":type ?region. \n" +
				"?region " + PREFIX_RDFS + ":subClassOf* " + PREFIX_ONTOCITY + ":" + OntoCity.Region.toString() + ". \n" +
				"?regionIndivName " + PREFIX_GEO + ":" + OpenGisGEOPattern.hasGeometry.toString() + " ?regionGeometry. \n" +
				"?regionGeometry " + PREFIX_GEO +  ":" + OpenGisGEOPattern.asWKT.toString() + "  ?regionBoundary. \n" +
				
				"?regionIndivName  " + PREFIX_GEO + ":" + OpenGisGEOPattern.sfOverlaps.toString() +  " " + segmentIndivName + ". \n" +
				 segmentIndivName + " " + PREFIX_RDF + ":type " + PREFIX_ONTOCITY + ":" + OntoCity.Segment.toString() + ". \n" +
				 segmentIndivName + " " + PREFIX_GEO + ":" + OpenGisGEOPattern.hasGeometry.toString() + " ?segmentGeometry. \n" +
				"?segmentGeometry " + PREFIX_GEO +  ":" + OpenGisGEOPattern.asWKT.toString() + " ?segmentBoundary. \n" +
				"} ";

		return getQueryResultSet(queryString);
	}
	
	/**
	 * This method retrieves all the eventAreas with the regions that overlap
	 * @return
	 */
	public List <QuerySolution> getAllEventAreaWithRegions(int eventAreaID) {
		String eventAreaIndivName = getEventAreaIndividualName(eventAreaID);
		String queryString =
				
				getBasePrefix() + 
				"\n" +
				"SELECT ?regionIndivName " +
						"\n" +
				  		
				"WHERE \n" +
				"{\n"  +
						"?regionIndivName " + PREFIX_RDF + ":type ?region. \n" +
						"?region " + PREFIX_RDFS + ":subClassOf* " + PREFIX_ONTOCITY + ":" + OntoCity.Region.toString() + ". \n" +
						
						eventAreaIndivName + " " + PREFIX_RDF + ":type " + PREFIX_ONTOCITY_EVENT + ":" +  OntoCityEventPattern.Area.toString() + ". \n" +
						eventAreaIndivName + " " + PREFIX_GEO + ":" + OpenGisGEOPattern.sfOverlaps.toString() + " ?regionIndivName. \n" +
				
				"} \n";

		return getQueryResultSet(queryString);
	}

	/**
	 * This method retrieves all the regions overlapping eventAreas
	 * @return
	 */
	public List <QuerySolution> getAllEventAffectedRegionIDs() {
		String queryString =
				getBasePrefix() + 
				"\n" +
				"SELECT DISTINCT ?regionIndivName " +
						"\n" +
				  		
				"WHERE \n" +
				"{\n"  +
						"?regionIndivName " + PREFIX_RDF + ":type ?region. \n" +
						"?region " + PREFIX_RDFS + ":subClassOf* " + PREFIX_ONTOCITY + ":" + OntoCity.Region.toString() + ". \n" +
						
						"?eventAreaIndivName  " + PREFIX_GEO + ":" + OpenGisGEOPattern.sfOverlaps.toString() + " ?regionIndivName. \n" +
						"?eventAreaIndivName  " + PREFIX_RDF + ":type " + PREFIX_ONTOCITY_EVENT + ":" +  OntoCityEventPattern.Area.toString() + ". \n" +
				
				
				"} \n";
		return getQueryResultSet(queryString);
	}
	
//	/**
//	 * This method returns all pair of regions with the distance relations between them
//	 * @return
//	 */
//	public List<QuerySolution> getAllRegionDistance () {
//		String queryString =
//				getBasePrefix() + 
//				"\n" +
//				"SELECT DISTINCT ?regionIndivName1  ?regionIndivName2 ?distanceRelationName" +
//						"\n" +
//				  		
//				"WHERE \n" +
//				"{\n"  +
//						"?regionIndivName1 " + PREFIX_RDF + ":type ?region1. \n" +
//						"?region1 " + PREFIX_RDFS + ":subClassOf* " + PREFIX_ONTOCITY_REGION + ":" + OntoCityRegionPattern.Region.toString() + ". \n" +
//						
//						"?regionIndivName2 " + PREFIX_RDF + ":type ?region2. \n" +
//						"?region2 " + PREFIX_RDFS + ":subClassOf* " + PREFIX_ONTOCITY_REGION + ":" + OntoCityRegionPattern.Region.toString() + ". \n" +
//						
//						
//						
//						"?regionIndivName1 ?distanceRelationName ?regionIndivName2. \n"  + 
//						"?distanceRelationName  " + PREFIX_RDFS + ":subPropertyOf* " + PREFIX_ONTOCITY + ":" + OntoCityObjectPropertyName.LocatedWithin + ". \n" +
//				"} \n";
//		return getQueryResultSet(queryString);
//	}


	
	// ---------- GeoSpatial Queries -----------------------------------------------
	
//	private void registerClassFunction (Class spatialClass) {
//		FunctionRegistry.get().put(FUNCTION_URI + URI_ENTITY_SPLITTER + spatialClass.getSimpleName() , spatialClass);
//	}	
}
