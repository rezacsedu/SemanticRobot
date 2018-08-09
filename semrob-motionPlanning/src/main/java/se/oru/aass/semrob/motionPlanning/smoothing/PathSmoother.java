package se.oru.aass.semrob.motionPlanning.smoothing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;

import se.oru.aass.semrob.motionPlanning.RRTMotionPlanner;

public class PathSmoother {
	private static final int RECONNECTING_CHECK_LIMIT = 3000;
	//private static final int PATH_POINT_NUMBER_LIMIT = 5;
	private static final int SMOOTHING_CHECK_LIMIT = 500;
	private List<Coordinate> pathPoints = null;
	private Random random;
	private int priorityIndex;
	public PathSmoother(List<Coordinate> pathPoints, Random random, int priorityIndex) {
		this.pathPoints = pathPoints;
		this.random = random;
		this.priorityIndex = priorityIndex;
	}
		
	public List<Coordinate> getSmoothPath() {
		if (pathPoints != null)
			return smooth(pathPoints);
		else return null;
	}
	
	private List<Coordinate> smooth(List<Coordinate> pathPoints) {
		int smoothingLoopNumber = 0;
		int recounnectingNumber = 0;
		List<Coordinate> points = null;
		List<Edge> edges = null;
		boolean hasMinimumEdge = false;
		while (smoothingLoopNumber < SMOOTHING_CHECK_LIMIT) {
			smoothingLoopNumber ++;
			
		
			/*
			 * it means that there was no reconnecting happened at the previous try, so there is no meaning to do subdivision again!
			 */
			if (recounnectingNumber == RECONNECTING_CHECK_LIMIT) { 
				break;
			}
			
			recounnectingNumber = 0;
			points = new ArrayList<>();
			edges = new ArrayList<>();
			boolean hasAnySubDividedEdge = false;
			for (int pointIndex = 0; pointIndex < pathPoints.size() - 1; pointIndex++) {

				Edge edge = new Edge(pathPoints.get(pointIndex), pathPoints.get(pointIndex + 1));
				if (edge.subDivide())
					hasAnySubDividedEdge = true;
				edges.add(edge);

				
				List<Coordinate> inlinePoints = null;
				if (pointIndex == pathPoints.size() - 2)
					inlinePoints = edge.getPoints(Edge.EDGE_POINT_ALL);
				else
					inlinePoints = edge.getPoints(Edge.EDGE_POINT_WITHOUT_DESTINATION);
				if (inlinePoints != null)
					points.addAll(inlinePoints);
			}
			pathPoints = new ArrayList<>(points);
			
			if (hasAnySubDividedEdge) {
				hasMinimumEdge = false;
				while(recounnectingNumber < RECONNECTING_CHECK_LIMIT) {
					List<Integer> edgeIndexes = getTwoDifferentEdgeIndexes(random, edges.size());
					if (edgeIndexes.size() == 2) {
						List<Coordinate> edge1Points =  edges.get(edgeIndexes.get(0)).getPoints(Edge.EDGE_POINT_INTERNAL);
						List<Coordinate> edge2Points =  edges.get(edgeIndexes.get(1)).getPoints(Edge.EDGE_POINT_INTERNAL);
						
						if (edge1Points != null && edge2Points != null) {
							if (edge1Points.size() > 0 && edge2Points.size() > 0) {
								Coordinate coordinate1 =  edge1Points.get(getRandomPointIndex(random, edge1Points.size()));
								Coordinate coordinate2 =  edge2Points.get(getRandomPointIndex(random, edge2Points.size()));
							
								if (RRTMotionPlanner.isConnectible(coordinate1, coordinate2, priorityIndex)) {
									pathPoints = getUpdatedPointList(pathPoints, edges, edgeIndexes, coordinate1, coordinate2);
									//pathPoints = getPathWithMinimumEdge(pathPoints);
									hasMinimumEdge = true;
									break;
								}
							}
						}
						recounnectingNumber ++;
					}
					else break;
				}
			}
			else
				hasMinimumEdge = true;
		}
		if (hasMinimumEdge)
			return pathPoints;
		else
			return getPathWithMinimumEdge(pathPoints);
	}
		
	private static List<Coordinate> getUpdatedPointList(List<Coordinate> points, List<Edge> edges, List<Integer> selectedEdges, Coordinate coordinate1, Coordinate coordinate2) {
		/*int indexMin = points.indexOf(coordinate1);
		int indexMax = points.indexOf(coordinate2);
		if (indexMax < indexMin) {
			int temp = indexMax;
			indexMax = indexMin;
			indexMin = temp;
		}
		
		List<Coordinate> updatedPoints = new ArrayList<>();
		updatedPoints.addAll(points.subList(0, indexMin + 1)); // to include indexMin item as well
		updatedPoints.addAll(points.subList(indexMax, points.size()));
		return updatedPoints;*/
		
		int edgeIndexMin = selectedEdges.get(0);
		int edgeIndexMax = selectedEdges.get(1);
		int indexMin = points.indexOf(coordinate1);
		int indexMax = points.indexOf(coordinate2);
		if (indexMax < indexMin) {
			int temp = indexMax;
			indexMax = indexMin;
			indexMin = temp;
			
			temp = edgeIndexMax;
			edgeIndexMax = edgeIndexMin;
			edgeIndexMin = temp;
		}
		
		List<Coordinate> updatedPoints = new ArrayList<>();
		for (int i = 0; i < edges.size(); i ++) {
			if (i < edgeIndexMin) {
				updatedPoints.add(edges.get(i).getSource());
			}
			else if (i == edgeIndexMin) {
				updatedPoints.add(edges.get(i).getSource());   // it is assumed that the points are selected from the middle of the edges and cannot be either the source or the destination
				updatedPoints.add(points.get(indexMin));
			}
			
			else if (i > edgeIndexMin && i < edgeIndexMax) {
				// do nothing
			}
			
			else if (i == edgeIndexMax) {
				updatedPoints.add(points.get(indexMax));
				updatedPoints.add(edges.get(i).getDestination()); 
			}
			
			else if (i > edgeIndexMax) {
				updatedPoints.add(edges.get(i).getDestination());
			}
		}
		return updatedPoints;
	}
	
	private static List<Integer> getTwoDifferentEdgeIndexes(Random random, int edgesNumber) {
		Set<Integer> set = new HashSet<>();
	    while (set.size() < 2) {
	        set.add(random.nextInt(edgesNumber));
	    }
	    return new ArrayList<Integer>(set);
	}
	
	private static int getRandomPointIndex(Random random, int pointsNumber) {
		return random.nextInt(pointsNumber);
		
	}
	
	private static List<Coordinate> getPathWithMinimumEdge(List<Coordinate> pathPoints) {
		int pointIndex = 0;
		List<Coordinate> finalPathPoints = new ArrayList<>(pathPoints);
		while (pointIndex <= pathPoints.size() - 3) {
			Coordinate coord1 = pathPoints.get(pointIndex);
			Coordinate coord2 = pathPoints.get(pointIndex + 1);
			Coordinate coord3 = pathPoints.get(pointIndex + 2);
			if (RRTMotionPlanner.getDistance(coord1, coord3) == 
					RRTMotionPlanner.getDistance(coord1, coord2) + RRTMotionPlanner.getDistance(coord2, coord3)) {
				finalPathPoints.remove(finalPathPoints.indexOf(coord2));
				pathPoints = new ArrayList<>(finalPathPoints);
			}
			else {
				pointIndex = pointIndex + 1;
			}
		}
		return pathPoints;
	}
}

