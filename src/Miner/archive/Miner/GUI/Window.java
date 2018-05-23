package Miner.GUI;

import Miner.Handler.State;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class Window extends JFrame {

	public static final int W = 400, H = 250;
	public static final String TITLE = "Yet Another RuneScape Mining bot";
	private JPanel RootPanel;
	private JTabbedPane Bank;
	private JPanel StartTab;

	private JTextField radiusField;
	private JComboBox bankLocation;
	private JButton startButton;
	private JLabel fightRadiusLabel;
	private JCheckBox dropAll;
	private State s;

	public Window() {

	}

	public Window(State s) {
		this.s = s;
		startButton.addActionListener(actionEvent -> {
			s.setRadius(radiusField.getText());
			s.setDrop(dropAll.isSelected());
			s.setBankArea(bankLocation.getSelectedIndex());
			s.setState(1);
			s.killFrame();
		});
	}

	public JPanel getRootPanel() {
		return RootPanel;
	}

}
