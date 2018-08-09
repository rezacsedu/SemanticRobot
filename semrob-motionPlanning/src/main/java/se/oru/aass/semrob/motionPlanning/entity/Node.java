package se.oru.aass.semrob.motionPlanning.entity;

import java.util.List;

public class Node {
	private int parentIndex;
	//private List<Integer> childrenIndexList;
	private Configuration configuration;
	/*public Node(int parentIndex, List<Integer> childrenIndexList, Configuration configuration) {
		this.parentIndex = parentIndex;
		this.childrenIndexList = childrenIndexList;
		this.configuration = configuration;
	}*/
	
	public Node(int parentIndex, Configuration configuration) {
		this.parentIndex = parentIndex;
		this.configuration = configuration;
	}
	
//	public List<Integer> getChildrenIndexList() {
//		return childrenIndexList;
//	}
	
	public Configuration getConfiguration() {
		return configuration;
	}
	
	public int getParentIndex() {
		return parentIndex;
	}
	
}
