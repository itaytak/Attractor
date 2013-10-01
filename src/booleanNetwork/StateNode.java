/**
 * 
 */
package booleanNetwork;

import java.util.ArrayList;

/**
 * @author Itay
 *
 */
public class StateNode {
	private String state;
	private String childState;
	private ArrayList<String> fatherStates;
	/**
	 * @param state
	 * @param childState
	 */
	public StateNode(String state, String childState) {
		super();
		this.state = state;
		this.childState = childState;
		this.fatherStates = new ArrayList<String>();
	}
	/**
	 * @param state
	 */
	public StateNode(String state) {
		super();
		this.state = state;
		this.childState = null;
		this.fatherStates = new ArrayList<String>();
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the childState
	 */
	public String getChildState() {
		return childState;
	}
	/**
	 * @param childState the childState to set
	 */
	public void setChildState(String childState) {
		this.childState = childState;
	}
	/**
	 * @return the fatherStates
	 */
	public ArrayList<String> getFatherStates() {
		return fatherStates;
	}
	
	public void addFatherState(String fatherState) {
		if(!this.fatherStates.contains(fatherState)) {
			this.fatherStates.add(fatherState);
		}
	}
	
	
}
