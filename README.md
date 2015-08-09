# Hero AIcademy

Hero AIcademy is a clone of the iOS game Hero Academy. The game features the Council team and is optimized for simulations and AI game reserach.

# How to run

## Human vs. Human
```
AI p1 = null;
AI p2 = null;
Game game = new Game(null, new GameArguments(true, p1, p2, "a", DECK_SIZE.STANDARD));
game.run();
```

## Human vs. AI
```
int budget = 4000; // 4 sec for AI's
AI p1 = null;
AI p2 = new RandomAI(RAND_METHOD.BRUTE);
GameArguments gameArgs = new GameArguments(true, p1, p2, "a", DECK_SIZE.STANDARD);
gameArgs.budget = budget; 
Game game = new Game(null, gameArgs);
game.run();
```

# How to make your own AI
```
public class myAwesomeAI implements AI {
  @Override
  public Action act(GameState state, long ms) {
    List<Actions> actions = state.possibleActions(actions);	
    return actions.get(0);  // Return first possible action
  }
}
```
