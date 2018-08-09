package se.oru.aass.semrob.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.vividsolutions.jts.geom.Coordinate;

import se.oru.aass.mpi.geo.coordinateProcessing.CoordinateConverter;
import se.oru.aass.mpi.geo.coordinateProcessing.CoordinateConverter.UTM;
import se.oru.aass.semrob.client.infoRequester.InfoPathRequester;
import se.oru.aass.semrob.client.infoRequester.InfoRequester;
import se.oru.aass.semrob.client.infoRequester.NeighbourRegionInfoRequester;
import se.oru.aass.semrob.client.infoRequester.RegionInfoRequester;

public class Client_QueryType_4 {
	private static String NULL_VALUE = "null";
	public static void main (String[] args1) throws IOException{
		try { 
			String args[] = {"5", "null", "674721.61", "6580248.21", "5", "20",  "null", "null", "null", "null", "null"};
			if (args.length == 11) {
	    	
		    	if (isRegionInfoRequest(args)) {
		    		//int x = Integer.parseInt(args[2]) + 1;
			    	//int y = Integer.parseInt(args[3]) + 1;
		    		
		    		//assuming that x and y are given in utm coordinates and not xy
		    		double easting = Double.parseDouble(args[2]);  //equivalent to x
		    		double northing = Double.parseDouble(args[3]); // equivalent to yy
		    		Coordinate coordinate = CoordinateConverter.utm2xy(new UTM(easting, -1 * northing, InfoPathRequester.ZONE, CoordinateConverter.HEMISPHERE_LAT_NORTH));
		    		
			    	boolean ignoreEvent = processEventIgnoreOption(args[10], true);
			    	
		    		Vector<Object> params = new Vector<>();
		    		//params.add(x);
				    //params.add(y);
		    		params.add((int)coordinate.x);
		    		params.add((int)coordinate.y);
				    params.add(ignoreEvent);
		    		String response = InfoRequester.requestInfo("getRegionInfo", params);
				    new RegionInfoRequester().printInfo (response); 
		    	}
		    	else if (isNeighbourRegionInfoRequest(args)) {
		    		String regionTypeList = processRegionTypes(args[0], 0);
			    	int direction = processArgument(args[1], 0);
			    	
			    	//int x = Integer.parseInt(args[2]) + 1;
			    	//int y = Integer.parseInt(args[3]) + 1;
		    		
		    		//assuming that x and y are given in utm coordinates and not xy
		    		double easting = Double.parseDouble(args[2]);  //equivalent to x
		    		double northing = Double.parseDouble(args[3]); // equivalent to y
		    		Coordinate coordinate = CoordinateConverter.utm2xy(new UTM(easting, -1 * northing, InfoPathRequester.ZONE, CoordinateConverter.HEMISPHERE_LAT_NORTH));

		    		
			    	int distance1 = processArgument(args[4], 0);
			    	int distance2 = processArgument(args[5], 8000);
			    	Double areaSize = (double) processArgument(args[6], 0);
			    	int areaSizeSign = processArgument(args[7], 1);
			    	//default is set to ignore events
			    	boolean ignoreEvent = processEventIgnoreOption(args[10], true);
			    	
			    	Vector<Object> params = new Vector<>();
			    	params.add(regionTypeList);
					params.add(direction);
		    		//params.add(x);
				    //params.add(y);
		    		params.add((int)coordinate.x);
		    		params.add((int)coordinate.y);
				    params.add(distance1);
				    params.add(distance2);
				    params.add(areaSize);
				    params.add(areaSizeSign); 
				    params.add(ignoreEvent);
				    
				    String response = InfoRequester.requestInfo("getNeighborRegionInfo", params);
				    new NeighbourRegionInfoRequester().printInfo (response); 
		    	}
		    	else {
		    		

			    	String regionTypeList = processRegionTypes(args[0], 0);
			    	int direction = processArgument(args[1], 0);
			    	//int x = Integer.parseInt(args[2]) + 1;
			    	//int y = Integer.parseInt(args[3]) + 1;
		    		
		    		//assuming that x and y are given in utm coordinates and not xy
		    		double easting = Double.parseDouble(args[2]);  //equivalent to x
		    		double northing = Double.parseDouble(args[3]); // equivalent to y
		    		Coordinate coordinate = CoordinateConverter.utm2xy(new UTM(easting, -1 * northing, InfoPathRequester.ZONE, CoordinateConverter.HEMISPHERE_LAT_NORTH));

			    	int distance1 = processArgument(args[4], 0);
			    	int distance2 = processArgument(args[5], 4000);
			    	Double areaSize = (double) processArgument(args[6], 0);
			    	int areaSizeSign = processArgument(args[7], 1);
			    	int distanceRelational = processArgument(args[8], 500);
			    	// TODO regionTypeRelational cannot be type REGION ... it should be something more specific otherwise it does not make sense!
			    	int regionTypeRelational =  processArgument(args[9], 5);
			    	//default is set to ignore events
			    	boolean ignoreEvent = processEventIgnoreOption(args[10], true);
			    	
			    	Vector<Object> params = new Vector<>();
			    	params.add(regionTypeList);
					params.add(direction);
					//params.add(x);
				    //params.add(y);
		    		params.add((int)coordinate.x);
		    		params.add((int)coordinate.y);
				    params.add(distance1);
				    params.add(distance2);
				    params.add(areaSize);
				    params.add(areaSizeSign);
				    params.add(distanceRelational);
				    params.add(regionTypeRelational);			    
				    params.add(ignoreEvent);
				    
				   String response = InfoRequester.requestInfo("getNeighborRegionWithRelationInfo", params);
				   new NeighbourRegionInfoRequester().printInfo (response); 
		    	}
			}
		    else {
		    	System.out.println("Please enter the required information for the query!");
		    }
			
	    } catch (Exception e) { 
            System.out.println("Please enter the required information for the query!"); 
        }
	}
	
	private static int processArgument(String argument, int defaultValue) {
		if (isNull(argument)) 
			return defaultValue;
		else {
			int n = Integer.parseInt(argument); 
			if (n < 0)
				return defaultValue;
			else return n;
		}
			
	}
	
	private static String processRegionTypes(String argument, int defaultValue) {
		if (isNull(argument)) 
			return Integer.toString(defaultValue);
		else {
			List<String> types = Arrays.asList(argument.split("_"));
			if (types.size() > 0 && types.contains("0")) {
				return Integer.toString(defaultValue);
			}
		}
		return argument;
			
	}
	
	private static boolean processEventIgnoreOption(String argument, boolean defaultValue) {
		if (isNull(argument)) 
    		return defaultValue;
    	else {
    		// if the parameter withEvent was 0, it means that the user does not want to have events => ignoreEvent = true
    		if (Integer.parseInt(argument) == 0)
	    		return true;
	    	else
	    		return false;
    	}
	}
	
	private static boolean isNull(String value) {
		if(value.trim().toLowerCase().equals(NULL_VALUE.trim().toLowerCase()))
			return true;
		else return false;
	}
	
	private static boolean isRegionInfoRequest(String[] args) {
		if (isNull(args[0]) && 
				isNull(args[1]) && 
				isNull(args[4]) && 
				isNull(args[5]) && 
				isNull(args[6]) && 
				isNull(args[7]) && 
				isNull(args[8]) && 
				isNull(args[9]))
			return true;
		else return false;
	}
	
	private static boolean isNeighbourRegionInfoRequest(String[] args) {
		
		if (isNull(args[8]) && isNull(args[9]))
			return true;
		else return false;
	}

}
