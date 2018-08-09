package se.oru.aass.semrob.client;

import java.io.IOException;
import java.util.Vector;

import com.vividsolutions.jts.geom.Coordinate;

import se.oru.aass.mpi.geo.coordinateProcessing.CoordinateConverter;
import se.oru.aass.mpi.geo.coordinateProcessing.CoordinateConverter.UTM;
import se.oru.aass.semrob.client.infoRequester.InfoPathRequester;
import se.oru.aass.semrob.client.infoRequester.InfoRequester;
import se.oru.aass.semrob.client.infoRequester.RegionInfoRequester;

public class Client_QueryType_1 {
	
	public static void main(String[] args) throws IOException { 
			
        try { 
		    if (args.length == 2) {
		    	//int x = Integer.parseInt(args[0]) + 1;
		    	//int y = Integer.parseInt(args[1]) + 1;

		    	double easting = Double.parseDouble(args[0]);
		    	double northing = Double.parseDouble(args[1]);
		    	
		    	Coordinate coord = CoordinateConverter.utm2xy(new UTM(easting, -1 * northing, InfoPathRequester.ZONE, CoordinateConverter.HEMISPHERE_LAT_NORTH));
								
					    	
		    	Vector<Object> params = new Vector<>();
				params.add((int) coord.x);
			    params.add((int) coord.y);
			    			    
			    String response = InfoRequester.requestInfo("getRegionInfo", params);
			    new RegionInfoRequester().printInfo (response); 
			}
		    else
		    	System.out.println("Please enter two values indicating x and y coordinates of the point of interest, respectively!");
			
	    } catch (Exception e) { 
            System.out.println(e.getMessage()); 
        } 
    }
}
