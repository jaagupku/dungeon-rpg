package game;

import java.util.Random;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Fighter {

	String name;
	Random rng = new Random();
	private int maxHealth;
	private IntegerProperty health;
	private int attackPower, attackAccuracy;
	private int defense, agility;
	// attackPower on max damage, attackAccuracy veeretab täringut vastase
	// agility vastu ehk täpsus
	// kui ründaja võidab täpsusega siis veeretatakse veeretatakse täringut
	// attackPoweriga, saadakse damage
	// siis vastane veeretab defence*2-1 täringut ja kui see on suurem kui
	// dmg ründaja poolt, siis blokeerib selle rünnaku

	public Fighter(String name, int maxHealth, int attackPower, int attackAccuracy, int defense, int agility) {
		super();
		this.name = name;
		this.maxHealth = maxHealth;
		health = new SimpleIntegerProperty(maxHealth);
		this.attackPower = attackPower;
		this.attackAccuracy = attackAccuracy;
		this.defense = defense;
		this.agility = agility;
	}

	public int attackOther(Fighter o) {
		int dmg = rng.nextInt(attackPower);
		if (rng.nextInt(attackAccuracy) > rng.nextInt(o.getAgility())) {
			System.out.println(getName() + " attacks " + o.getName());
			o.defendFromAttack(dmg);
			return dmg;

		} else {
			System.out.println(o.getName() + " dodged the attack from " + getName() + ".");
			return 0;
		}
	}

	public void defendFromAttack(int dmg) {
		if (rng.nextInt(getDefense()) < dmg) {
			takeDamage(dmg);
		} else {
			System.out.println(getName() + " successfully blocked the attack.");
		}
	}

	private void takeDamage(int dmg) {
		health.set(getHealth() - dmg);
		System.out.print(getName() + " got damaged for " + dmg + " hitpoints");
		if (getHealth() <= 0) {
			health.set(0);
			System.out.println(".");
			System.out.println(getName() + " is now dead.");
		} else {
			System.out.println(" it now has " + getHealth() + " health.");
		}
	}

	public void heal(int health) {
		this.health.set(getHealth() + health > maxHealth ? maxHealth : getHealth() + health);
	}

	protected void levelUp() {
		maxHealth += 10;
		health.set(getHealth() + 10);
		attackPower += 2;
		attackAccuracy += 1;
		defense += 1;
		agility += 1;
	}

	public int getDefense() {
		return defense;
	}

	public int getAgility() {
		return agility;
	}

	public String getName() {
		return name;
	}

	public int getHealth() {
		return health.intValue();
	}
	
	IntegerProperty healthProperty(){
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public int getAttackPower() {
		return attackPower;
	}

	public int getAttackAccuracy() {
		return attackAccuracy;
	}
}
