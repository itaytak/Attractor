package booleanNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import utils.StateNetworkOperations;
import utils.StateOperations;

public class NodeOptimizer {
	
	private Graph graph;
	private TTNode node;
	private ArrayList<String> possibleOutputs;
	private Integer initialInd;
	private Integer longestAttractor;
	private String initialOutput;
	private double[] initialAttractor;
	private double[] optimizedAttractor;
	private double initialGrade;
	private double optimizedGrade;
	private int initialRanking;
	
	/**
	 * @param graph
	 * @param node name to optimize
	 */
	public NodeOptimizer(Graph graph, String node, boolean populateOutputs) {
		super();
		this.graph = Graph.copyWithTTNodes(graph);
		this.node = (TTNode)this.graph.getNodes().get(node);
		this.initialOutput = this.node.getTruthTableOutputs();
		this.initialAttractor = StateNetworkOperations.getConvergenceValuesAsDoubleArr(this.graph);
		this.optimizedAttractor = this.initialAttractor;
		
		if(populateOutputs){
			this.possibleOutputs = new ArrayList<String>();
			populatePossibleOutputs();
			setInitialAttractorInd();
		}
	}

	private void populatePossibleOutputs() {
		int ttSize = this.node.getTruthTable().size();
		
		if(ttSize > 0) {
			long size = (long)Math.pow(2, ttSize);
			for(long i = 0; i < size; ++i) {
				String state = StateOperations.generateState(ttSize, i);
				this.possibleOutputs.add(state);
			}
		}
	}
	
	private void setInitialAttractorInd() {	
		this.initialInd = new Integer(this.possibleOutputs.indexOf(this.initialOutput));
	}

	/**
	 * @return the graph
	 */
	public Graph getGraph() {
		return graph;
	}


	/**
	 * @param possibleOutputs the possibleOutputs to set
	 */
	public void setPossibleOutputs(ArrayList<String> possibleOutputs) {
		this.possibleOutputs = possibleOutputs;
	}

	/**
	 * @return the possibleOutputs
	 */
	public ArrayList<String> getPossibleOutputs() {
		return possibleOutputs;
	}

	/**
	 * @return the node
	 */
	public TTNode getNode() {
		return node;
	}

	/**
	 * @return the initialAttractor
	 */
	public ArrayList<Integer> getInitialAttractor() {
		ArrayList<Integer> attractorData = new ArrayList<Integer>();
		
		for(int i = 0; i < this.initialAttractor.length; ++i) {
			attractorData.add((int)this.initialAttractor[i]);
		}
		return attractorData;
	}

	/**
	 * @return the optimizedAttractor
	 */
	public ArrayList<Integer> getOptimizedAttractor() {
		ArrayList<Integer> attractorData = new ArrayList<Integer>();
		
		for(int i = 0; i < this.optimizedAttractor.length; ++i) {
			attractorData.add((int)this.optimizedAttractor[i]);
		}
		return attractorData;
	}

	/**
	 * @return the initialOutput
	 */
	public String getInitialOutput() {
		return initialOutput;
	}


	/**
	 * @return the initialGrade
	 */
	public double getInitialGrade() {
		return initialGrade;
	}

	/**
	 * @return the optimizedGrade
	 */
	public double getOptimizedGrade() {
		return optimizedGrade;
	}

	/**
	 * @return the initialRanking
	 */
	public int getInitialRanking() {
		return initialRanking;
	}

	//grades each attractor according to the area under its convergence graph
	public void optimizeWithSimpleGrading() {
		double bestAttractorGrade = generateSimpleGrade(this.optimizedAttractor);
		Set<String> ttInputs = this.node.getTruthTable().keySet();
		
		for(String singleOutputSet : this.possibleOutputs) {
			node.setTruthTableOutputs(singleOutputSet, ttInputs);
			double[] tempAttractor = StateNetworkOperations.getConvergenceValuesAsDoubleArr(this.graph);
			double tempAttractorGrade = generateSimpleGrade(tempAttractor);
			
			if(tempAttractorGrade < bestAttractorGrade) {
				bestAttractorGrade = tempAttractorGrade;
				this.optimizedAttractor = tempAttractor;
			}
		}
	}
	
	public double generateSimpleGrade(double[] attractorData) {
		double sum = 0;
		
		for(int i = 0; i < attractorData.length; ++i) {
			sum += attractorData[i];
		}
		double res = 0.5 * sum;
		
		return res;
	}
	
