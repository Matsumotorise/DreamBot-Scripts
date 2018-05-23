package nezz.dreambot.scriptmain.powerminer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import nezz.dreambot.powerminer.gui.ScriptVars;
import nezz.dreambot.powerminer.gui.minerGui;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;

@ScriptManifest(author = "Andrew", description = "Power Miner", name = "Miner 1.1", version = 1.1, category = Category.MINING)
public class Miner extends AbstractScript {

	Bank bank;
	Inventory inv;
	private ScriptVars sv = new ScriptVars();
	private Timer timer;
	//current state
	private State state;
	private GameObject currRock = null;
	//private Tile startTile = null;
	private MineTask currTask = null;
	private int taskPlace = 0;
	private boolean started = false;
	private minerGui gui = null;

	private State getState() {
		if (!started) {
			return State.GUI;
		}
		if (currTask.isPowerMine()) {
			if (getInventory().contains(currTask.getOreName())) {
				return State.DROP;
			}
		} else if (getInventory().isFull()) {
			return State.BANK;
		}
		return State.MINE;
	}

	@Override
	public void onStart() {
		getClient().disableIdleCamera();
		getClient().disableIdleMouse();
		log("Starting DreamBot AIO Mining script!");
	}

	@Override
	public int onLoop() {
		if (started) {
			if (currTask.reachedGoal()) {
				log("Finished current task!");
				taskPlace++;
				if (taskPlace >= sv.tasks.size()) {
					log("Finished all tasks!");
					stop();
					return 1;
				}
				currTask = sv.tasks.get(taskPlace);
				currTask.resetTimer();
				return 200;
			}

			Player myPlayer = getLocalPlayer();
			if (!getWalking().isRunEnabled() && getWalking().getRunEnergy() > Calculations.random(30, 70)) {
				getWalking().toggleRun();
			}
			if (myPlayer.isMoving() && getClient().getDestination() != null
					&& getClient().getDestination().distance(myPlayer) > 5) {
				return Calculations.random(300, 600);
			}
			if (getLocalPlayer().isInCombat()) {
				return Calculations.random(300, 600);
			}
		}
		state = getState();
		switch (state) {
			case GUI:
				if (gui == null) {
					gui = new minerGui(sv, getClient().getMethodContext());
					sleep(300);
				} else if (!gui.isVisible() && !sv.started) {
					gui.setVisible(true);
					sleep(1000);
				} else {
					if (!sv.started) {
						sleep(300);
					} else {
						bank = getBank();
						inv = getInventory();
						currTask = sv.tasks.get(0);
						currTask.resetTimer();
						getSkillTracker().start(Skill.MINING);
						timer = new Timer();
						started = true;
					}
				}
				break;
			case BANK:
				if (bank.isOpen()) {
					if (inv.get(i -> {
						if (i == null || i.getName() == null) {
							return false;
						}
						return i.getName().contains("pickaxe");
					}) != null) {
						for (int i = 0; i < 28; i++) {
							final Item item = inv.getItemInSlot(i);
							if (item != null && !item.getName().contains("pickaxe")) {
								bank.depositAll(item.getName());
								sleepUntil(() -> !inv.contains(item.getName()), 2000);
							}
						}
					} else {
						bank.depositAllItems();
						sleepUntil(() -> inv.isEmpty(), 2000);
					}
				} else {
					if (currTask.getBank().getArea(4).contains(getLocalPlayer())) {
						bank.open();
						sleepUntil(() -> bank.isOpen(), 2000);
					} else {
						getWalking().walk(currTask.getBank().getCenter());
						sleepUntil(() -> getLocalPlayer().isMoving(), 2000);
					}
				}
				break;
			case DROP:
				currRock = null;
				Item ore = inv.get(currTask.getOreName());
				if (ore != null) {
					inv.interact(ore.getName(), "Drop");
					sleepUntil(() -> {
						Item ore1 = inv.get(currTask.getOreName());
						return ore1 == null;
					}, 1200);
				}
				break;
			case MINE:
				if (bank.isOpen()) {
					bank.close();
					sleepUntil(() -> !bank.isOpen(), 1200);
				} else {
					if (currTask.getStartTile().distance(getLocalPlayer()) > 10) {
						getWalking().walk(currTask.getStartTile());
						sleepUntil(() -> getLocalPlayer().isMoving(), 1000);
					} else if ((currTask.dontMove() && !getLocalPlayer().getTile().equals(currTask.getStartTile()))) {
						getWalking().walk(currTask.getStartTile());
						sleepUntil(() -> getLocalPlayer().isMoving(), 1000);
						sleepUntil(() -> !getLocalPlayer().isMoving(), 2000);
					} else {
						if (getCamera().getPitch() < 270) {
							getCamera().rotateToPitch((int) (Calculations.random(300, 400) * getClient().seededRandom()));
						}
						if (getLocalPlayer().getAnimation() == -1 && (currRock == null || !currRock.isOnScreen() || !currTask
								.isPowerMine())) {
							currRock = currTask.getRock(); //getGameObjects().getClosest(currTask.getIDs());
						}
						if (getLocalPlayer().getAnimation() == -1) {  //if your'e free
							if (currRock != null && currRock.exists()) {
								if (currRock.interact("Mine")) {
									if (currTask.isPowerMine()) {
										hover(true);
									} else {
										sleepUntil(() -> getLocalPlayer().getAnimation() != -1, 1000);//Mining time
									}
								}
							}
						} else {  //if your still mining
							sleep(20, 55);
						}
					}
				}
				currTask.getTracker().update();
				break;
		}
		return Calculations.random(50, 100);
	}

