package booleanNetwork;

public class TestGraphBuilder {

	/**
	 * @param args
	 */
	public static void run(String[] args) {
		GraphBuilder gb = new XlsGraphBuilder("C:\\Users\\Itay\\Documents\\Computer Sciences\\M.Sc\\Attractors\\Supplementary_Table_S5.xls", 0);
		Graph graph = gb.buildGraph();
		
		int x = 100;
		System.out.println(Integer.toBinaryString(x));
		
		for(Node n : graph.getNodes().values()) {
			System.out.println(n.toString() + "\n");
		}
	}

}
