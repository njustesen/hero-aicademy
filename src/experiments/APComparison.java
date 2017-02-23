package experiments;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ai.AI;
import ai.GreedyActionAI;
import ai.GreedyTurnAI;
import ai.RandomAI;
import ai.RandomHeuristicAI;
import ai.evaluation.HeuristicEvaluator;
import ai.evaluation.RolloutEvaluator;
import ai.evolution.OnlineEvolution;
import ai.evolution.OnlineIslandEvolution;
import ai.mcts.Mcts;
import ai.util.RAND_METHOD;
import model.DECK_SIZE;
import game.Game;
import game.GameArguments;
import game.GameState;

public class APComparison {

	public static void main(String[] args) {
		
		if (args.length < 5){
			System.out.println("5 args: budget AP games p1 p2. Players: (cutting|non|oe)");
			return;
		}
		
		int budget = Integer.parseInt(args[0]); 
		List<Integer> aps = new ArrayList<Integer>();
		for (String ap : args[1].split(",")){
			aps.add(Integer.parseInt(ap));
		}
		int g = Integer.parseInt(args[2]);
		
		AI mcts = new Mcts(budget, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(true)));
		AI cutting = new Mcts(budget, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.5), new HeuristicEvaluator(true)));
		((Mcts)cutting).cut = true;
		AI noneExp = new Mcts(budget, new RolloutEvaluator(1, 1, new RandomHeuristicAI(1), new HeuristicEvaluator(true)));
		((Mcts)noneExp).c = 0;
		
		AI onlineE = new OnlineEvolution(true, 100, 0.1, 0.5, budget, new HeuristicEvaluator(false));
		
		AI p1 = null;
		AI p2 = null;
		
		if (args[3].toLowerCase().equals("cutting")){
			p1 = cutting;
		} else if (args[3].toLowerCase().equals("non")){
			p1 = noneExp;
		} else if (args[3].toLowerCase().equals("oe")){
			p1 = onlineE;
		} else if (args[3].toLowerCase().equals("mcts")){
			p1 = mcts;
		}
		
		if (args[4].toLowerCase().equals("cutting")){
			p2 = cutting;
		} else if (args[4].toLowerCase().equals("non")){
			p2 = noneExp;
		} else if (args[4].toLowerCase().equals("oe")){
			p2 = onlineE;
		} else if (args[4].toLowerCase().equals("mcts")){
			p2 = mcts;
		}
		
		String p1Name = args[3];
		String p2Name = args[4];
		
		if (p1 == null || p2 == null){
			System.out.println("5 args: budget AP games p1 p2. Players: (cutting|non|oe|mcts)");
			return;
		}
		
		for (int ap : aps){
			test(p1, p1Name, p2, p2Name, ap, budget, g);
		}
	}

	private static void test(AI p1, String p1Name, AI p2, String p2Name, int ap, int budget, int games) {
		
		GameState.ACTION_POINTS = ap;
		GameState.TURN_LIMIT = 100;
		if (ap < 5){
			GameState.TURN_LIMIT = (int) (GameState.TURN_LIMIT * 5.0 / (double)ap);				
		}
		
		String out = "";
		out += "-----------------------" + "\n";
		out += "Proc. cores: " + Runtime.getRuntime().availableProcessors() + "\n";
		out += "Budget: " + budget + "\n";
		out += "AP: " + ap + "\n";
		out += "TURN LIMIT: " + GameState.TURN_LIMIT + "\n";
		out += "Games: " + games + "\n";
		out += "P1: " + p1Name + "\n";
		out += "P2: " + p2Name + "\n";
		
		int p1Wins = 0;
		int p2Wins = 0;
		int draws = 0;
		System.out.println("Running: \n" + out);
		for (int i = 0; i < games; i++){
			GameArguments gameArgs = new GameArguments(false, p1, p2, "a", DECK_SIZE.STANDARD);
			Game game = new Game(null, gameArgs);
			game.run();
			if (game.state.getWinner() == 1){
				p1Wins += 1;
			} else if (game.state.getWinner() == 2){
				p2Wins += 1;
			} else {
				draws += 1;
			}
			System.out.print("|");
		}
		System.out.print("\n");
		
		out += p1Name + ": " + p1Wins + "\n";
		out += p2Name + ": " + p2Wins + "\n";
		out += "Draws: " + draws + "\n";
		out += "-----------------------" + "\n";
		try {
			PrintWriter writer = new PrintWriter("HAStats_p1" + p1Name + "_p2" + p2Name + "_b" + budget + "_ap" + ap + "_g" + games + ".txt");
			writer.println(out);
			writer.close();  
		} catch (Exception e){
			System.out.println("Could not save file!" + e.getMessage());
		}
		
		System.out.println(out);
		System.out.println("Done");
		
	}

}
