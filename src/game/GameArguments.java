package game;

import model.DECK_SIZE;
import ai.AI;
import ai.GreedyActionAI;
import ai.GreedyTurnAI;
import ai.HeuristicAI;
import ai.HybridAI;
import ai.NmSearchAI;
import ai.RandomAI;
import ai.RandomHeuristicAI;
import ai.RandomSwitchAI;
import ai.evaluation.HeuristicEvaluator;
import ai.evaluation.LeafParallelizer;
import ai.evaluation.MaterialBalanceEvaluator;
import ai.evaluation.RolloutEvaluator;
import ai.evaluation.LeafParallelizer.LEAF_METHOD;
import ai.evolution.OnlineIslandEvolution;
import ai.evolution.OnlineEvolution;
import ai.mcts.Mcts;
import ai.util.RAND_METHOD;

public class GameArguments {
	
	public final AI[] players;
	public String mapName;
	public DECK_SIZE deckSize = DECK_SIZE.STANDARD;
	public int sleep;
	public boolean gfx;
	public int budget = 6000;
	
	private int p;
	private boolean m = false;
	private boolean d = false;
	private boolean aa = false;
	
	public GameArguments(String[] args) {
		players = new AI[2];
		mapName = "a";
		p = -1;
		sleep = 40;
		gfx = true;
		setup(args);
	}
	
	public GameArguments(boolean gfx, AI p1, AI p2, String mapName, DECK_SIZE deckSize) {
		players = new AI[2];
		players[0] = p1;
		players[1] = p2;
		this.mapName = mapName;
		p = -1;
		this.gfx = gfx;
		sleep = gfx ? 40 : 0;
		this.deckSize = deckSize;
	}

	public GameArguments(boolean gfx, AI p1, AI p2, String mapName, DECK_SIZE deckSize, int ap) {
		players = new AI[2];
		players[0] = p1;
		players[1] = p2;
		this.mapName = mapName;
		p = -1;
		this.gfx = gfx;
		sleep = gfx ? 40 : 0;
		this.deckSize = deckSize;
		setAP(ap);
	}

	private void setAP(int ap) {
		GameState.STARTING_AP = Math.max(1, ap - 1);
		GameState.ACTION_POINTS = ap;
		GameState.TURN_LIMIT = 100 * Math.max(1, (6-ap));
	}

	private void setup(String[] args) {
		AI randomSwitch = new RandomSwitchAI(0.5, new HeuristicAI(), new RandomAI(RAND_METHOD.TREE));
		for (int a = 0; a < args.length; a++) {
			if (args[a].toLowerCase().equals("map")) {
				m = true;
				continue;
			}
			if (args[a].toLowerCase().equals("ap")) {
				aa = true;
				continue;
			}
			if (args[a].toLowerCase().equals("deck")) {
				d = true;
				continue;
			}
			if (args[a].toLowerCase().equals("p1")) {
				p = 0;
				continue;
			} else if (args[a].toLowerCase().equals("p2")) {
				p = 1;
				continue;
			}
			if (m){
				mapName = args[a];
				m = false;
				continue;
			}
			if (aa){
				setAP(Integer.parseInt(args[a]));
				aa = false;
				continue;
			}
			if (d){
				if (args[a].equals("standard"))
					deckSize = DECK_SIZE.STANDARD;
				if (args[a].equals("small"))
					deckSize = DECK_SIZE.SMALL;
				if (args[a].equals("tiny"))
					deckSize = DECK_SIZE.TINY;
				d = false;
				continue;
			}
			if (p == 0 || p == 1) {
				if (args[a].toLowerCase().equals("human"))
					players[p] = null;
				else if (args[a].toLowerCase().equals("random"))
					players[p] = new RandomAI(RAND_METHOD.TREE);
				if (args[a].toLowerCase().equals("greedyaction")) {
					players[p] = new GreedyActionAI(new HeuristicEvaluator(false));
				}
				if (args[a].toLowerCase().equals("greedyturn")) {
					players[p] = new GreedyTurnAI(new HeuristicEvaluator(false), budget);
				}
				if (args[a].toLowerCase().equals("mcts-vanilla")) {
					players[p] = new Mcts(budget, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(true)));
				}
				if (args[a].toLowerCase().equals("mcts-ne")) {
					players[p] = new Mcts(budget, new RolloutEvaluator(1, 1, new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
					((Mcts)players[p]).c = 0;
				}
				if (args[a].toLowerCase().equals("mcts-cut")) {
					players[p] = new Mcts(budget, new RolloutEvaluator(1, 1, new RandomHeuristicAI(.5), new HeuristicEvaluator(true)));
					((Mcts)players[p]).cut = true;
				}
				if (args[a].toLowerCase().equals("mcts-collapse")) {
					players[p] = new Mcts(budget, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0), new HeuristicEvaluator(true)));
					((Mcts)players[p]).collapse = true;
				}
				if (args[a].toLowerCase().equals("online-evolution")){
					players[p] = new OnlineIslandEvolution(false, 100, .33, .66, budget, new RolloutEvaluator(1, 1, randomSwitch, new HeuristicEvaluator(false)));
				}
				p = -1;
			} else if (args[a].toLowerCase().equals("sleep")) {
				a++;
				sleep = Integer.parseInt(args[a]);
				continue;
			} else if (args[a].toLowerCase().equals("gfx")) {
				a++;
				gfx = Boolean.parseBoolean(args[a]);
				continue;
			} else if (args[a].toLowerCase().equals("budget")) {
				a++;
				budget = Integer.parseInt(args[a]);
				continue;
			}
		}
	}

	
	
}
