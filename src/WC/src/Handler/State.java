package Handler;

import GUI.Window;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class State {

	private JFrame frame;
	private Window window;
	private int bankLocation, state = 0, radius;
	private String tree;

	public State() {
		SwingUtilities.invokeLater(() -> {
			window = new Window(this);
			frame = (new JFrame(Window.TITLE));
			frame.setSize(Window.W, Window.H);
			frame.setLocationRelativeTo(null);
			frame.setPreferredSize(new Dimension(Window.W, Window.H));
			frame.setContentPane(window.getRootPanel());
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
		});
	}

	public String getTree() {
		return tree;
	}

	public void setTree(int l) {
		switch (l) {
			case 0:
				tree = "Tree";
				break;
			case 1:
				tree = "Oak Tree";
				break;
			case 2:
				tree = "Willow Tree";
				break;
			case 3:
				tree = "Yew Tree";
				break;
		}
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(String r) {
		try {
			radius = Integer.parseInt(r);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Some shit went wrong");
		}
	}

	public int getBankLocation() {
		return bankLocation;
	}

	public void setBankLocation(int bankLocation) {
		this.bankLocation = bankLocation;
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

	public int getState() {
		return state;
	}

	public void setState(int s) {
		state = s;
	}


}