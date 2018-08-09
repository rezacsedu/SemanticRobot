package se.oru.aass.semrob.client;

import java.io.IOException;
import java.util.Vector;

import se.oru.aass.semrob.client.infoRequester.InfoRequester;
import se.oru.aass.semrob.client.infoRequester.NeighbourRegionInfoRequester;

public class Client_QueryType_2 {
	
	public static void main(String[] args) throws IOException { 
		
        try { 
		    if (args.length == 7) {
		    	
		    	String regionTypes = args[0];
		    	int direction = Integer.parseInt(args[1]);
		    	int x = Integer.parseInt(args[2]) + 1;
		    	int y = Integer.parseInt(args[3]) + 1;
		    	int distance1 = Integer.parseInt(args[4]);
		    	int distance2 = Integer.parseInt(args[5]);
		    	int withEvent = Integer.parseInt(args[6]);
		    	boolean ignoreEvent;
		    	if (withEvent == 0)
		    		ignoreEvent = true;
		    	else
		    		ignoreEvent = false;

		    	
		    	Vector<Object> params = new Vector<>();
				params.add(regionTypes);
			    params.add(direction);
			    params.add(x);
			    params.add(y);
			    params.add(distance1);
			    params.add(distance2);
			    params.add(ignoreEvent);
			    
			    
			    String response = InfoRequester.requestInfo("getNeighborRegionInfo", params);
			    new NeighbourRegionInfoRequester().printInfo (response); 
			}
		    System.out.println("Please enter the required information for the query!");
			
	    } catch (Exception e) { 
            System.out.println(e.getMessage()); 
        } 
    }
}
