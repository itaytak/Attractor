/**
 * 
 */
package utils;

import java.util.HashMap;
import booleanNetwork.Node;

/**
 * @author Itay
 *
 */
public class BooleanFunction extends TruthRow {

	public BooleanFunction(BooleanFunction other) {
		super(other);
	}
	
	public BooleanFunction(String nodeName, Boolean futureValue) {
		super(nodeName, futureValue);
	}

	/* (non-Javadoc)
	 * @see utils.TruthRow#isSatisfied(java.util.Dictionary)
	 */
	
	@Override
	public boolean isSatisfied(HashMap<String, Node> currStateGraph) {
		boolean isSatisfied = true;
		
		for(NamedBoolean condition : conditions) {
			Node n = currStateGraph.get(condition.getName());
			if(n == null) {
				throw new IllegalStateException("Boolean function contains node which not exists: " + condition.getName());
			}
			isSatisfied = (condition.getValue()) ? (isSatisfied && n.getValue()) : (isSatisfied && !n.getValue());
			
			if(!isSatisfied) {
				break;
			}
		}
		
		return isSatisfied;
	}

	@Override
	public String conditionsToString() {
		StringBuffer sb = new StringBuffer();
		NamedBoolean nb = this.conditions.get(0);
		String val = (nb.getValue() == true) ? (nb.getName()) : ("!" + nb.getName());
		sb.append(val);
		
		for(int i = 1; i < this.conditions.size(); i++) {
			nb = this.conditions.get(i);
			val = (nb.getValue() == true) ? (nb.getName()) : ("!" + nb.getName());
			sb.append(" && " + val);
		}
		
		return sb.toString();
	}
	
	

}
