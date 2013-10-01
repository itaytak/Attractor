/**
 * 
 */
package booleanNetwork;

import java.util.ArrayList;
import java.util.HashMap;

import utils.StateOperations;

/**
 * @author Itay
 *
 */
public class StatesGraph {
	
	Graph baseGraph;
	ArrayList<String> states;
	HashMap<String, StateNode> stateNetwork;
	
	/**
	 * @param graph
	 */
	public StatesGraph(Graph graph) {
		super();
		this.baseGraph = graph;
		this.stateNetwork = new HashMap<String, StateNode>();
		buildStates();
	}
	
	private void buildStates() {
		this.states = new ArrayList<String>();
		int fSize = this.baseGraph.getFunctionalSize();
		
		if(this.baseGraph != null && fSize > 0) {
			int size = (int)Math.pow(2, fSize);
			
			for(int i = 0; i < size; ++i) {
				//Graph g = new Graph(baseGraph);
				String state = StateOperations.generateState(fSize, i);
				//g.setState(state);
				this.states.add(state);
			}
		}
	}
	
	private void addStateToNetwork(String currState, String nextState) {
		StateNode curr = null;
		if(this.stateNetwork.containsKey(currState)) {
			curr = this.stateNetwork.get(currState);
			if(curr.getChildState() == null) {
				curr.setChildState(new String(nextState));
			} else {
				throw new IllegalStateException("Encountered the same current state twice: " + currState);
			}
		} else {
			curr = new StateNode(currState,nextState);
			this.stateNetwork.put(curr.getState(), curr);
		}
		
		StateNode next = null;
		if(this.stateNetwork.containsKey(nextState)) {
			next = this.stateNetwork.get(nextState);
			next.addFatherState(currState);
		} else {
			next = new StateNode(nextState);
			next.addFatherState(currState);
			this.stateNetwork.put(next.getState(), next);
		}
	}
	
	public void generateStateNetwork() {
		for(String currState : this.states) {
			this.baseGraph.setState(currState);
			String nextState = this.baseGraph.getNextState();
			addStateToNetwork(currState, nextState);	
		}
	}
	
	public HashMap<String, StateNode> triggerStateNetwork(HashMap<String, StateNode> sn) {
		HashMap<String, StateNode> nextTimeStamp = new HashMap<String, StateNode>();
		
		for(StateNode node : sn.values()) {
			String childState = node.getChildState();
			StateNode child = sn.get(childState);
			if(child != null) {
				if(!nextTimeStamp.containsKey(child.getState())) {
					nextTimeStamp.put(child.getState(), child);
				}
			} else {
				throw new IllegalStateException("Found state with missing child sate in network. childState: " + childState);
			}
		}
		
		return nextTimeStamp;
	}
	
	/**
	 * @return the baseGraph
	 */
	public Graph getBaseGraph() {
		return baseGraph;
	}

	/**
	 * @return the stateNetwork
	 */
	public HashMap<String, StateNode> getStateNetwork() {
		return stateNetwork;
	}
	
}
