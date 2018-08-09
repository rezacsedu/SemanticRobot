package se.oru.aass.semrob.client;

import java.io.IOException;
import java.util.Vector;

public class Client_QueryType_3 {
	public static void main(String[] args) throws IOException { 
		
	        try { 
			    if (args.length == 5) {
			    	String regionsType = args[0];
			    	int areaSize = Integer.parseInt(args[1]);
			    	int areaSizeSign = Integer.parseInt(args[2]);
			    	int distanceRelational = Integer.parseInt(args[3]);
			    	int regionTypeRelational = Integer.parseInt(args[4]);
	
	
			    	Vector<Object> params = new Vector<>();
					params.add(regionsType);
				    params.add(areaSize);
				    params.add(areaSizeSign);
				    params.add(distanceRelational);
				    params.add(regionTypeRelational);
	
				    
			    
				    //String response = InfoRequester.requestInfo("getRegionsWithRelationInfo", params);
				    //TODO we need to implement a new requester because it contains a set of regions none of them are mainRegion
				    //new RegionInfoRequester().printInfo (response); 
				}
			    System.out.println("Please enter the required information for the query!");
				
		    } catch (Exception e) { 
	            System.out.println(e.getMessage()); 
	        } 
	    }
}
