package se.oru.aass.semrob;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import se.oru.aass.mpi.geo.featureProcessing.GeometryAnalyser;
import se.oru.aass.semrob.geometry.E_RCC;
import se.oru.aass.semrob.motionPlanning.collisionDetection.Box;
import se.oru.aass.semrob.motionPlanning.collisionDetection.CollisionDetector;
import se.oru.aass.semrob.motionPlanning.collisionDetection.Line;


public class Test {
	
	public static void main(String[] args) throws ParseException {
		WKTReader wkt = new WKTReader();
		Polygon polygon = (Polygon) wkt.read("POLYGON ((1 6, 2 5, 3 6, 4 5, 3 8, 1 6))");
		Envelope envelope = polygon.getEnvelopeInternal();
		Box box = new Box((int) envelope.getMinX(), (int) envelope.getMinY(), (int) envelope.getMaxX(), (int) envelope.getMaxY(), 70);
		//Box box = new Box(3, 1, 9, 5, 4);
		Coordinate point1 = new Coordinate(4, 9, 7);
		Coordinate point2 = new Coordinate(5, 7, 10);
		Line line = new Line(point1, point2);
		
		CollisionDetector obj = new CollisionDetector();
		System.out.println(obj.hasCollision(line, box));
		
		
		
		polygon = (Polygon) wkt.read("POLYGON ((10 10, 30 10, 30 30, 10 30, 10 10))");
		Polygon polygon1 = (Polygon) wkt.read("POLYGON ((20 20, 25 20, 25 25, 20 25, 20 20))");
		
		E_RCC rcc = se.oru.aass.semrob.geometry.GeometryAnalyser.getRCCRelation(polygon1, polygon);
		System.out.println(rcc.name().toString());
		if (rcc.equals(E_RCC.NTPPi))
			System.out.println(":D");
		
		Polygon p1 = (Polygon) wkt.read("POLYGON ((10 10, 20 10, 30 20, 20 40, 10 10))");
		Polygon p2 = (Polygon) wkt.read("POLYGON ((25 10, 40 10, 40 40, 20 40, 25 10))");
		
		p1 = (Polygon) wkt.read("POLYGON ((20 10, 20 20, 20 30, 30 30, 40 30, 40 10, 20 10))");
		p2 = (Polygon) wkt.read("POLYGON ((10 30, 10 40, 40 40, 40 30, 30 30, 20 30, 10 30))");
		
		Geometry u1 = p1.union(p2);
		Polygon p3 = (Polygon) wkt.read("POLYGON ((25 10, 40 10, 40 40, 20 40, 25 10))");
		Geometry u2 = u1.union(p3);
		
		
		
		p1 = (Polygon) wkt.read("POLYGON ((10 10, 10 40, 40 40, 40 10, 10 10),(20 20, 20 30, 30 30, 30 20, 20 20))");
		p2 = (Polygon) wkt.read("POLYGON ((10 41, 10 50, 40 50, 40 41, 10 41))");
		u2 = p1.union(p2);
		
		System.out.println(u2.getNumGeometries());
		System.out.println(":D");
	
		
		p1 = (Polygon) wkt.read("POLYGON ((10 10, 10 40, 40 40, 40 10, 10 10))");
		p2 = (Polygon) wkt.read("POLYGON ((20 20, 20 50, 30 50, 30 20, 20 20))");
		System.err.println(se.oru.aass.semrob.geometry.GeometryAnalyser.getRCCRelation(p1, p2));
		
		String s = se.oru.aass.semrob.geometry.GeometryAnalyser.convertToWKTPolygon(p1);
		System.out.println(s.substring(9, s.length()-1));
	
	}
	
	

}
