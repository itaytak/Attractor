/**
 * 
 */
package booleanNetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.ss.formula.eval.NotImplementedException;

import utils.BooleanFunction;
import utils.NamedBoolean;
import utils.StateOperations;
import utils.TruthRow;

/**
 * @author Itay
 *
 */
public class TTNode extends Node {
	private HashMap<String, Boolean> truthTable;

	/**
	 * @param name
	 * @param value
	 */
	public TTNode(Node n) {
		super(n.getName(), n.getValue(), n.getSortedNeighbours());
		populateTruthTable(n);
	}
	
	public TTNode(TTNode n, Boolean futureValue) {
		this(n, futureValue, false);
	}
	
	public TTNode(TTNode n, Boolean futureValue, boolean duplicateConditions) {
		super(n.getName(), futureValue, n.getSortedNeighbours());
		
		if(!duplicateConditions) {
			this.truthTable = n.truthTable;
		} else {
			this.truthTable = new HashMap<String, Boolean>();
			for(Entry<String, Boolean> e : n.truthTable.entrySet()) {
				String state = new String(e.getKey());
				Boolean value = new Boolean(e.getValue());
				this.truthTable.put(state, value);
			}
		}
	}

	/**
	 * @return the truthTable
	 */
	public HashMap<String, Boolean> getTruthTable() {
		return truthTable;
	}

	/**
	 * @param truthTable the truthTable to set
	 */
	public void setTruthTable(HashMap<String, Boolean> truthTable) {
		this.truthTable = truthTable;
	}

	private void populateTruthTable(Node n) {
		this.truthTable = new HashMap<String, Boolean>();
		InitTruthTable();
		addExistingFunctions(n.getConditionsTable());
	}

	private void InitTruthTable() {
		int size = (int)Math.pow(2, this.sortedNeighbours.size());
		
		for(int i = 0; i < size; i++) {
			String booleanState = StateOperations.generateState(this.sortedNeighbours.size(), i);
			this.truthTable.put(booleanState, new Boolean(false));
		}
	}

	private void addExistingFunctions(ArrayList<TruthRow> conditionsTable) {
		for(TruthRow tr : conditionsTable) {
			if(tr instanceof BooleanFunction) {
				addFunction(tr);
			} else {
				throw new NotImplementedException("TTNode was originally designed to be populated from boolean functions");
			}
		}
		
	}

	private void addFunction(TruthRow tr) {
		int freeVarsAmount = this.sortedNeighbours.size() - tr.getConditions().size();
		int freeStatesSize = (int)Math.pow(2, freeVarsAmount);
		
		for(int i = 0; i < freeStatesSize; ++i) {
			String freeVarsState = StateOperations.generateState(freeVarsAmount, i);
			addState(tr, freeVarsState, tr.getFutureValue());
		}
		
	}

	private void addState(TruthRow tr, String freeVarsState, boolean futureValue) {
		StringBuffer sb = new StringBuffer();
		int freeVarsCounter = 0;
		
		for(int i = 0; i < this.sortedNeighbours.size(); i++) {
			NamedBoolean nb = tr.getNamedBoolean(this.sortedNeighbours.get(i));
			
			if(nb == null) {
				sb.append(freeVarsState.charAt(freeVarsCounter));
				++freeVarsCounter;
			} else {
				if(nb.getValue() == null) {
					throw new IllegalStateException("Boolean function contails valueless variable");
				}
				
				char nodeVal = (nb.getValue() == true) ? '1' : '0';
				sb.append(nodeVal);
			}
		}
		
		String neighboursState = sb.toString();
		this.truthTable.put(neighboursState, futureValue);
	}

	/* (non-Javadoc)
	 * @see booleanNetwork.Node#trigger(java.util.HashMap)
	 */
	@Override
	public Node trigger(HashMap<String, Node> currStateGraph) {
		Boolean futureValue = getNextValue(currStateGraph);
		
		return new TTNode(this, futureValue);
	}
	
	@Override
	public Boolean getNextValue(HashMap<String, Node> currStateGraph) {
		Boolean futureValue = null;
		
		if(!this.isOff) {
			StringBuffer sb = new StringBuffer();
			
			for(String neighbour : this.sortedNeighbours) {
				Node n = currStateGraph.get(neighbour);
				if(n.getValue() == null) {
					throw new IllegalStateException("Encountered valueless node");
				}
				char nodeValue = (n.getValue() == true) ? '1' : '0';
				sb.append(nodeValue);
			}
			futureValue = this.truthTable.get(sb.toString());
			
			if(futureValue == null) {
				throw new IllegalStateException("Encountered missing state: " + sb.toString());
			}
		} else {
			futureValue = new Boolean(false);
		}
		
		return futureValue;
	}

	/* (non-Javadoc)
	 * @see booleanNetwork.Node#mutateTruthValues(java.lang.Double)
	 */
	@Override
	public void mutateTruthValues(Double mutationRatio) {
		for(Entry<String, Boolean> e : this.truthTable.entrySet()) {
			Double mutationFactor = new Double(Math.random());
			if(mutationFactor.compareTo(mutationRatio) >= 0) {
				Boolean currRes = e.getValue();
				Boolean futureRes = new Boolean(!currRes);
				this.truthTable.put(e.getKey(), futureRes);
			}
		}
	}
	
	public String getTruthTableOutputs() {
		StringBuffer sb = new StringBuffer();
		
		for(String input : truthTable.keySet()) {
			Boolean output = this.truthTable.get(input);
			if(output == null) {
				throw new IllegalStateException("Encountered missing output for input: " + input);
			}
			
			char outputCh = (output == true) ? '1' : '0';
			sb.append(outputCh);
		}
		
		return sb.toString();
	}
	
	public void setTruthTableOutputs(String outputs, Set<String> keys) {
		int tRowCounter = 0;
		
		for(String row : keys) {
			Boolean rowOutput = (outputs.charAt(tRowCounter) == '1') ? 
					new Boolean(true) : new Boolean(false);
			this.truthTable.put(row, rowOutput);
			++tRowCounter;
		}
	}

	/* (non-Javadoc)
	 * @see booleanNetwork.Node#conditionsToString()
	 */
	@Override
	public String conditionsToString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("Boolean logic:\n");
		for(Entry<String, Boolean> e : this.truthTable.entrySet()) {
			char resVal = (e.getValue() == true) ? '1' : '0';
			sb.append(String.format("Row result: %c\n", resVal));
			
			for(int i = 0; i < this.sortedNeighbours.size(); ++i) {
				sb.append(String.format("| %s = %c ", this.sortedNeighbours.get(i), e.getKey().charAt(i)));
			}
			sb.append("|\n");
		}
		
		return sb.toString();
	}
	
	
}
