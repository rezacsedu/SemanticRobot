package se.oru.aass.semrob.client;

import java.io.IOException;
import java.util.Vector;

import com.vividsolutions.jts.geom.Coordinate;

import se.oru.aass.mpi.geo.coordinateProcessing.CoordinateConverter;
import se.oru.aass.mpi.geo.coordinateProcessing.CoordinateConverter.UTM;
import se.oru.aass.semrob.client.infoRequester.InfoPathRequester;
import se.oru.aass.semrob.client.infoRequester.InfoRequester;
import se.oru.aass.semrob.client.infoRequester.PathInfoRequester;

public class Client_PathQueryType_1 {

	
	public static void main(String[] args) throws IOException { 
		
        try { 
        	//String args[] = {"673468", "6587773", "24", "675508", "6586655", "46"};
        	//String args[] = {"672240.63", "6583567.42", "43", "672551.62", "6582761.25", "37", "1"};

		    if (args.length == 7) {
		    	
		    	/*int sourceX = Integer.parseInt(args[0]) + 1;
		    	int sourceY = Integer.parseInt(args[1]) + 1;
		    	int sourceZ = Integer.parseInt(args[2]) + 1;
		    	int destinationX = Integer.parseInt(args[3]) + 1;
		    	int destinationY = Integer.parseInt(args[4]) + 1;
		    	int destinationZ = Integer.parseInt(args[5]) + 1;*/
		    	
				double sourceEasting = Double.parseDouble(args[0]);
		    	double sourceNorthing = Double.parseDouble(args[1]);
		    	int sourceZ = Integer.parseInt(args[2]);
		    	double destinationEasting = Double.parseDouble(args[3]);
		    	double destinationNorthing = Double.parseDouble(args[4]);
		    	int destinationZ = Integer.parseInt(args[5]);
		    	int priorityIndex = Integer.parseInt(args[6]);
		    	
		    	Vector<Object> params = new Vector<>();
				Coordinate coordSource = CoordinateConverter.utm2xy(new UTM(sourceEasting, -1 * sourceNorthing, InfoPathRequester.ZONE, CoordinateConverter.HEMISPHERE_LAT_NORTH));
				Coordinate coordDestination = CoordinateConverter.utm2xy(new UTM(destinationEasting, -1 * destinationNorthing, InfoPathRequester.ZONE, CoordinateConverter.HEMISPHERE_LAT_NORTH));
				
				//if (coordSource.x >= 0 && coordSource.y >= 0 && coordDestination.x < 1600 && coordDestination.y < 1600) {
					params.add((int) coordSource.x);
				    params.add((int) coordSource.y);
				    params.add(sourceZ);
				    params.add((int) coordDestination.x);
				    params.add((int) coordDestination.y);
				    params.add(destinationZ);
				    params.add(priorityIndex);
				    
				    
				    String response = InfoRequester.requestInfo("getPathInfo", params);
				    new PathInfoRequester().printInfo (response, sourceEasting, sourceNorthing, sourceZ, destinationEasting, destinationNorthing, destinationZ);
				/*}
				else
					System.out.println("Please enter valid coordinates located in the chosen area!");*/
			}
		    else
		    	System.out.println("Please enter the required information for the query!");
			
	    } catch (Exception e) { 
            System.out.println(e.getMessage()); 
        } 
    }
}