	public void optimizeWithInitialMemeber() {
		if(this.initialInd == null) {
			setInitialAttractorInd();
		}
		
		if(this.initialInd == -1) {
			this.possibleOutputs.add(this.initialOutput);
			this.initialInd = new Integer(this.possibleOutputs.size() - 1);
		}
		
		double[] grades = optimizeWithSTDWeights();
		this.initialGrade = grades[this.initialInd];
		setInitialRanking(grades);
	}
	
	public double[] optimizeWithSTDWeights() {
		Set<String> ttInputs = this.node.getTruthTable().keySet();
		int attractorCounter = 0;
		double[][] bulkAttractorsData = new double[this.possibleOutputs.size()][];
		int longest = 0;
		
		for(String singleOutputSet : this.possibleOutputs) {
			node.setTruthTableOutputs(singleOutputSet, ttInputs);
			bulkAttractorsData[attractorCounter] = StateNetworkOperations.getConvergenceValuesAsDoubleArr(this.graph);
			int attractorLen = bulkAttractorsData[attractorCounter].length;
			longest = (longest < attractorLen) ? attractorLen : longest;
			++attractorCounter;
		}
		this.longestAttractor = new Integer(longest);
		double[] grades = getSTDWeightedGrades(bulkAttractorsData);
		int optimizedInd = getMinValInd(grades);
		this.optimizedAttractor = bulkAttractorsData[optimizedInd];
		this.optimizedGrade = grades[optimizedInd];
		
		return grades;
	}

	private double[] getSTDWeightedGrades(double[][] bulkAttractorsData) {
		double[] stdValues = getSTDAttractionValues(bulkAttractorsData);
		double[] grades = generateSTDWeightedGrades(bulkAttractorsData, stdValues);
		
		return grades;
	}

	private double[] getSTDAttractionValues(double[][] bulkAttractorsData) {
		double[] stdAttractionValues = new double[this.longestAttractor];
		int dataSize = bulkAttractorsData.length;
		
		for(int i = 0; i < this.longestAttractor; ++i) {
			DescriptiveStatistics ds = new DescriptiveStatistics(dataSize);
			for(int j = 0; j < dataSize; ++j) {
				if(i < bulkAttractorsData[j].length) {
					ds.addValue(bulkAttractorsData[j][i]);
				} else {
					int lastVarInd = bulkAttractorsData[j].length - 1;
					ds.addValue(bulkAttractorsData[j][lastVarInd]);
				}
			}
			stdAttractionValues[i] = ds.getStandardDeviation();
		}
		
		return stdAttractionValues;
	}
	
	private double[] generateSTDWeightedGrades(double[][] bulkAttractorsData, double[] stdValues) {
		double[] grades = new double[bulkAttractorsData.length];
		
		for(int i = 0; i < bulkAttractorsData.length; ++i) {
			int attractorLen = bulkAttractorsData[i].length;
			double weightedSum = 0;
			for(int j = 1; j < stdValues.length; j++) {
				double convergenceVal = (j < attractorLen) ? bulkAttractorsData[i][j] : bulkAttractorsData[i][attractorLen - 1];
				if(Double.compare(stdValues[j], Double.MIN_VALUE) <= 0) {
					continue;
				}
				weightedSum += (convergenceVal / stdValues[j]);
			}
			
			grades[i] = weightedSum / 2;
		}
		
		return grades;
	}
	
	private int getMinValInd(double[] grades) {
		double minVal = Double.MAX_VALUE;
		int minInd = 0;
		
		for(int i = 0; i < grades.length; ++i) {
			if(Double.compare(minVal, grades[i]) > 0) {
				minVal = grades[i];
				minInd = i;
			}
		}
		
		return minInd;
	}
	
	private void setInitialRanking(double[] grades) {
		ArrayList<Double> sortedGrades = new ArrayList<Double>();
		
		for(int i = 0; i < grades.length; ++i) {
			sortedGrades.add(grades[i]);
		}
		
		Collections.sort(sortedGrades);
		
		if(sortedGrades.indexOf(this.optimizedGrade) != 0) {
			throw new IllegalStateException("Optimized grade isn't the best grade in collection");
		}
		
		this.initialRanking = sortedGrades.indexOf(this.initialGrade) + 1;
	}
}
