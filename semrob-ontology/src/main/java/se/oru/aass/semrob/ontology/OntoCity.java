package se.oru.aass.semrob.ontology;

import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

public enum OntoCity {
	Segment(-1),
	Region(-1),
	//Vegetation(1), 
	Vegetation(-1),
	//Ground(2), 
	Ground(1),
	//Road(3), 
	Road(2),
	//Parking(4), 
	Parking(-1),
	//Building(5),
	Building(3),
	//Water(6), 
	Water(4),
	//RailRoad(7),
	RailRoad(5),
	Unknown(8),
	Industry(9),
	Residence(11),
	School(13),
	PoliceStation(15),
	SportHall(16),
	Hospital(18),
	TrainStation(20),
	RidingHall(22),
	University(23),
	CityHall(24),
	HealthCenter(25),
	FireStation(26),
	College(27),
	IceHall(28),
	Shadow(100),
	WaterBridge(101),
	EnrichedBuilding(-1), 
	Flood(-1),
	hasAbsoluteElevationValue(-1),
	hasRelativeElevationValue(-1),
	hasPrecision(-1);
	
	private int labelID;
	
	private OntoCity(int labelID) { 

	    this.labelID = labelID;
	} 
	
	private static PrefixManager prefixManager;
	private static String patternURI;
	
	public static void setPrefixManager (String uri) {
		patternURI = uri;
		prefixManager = new DefaultPrefixManager(patternURI + "#");
	}
	
	public static String getURI() {
		return patternURI;
	}

	public String getEntityURI() {
    	return prefixManager.getDefaultPrefix() + this.toString();
    }
	
	public static String getEntityURI(String entity) {
    	return prefixManager.getDefaultPrefix() + entity;
    }
	
	public int getLabelID () {
		return  labelID;
	} 
	
	public static String getClassName (int labelID) {
		for (OntoCity obj : OntoCity.values()) {
    		if (obj.labelID == labelID)
    			return obj.toString();
    	}
		return null;
	}
	
	public static int getLabelCode (String name) {
		for (OntoCity obj : OntoCity.values()) {
    		if (obj.toString().trim().toLowerCase().equals(name.trim().toLowerCase()))
    			return obj.labelID;
    	}
    	return -1;
    }
}
