package GUI;

import Handler.State;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Window extends JFrame {

	public static final int W = 400, H = 290;
	public static final String TITLE = "Yet Another RuneScape Walker bot";
	private JPanel RootPanel;

	private JButton startButton;
	private JComboBox walkLocation;
	private JPanel StartTab;
	private JTabbedPane tabs;
	private State s;

	public Window() {
	}

	public Window(State s) {
		this.s = s;
		startButton.addActionListener(actionEvent -> {
			s.setDestination(walkLocation.getSelectedIndex());
			s.killFrame();
			s.setState(1);
		});
	}

	public JPanel getRootPanel() {
		return RootPanel;
	}

}
