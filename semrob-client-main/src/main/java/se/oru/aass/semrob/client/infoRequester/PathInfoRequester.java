package se.oru.aass.semrob.client.infoRequester;

import com.vividsolutions.jts.geom.Coordinate;

import se.oru.aass.mpi.geo.coordinateProcessing.CoordinateConverter;
import se.oru.aass.mpi.geo.coordinateProcessing.CoordinateConverter.UTM;

public class PathInfoRequester extends InfoPathRequester{
	
	public void printInfo(String response, double sourceEasting, double sourceNorthing, double sourceZ, double destinationEasting, double destinationNorthing, double destinationZ) {
		//System.out.println(response);
		boolean invalidPath = false;
		super.printInfo(response);
		String xmlResponse = "";
		if (response != null && response.trim().length() > 0) {
			String[] pathInfo = response.split(SPLITTER_SERVER_RESPONSE_INSTANCE);
			if (pathInfo.length > 0) {
				xmlResponse += XML_RESPONSE_HEADING;
				xmlResponse += "<path sourceX=\"" + sourceEasting + "\" sourceY=\"" + sourceNorthing + "\" sourceZ=\"" + sourceZ + 
						"\" destinationX=\"" + destinationEasting + "\" destinationY=\"" + destinationNorthing + "\" destinationZ=\"" + destinationZ +"\">";
				xmlResponse += "<length count=\"" + (int)(pathInfo.length - 1) + "\">";
				for (String info :  pathInfo) {
					if (info.length() > 0) {
						String[] coordinates = info.split(SPLITTER_SERVER_RESPONSE_INSTANCE_INFO);
						
						if (coordinates.length == 3) {
							
							double coordinateX = Double.parseDouble(coordinates[0]);
							double coordinateY = Double.parseDouble(coordinates[1]);
							UTM utmCoordinate = CoordinateConverter.xy2utm(new Coordinate(coordinateX, coordinateY), ZONE, CoordinateConverter.HEMISPHERE_LAT_NORTH);
							xmlResponse += utmCoordinate.easting + " " + (utmCoordinate.northing) * -1 + " " + coordinates[2] + " ";
						}
						else {
							invalidPath = true;
							break;
						}
					}
				}
				if (invalidPath) {
					xmlResponse = "";
				}
				else {
					xmlResponse += "</length>"; 
					xmlResponse += "</path>";
				}
			}
		}
		
		if (xmlResponse == "") {
			xmlResponse += XML_RESPONSE_HEADING;
			xmlResponse += "<path sourceX=\"" + sourceEasting + "\" sourceY=\"" + sourceNorthing + "\" sourceZ=\"" + sourceZ + 
					"\" destinationX=\"" + destinationEasting + "\" destinationY=\"" + destinationNorthing + "\" destinationZ=\"" + destinationZ +"\">";
			xmlResponse += "<length count=\"0\"></length>";
			xmlResponse += "</path>";
		}

		System.out.println(xmlResponse);

	}
}
