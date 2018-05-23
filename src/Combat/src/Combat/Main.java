package Combat;

import Handler.State;
import java.util.Random;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;

@ScriptManifest(category = Category.COMBAT, name = "Combat.01", author = "Andrew", version = .01)

public class Main extends AbstractScript {

	private Filter<NPC> targetfilter;
	private Area kArea, bankArea;
	private State s;
	private NPC target;
	private GroundItem lootItem;

	@Override
	public void onStart() { //0th state
		super.onStart();
		s = new State();
		while (s.getState() < 1) {
			sleep(100);
		}
		for (String s : s.getLoot()) {
			log("Looting:" + s);
		}
		init();
	}

	private void init() { //1st state
		kArea = Area.generateArea(s.getFightRadius(), getLocalPlayer().getTile());
		bankArea = getBankArea();
		targetfilter = npc -> npc != null && npc.getName().equals(s.getTarget()) && !npc.isInCombat();
		target = getNpcs().closest(targetfilter);
		if (s.isBuryBones()) {
			s.getLoot().add("Bones");
		}
	}

	private Area getBankArea() {
		BankLocation tmp = BankLocation.getNearest(getLocalPlayer());
		switch (s.getBankLocation()) {
			case 0:
				break;
			case 1:
				tmp = BankLocation.DRAYNOR;
				break;
			case 2:
				tmp = BankLocation.FALADOR_EAST;
				break;
			case 3:
				tmp = BankLocation.FALADOR_WEST;
				break;
			case 4:
				tmp = BankLocation.GRAND_EXCHANGE;
				break;
			case 5:
				tmp = BankLocation.LUMBRIDGE;
				break;
			case 6:
				tmp = BankLocation.VARROCK_EAST;
				break;
			case 7:
				tmp = BankLocation.VARROCK_WEST;
				break;
		}
		return tmp.getArea(3);
	}

	private void checkState() {
		if (getInventory().isFull()) {
			if (s.isBuryBones() && getInventory().contains("Bones")) {
				getInventory().all(item -> item.getName().equals("Bones")).stream().forEach(item -> item.interact("Bury"));
			} else if (bankArea.contains(getLocalPlayer())) {
				s.setState(4);  //Bank
			} else {
				s.setState(3);  //Walk to Bank
			}
		} else {
			if (kArea.contains(getLocalPlayer())) {
				lootItem = getGroundItems().closest(
						groundItem -> groundItem != null && groundItem.exists() && groundItem.getName() != null && (target
								.getSurroundingArea(1)).contains(groundItem) && s.getLoot().stream()
								.anyMatch(lootStr -> lootStr.equals(groundItem.getName())));
				if (lootItem != null && !getLocalPlayer().isInCombat()) {
					s.setState(5);  //Loot
				} else {
					s.setState(2);  //Attack
				}
			} else {
				s.setState(6);  //Walk back
			}
		}
	}

	private int bank() {  //4th state
		if (getBank().isOpen()) {
			getBank().depositAllItems();
			sleepUntil(() -> getInventory().isEmpty(), 1000);
			getBank().withdraw(s.getFood(), s.getFoodAmt());
		} else {
			getBank().openClosest();
			sleepUntil(() -> getBank().isOpen(), 1500);
		}
		return (int) (Math.random() * 51) + 200;
	}

	private void moveToBank() {  //////////////3rd state////////////////
		getWalking().walk(bankArea.getRandomTile());
		sleepUntil(() -> !getLocalPlayer().isMoving(), 2500);
	}

	/////////////////////2nd STATE/////////////////////////
	private int checkCombat() {
		if (!target.exists()) {
			target = getNpcs().closest(targetfilter);
			getCamera().rotateToEntity(target);
		}
		if (getLocalPlayer().getHealthPercent() < s.getEatPercentage()) { //Eat if Low
			getInventory().interact(s.getFood(), "Eat");
		}
		if (!getLocalPlayer().isInCombat()) {
			target.interact("Attack");
			sleepUntil(() -> !getLocalPlayer().isInCombat(), 1500);
		}
		return (int) (Math.random() * 201) + 1200;
	}

	/////////////////////5th State/////////////////////////
	private void loot() {
		lootItem.interact("Take");
	}

	/////6th STATE/////////////////////
	private void walkBack() {
		getWalking().walk(kArea.getCenter().getArea(1).getRandomTile());
	}

	private void antiBan() {
		Random srand = new Random();
		double chances = srand.nextDouble();
		if (chances < 0.096) {
			log("Antiban; changing camera angle...");
			getCamera().rotateToEvent(srand.nextInt() + 360, srand.nextInt() + 90);
		} else {
		}
	}

	@Override
	public int onLoop() {
		checkState();
		/* Key
		0/1 - GUI StartUp
		2 CombatChecks
		3 - Move to Bank
		4 - Bank
		5 - Loot
		6 - Return
		*/
		switch (s.getState()) {
			case 2:
				return checkCombat();
			case 3:
				moveToBank();
				break;
			case 4:
				return bank();
			case 5:
				loot();
				break;
			case 6:
				walkBack();
				break;
			case 7:
				antiBan();
				break;
		}

		//DEFAULT:
		return ((int) (Math.random() * 200)) + 493;
	}
}
