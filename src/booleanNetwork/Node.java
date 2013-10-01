/**
 * 
 */
package booleanNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import utils.BooleanFunction;
import utils.NamedBoolean;
import utils.TruthRow;

/**
 * @author Itay
 *
 */
public class Node {
	private String name;
	private Boolean value;
	protected Boolean isOff;
	private ArrayList<TruthRow> conditionsTable;
	protected ArrayList<String> sortedNeighbours;

	/**
	 * @param name
	 * @param value
	 */
	public Node(String name, Boolean value, ArrayList<String> sortedNeighbours) {
		super();
		this.name = new String(name);
		this.value = (value != null) ? new Boolean(value) : null;
		this.isOff = new Boolean(false);
		this.conditionsTable = new ArrayList<TruthRow>();
		this.sortedNeighbours = sortedNeighbours;
	}
	/**
	 * @param n
	 * @param value
	 */
	public Node(Node n, Boolean value) {
		this(n, value, false);
	}
	/**
	 * @param n
	 * @param value
	 * @param duplicateConditions
	 */
	public Node(Node n, Boolean value, boolean duplicateConditions) {
		this(n.name, value, n.sortedNeighbours);
		
		if(n.isOff) {
			this.isOff = new Boolean(true);
		}
		
		if(!duplicateConditions)
		{
			this.conditionsTable = n.conditionsTable;
		} else {
			for(TruthRow tr : n.conditionsTable) {
				TruthRow copy = (tr instanceof BooleanFunction) ? 
						new BooleanFunction((BooleanFunction)tr) : new TruthRow(tr);
				this.conditionsTable.add(copy);
			}
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public Boolean getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Boolean value) {
		this.value = value;
	}
	
	public NamedBoolean getCurrState() {
		return new NamedBoolean(this.name, this.value);
	}
	
	/**
	 * @return the sortedNeighbours
	 */
	public ArrayList<String> getSortedNeighbours() {
		return sortedNeighbours;
	}

	/**
	 * @return the conditionsTable
	 */
	public ArrayList<TruthRow> getConditionsTable() {
		return conditionsTable;
	}
	
	/**
	 * @return the isOff
	 */
	public Boolean getIsOff() {
		return isOff;
	}
	
	public void setOff() {
		this.value = new Boolean(false);
		this.isOff = new Boolean(true);
	}
	
	public void addConditionsRow(TruthRow tr) {
		this.conditionsTable.add(tr);
	}
	
	public void addNeighbour(String neighbour) {
		if(!this.sortedNeighbours.contains(neighbour)) {
			this.sortedNeighbours.add(neighbour);
			Collections.sort(this.sortedNeighbours);
		}
	}

	public Node trigger(HashMap<String, Node> currStateGraph) {
		Boolean nextValue = getNextValue(currStateGraph);
		
		return new Node(this, nextValue);
	}
	
	public Boolean getNextValue(HashMap<String, Node> currStateGraph) {
		Boolean nextValue = null;
		
		if(!this.isOff) {
			for(TruthRow row : this.conditionsTable) {
				Boolean rowValue = row.trigger(currStateGraph);
				
				if(rowValue != null) {
					nextValue = rowValue;
					break;
				}
			}
		}
		if(nextValue == null) {
			nextValue = new Boolean(false);
		}
		
		return nextValue;
	}
	
	public void mutateTruthValues(Double mutationRatio) {
		for(TruthRow condition : this.conditionsTable) {
			condition.mutate(mutationRatio);
		}
	}
	
	public String basicDetailsToString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Name: " + this.name + "\n");
		sb.append("Value: " + this.value + "\n");
		
		return sb.toString();
	}
	
	public String neighboursToString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("Node neighbours:\n");
		for(String neighbour : this.sortedNeighbours) {
			sb.append(neighbour + " ");
		}
		sb.append("\n");
		
		return sb.toString();
	}
	
	public String conditionsToString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("Boolian logic:\n");
		for(TruthRow tr : this.conditionsTable) {
			sb.append(tr.conditionsToString() + "\n");
		}
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(basicDetailsToString());
		sb.append(neighboursToString());
		sb.append(conditionsToString());
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((conditionsTable == null) ? 0 : conditionsTable.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof Node)) {
			return false;
		}

		Node other = (Node) obj;
		if (conditionsTable == null) {
			if (other.conditionsTable != null) {
				return false;
			}
		} else if (!conditionsTable.equals(other.conditionsTable)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}
}
