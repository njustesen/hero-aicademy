package util;

import java.util.Collections;
import java.util.List;

public class Statistics {

	public static double avgDouble(List<Double> vals){
		
		double sum = 0;
		for(double d : vals)
			sum += d;
		
		return sum / vals.size();
		
	}
	
	public static double avgInteger(List<Integer> vals){
		
		double sum = 0;
		for(int d : vals)
			sum += d;
		
		return sum / vals.size();
		
	}
	
	public static double stdDevDouble(List<Double> vals){
		
		double avg = avgDouble(vals);
		
		double sum = 0;
		for(double d : vals)
			sum += (d - avg) * (d - avg);
		
		double davg = sum / vals.size();
		
		return Math.sqrt(davg);
		
	}
	
	public static double stdDevInteger(List<Integer> vals){
		
		double avg = avgInteger(vals);
		
		double sum = 0;
		for(double d : vals)
			sum += (d - avg) * (d - avg);
		
		double davg = sum / vals.size();
		
		return Math.sqrt(davg);
		
	}

	public static int max(List<Integer> vals) {
		
		int min = Integer.MIN_VALUE;
		
		for(int n : vals)
			if (n > min)
				min = n;
		
		return min;
	}
	
}
