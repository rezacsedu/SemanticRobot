package se.oru.aass.semrob.motionPlanning.entity;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

public class Configuration {
	List<Particle> particles = new ArrayList<>();
	
	public Configuration() {
	}
	
	public Configuration(List<Particle> particles) {
		this.particles = particles;
	}
	
	/**
	 * This method returns the number of particles that exist in a configuration, where each particle has its own features (e.g., coordinates, degree, etc,...)
	 * @return
	 */
	public int getDimentionSize() {
		return particles.size();
	}
	
	public List<Particle> getParticles() {
		return particles;
	}
	
	public void addParticle(Particle particle) {
		particles.add(particle);
	}
	
	/**
	 * A particle indicates a part of a system (or a particle) whose positions can be represented in the form of coordination, rotations, angel, etc
	 * @author marjan
	 *
	 */
	public static class Particle {
		private Coordinate coordinate;
		public Particle(Coordinate coordinate) {
			this.coordinate = coordinate;
		}
		
		public Coordinate getCoordinate() {
			return coordinate;
		}
		
		public void setCoordinate(Coordinate coordinate) {
			this.coordinate = coordinate;
		}
	}
}
