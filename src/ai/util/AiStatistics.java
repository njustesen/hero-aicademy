package ai.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Statistics;

public class AiStatistics {

	public Map<String, Double> stats;
	public Map<String, List<Double>> statsLists;

	public AiStatistics() {
		super();
		this.stats = new HashMap<String, Double>();
		this.statsLists = new HashMap<String, List<Double>>();
	}

	@Override
	public String toString() {
		String str = "";
		for(String key : stats.keySet())
			str += key + " = "+ stats.get(key) + "\n";
		for(String key : statsLists.keySet()){
			if (statsLists.get(key) == null || statsLists.get(key).isEmpty())
				continue;
			str += key + " (avg) = "+ Statistics.avgDouble(statsLists.get(key)) + "\n";
			str += key + " (std.dev) = "+ Statistics.stdDevDouble(statsLists.get(key)) + "\n";
			str += key + " (min) = "+ Collections.min(statsLists.get(key)) + "\n";
			str += key + " (max) = "+ Collections.max(statsLists.get(key)) + "\n";
			str += key + " (datapoints) = "+ statsLists.get(key).size() + "\n";
		}
		return str + "\n";
	}
	
}
