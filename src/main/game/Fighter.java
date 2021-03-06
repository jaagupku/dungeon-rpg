package main.game;

import java.util.Random;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import main.Game;
import main.hud.HitSplat;

public class Fighter {

	String name;
	Random rng = new Random();
	private DoubleProperty x, y;
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
		this.x = new SimpleDoubleProperty();
		this.y = new SimpleDoubleProperty();
	}

	public int attackOther(Fighter o) {
		int dmg = rng.nextInt(attackPower);
		if (rng.nextInt(attackAccuracy) > rng.nextInt(o.getAgility())) {
			o.defendFromAttack(dmg);
			return dmg;

		} else {
			Game.hitSplats.add(new HitSplat("DODGED", (o.getX() + 0.38), (o.getY() + 0.8)));
			return 0;
		}
	}

	public void defendFromAttack(int dmg) {
		if (rng.nextInt(getDefense()) < dmg) {
			takeDamage(dmg);
			Game.hitSplats.add(new HitSplat(Integer.toString(dmg), (getX() + 0.38), (getY() + 0.8)));
		} else {
			Game.hitSplats.add(new HitSplat("BLOCKED", (getX() + 0.38), (getY() + 0.8)));
		}
	}

	/**
	 * Subtracts damage from hp.
	 * 
	 * @param dmg
	 *            - amount damage taken
	 */
	private void takeDamage(int dmg) {
		health.set(getHealth() - dmg);
		if (getHealth() <= 0) {
			health.set(0);
		}
	}

	/**
	 * Increases fighters health by amount. Health won't go higher than max
	 * health.
	 * 
	 * @param amount
	 *            - amount healed
	 */
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

	public double getX() {
		return x.doubleValue();
	}

	public double getY() {
		return y.doubleValue();
	}

	public final DoubleProperty xProperty() {
		return x;
	}

	public final DoubleProperty yProperty() {
		return y;
	}

	public void setX(double x) {
		this.x.set(x);
	}

	public void setY(double y) {
		this.y.set(y);
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

	final ReadOnlyIntegerProperty healthProperty() {
		return IntegerProperty.readOnlyIntegerProperty(health);
	}

	void setHealth(int hp) {
		health.set(hp);
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
