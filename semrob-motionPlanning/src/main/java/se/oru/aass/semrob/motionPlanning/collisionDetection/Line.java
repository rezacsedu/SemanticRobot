package se.oru.aass.semrob.motionPlanning.collisionDetection;

import com.vividsolutions.jts.geom.Coordinate;

public class Line {
	private Coordinate coordinate1;
	private Coordinate coordinate2;

	public Line(Coordinate coordinate1, Coordinate coordinate2) {
		this.coordinate1 = coordinate1;
		this.coordinate2 = coordinate2;
	}

	public Coordinate getFirstCoordinate() {
		return coordinate1;
	}

	public Coordinate getSecondCoordinate() {
		return coordinate2;
	}
}
