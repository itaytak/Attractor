/**
 * 
 */
package utils.jenetics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import booleanNetwork.Graph;
import booleanNetwork.NodeOptimizer;

import utils.StateOperations;

/**
 * @author Itay
 *
 */
public class Population {
	private ArrayList<Chromosome> population;
	private Double mutationRatio;
	private Double coRatio;
	private Boolean elitism;
	private Double solutionsProximityLimit;
	private Integer iterationsLimit;
	private Integer bestMembersNum;
	private Random randomGen;
	private NodeOptimizer optimizer;
	
	/**
	 * @param mutationRatio
	 * @param coRatio
	 * @param elitism
	 * @param solutionsProximityLimit
	 * @param iterationsLimit
	 */
	public Population(int populationSize, Graph graph, String nodeName, 
			Double mutationRatio, Double coRatio, Boolean elitism,
			Double solutionsProximityLimit, Integer iterationsLimit, Integer bestMembersNum) {
		super();
		this.randomGen = new Random();
		this.mutationRatio = mutationRatio;
		this.coRatio = coRatio;
		this.elitism = elitism;
		this.solutionsProximityLimit = solutionsProximityLimit;
		this.iterationsLimit = iterationsLimit;
		this.bestMembersNum = bestMembersNum;
		this.population = new ArrayList<Chromosome>();
		optimizer = new NodeOptimizer(graph, nodeName, false);
		generateInitialPopulation(populationSize);
	}
	
	/**
	 * @return the optimizer
	 */
	public NodeOptimizer getOptimizer() {
		return optimizer;
	}

	private void generateInitialPopulation(int populationSize) {
		int chromosomeSize = optimizer.getNode().getTruthTable().size();
		
		if(chromosomeSize <= 16) {
			generateSmallChromosomePopulation(populationSize, chromosomeSize);
		} else {
			generateLargeChromosomePopulation(populationSize, chromosomeSize);
		}
		
		if(this.elitism) {
			int replaceInd = randomGen.nextInt(this.population.size());
			this.population.remove(replaceInd);
			String initialNodeOutput = this.optimizer.getInitialOutput();
			Chromosome initialBest = new Chromosome(initialNodeOutput, null);
			this.population.add(initialBest);
		}
	}
	
	private void generateSmallChromosomePopulation(int populationSize, int chromosomeSize) {
		int range = (int)Math.pow(2, chromosomeSize);
		for(int i = 0; i < populationSize; ++i) {
			int randStateNum = randomGen.nextInt(range);
			String randState = StateOperations.generateState(chromosomeSize, randStateNum);
			Chromosome member = new Chromosome(randState, null);
			this.population.add(member);
		}
	}
	
	private void generateLargeChromosomePopulation(int populationSize, int chromosomeSize) {
		for(int i = 0; i < populationSize; ++i) {
			StringBuffer randState = new StringBuffer();
			
			for(int j = 0; j < chromosomeSize; ++j) {
				char gen = (randomGen.nextInt(2) == 0) ? '0' : '1';
				randState.append(gen);
			}
			
			Chromosome member = new Chromosome(randState.toString(), null);
			this.population.add(member);
		}
	}
	
	public String evolve() {
		int count = 0;
		double bestMembersSTD = Double.MAX_VALUE;
		boolean proceedEvolution = true;
		
		do {
			evoloveSingleGeneration();
			bestMembersSTD = getBestMembersSTD();
			++count;
			
			proceedEvolution = (count < this.iterationsLimit) && (this.solutionsProximityLimit.compareTo(bestMembersSTD) < 0);
			if(proceedEvolution) {
				createNextGeneration();
			}
		} while(proceedEvolution);
		
		String optimizedOutput = this.population.get(0).getContent();
		
		return optimizedOutput;
	}

	private double getBestMembersSTD() {
		DescriptiveStatistics ds = new DescriptiveStatistics(this.bestMembersNum);
		
		for(int i = 0; i < this.bestMembersNum; ++i) {
			Chromosome c = this.population.get(i);
			ds.addValue(c.getGrade().doubleValue());
		}
		
		double stdVal = ds.getStandardDeviation();
		
		return stdVal;
	}

	private void evoloveSingleGeneration() {
		ArrayList<String> singleGeneration = new ArrayList<String>();
		
		for(Chromosome member : this.population) {
			singleGeneration.add(member.getContent());
		}
		
		this.optimizer.setPossibleOutputs(singleGeneration);
		double[] grades = this.optimizer.optimizeWithSTDWeights();
		int ind = 0;
		
		for(Chromosome member : this.population) {
			Double grade = new Double(grades[ind]);
			member.setGrade(grade);
			++ind;
		}
		
		Collections.sort(this.population);
	}

	private void createNextGeneration() {
		int sum = 0;
		
		for(int i = 1; i <= this.population.size(); ++i) {
			sum += i;
		}
		
		ArrayList<Chromosome> nextGen = new ArrayList<Chromosome>();
		
		do {
			Chromosome p1 = selectChromosome(sum);
			Chromosome p2 = selectChromosome(sum);
			ArrayList<Chromosome> childs = Chromosome.crossover(p1, p2, this.coRatio, this.randomGen);
			
			for(Chromosome c : childs) {
				c.mutate(this.mutationRatio, this.randomGen);
				nextGen.add(c);
			}
			
		} while(nextGen.size() < this.population.size());
		
		if(this.elitism) {
			int replaceInd = randomGen.nextInt(this.population.size());
			nextGen.remove(replaceInd);
			nextGen.add(this.population.get(0));
		}
		
		this.population = nextGen;
	}
	
	private Chromosome selectChromosome(int sum) {
		int selected = randomGen.nextInt(sum);
		int selectedInd = this.population.size() - 1;
		int tempSum = 0;
		
		for(int i = 1; (i <= this.population.size()); ++i) {
			tempSum += i;
			
			if(tempSum < selected) {
				--selectedInd;
			} else {
				break;
			}
		}
		
		return this.population.get(selectedInd);
	}
}
