package Miner.Handler;


import Miner.GUI.Window;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class State {

	private JFrame frame;
	private Window window;
	private int radius, bankArea, state = 0;
	private boolean drop;

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

	public int getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		try {
			this.radius = Integer.parseInt(radius);
		} catch (Exception e) {
			this.radius = 10;
			System.out.println(e);
		}
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public boolean isDrop() {
		return drop;
	}

	public void setDrop(boolean drop) {
		this.drop = drop;
	}

	public int getBankArea() {
		return bankArea;
	}

	public void setBankArea(int bankArea) {
		this.bankArea = bankArea;
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

	public void setState(int state) {
		this.state = state;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
}
