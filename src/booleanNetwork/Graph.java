/**
 * 
 */
package booleanNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Itay
 *
 */
public class Graph {
	private static ArrayList<String> sortedNodeNames;
	private HashMap<String, Node> nodes;

	/**
	 * 
	 */
	public Graph() {
		super();
		this.nodes = new HashMap<String, Node>();
		
		if(Graph.sortedNodeNames == null) {
			Graph.sortedNodeNames = new ArrayList<String>();
		}
	}
	/**
	 * @param graph
	 */
	public Graph(Graph graph) {
		this(graph, false);
	}
	/**
	 * @param graph
	 * @param duplicateConditions
	 */
	public Graph(Graph graph, boolean duplicateConditions) {
		super();
		this.nodes = new HashMap<String, Node>();
		HashMap<String, Node> nodes = graph.getNodes();
		
		if(nodes != null) {
			for(Node n : nodes.values()) {
				if(n instanceof TTNode) {
					TTNode ttn = (TTNode)n;
					addNode(new TTNode(ttn, null, duplicateConditions));
				} else {
					addNode(new Node(n, null, duplicateConditions));
				}
			}
		}
	}
	
	public static Graph copyWithTTNodes(Graph graph) {
		if(Graph.sortedNodeNames == null) {
			Graph.sortedNodeNames = new ArrayList<String>();
		}
		Graph copy = new Graph();
		HashMap<String, Node> nodes = graph.getNodes();
		
		if(nodes != null) {
			for(Node n : nodes.values()) {
				if(n instanceof TTNode) {
					TTNode ttn = (TTNode)n;
					copy.addNode(new TTNode(ttn, null, true));
				} else {
					copy.addNode(new TTNode(n));
				}
			}
		}
		
		return copy;
	}

	/**
	 * @return the nodes
	 */
	public HashMap<String, Node> getNodes() {
		return nodes;
	}
	
	/**
	 * @return the sortedNodeNames
	 */
	public static ArrayList<String> getSortedNodeNames() {
		return sortedNodeNames;
	}
	
	/**
	 * @return the size
	 */
	public int getSize() {
		return this.nodes.size();
	}

	/**
	 * @return the number of nodes that are not setOff
	 */
	public int getFunctionalSize() {
		int fSize = 0;
		
		for (Node n : this.nodes.values()) {
			if(!n.getIsOff()) {
				fSize++;
			}
		}
		
		return fSize;
	}
	
	public void addNode(Node n) {
		if(!this.nodes.containsKey(n.getName())) {
			this.nodes.put(n.getName(), n);
			
			if((Graph.sortedNodeNames.size() < this.nodes.size()) && !Graph.sortedNodeNames.contains(n.getName())) {
				Graph.sortedNodeNames.add(n.getName());
				Collections.sort(Graph.sortedNodeNames);
			}
		}
	}
	
	public void setOffNode(String nodeName) {
		if(this.nodes.containsKey(nodeName)) {
			Node n = this.nodes.get(nodeName);
			n.setOff();
		}
	}
	
	public Graph trigger() {
		Graph res = new Graph();
		
		for(Node currState : this.nodes.values()) {
			Node nextState = currState.trigger(this.nodes);
			res.addNode(nextState);
		}
		
		return res;
	}
	
	public String getNextState() {
		StringBuffer sb = new StringBuffer();
		
		for(String nodeName : Graph.sortedNodeNames) {
			Node n = this.nodes.get(nodeName);
			if(!n.getIsOff()) {
				Boolean nextVal = n.getNextValue(this.nodes);
				char nextValCh = (nextVal == true) ? '1' : '0';
				sb.append(nextValCh);
			}
		}
		
		return sb.toString();
	}
	
	public void setState(String binaryString) {
		if(binaryString.length() > this.nodes.size()) {
			throw new IllegalArgumentException(String.format("Binary string: %s size doesn't match graph size", binaryString));
		}
		
		int nodeInd = 0;
		char nodeVal = '\0';
		
		for(String nodeName : Graph.sortedNodeNames) {
			Node n = this.nodes.get(nodeName);
			if(!n.getIsOff()) {
				nodeVal = binaryString.charAt(nodeInd);
				
				if(nodeVal == '1') {
					n.setValue(true);
				} else if (nodeVal == '0') {
					n.setValue(false);
				} else {
					throw new IllegalArgumentException(String.format("Binary string: %s is not legal", binaryString));
				}
				++nodeInd;
			} else {
				n.setValue(false);
			}
		}
	}
	
	public String getState() {
		StringBuffer sb = new StringBuffer();
		
		for(String nodeName : Graph.sortedNodeNames) {
			Node n = this.nodes.get(nodeName);
			if(!n.getIsOff()) {
				char nodeValue = (n.getValue() == true) ? '1' : '0';
				sb.append(nodeValue);
			}
		}
		
		return sb.toString();
	}
	
	public String getExtendedState() {
		StringBuffer sb = new StringBuffer("| ");
		
		for(String nodeName : Graph.sortedNodeNames) {
			Node n = this.nodes.get(nodeName);
			char nodeValue = (n.getValue() == true) ? '1' : '0';
			sb.append(String.format("%s = %c | ", n.getName(), nodeValue));
		}
		
		return sb.toString();
	}
	
	public String getExtendedState(String state) {
		StringBuffer sb = new StringBuffer("| ");
		int nodeInd = 0;
		char nodeValue = '\0';
		
		
		for(String nodeName : Graph.sortedNodeNames) {
			Node n = this.nodes.get(nodeName);
			nodeValue = state.charAt(nodeInd);
			sb.append(String.format("%s = %c | ", n.getName(), nodeValue));
			++nodeInd;
		}
		
		return sb.toString();
	}
	
	public void mutateTruthValues(Double mutationRatio) {
		for(Node n : this.nodes.values()) {
			n.mutateTruthValues(mutationRatio);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof Graph)) {
			return false;
		}

		Graph other = (Graph) obj;
		if (nodes == null) {
			if (other.nodes != null) {
				return false;
			}
		} else if (!nodes.equals(other.nodes)) {
			return false;
		}
		return true;
	}
	
}
