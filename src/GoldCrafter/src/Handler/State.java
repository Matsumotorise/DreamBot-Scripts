package Handler;


import GUI.Window;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class State {

	private JFrame frame;
	private Window window;
	private int state = 0, bankLocation, smeltLocation;

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
		setState(1);
	}

	public int getSmeltLocation() {
		return smeltLocation;
	}

	public void setSmeltLocation(int smeltLocation) {
		this.smeltLocation = smeltLocation;
	}

	public int getState() {
		return state;
	}

	public void setState(int s) {
		state = s;
	}


}