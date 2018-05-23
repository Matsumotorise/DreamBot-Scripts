package Craft;

import Handler.State;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.widgets.WidgetChild;

@ScriptManifest(category = Category.CRAFTING, name = "Crafting.01", author = "Andrew", version = .01)

public class Main extends AbstractScript {

	private final int FURNACE_ID = 24009, GOLD_BAR_ID = 2357;

	private State s;
	private Area bankArea;
	private Tile smeltTile;
	private WidgetChild wig;

	@Override
	public void onStart() { //0th state
		super.onStart();
		s = new State();
		while (s.getState() < 1) {
			log("Starting");
			sleep(200);
		}
		init();
	}

	private void init() { //1st state
		bankArea = getBankArea();
		smeltTile = getSmeltTile();
	}

	private Tile getSmeltTile() {
		switch (s.getSmeltLocation()) {
			case 0:
				return new Tile(3274, 3186, 0);
			case 1:
				break;
		}
		return null;
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

	private void moveToSmelt() {
		getWalking().walk(smeltTile);
		sleepUntil(() -> {
			sleep(100);
			return !getLocalPlayer().isMoving();
		}, 2500);
	}

	private void smelt() {
		if (getInventory().isItemSelected()) {
			getInventory().deselect();
		} else {
			wig = getWidgets().getWidgetChild(548, 17);
			if (wig != null && wig.isVisible()) {
				if (wig.interact("Make-All")) {
					if (sleepUntil(() -> getLocalPlayer().isAnimating(), 5000)) {
						int startLvl = getSkills().getRealLevel(Skill.CRAFTING);
						sleepUntil((() -> {
							sleep(1000);
							return !getInventory().contains(GOLD_BAR_ID) || startLvl != getSkills().getRealLevel(Skill.CRAFTING);
						}), 120000);
					}
				}
			} else {
				if (!getTabs().isOpen(Tab.INVENTORY)) {
					getTabs().open(Tab.INVENTORY);
					sleep(Calculations.random(400, 500));
				}
				getGameObjects().closest(FURNACE_ID).interact("Smelt");
			}
		}
	}

	private void moveToBank() {  //////////////3rd state////////////////
		getWalking().walk(bankArea.getRandomTile());
		sleepUntil(() -> {
			sleep(200);
			return !getLocalPlayer().isMoving();
		}, 2500);
	}

	private int bank() {  //4th state
		if (getBank().isOpen()) {
			getBank().depositAllExcept("Amulet mould");
			sleep(100, 200);
			getBank().withdrawAll(GOLD_BAR_ID);
			sleep(100, 200);
		} else {
			getBank().openClosest();
			sleepUntil(() -> getBank().isOpen(), 1500);
		}
		return (int) (Math.random() * 51) + 200;
	}

	private void checkState() {
		if (getInventory().contains(GOLD_BAR_ID)) {
			if (smeltTile.distance() > 4) {
				s.setState(3);
			} else {
				s.setState(2);
			}
		} else {
			if (!bankArea.contains(getLocalPlayer())) {
				s.setState(4);
			} else {
				s.setState(5);
			}
		}
		/*
		3 - move to smelt
		2 - smelt
		4 - move to bank
		5 - bank
		 */
	}

	@Override
	public int onLoop() {
		checkState();
		switch (s.getState()) {
			case 2:
				smelt();
				break;
			case 3:
				moveToSmelt();
				break;
			case 4:
				moveToBank();
				break;
			case 5:
				bank();
				break;
		}

		//RUN EVERY SECOND-ISH
		return Calculations.random(200, 800);
	}
}
