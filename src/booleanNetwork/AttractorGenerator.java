/**
 * 
 */
package booleanNetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import utils.StateNetworkOperations;
import utils.jenetics.Population;

/**
 * @author Itay
 *
 */
public class AttractorGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String xlsInDataPath;
		String xlsOutDataPath;
		int sheetNumber;
		double mutationRatio = 0.5;
		int numOfGraphs = 50;
		
		if(args.length == 3) {
			xlsInDataPath = args[0];
			sheetNumber = Integer.parseInt(args[1]);
			xlsOutDataPath = args[2];
		} else if (args.length != 0) {
			System.out.println("The following cmd arguments should be entered: <xlsDataPath> <sheetNumber>");
			return;
		} else {
			xlsInDataPath = "C:\\Users\\Itay\\Documents\\Computer Sciences\\M.Sc\\Attractors\\Supplementary_Table_S5.xls";
			sheetNumber = 0;
			xlsOutDataPath = "C:\\Users\\Itay\\Documents\\Computer Sciences\\M.Sc\\Attractors\\Output\\OutData.xls";
		}
		
		GraphBuilder gb = new XlsGraphBuilder(xlsInDataPath, sheetNumber);
		Graph graph = gb.buildGraph();
		
		//generateBasicConvergenceXls(graph, mutationRatio, numOfGraphs, xlsOutDataPath);
		//generateNodeRankingXls(graph, xlsOutDataPath);
		//testTTNodeConvergenceXls(graph, xlsOutDataPath);
		generateFuncRankingXls(graph, xlsOutDataPath);
	}
	
	private static void testTTNodeConvergenceXls(
			Graph base, 
			String xlsOutDataPath
			) {
		
		ArrayList<Graph> graphs = new ArrayList<Graph>();
		Graph ttNodes = Graph.copyWithTTNodes(base);
		Graph regNodes = new Graph(base);
		
		graphs.add(regNodes);
		graphs.add(ttNodes);
		
		converge(graphs, xlsOutDataPath, "basic");
	}
	
	private static void generateBasicConvergenceXls(
			Graph base, 
			double mutationRatio, 
			int numOfGraphs,
			String xlsOutDataPath
			) {
		
		ArrayList<Graph> mutatedGraphs = new ArrayList<Graph>();
		
		for(int i = 0; i < numOfGraphs; i++) {
			Graph mutated = Graph.copyWithTTNodes(base);
			mutated.mutateTruthValues(mutationRatio);
			mutatedGraphs.add(mutated);
		}
		
		converge(mutatedGraphs, xlsOutDataPath, "basic");
	}
	
	private static void generateNodeRankingXls(
			Graph base, 
			String xlsOutDataPath
			) {
		
		ArrayList<Graph> graphs = new ArrayList<Graph>();
		
		for(String nodeName : Graph.getSortedNodeNames()) {
			Graph singleNodeReduction = new Graph(base);
			singleNodeReduction.setOffNode(nodeName);
			graphs.add(singleNodeReduction);
		}
		
		converge(graphs, xlsOutDataPath, "nodeRanking");
	}

	private static void generateFuncRankingXls(Graph base, String xlsOutDataPath) {
		
		ArrayList<ArrayList<Integer>> convergenceData = new ArrayList<ArrayList<Integer>>();
		ArrayList<NodeOptimizer> optimizationData = new ArrayList<NodeOptimizer>();
		int longestConvergence = 0;
		
		for(String nodeName : Graph.getSortedNodeNames()) {
			Node n = base.getNodes().get(nodeName);
			NodeOptimizer no = null;
			
			if(n.getSortedNeighbours().size() <= 3) {
				no = new NodeOptimizer(base, nodeName, true);
			} else {
				Population p = null;
				
				if(nodeName != "mdm2") {
					p = new Population(500, base, nodeName, 0.1, 0.7, true, 0.6, 300, 20);
				} else {
					p = new Population(2000, base, nodeName, 0.1, 0.7, true, 0.6, 1000, 20);
				}
				
				String optimizeOutput = p.evolve();
				validateOptimisation(p, optimizeOutput);
				no = p.getOptimizer();
			}
			no.optimizeWithInitialMemeber();
			ArrayList<Integer> optimized = no.getOptimizedAttractor();
			longestConvergence = (longestConvergence > optimized.size()) ? longestConvergence : optimized.size();
			convergenceData.add(optimized);
			optimizationData.add(no);
			
			createFuncOptimizationData(no);
		}
		ArrayList<Integer> initial = optimizationData.get(0).getInitialAttractor();
		longestConvergence = (longestConvergence > initial.size()) ? longestConvergence : initial.size();
		convergenceData.add(initial);
		
		//createFuncOptimizationData(optimizationData);
		createAttractorAnalysisXls(convergenceData, longestConvergence, xlsOutDataPath, "funcRanking");
	}
	
	private static void validateOptimisation(Population p, String optimizeOutput) {
		NodeOptimizer no = p.getOptimizer();
		TTNode ttn = no.getNode();
		ttn.setTruthTableOutputs(optimizeOutput, ttn.getTruthTable().keySet());
		ArrayList<Integer> optimizedAtt = StateNetworkOperations.getConvergenceValues(no.getGraph());
		
		if(!optimizedAtt.equals(no.getOptimizedAttractor())) {
			throw new IllegalStateException();
		}
	}

	//private static ArrayList<Integer> optimize(Graph graph, String nodeName) {

	private static void converge(ArrayList<Graph> graphs, String xlsOutDataPath, String outputType) {
		int longestConvergence = 0;
		ArrayList<ArrayList<Integer>> convergenceData = new ArrayList<ArrayList<Integer>>();
		
		for(Graph g : graphs) {
			ArrayList<Integer> attractorConvergenceValues = StateNetworkOperations.getConvergenceValues(g);
			longestConvergence = (longestConvergence < attractorConvergenceValues.size()) ?
					attractorConvergenceValues.size() : longestConvergence;
			
			convergenceData.add(attractorConvergenceValues);
		}
		creatBasicAttractorAnalysisData(convergenceData);
		createAttractorAnalysisXls(convergenceData, longestConvergence, xlsOutDataPath, outputType);
	}
	
	private static void creatBasicAttractorAnalysisData(ArrayList<ArrayList<Integer>> convergenceData) {
		double mean = calcConvergenceMean(convergenceData);
		double std = calcConvergenceStd(convergenceData, mean);
		System.out.println(String.format("Amount of convergence steps mean and std values for %d random boolean graphs.\nMean value: %f\nSTD value: %f", 
				convergenceData.size(), mean, std));
	}
	
	private static void createFuncOptimizationData(ArrayList<NodeOptimizer> optimizationData) {
		for(NodeOptimizer no : optimizationData) {
			String nodeName = no.getNode().getName();
			double initialGrade = no.getInitialGrade();
			double optimizedGrade = no.getOptimizedGrade();
			int ranking = no.getInitialRanking();
			System.out.println(String.format("Node: %s - initialGrade: %f, optimizedGrade: %f, ranking: %d\n", nodeName, initialGrade, optimizedGrade, ranking));
		}
		
	}
	
	private static void createFuncOptimizationData(NodeOptimizer no) {
		String nodeName = no.getNode().getName();
		double initialGrade = no.getInitialGrade();
		double optimizedGrade = no.getOptimizedGrade();
		int ranking = no.getInitialRanking();
		System.out.println(String.format("Node: %s - initialGrade: %f, optimizedGrade: %f, ranking: %d\n", nodeName, initialGrade, optimizedGrade, ranking));
	}
	
	private static double calcConvergenceMean(ArrayList<ArrayList<Integer>> convergenceData) {
		double sum = 0;
		int numOfGraphs = convergenceData.size();
		
		for(ArrayList<Integer> singleConvergence : convergenceData) {
			sum += singleConvergence.size();
		}
		
		double mean = (numOfGraphs > 0) ? (sum / numOfGraphs) : 0;
		return mean;
	}
	
	private static double calcConvergenceStd(ArrayList<ArrayList<Integer>> convergenceData, double mean) {
		double sum = 0;
		int numOfGraphs = convergenceData.size();
		
		for(ArrayList<Integer> singleConvergence : convergenceData) {
			double powDelta = Math.pow((singleConvergence.size() - mean), 2);
			sum += powDelta;
		}
		double powStd = ((numOfGraphs - 1) > 0) ? (sum / (numOfGraphs - 1)) : Double.NaN; 
		double std = Math.sqrt(powStd);
		
		return std;
	}
	
	private static void createAttractorAnalysisXls(
			ArrayList<ArrayList<Integer>> convergenceData,
			int longestConvergence,
			String xlsOutDataPath,
			String outputType
			) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Node_Function_Ranking");
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("Timestamp / Node reduction:");
		
		for (int i = 1; i <= convergenceData.size(); ++i) {
			cell = row.createCell(i);
			cell.setCellValue(getColumnHeader(i, outputType));
		}
		
		for(int i = 0; i < longestConvergence; ++i) {
			row = sheet.createRow(i+1);
			cell = row.createCell(0);
			cell.setCellValue(i);
			for (int j = 0; j < convergenceData.size(); ++j) {
				cell = row.createCell(j+1);
				ArrayList<Integer> attractorConvergenceValues = convergenceData.get(j);
				int convergenceSize = attractorConvergenceValues.size();
				Integer convergenceValue = (i < convergenceSize) ? attractorConvergenceValues.get(i) : attractorConvergenceValues.get(convergenceSize-1);
				cell.setCellValue(convergenceValue.doubleValue());
			}
		}
		
		try {
		    FileOutputStream out = 
		            new FileOutputStream(new File(xlsOutDataPath));
		    workbook.write(out);
		    out.close();
		    System.out.println("Excel written successfully..");
		     
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	private static String getColumnHeader(int columnInd, String outputType) {
		String res = null;
		
		if(outputType.equals("basic")) {
			res = getBasicColumnHeader(columnInd);
		} else if(outputType.equals("nodeRanking")) {
			res = getNodeReductionColumnHeader(columnInd);
		} else if(outputType.equals("funcRanking")) {
			res = getFuncRankingColumnHeader(columnInd);
		}
		
		return res;
	}

	private static String getBasicColumnHeader(int columnInd) {
		return String.format("Graph %d amount of nodes:", columnInd);
	}
	
	private static String getNodeReductionColumnHeader(int columnInd) {
		String nodeName = Graph.getSortedNodeNames().get(columnInd - 1);
		return String.format("%s: ", nodeName);
	}
	
	private static String getFuncRankingColumnHeader(int columnInd) {
		String res;
		if(columnInd - 1 < Graph.getSortedNodeNames().size()) {
			String nodeName = Graph.getSortedNodeNames().get(columnInd - 1);
			res = String.format("%s: ", nodeName);
		} else {
			res = "Initial attractor: ";
		}
		
		return res;
	}

}
