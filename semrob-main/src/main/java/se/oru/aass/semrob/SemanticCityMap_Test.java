package se.oru.aass.semrob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;

import se.oru.aass.mpi.geo.coordinateProcessing.CoordinateConverter;
import se.oru.aass.mpi.geo.coordinateProcessing.CoordinateConverter.LatLon;
import se.oru.aass.mpi.geo.coordinateProcessing.CoordinateConverter.UTM;

public class SemanticCityMap_Test {
	public static void main(String[] args) throws Exception   {
		
		List<String> al = new ArrayList<String>();

	     //Addition of elements in ArrayList
	     al.add("Steve");
	     al.add("Justin");
	     al.add("Ajeet");
	     al.add("John");
	     al.add("Arnold");
	     al.add("Chaitanya");

	     System.out.println(al.size());
	     al.add(0, "marjan");
	     
	     
	     System.out.println(al.size());
	     
	     List<String> al2 = new ArrayList<String>(al.subList(0, 4));
	     System.out.println("SubList stored in ArrayList: "+al2);
		
		int x = 0;
		int y = 0;
		int zone = 33;
		UTM utm1 = CoordinateConverter.xy2utm(new Coordinate(x, y), zone, CoordinateConverter.HEMISPHERE_LAT_NORTH);
		utm1.printUTM();
		

		double easting = 675472.000003148335963;
		double northing = -6579836.190335515886545;
		Coordinate coord = CoordinateConverter.utm2xy(new UTM(easting, northing, 33, CoordinateConverter.HEMISPHERE_LAT_NORTH));
		System.out.println(coord.x);
		System.out.println(coord.y);
		
		
		UTM utmCoordinate = CoordinateConverter.xy2utm(new Coordinate(coord.x, coord.y), 33, CoordinateConverter.HEMISPHERE_LAT_NORTH);
		utmCoordinate.printUTM();
		
		LatLon latlon = CoordinateConverter.utm2LatLon(easting, -1 * northing, 33, CoordinateConverter.HEMISPHERE_LAT_NORTH);
		latlon.printDMSLat();
		latlon.printDMSLon();
		latlon.printLat();
		latlon.printLon();
		
		
		x = 8192;
		y = 128838;
		utm1 = CoordinateConverter.xy2utm(new Coordinate(x, y), zone, CoordinateConverter.HEMISPHERE_LAT_NORTH);
		utm1.printUTM();
		
		Random rand = new Random();
		int w = 10 + rand.nextInt((int) 10 - (int) 10 + 1);
		System.out.println("random:" + w);
		
		
		Coordinate coord1 = new Coordinate(1, 2, 1);
		Coordinate coord2 = new Coordinate(2, 4, 2);
		Coordinate coord3 = new Coordinate(3, 6, 3);
		double n = coord1.distance(coord3);
		double m = coord1.distance(coord2) + coord2.distance(coord3); 
		
		double d13 = Math.sqrt( Math.pow((coord1.x-coord3.x), 2) + Math.pow((coord1.y-coord3.y), 2) + Math.pow((coord1.z-coord3.z), 2));
		double d12 = Math.sqrt( Math.pow((coord1.x-coord2.x), 2) + Math.pow((coord1.y-coord2.y), 2) + Math.pow((coord1.z-coord2.z), 2));
		double d23 = Math.sqrt( Math.pow((coord2.x-coord3.x), 2) + Math.pow((coord2.y-coord3.y), 2) + Math.pow((coord2.z-coord3.z), 2));
		
		
		
		if (d13 == d12+d23 )
			System.out.println(":D");
		else
			System.out.println(":(");
		
		easting = 672551.62;
		northing = 6582761.25;
		Coordinate coordSource = CoordinateConverter.utm2xy(new UTM(easting, -1 * northing, 33, CoordinateConverter.HEMISPHERE_LAT_NORTH));
		System.out.println(coordSource);
		
		HashMap<Integer, String> t = new HashMap<>();
		t.put(1, "marjan");
		t.put(2, "lida");
		t.put(3, "lili");
		System.out.println(t);
		System.out.println(t.size());
		t.put(2, "shahin");
		System.out.println(t);
		System.out.println(t.size());

	}

}
