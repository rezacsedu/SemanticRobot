package se.oru.aass.semrob.motionPlanning.smoothing;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import se.oru.aass.semrob.motionPlanning.RRTMotionPlanner;

public class Edge {
	private static final int MINIMAL_EDGE_LENGTH = 100;
	private static final double SUBDIVISION_NUMBER = 10;
	
	public static final int EDGE_POINT_ALL = 0;
	public static final int EDGE_POINT_INTERNAL = 1;
	public static final int EDGE_POINT_WITHOUT_DESTINATION = 2;
	

	private Coordinate source;
	private Coordinate destination;
	private List<Coordinate> points;
	
	public Edge(Coordinate source, Coordinate destination) {
		this.source = source;
		this.destination = destination;
		points = new ArrayList<>();
	}

	public Coordinate getSource() {
		return source;
	}
	
	public Coordinate getDestination() {
		return destination;
	}

	public List<Coordinate> getPoints(int filterCode) {
		List<Coordinate> filteredPoints = new ArrayList<>(this.points);
		
		if (filterCode == EDGE_POINT_INTERNAL) {
			if (filteredPoints.size() >= 2) {
				filteredPoints.remove(0); // remove the source
				filteredPoints.remove(filteredPoints.size() - 1); // remove the destination
			}
			else return null;
		}
		else if(filterCode == EDGE_POINT_WITHOUT_DESTINATION) {
			if (filteredPoints.size() >= 2) {
				filteredPoints.remove(filteredPoints.size() - 1); // remove the destination
			}
			else return null;
		}
		
		return filteredPoints;
	}
	
		
	public double getLength() {
		return RRTMotionPlanner.getDistance(source, destination);
	}

	public boolean subDivide() {
		if (points.size() == 0) {	
			double edgeLength = getLength();
			if (edgeLength > MINIMAL_EDGE_LENGTH) {
				Coordinate subtract = new Coordinate(destination.x - source.x, destination.y - source.y, destination.z - source.z);
				// will also add both the source (i = 0) and the destination (i = SUBDIVISION_NUMBER)
				for (int i = 0; i <= SUBDIVISION_NUMBER; i++) {
					Coordinate point = new Coordinate(getValueOnEdge(source.x, subtract.x, i), getValueOnEdge(source.y, subtract.y, i), getValueOnEdge(source.z, subtract.z, i));
					points.add(point);
				}
				return true;
			}
			else {
				points.add(source);
				points.add(destination);
				return false;
			}
		}
		else return false;
	}
	
	private static double getValueOnEdge(double sourceValue, double subtractValue, double index) {
		return sourceValue + (subtractValue * (index / SUBDIVISION_NUMBER));
	}
	
}
