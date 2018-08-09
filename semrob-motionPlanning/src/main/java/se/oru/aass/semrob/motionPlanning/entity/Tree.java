package se.oru.aass.semrob.motionPlanning.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;

public class Tree {
	public static final int  INVALID_NODE_INDEX = -1;
	public static final int  TREE_ROOT_PARENT_INDEX = INVALID_NODE_INDEX;
	private int treeIndex = 0;
	HashMap<Integer, Node> mapTree;
	
	
	public Tree(Configuration root) {
		mapTree = new HashMap<>();
		addConfiguration(TREE_ROOT_PARENT_INDEX, root);
	}
	
	public Tree() {
		mapTree = new HashMap<>();
	}
	
	/**
	 * This method adds a new configuration to the tree and returns back its index
	 * @param parentIndex
	 * @param configuration
	 * @return
	 */
	public int addConfiguration(int parentIndex, Configuration configuration) {
		//mapTree.put(treeIndex, new Node(parentIndex, new ArrayList<>(), configuration));
		mapTree.put(treeIndex, new Node(parentIndex, configuration));
		treeIndex ++;
		return (treeIndex - 1);
	}
	
	public Configuration getConfigurationNode(int nodeIndex) {
		return mapTree.get(nodeIndex).getConfiguration();
	}
	
	public Set<Integer> getNodes() {
		return mapTree.keySet();
	}
	
	public int getNearestConfigurationIndex(Configuration configuration) {
		double minimumDistance = Double.MAX_VALUE;
		int nearestNodeIndex = INVALID_NODE_INDEX;
		Set<Integer> indexSet = mapTree.keySet();
		Coordinate coordinate = configuration.getParticles().get(0).getCoordinate();
		for (int index : indexSet) {
			Configuration nodeConfig = mapTree.get(index).getConfiguration();
			Coordinate nodeCoordinate = nodeConfig.getParticles().get(0).getCoordinate();
			double distance = getDistance(coordinate, nodeCoordinate);
			if (distance == 0) {
				nearestNodeIndex = INVALID_NODE_INDEX;
				break;
			}
			else if (distance < minimumDistance) {
				minimumDistance = distance;
				nearestNodeIndex = index;
			}
			
		}
		return nearestNodeIndex;
	}

	
	private static double getDistance(Coordinate coord1, Coordinate coord2) {
	    float dx = (float) (coord1.x - coord2.x);
	    float dy = (float) (coord1.y - coord2.y);
	    float dz = (float) (coord1.z - coord2.z);

	    return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	/**
	 * This method prints the nodes located on the path from the given nodeIndex to the root of the tree
	 * @param destinationNodeIndex
	 */
	public void printTree(int nodeIndex) {
		while (nodeIndex != TREE_ROOT_PARENT_INDEX) {
			Node node = mapTree.get(nodeIndex);
			Coordinate coordinate = node.getConfiguration().getParticles().get(0).getCoordinate();
			System.out.println("NodeIndex: " + nodeIndex + " at: (" + coordinate.x + "," + coordinate.y + ", " + coordinate.z + ")");
			nodeIndex = node.getParentIndex();
		}
	}
	
	/**
	 * This method return the nodes located on the path from the given nodeIndex to the root of the tree
	 * @param destinationNodeIndex
	 */
	public List<Coordinate> getPath(int nodeIndex) {
		List<Coordinate> pathInfo = new ArrayList<>();
		while (nodeIndex != TREE_ROOT_PARENT_INDEX) {
			Node node = mapTree.get(nodeIndex);
			pathInfo.add(node.getConfiguration().getParticles().get(0).getCoordinate());
			nodeIndex = node.getParentIndex();
		}
		return pathInfo;
	}
}
