/**
 * 
 */
package utils;

/**
 * @author Itay
 *
 */
public class StateOperations {
	
	public static String generateState(long statesSize, long stateNum) {
		String state = Long.toBinaryString(stateNum);
		StringBuffer sb = new StringBuffer();
		long leadingZerosAmount = statesSize - state.length();
		
		for(int i = 0; i < leadingZerosAmount; ++i) {
			sb.append("0");
		}
		sb.append(state);
		
		return sb.toString();
	}
}
