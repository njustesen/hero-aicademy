package ai.util;

public class NormUtil {
	
	public static double normalize(double x, double dataLow, double dataHigh, double normHigh, double normLow) {
		return ((x - dataLow) 
				/ (dataHigh - dataLow))
				* (normHigh - normLow) + normLow;
	}
	
}
