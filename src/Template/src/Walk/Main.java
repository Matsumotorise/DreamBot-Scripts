package Walk;


import Handler.State;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

@ScriptManifest(category = Category.MISC, name = "tmp.01", author = "Andrew", version = .01)

public class Main extends AbstractScript {

	private State s;

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
	}


	private void checkState() {

	}


	private void moveToBank() {  //////////////3rd state////////////////
	}


	@Override
	public int onLoop() {
		checkState();
		switch (s.getState()) {
			case 2:
				break;
			case 3:
				break;
		}

		//RUN EVERY SECOND-ISH
		return Calculations.random(200, 800);
	}
}
