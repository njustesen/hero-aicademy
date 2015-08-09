package calcs;

import java.math.BigDecimal;

public class configs {

	public static void main(String[] args){
		
		BigDecimal conf_all = new BigDecimal(0);
		
		int hp = 800;
		//int hp = 1;
		
		for(int n = 1; n <= 26; n++){
			//int n = 26;
			BigDecimal conf_hp = new BigDecimal(0);
			for(int i = 1; i <= n; i++){
				System.out.println("ADD:"+ new BigDecimal((39-i+1)*hp));
				if (conf_hp.toString().equals("0"))
					conf_hp = new BigDecimal((39-i+1)*hp);
				else
					conf_hp = conf_hp.multiply(new BigDecimal((39-i+1)*hp));
				System.out.println("conf_hp:"+conf_hp.toEngineeringString());
			}
			conf_all.add(conf_hp);
			System.out.println("conf_all:"+conf_hp.toEngineeringString());
		}
		
		//System.out.println(big.to);
		
	}
	
	/*
	 * public static void main(String[] args){
		
		BigDecimal big = new BigDecimal(0);
		
		int hp = 800;
		//int hp = 1;
		
		for(int n = 0; n <= 26; n++){
			//int n = 26;
			BigDecimal confhp = new BigDecimal(0);
			for(int i = 1; i <= n; i++){
				System.out.println("ADD:"+ new BigDecimal((39-i+1)*hp));
				if (big.toString().equals("0"))
					big = new BigDecimal((39-i+1)*hp);
				else
					big = big.multiply(new BigDecimal((39-i+1)*hp));
				System.out.println("RES:"+big.toEngineeringString());
			}
		}
		
		//System.out.println(big.to);
		
	}
	 */
	
}
