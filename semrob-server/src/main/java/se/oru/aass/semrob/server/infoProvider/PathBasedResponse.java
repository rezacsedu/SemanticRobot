package se.oru.aass.semrob.server.infoProvider;


import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import se.oru.aass.semrob.motionPlanning.RRTMotionPlanner;
import se.oru.aass.semrob.setting.NumericRange;

/** 
 * This class provides the basis for generating/finding paths on the map.
 */
public class PathBasedResponse implements Response{

	protected Coordinate source;
	protected Coordinate destination;
	protected RRTMotionPlanner motionPlanner;
	protected List<Coordinate> pathInfo;
	protected int priorityIndex;

	
	public PathBasedResponse(Coordinate source, Coordinate destination, NumericRange xRange, NumericRange yRange, NumericRange zRange, int priorityIndex) {
		this.source = source;
		this.destination = destination;
		motionPlanner = new RRTMotionPlanner(source, destination, xRange, yRange, zRange, priorityIndex);
		pathInfo = new ArrayList<Coordinate>();
		this.priorityIndex = priorityIndex;
	}
	
	@Override
	public void query() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printResult() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getResponse() {
		String response = "";
		if (pathInfo != null) {
			int coordinateIndex = 0;
			for (Coordinate coordinate : pathInfo) {
				
				response += (coordinate.x) + SPLITTER_SERVER_RESPONSE_INSTANCE_INFO + (coordinate.y) + SPLITTER_SERVER_RESPONSE_INSTANCE_INFO + (coordinate.z);
				
				if (coordinateIndex < (pathInfo.size() - 1))
					response += SPLITTER_SERVER_RESPONSE_INSTANCE;
				
				coordinateIndex++;
			}
		}
		
		System.out.println("Server is waiting for the next request: ");	
		return response;
	}
}
