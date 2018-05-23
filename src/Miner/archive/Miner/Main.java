package Miner;

import Miner.Handler.State;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;

@ScriptManifest(category = Category.MINING, name = "Miner.01", author = "Andrew", version = .01)
public class Main extends AbstractScript {

	private State s;
	private Area mArea, bankArea;

	@Override
	public void onStart() { //0th state
		super.onStart();
		s = new State();
		while (s.getState() < 1) {
			sleep(100);
		}
		init();
	}

	private void init() { //1st state
		mArea = Area.generateArea(s.getRadius(), getLocalPlayer().getTile());
		bankArea = getBankArea();
	}

	private Area getBankArea() {
		BankLocation tmp = BankLocation.getNearest(getLocalPlayer());
		switch (s.getBankArea()) {
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
			if (s.isDrop()) {
				s.setState(6);
			} else if (bankArea.contains(getLocalPlayer())) {
				s.setState(4);  //Bank
			} else {
				s.setState(3);  //Walk to bank
			}
		} else if (!mArea.contains(getLocalPlayer())) {
			s.setState(5);  //Returning
		} else {
			s.setState(2);  //Mining
		}
	}

	private void mine() {      ///////////2nd state///////////
		GameObject rock = getGameObjects()
				.closest(obj -> obj != null && obj.getName().equals("Rocks") && obj.getModelColors()[0] == ROCK_COLOR);
	}

	private void moveToBank() {  //////////////3rd state////////////////
		getWalking().walk(bankArea.getRandomTile());
		sleepUntil(() -> bankArea.contains(getLocalPlayer()), 2500);
	}

	private void bank() {    ////////////4th state/////////////
		if (getBank().isOpen()) {
			getBank().depositAllItems();
			sleepUntil(() -> getInventory().isEmpty(), 1000);
		} else {
			getBank().openClosest();
			sleepUntil(() -> getBank().isOpen(), 1500);
		}
	}

	private void walkBack() {    /////////////5th state///////////
		getWalking().walk(mArea.getCenter().getArea(1).getRandomTile());
		sleepUntil(() -> mArea.contains(getLocalPlayer()), 2500);
	}

	private void drop() {
		getInventory().dropAll(str -> str.getName().endsWith("ore") || str.getName().equals("Coal"));
		sleepUntil(() -> getInventory()
				.onlyContains((Item item) -> !(item.getName().endsWith("ore") || item.getName().equals("Coal"))), 2500);
	}

	public int onLoop() {
		checkState();
		switch (s.getState()) {
			case 2: //Mine
				mine();
				break;
			case 3: //Go to Bank
				moveToBank();
				break;
			case 4: //Bank
				bank();
				break;
			case 5: //Returning
				walkBack();
				break;
			case 6:
				drop();
				break;
		}
		return Calculations.random(400, 1000);
	}

}
