package Handler;


import GUI.Window;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class State {

	private JFrame frame;
	private Window window;
	private boolean buryBones, takeOtherPlayersLoot;
	private String target, food;
	private int eatPercentage, bankLocation, foodAmt, fightRadius, state = 0;
	private List<String> loot;

	public State() {
		SwingUtilities.invokeLater(() -> {
			window = new Window(this);
			setFrame(new JFrame(Window.TITLE));
			getFrame().setSize(Window.W, Window.H);
			getFrame().setLocationRelativeTo(null);
			getFrame().setPreferredSize(new Dimension(Window.W, Window.H));
			getFrame().setContentPane(window.getRootPanel());
			getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			getFrame().pack();
			getFrame().setVisible(true);
		});
	}

	public void killFrame() {
		try {
			frame.setVisible(false);
			frame.dispose();
			window.dispose();
		} catch (Exception woeIsMe) {
			woeIsMe.printStackTrace();
		}
	}

	public boolean isTakeOtherPlayersLoot() {
		return takeOtherPlayersLoot;
	}

	public void setTakeOtherPlayersLoot(boolean takeOtherPlayersLoot) {
		this.takeOtherPlayersLoot = takeOtherPlayersLoot;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public int getFoodAmt() {
		return foodAmt;
	}

	public void setFoodAmt(String foodAmt) {
		try {
			this.foodAmt = Integer.parseInt(foodAmt);
		} catch (Exception e) {
			e.printStackTrace();
			this.foodAmt = 5;
		}
	}

	public int getState() {
		return state;
	}

	public void setState(int s) {
		state = s;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public List<String> getLoot() {
		return loot;
	}

	public void setLoot(List<String> loot) {
		this.loot = loot;
	}

	public void setLoot(String loot) {
		this.loot = new ArrayList<>(Arrays.asList(loot.split(", ")));
	}

	public String getFood() {
		return food;
	}

	public void setFood(String food) {
		this.food = food;
	}

	public boolean isBuryBones() {
		return buryBones;
	}

	public void setBuryBones(boolean buryBones) {
		this.buryBones = buryBones;
	}

	public int getEatPercentage() {
		return eatPercentage;
	}

	public void setEatPercentage(int eatPercentage) {
		this.eatPercentage = eatPercentage;

	}

	public int getBankLocation() {
		return bankLocation;
	}

	public void setBankLocation(int bankLocation) {
		this.bankLocation = bankLocation;
	}

	public int getFightRadius() {
		return fightRadius;
	}

	public void setFightRadius(String fightRadius) {
		try {
			this.fightRadius = Integer.parseInt(fightRadius);
		} catch (Exception e) {
			this.fightRadius = 10;
			System.out.println(e);
		}
	}
}
