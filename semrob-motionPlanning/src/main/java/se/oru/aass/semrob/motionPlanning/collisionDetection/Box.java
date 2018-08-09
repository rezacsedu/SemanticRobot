package se.oru.aass.semrob.motionPlanning.collisionDetection;

import com.vividsolutions.jts.geom.Coordinate;

public class Box {
	Coordinate smallestCoordinate;
	Coordinate largestCoordinate;

	public Box(int boundingBoxMinx, int boundingBoxMinY, int boundingBoxMaxX, int boundingBoxMaxY, int height) {
		this.smallestCoordinate = new Coordinate(boundingBoxMinx, boundingBoxMinY, 0);
		this.largestCoordinate = new Coordinate(boundingBoxMaxX, boundingBoxMaxY, height);
	}

	public Coordinate getLargestCoordinate() {
		return largestCoordinate;
	}

	public Coordinate getSmallestCoordinate() {
		return smallestCoordinate;
	}
}
