package se.oru.aass.semrob.server;

import java.security.CodeSource;
import com.vividsolutions.jts.geom.Coordinate;

import se.oru.aass.semrob.server.infoProvider.PathResponse;
import se.oru.aass.semrob.server.infoProvider.RegionResponse;
import se.oru.aass.semrob.setting.NumericRange;

public class Server_3 extends Server{
	
	private  NumericRange xRange; 
	private  NumericRange yRange;
	private  NumericRange zRange;
	
	/**
	 * This method initializes the server by loading the ontology and indexing the regions, segments
	 * @param ontoFileName
	 * @return
	 */
	public boolean initialize (boolean runningFromJar, CodeSource codeSource, int mapWidthSize, int mapHeightSize, int mapElevationSize) {
		try {
			
			super.initialize(runningFromJar, codeSource);		
			xRange = new NumericRange(0, mapWidthSize);
			yRange = new NumericRange(0, mapHeightSize);
			zRange = new NumericRange(0, mapElevationSize);
		
			
			initializationReport();
			return true;
		} catch (Exception e) {
			e.printStackTrace(); 
			return false;
		}
	}
	
	protected void initializationReport() {
		super.initializationReport();
		
	}
	
	//--------------------------------------------------------------
	// Methods Called by the clients
	//---------------------------------------------
	
	public String getPathInfo(int sourceX, int sourceY, int sourceZ, int destinationX, int destinationY, int destinationZ, int priprityIndex) {
		PathResponse objResponse;
		Coordinate source = new Coordinate(sourceX, sourceY, sourceZ);
		Coordinate destination = new Coordinate(destinationX, destinationY, destinationZ);
		objResponse	= new PathResponse(source, destination, xRange, yRange, zRange, priprityIndex);
		objResponse.query();
		String s = objResponse.getResponse();
		System.out.println(s);
		return s;
	}
	
	/**
	 * The method getRegionInfo returns the information about the region containing the given point (x, y)
	 * without checking if the region is affected by events
	 * @param x indicates the x-coordinate of the point
	 * @param y indicates the y-coordinate of the point
	 */
	public String getRegionInfo(int x, int y) {
		RegionResponse objResponse;
		objResponse	= new RegionResponse(ontoCitySPARQL, x, y);
		objResponse.query();
		return objResponse.getResponse();
	}
}
