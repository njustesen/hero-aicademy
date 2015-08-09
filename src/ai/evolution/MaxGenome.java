package ai.evolution;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.evaluation.IStateEvaluator;

public class MaxGenome extends Genome {

	public GameState state;
	public IStateEvaluator heuristic;
	public double killRate;
	public int popSize;
	public double mutRate;
	
	public List<Genome> pop;
	private GameState clone;
	private List<Genome> killed;
	private int idx;
	
	public MaxGenome(int popSize, double killRate, double mutRate, IStateEvaluator heuristic) {
		super();
		this.popSize = popSize;
		this.killRate = killRate;
		this.mutRate = mutRate;
		this.heuristic = heuristic;
		this.idx = popSize - (int) Math.floor(popSize * killRate);
	}
	
	@Override
	public double fitness(){
		return value;
	}

	public void run(){
		
		if (pop == null)
			setup();
		
		if (clone.isTerminal){
			value = heuristic.eval(clone, !state.p1Turn);
			return;
		}

		// Crossover - reuse killed genomes
		if (visits > 0)
			crossover(killed);
		
		// Test
		for (int i = 0; i < pop.size(); i++) {
			if (pop.get(i).visits == 0){
				clone.imitate(state);
				clone.update(pop.get(i).actions);
				pop.get(i).value = heuristic.eval(clone, !state.p1Turn);
				pop.get(i).visits++;
			}
		}
		Collections.sort(pop);
		
		// Kill
		killed.clear();
		for (int i = idx; i < pop.size(); i++)
			killed.add(pop.get(i));
		
		value = pop.get(0).value;
		
	}
	
	private void crossover(List<Genome> genomes) {
		int a;
		int b;
		for (final Genome genome : killed) {
			a = random.nextInt(idx-1);
			b = random.nextInt(idx-1);
			while (b == a)
				b = random.nextInt(idx-1);
			
			clone.imitate(state);
			genome.visits = 0;
			genome.crossover(pop.get(a), pop.get(b), clone);
			
			if (genome.actions.isEmpty())
				continue;
			
			// Mutation
			if (Math.random() < mutRate) {
				clone.imitate(state);
				genome.mutate(clone);
			}
		}
	}

	private void setup() {
		clone = state.copy();
		pop = new ArrayList<Genome>();
		killed = new ArrayList<Genome>();
		for(int i = 0; i < popSize; i++){
			if (i > 0)
				clone.imitate(state);
			Genome genome = new MinGenome();
			genome.random(clone);
			pop.add(genome);
		}
	}

	@Override
	public String toString() {
		return "Genome [value=" + value + ", visits="+ visits + ", fitness=" + fitness() + "]";
	}
	
}
