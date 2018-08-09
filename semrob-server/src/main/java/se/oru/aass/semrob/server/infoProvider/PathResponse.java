package se.oru.aass.semrob.server.infoProvider;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;

import se.oru.aass.semrob.setting.NumericRange;


/**
 * This class is responsible to generate 3D aerial paths existing between the two given coordinates of source and destination on the map
 * @author marjan
 *
 */
public class PathResponse extends PathBasedResponse{
		
	public PathResponse(Coordinate source, Coordinate destination, NumericRange xRange, NumericRange yRange, NumericRange zRange, int prioriytIndex) {
		super(source, destination, xRange, yRange, zRange, prioriytIndex);
	}
	
	
	@Override
	public void query() {
		pathInfo = new ArrayList<Coordinate>();
		pathInfo = motionPlanner.generatePath();
	}
	
	@Override
	public void printResult() {
		// TODO Auto-generated method stub
		super.printResult();
	}
	
	@Override
	public String getResponse() {
		// TODO Auto-generated method stub
		return super.getResponse();
	}
}
