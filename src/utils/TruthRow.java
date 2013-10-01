/**
 * 
 */
package utils;

import java.util.ArrayList;
import java.util.HashMap;

import booleanNetwork.Node;

/**
 * @author Itay
 *
 */
public class TruthRow {
	protected String nodeName;
	protected Boolean futureValue;
	protected ArrayList<NamedBoolean> conditions;
	
	/**
	 * @param other
	 */
	public TruthRow(TruthRow other) {
		super();
		this.nodeName = new String(other.nodeName);
		this.futureValue = new Boolean(other.futureValue);
		this.conditions = new ArrayList<NamedBoolean>();
		
		for(NamedBoolean nb : other.conditions) {
			NamedBoolean copy = new NamedBoolean(nb);
			this.conditions.add(copy);
		}
	}
	/**
	 * @param nodeName
	 * @param futureValue
	 */
	public TruthRow(String nodeName, Boolean futureValue) {
		super();
		this.nodeName = new String(nodeName);
		this.futureValue = new Boolean(futureValue);
		this.conditions = new ArrayList<NamedBoolean>();
	}

	/**
	 * @return the nodeName
	 */
	public String getNodeName() {
		return nodeName;
	}

	/**
	 * @return the futureValue
	 */
	public Boolean getFutureValue() {
		return futureValue;
	}

	/**
	 * @return the conditions
	 */
	public ArrayList<NamedBoolean> getConditions() {
		return conditions;
	}
	
	public void addCondition(NamedBoolean condition) {
		this.conditions.add(condition);
	}
	
	public NamedBoolean getNamedBoolean(String node) {
		NamedBoolean chosen = null;
		
		for(NamedBoolean nb : this.conditions) {
			if(nb.getName().equals(node)) {
				chosen = nb;
			}
		}
		
		return chosen;
	}
	
	/**
	 * Should be invoked when working with
	 * truth tables;
	 * @return the isSatisfied
	 * @throws Exception 
	 */
	protected boolean isSatisfied(HashMap<String, Node> currStateGraph) {
		boolean isSatisfied = true;
		
		for(NamedBoolean condition : conditions) {
			Node n = currStateGraph.get(condition.getName());
			if(n == null) {
				throw new IllegalStateException("Boolean function contains node which not exists");
			}
			else if(!n.getCurrState().equals(condition)) {
				isSatisfied = false;
				break;
			}
			
		}
		
		return isSatisfied;
	}
	
	public Boolean trigger(HashMap<String, Node> currStateGraph) {
		Boolean resValue = null;
		if(isSatisfied(currStateGraph)) {
			resValue = new Boolean(this.futureValue);
		}
		
		return resValue;
	}
	
	public void mutate(Double mutationRatio) {
		for (NamedBoolean nb : this.conditions) {
			nb.mutate(mutationRatio);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("belongs to node: " + this.nodeName + "\n");
		sb.append("Node future value:" + this.futureValue + "\n");
		sb.append("Boolean Conditions:\n");
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
				+ ((conditions == null) ? 0 : conditions.hashCode());
		result = prime * result
				+ ((futureValue == null) ? 0 : futureValue.hashCode());
		result = prime * result
				+ ((nodeName == null) ? 0 : nodeName.hashCode());
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
		if (obj == null || !(obj instanceof TruthRow)) {
			return false;
		}
		
		TruthRow other = (TruthRow) obj;
		if (conditions == null) {
			if (other.conditions != null) {
				return false;
			}
		} else if (!conditions.equals(other.conditions)) {
			return false;
		}
		if (futureValue == null) {
			if (other.futureValue != null) {
				return false;
			}
		} else if (!futureValue.equals(other.futureValue)) {
			return false;
		}
		if (nodeName == null) {
			if (other.nodeName != null) {
				return false;
			}
		} else if (!nodeName.equals(other.nodeName)) {
			return false;
		}
		return true;
	}

	public String conditionsToString() {
		StringBuffer sb = new StringBuffer();
		sb.append("|");
		for(NamedBoolean nb : this.conditions) {
			String val = (nb.getValue() == true) ? "1" : "0";
			sb.append("\t" + nb.getName() + " = " + val + "\t|");
			
		}
		
		return sb.toString();
	}

}
