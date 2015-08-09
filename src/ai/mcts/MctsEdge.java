package ai.mcts;

import java.util.HashSet;
import java.util.List;

import action.Action;

public class MctsEdge {
	
	public int visits;
	public double value;
	public boolean p1;
	public MctsNode from;
	public MctsNode to;
	public Action action;
	
	public MctsEdge (MctsNode from, MctsNode to, Action action, boolean p1){
		this.from = from;
		this.to = to;
		this.action = action;
		this.p1 = p1;
	}

	public boolean isLeaf() {
		return to == null;
	}
	
	public double avg() {
		return (double)value / (double)visits;
	}

	public String toXml(int depth, HashSet<MctsNode> visited, int max) {
		String tabs = "";
		String str = "";
		for (int i = 0; i < depth; i++)
			tabs += "\t";
		if (visits >= 1){
			str = tabs + "<edge p='" + (p1 ? "1" : "2") + "' a='" + action + "' vis='" + visits + "' avg='" + avg() + "' >\n";
			str += to.toXml(depth+1, visited, max);
			str += tabs + "</edge>\n";
		} else {
			str = tabs + "<edge p='" + (p1 ? "1" : "2") + "' a='" + action + "' vis='" + visits + "' avg='" + avg() + "' />\n";
		}
			
		return str;
	}

	@Override
	public String toString() {
		return "MctsEdge [vis=" + visits + ", val=" + value + ", p1=" + p1
				+ ", a=" + action + "]";
	}

	public void depth(int depth, List<Double> depths, HashSet<MctsNode> visited) {
		if (visits >= 1)
			to.depth(depth+1, depths, visited);
		else
			depths.add((double)depth);
	}
	
}
