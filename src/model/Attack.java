package model;

public class Attack {

	public int range;
	public AttackType attackType;
	public double meleeMultiplier;
	public double rangeMultiplier;
	public boolean chain;
	public boolean push;
	
	public Attack(int range, AttackType attackType,
			int damage, double meleeMultiplier, double rangeMultiplier,
			boolean chain, boolean push) {
		super();
		this.range = range;
		this.attackType = attackType;
		this.meleeMultiplier = meleeMultiplier;
		this.rangeMultiplier = rangeMultiplier;
		this.chain = chain;
		this.push = push;
		
	}

	
}
