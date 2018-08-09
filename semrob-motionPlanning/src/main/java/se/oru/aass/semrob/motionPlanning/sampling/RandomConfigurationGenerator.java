package se.oru.aass.semrob.motionPlanning.sampling;

import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;

import se.oru.aass.semrob.motionPlanning.RRTMotionPlanner;
import se.oru.aass.semrob.motionPlanning.entity.Configuration;
import se.oru.aass.semrob.setting.NumericRange;


public class RandomConfigurationGenerator {
	
	
	public static Configuration getSampleConfiguration(Random random, NumericRange xRange, NumericRange yRange, NumericRange zRange) {
		int sampleCoordinateX = getRandomValue(random, xRange);
		int sampleCoordinateY = getRandomValue(random, yRange);
		
		//System.out.println("sample coordinate-->   X: " + sampleCoordinateX + ", Y: " + sampleCoordinateY);
		
		/*int sampleCoordinateZ = getRandomValue(random, zRange);
		Coordinate coordinate = new Coordinate(sampleCoordinateX, sampleCoordinateY, sampleCoordinateZ);
		return RRTMotionPlanner.getConfiguration(coordinate);*/

		/*RRTMotionPlanner.RegionElevationValue regionElevationValue = RRTMotionPlanner.getElevationOfHoldingRegion(sampleCoordinateX, sampleCoordinateY);
		if (regionElevationValue != null) {
			double difference = regionElevationValue.getRegionAbsoluteElevation() - regionElevationValue.getRegionRelativeElevation();
		
			// if the elevation of the region is within the valid range of z-axis, we get a random z-value within a new range between the elevation of the region (a bit higher than this) and the maximum possible elevation value
			if (regionElevationValue.getRegionRelativeElevation() <= zRange.getMaxValue()) {
				int sampleCoordinateZ = getRandomValue(random, 
						new NumericRange(regionElevationValue.getRegionAbsoluteElevation() + RRTMotionPlanner.OFFSET_ELEVATION_VALUE, zRange.getMaxValue() + difference));
				Coordinate coordinate = new Coordinate(sampleCoordinateX, sampleCoordinateY, sampleCoordinateZ);
				return RRTMotionPlanner.getConfiguration(coordinate);
			}
			// otherwise, we return null configuration
			else return null;
		}
		else return null;*/
		
		
		// we consider the zRange based on the absolute values and not relative!
		RRTMotionPlanner.RegionElevationValue regionElevationValue = RRTMotionPlanner.getElevationOfHoldingRegion(sampleCoordinateX, sampleCoordinateY);
		if (regionElevationValue != null) {
			
			// if the elevation of the region is within the valid range of z-axis, we get a random z-value within a new range between the elevation of the region (a bit higher than this) and the maximum possible elevation value
			if (regionElevationValue.getRegionAbsoluteElevation() <= zRange.getMaxValue()) {
				int sampleCoordinateZ = getRandomValue(random, 
						new NumericRange(regionElevationValue.getRegionAbsoluteElevation() + RRTMotionPlanner.OFFSET_ELEVATION_VALUE, zRange.getMaxValue()));
				Coordinate coordinate = new Coordinate(sampleCoordinateX, sampleCoordinateY, sampleCoordinateZ);
				return RRTMotionPlanner.getConfiguration(coordinate);
			}
			// otherwise, we return null configuration
			else return null;
		}
		else return null;
	}
	
	private static int getRandomValue(Random random, NumericRange range) {
		//return range.getMinValue() + (int)(Math.random() * range.getMaxValue());
		int n = (int) range.getMinValue() + random.nextInt((int) range.getMaxValue() - (int) range.getMinValue() + 1);
		if (n < 0)
			System.out.println("here...");
		return n;
	}
	
}