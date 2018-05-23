package Walk;


import Handler.State;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

@ScriptManifest(category = Category.MISC, name = "Walker.01", author = "Andrew", version = .01)

public class Main extends AbstractScript {

	private State s;
	private boolean isRun;
	private Area destinationA = null;
	private Tile destinationT = null;


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
		generateDestination();
	}

	private void generateDestination() {
		Tile tmpTile = null;
		switch (s.getDestination()) {
			case 0: //Al Kharid bank
				destinationA = BankLocation.AL_KHARID.getArea(3);
			case 1: //Al Kharid mining spot (3296, 3277, 0)
				tmpTile = new Tile(3296, 3777, 0);
				break;
			case 2: //Barbarian Village (3092, 2421, 0)
				tmpTile = new Tile(3092, 2421, 0);
				break;
			case 3: //Chaos Temple (2935, 3516, 0)
				tmpTile = new Tile(2935, 3516, 0);
				break;
			case 4: //Draynor
				tmpTile = new Tile(3094, 3244, 0);
				break;
			case 5: //Duel Arena
				destinationA = BankLocation.DUEL_ARENA.getArea(3);
				break;
			case 6: //Edgeville Monastery (3052, 3485, 0)
				tmpTile = new Tile(3052, 3485, 0);
				break;
			case 7: //Edgeville Bank
				destinationA = BankLocation.EDGEVILLE.getArea(3);
				break;
			case 8: //Falador East
				destinationA = BankLocation.FALADOR_EAST.getArea(3);
				break;
			case 9: // Falador West
				destinationA = BankLocation.FALADOR_WEST.getArea(3);
				break;
			case 10: //G.E.       (3164, 3218, 0)
				tmpTile = new Tile(3164, 3487, 0);
				break;
			case 11: //Hill Giants (3115, 3449, 0)
				tmpTile = new Tile(3115, 3449, 0);
				break;
			case 12: //Lumbridge  (3221, 3218, 0)
				tmpTile = new Tile(3221, 3218, 0);
				break;
			case 13: //Port Sarim (3031, 3236, 0)
				tmpTile = new Tile(3031, 3236, 0);
				break;
			case 14: //Rimmington (2955, 3215, 0)
				tmpTile = new Tile(2956, 3215, 0);
				break;
			case 15:  // Varrock center (3212, 3428, 0)
				tmpTile = new Tile(3212, 3428, 0);
				break;
			case 16:  // Varrock East
				destinationA = BankLocation.VARROCK_EAST.getArea(3);
				break;
			case 17:  // Varrock West
				destinationA = BankLocation.VARROCK_WEST.getArea(3);
				break;
			case 18:  //Wizards tower
				tmpTile = new Tile(3108, 3164, 0);
				break;
		}

		log((new Integer(s.getDestination())).toString());

		if (destinationA == null) {
			destinationA = tmpTile.getArea(3);
		}
		destinationT = destinationA.getRandomTile();
	}

	private void checkState() {
		if (!destinationA.contains(getLocalPlayer())) {
			s.setState(2);
		} else {
			stop();
		}
	}

	private void move() {
		getWalking().walk(destinationT);
		sleepUntil(() -> !getLocalPlayer().isMoving(),
				(isRun ? (Calculations.random(1800, 1900)) : (Calculations.random(3600, 3800))));
	}

	@Override
	public void onExit() {
		super.onExit();
		getMouse().moveMouseOutsideScreen();
	}

	@Override
	public int onLoop() {
		isRun = getWalking().isRunEnabled();
		checkState();
		switch (s.getState()) {
			case 2:
				move();
		}
		//RUN EVERY SECOND-ISH
		return Calculations.random((isRun ? 300 : 500), (isRun ? 600 : 900));
	}
}