	private int getFirstEmptySlot() {
		for (int i = 0; i < 28; i++) {
			Item it = getInventory().getItemInSlot(i);
			if (it == null || it.getName().contains("ore")) {
				return i;
			}
		}
		return 0;
	}

	private void hover(boolean fromInteract) {
		int firstEmpty = getFirstEmptySlot();
		Rectangle r = getInventory().slotBounds(firstEmpty);
		if (!r.contains(getMouse().getPosition())) {
			int x1 = (int) r.getCenterX() - Calculations.random(0, 10);
			int y1 = (int) r.getCenterY() - Calculations.random(0, 10);
			int x2 = (int) r.getCenterX() + Calculations.random(0, 10);
			int y2 = (int) r.getCenterY() + Calculations.random(0, 10);
			int fX = Calculations.random(x1, x2);
			int fY = Calculations.random(y1, y2);
			getMouse().move(new Point(fX, fY));
		}
		if (fromInteract) {
			sleepUntil(() -> getLocalPlayer().getAnimation() != -1, 2000);
		}
	}

	@Override
	public void onExit() {
		log("Stopping testing!");
	}

	public void onPaint(Graphics g) {
		if (started) {
			g.setColor(Color.green);
			if (state != null) {
				g.drawString("State: " + state.toString(), 5, 50);
			}
			g.drawString("Total Runtime: " + timer.formatTime(), 5, 65);
			g.drawString("Task Runtime: " + currTask.getTimer().formatTime(), 5, 80);
			g.drawString("Experience(p/h): " + getSkillTracker().getGainedExperience(Skill.MINING) + "(" + getSkillTracker()
					.getGainedExperiencePerHour(Skill.MINING) + ")", 5, 95);
			g.drawString("Level(gained): " + getSkills().getRealLevel(Skill.MINING) + "(" + getSkillTracker()
					.getGainedLevels(Skill.MINING) + ")", 5, 110);
			g.drawString("Ores(p/h): " + currTask.getTracker().getAmount() + "(" + timer
					.getHourlyRate(currTask.getTracker().getAmount()) + ")", 10, 125);
			g.drawString("Current task: " + currTask.getOreName() + "::" + currTask.getGoal(), 10, 140);
			for (int i = 0; i < sv.tasks.size(); i++) {
				MineTask mt = sv.tasks.get(i);
				if (mt != null) {
					if (mt.getFinished()) {
						g.setColor(Color.blue);
					} else {
						g.setColor(Color.red);
					}
					String task = mt.getOreName() + "::" + mt.getGoal();
					g.drawString(task, 10, 155 + i * 15);
				}
			}
		} else {
			List<GameObject> rocks = getGameObjects().all(go -> {
				if (go == null || !go.exists() || go.getName() == null || !go.getName().equals("Rocks")) {
					return false;
				}
				return go.isOnScreen();
			});
			if (!rocks.isEmpty()) {
				for (GameObject go : rocks) {
					Tile rockTile = go.getTile();
					Rectangle tileRect = getMap().getBounds(rockTile);
					Point startPoint = new Point(tileRect.x, (int) tileRect.getCenterY());
					g.drawString("ID: " + go.getID(), startPoint.x, startPoint.y);
				}
			}
		}
	}

	public boolean walkOnScreen(Tile t) {
		getMouse().move(getClient().getViewportTools().tileToScreen(t));
		String action = getClient().getMenu().getDefaultAction();
		if (action != null && action.equals("Walk here")) {
			return getMouse().click();
		} else {
			getMouse().click(true);
			sleepUntil(() -> getClient().getMenu().isMenuVisible(), 600);
			return getClient().getMenu().clickAction("Walk here");
		}
	}

	private enum State {
		MINE, DROP, BANK, GUI
	}

}
