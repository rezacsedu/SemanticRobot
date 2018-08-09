package se.oru.mpi.rtree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.Node;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import gnu.trove.TIntProcedure;


public class RTree {
	private com.infomatiq.jsi.rtree.RTree rtree;
	
	class ListIntProcedure implements TIntProcedure{
		private List<Integer> ids = new ArrayList<Integer>();

	    public boolean execute(int id) {
	        ids.add(id);
	        return true;
	    }
	    
	    private List<Integer> getIDs() {
	        return ids;
	    }
	}
	
	public RTree () {
		rtree = new com.infomatiq.jsi.rtree.RTree();
		rtree.init(null);
	}
	
	public void add (int ID, int minX, int minY, int maxX, int maxY) {
		Rectangle rectangle = new Rectangle(minX, minY, maxX, maxY);
		rtree.add(rectangle, ID);
	}
	
	public void add (int ID, Coordinate coordinate) {
		add(ID, (int) coordinate.x, (int) coordinate.y, (int) coordinate.x, (int) coordinate.y);
	}
	
	public void add (int ID, Geometry geometry) {
		Envelope envelope  = geometry.getEnvelopeInternal();
		add(ID, (int) envelope.getMinX(), (int) envelope.getMinY(), (int) envelope.getMaxX(), (int) envelope.getMaxY());
	}
	
	/**
	 * This method returns back all the IDs of rectangles containing the given point
	 * @param x
	 * @param y
	 * @return
	 */
	public List<Integer> getBoundingBox(int x, int y) {
		ListIntProcedure solution = new ListIntProcedure();
		rtree.intersects(new Rectangle(x, y, x, y), solution);
		//return solution.getIDs();
		return new ArrayList<Integer>(new HashSet<Integer>(solution.getIDs()));
	}
	
	
	public List<Integer> findNearestBoundingBox(int x, int y, int number, float distance) {
		ListIntProcedure solution = new ListIntProcedure();
		com.infomatiq.jsi.Point point =	new com.infomatiq.jsi.Point(x, y);
		//rtree.nearestNUnsorted(point, solution, numberOfNearest, distance);
		rtree.nearestN(point, solution, number, distance);
		
		//return solution.getIDs();
		return new ArrayList<Integer>(new HashSet<Integer>(solution.getIDs()));
	}

	public List<Integer> findIntersectingBoundingBox(int minX, int minY, int maxX, int maxY) {
		Rectangle rectangle = new Rectangle(minX, minY, maxX, maxY);
		ListIntProcedure solution = new ListIntProcedure();
		rtree.intersects(rectangle, solution);
		
		//return solution.getIDs();
		return new ArrayList<Integer>(new HashSet<Integer>(solution.getIDs()));
	}
	
	public List<Integer> findIntersectingBoundingBox (Geometry geometry) {
		Envelope envelope  = geometry.getEnvelopeInternal();
		return findIntersectingBoundingBox((int) envelope.getMinX(), (int) envelope.getMinY(), (int) envelope.getMaxX(), (int) envelope.getMaxY());
	}
	
	public int size() {
		return rtree.size();
	}

	public Node getNode (int ID) {
		return rtree.getNode(ID);
	}

}
