package WC;

import Handler.State;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;

@ScriptManifest(category = Category.WOODCUTTING, name = "WC.01", author = "Andrew", version = .01)

public class Main extends AbstractScript {

	private State s;
	private Area bArea, cArea;
	private GameObject curTree;
	private Filter<GameObject> treeFilter;

	@Override
	public void onExit() {
		super.onExit();
		getMouse().moveMouseOutsideScreen();
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
		treeFilter = (tree -> tree.getName().equals(s.getTree()) && cArea.contains(tree));
		bArea = getBankArea();
		cArea = getLocalPlayer().getSurroundingArea(s.getRadius());
	}

	private void checkState() {
		if (getInventory().isFull()) {
			if (bArea.contains(getLocalPlayer())) { //Bank
				s.setState(4);
			} else {  //Move to bank
				s.setState(3);
			}
		} else if (!cArea.contains(getLocalPlayer())) {
			s.setState(5);
		} else {
			if (!getLocalPlayer().isAnimating()) {
				s.setState(2);  //Find next tree to chop
			}
		}
	}


	private void moveToBank() {  //////////////3rd state////////////////
		getWalking().walk(bArea.getRandomTile());
		sleepUntil(() -> !getLocalPlayer().isMoving(), 2500);
	}

	private int bank() {  //4th state
		if (getBank().isOpen()) {
			getBank().depositAllItems();
			sleepUntil(() -> getInventory().isEmpty(), 1000);
		} else {
			getBank().openClosest();
			sleepUntil(() -> getBank().isOpen(), 1500);
		}
		return (int) (Math.random() * 51) + 200;
	}

	private void cut() {
		curTree = getGameObjects().closest(treeFilter);
		if (getLocalPlayer().distance(curTree) > 5) {
			getWalking().walk(curTree);
			sleepUntil(() -> !getLocalPlayer().isMoving() || getLocalPlayer().distance(getClient().getDestination()) < 7,
					Calculations.random(4600, 5400));
		} else {
			if (curTree.interact("Chop down")) {
				sleepUntil(() -> !curTree.exists() || !getLocalPlayer().isAnimating(), Calculations.random(12000, 15000));
			}
		}
	}

	private void walkBack() {
		getWalking().walk(cArea.getCenter().getArea(1).getRandomTile());
	}

	@Override
	public int onLoop() {
		checkState();
		switch (s.getState()) {
			case 2: //Cut
				cut();
				break;
			case 3:  //Move to bank
				moveToBank();
				break;
			case 4:
				bank();
				break;
			case 5: //Move back
				walkBack();
				break;
		}

		//RUN EVERY SECOND-ISH
		return Calculations.random(200, 500);
	}
}

