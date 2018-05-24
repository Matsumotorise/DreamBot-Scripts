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
	private Product jewelery;

	private State s;
	private Area bankArea, smeltArea;
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
		smeltArea = getSmeltArea();
		smeltTile = getSmeltTile();
		jewelery = getJewelery();
	}

	private Tile getSmeltTile() {
		return smeltArea.getRandomTile();
	}

	private Area getSmeltArea() {
		switch (s.getSmeltLocation()) {
			case 0:
				return new Tile(3276, 3186, 0).getArea(2);
			case 1:
				return new Tile(2973, 3370, 0).getArea(2);
		}
		return null;
	}

	private Product getJewelery() {
		switch (s.getProduct()) {
			case 0:
				return Product.AMULET;
			case 1:
				return Product.NECKLACE;
			case 2:
				return Product.RING;
		}
		return null;
	}

	private Area getBankArea() {
		return BankLocation.getNearest(getLocalPlayer()).getArea(4);
	}

	private void moveToSmelt() {
		smeltTile = getSmeltTile();
		getWalking().walk(smeltTile);
		sleepUntil(() -> {
			sleep(200);
			return !getLocalPlayer().isMoving();
		}, 2500);
	}

	private void smelt() {
		if (getInventory().isItemSelected()) {
			getInventory().deselect();
			log("Deselected");
		} else {
			wig = getWidgets().getWidgetChild(446, jewelery.getChildID());
			log("Widget made");
			if (wig != null && wig.isVisible()) {
				log("Widget Visible");
				if (wig.interact("Make-All")) {
					log("Making all");
					if (sleepUntil(() -> getLocalPlayer().isAnimating(), 5000)) {
						log("Player animating");
						getMouse().moveMouseOutsideScreen();
						int startLvl = getSkills().getRealLevel(Skill.CRAFTING);
						sleepUntil((() -> {
							sleep(1000);
							return !getInventory().contains(GOLD_BAR_ID) || startLvl != getSkills().getRealLevel(Skill.CRAFTING);
						}), 120000);
						log("Done");
						sleep(400, 5000);
					}
				}
			} else {
				if (Math.random() > .1) {
					getGameObjects().closest(FURNACE_ID).interact("Smelt");
					log("Interacting by smelt");
				} else {
					if (!getTabs().isOpen(Tab.INVENTORY)) {
						getTabs().open(Tab.INVENTORY);
						sleep(Calculations.random(400, 500));
						log("Switching to inv");
					}
					getInventory().get(GOLD_BAR_ID).useOn(getGameObjects().closest(FURNACE_ID));
					log("Using gold bar on furnace");
					sleep(100);
				}
			}
		}
	}

	private void moveToBank() {  //////////////3rd state////////////////
		getWalking().walk(bankArea.getRandomTile());
		sleepUntil(() -> {
			sleep(250, 350);
			return !getLocalPlayer().isMoving();
		}, 2500);
	}

	private int bank() {  //4th state
		if (getBank().isOpen()) {
			getBank().depositAllExcept(jewelery.getMouldID());
			sleep(100, 200);
			getBank().withdrawAll(GOLD_BAR_ID);
			sleep(100, 200);
			if (!getInventory().contains(jewelery.getMouldID())) {
				getBank().withdraw(jewelery.getMouldID());
			}
		} else {
			getBank().openClosest();
			sleepUntil(() -> getBank().isOpen(), 1500);
		}
		return (int) (Math.random() * 51) + 200;
	}

	private void checkState() {
		if (getInventory().contains(GOLD_BAR_ID)) {
			if (smeltTile.distance() > 6) {
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
