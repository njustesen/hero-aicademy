package ai.evolution;

import java.util.HashMap;
import java.util.Map;

public class SharedStateTable {

	private Map<Long, Double> visited;

	public SharedStateTable(){
		visited = new HashMap<Long, Double>();
	}
	
	public double visit(Long hash, double value){
		synchronized (this) {
			if (visited.containsKey(hash) && value < visited.get(hash))
				visited.put(hash, value);
			else
				visited.put(hash, value);
			return visited.get(hash);
		}
	}

	public void clear() {
		visited.clear();
	}
	
}
