/**
 * 
 */
package utils;

import java.util.ArrayList;
import java.util.HashMap;

import booleanNetwork.Graph;
import booleanNetwork.StateNode;
import booleanNetwork.StatesGraph;

/**
 * @author Itay
 *
 */
public class StateNetworkOperations {

	private static ArrayList<HashMap<String, StateNode>> generateAttractorData(Graph graph) {
		
		ArrayList<HashMap<String, StateNode>> attractorData = new ArrayList<HashMap<String, StateNode>>();
		StatesGraph sg = new StatesGraph(graph);
		sg.generateStateNetwork();
		HashMap<String, StateNode> stateNetwork = sg.getStateNetwork();
		int prevNumOfStates;
		int iterationsLimit = 200;
		
		do {
			attractorData.add(stateNetwork);
			prevNumOfStates = stateNetwork.size();
			stateNetwork = sg.triggerStateNetwork(stateNetwork);
			--iterationsLimit;
		} while(prevNumOfStates > stateNetwork.size() && iterationsLimit > 0);
		
		return attractorData;
	}
	
	private static ArrayList<Integer> generateConvergenceValues(ArrayList<HashMap<String, StateNode>> attractorData) {
		ArrayList<Integer> convergenceValues = new ArrayList<Integer>();
		
		for(HashMap<String, StateNode> sn : attractorData) {
			Integer convergenceValue = new Integer(sn.size());
			convergenceValues.add(convergenceValue);
		}
		
		return convergenceValues;
	}
	
	public static ArrayList<Integer> getConvergenceValues(Graph graph) {
		ArrayList<HashMap<String, StateNode>> attractorData = generateAttractorData(graph);
		ArrayList<Integer> convergenceValues = generateConvergenceValues(attractorData);
		
		return convergenceValues;
	}
	
	public static double[] getConvergenceValuesAsDoubleArr(Graph graph) {
		ArrayList<Integer> convergenceValues = getConvergenceValues(graph);
		double[] doubleConvergenceValues = new double[convergenceValues.size()];
		
		for(int i = 0; i < convergenceValues.size(); i++) {
			doubleConvergenceValues[i] = convergenceValues.get(i).doubleValue();
		}
		
		return doubleConvergenceValues;
	}
}
