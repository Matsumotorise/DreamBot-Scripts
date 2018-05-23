package Miner.GUI;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;

public class Slider extends JSlider {

	public Slider() {
		super(JSlider.HORIZONTAL, 0, 100, 50);
		customSetup();
	}

	public Slider(int i) {
		super(i);
	}

	public Slider(int i, int i1) {
		super(i, i1);
	}

	public Slider(int i, int i1, int i2) {
		super(i, i1, i2);
	}

	public Slider(int i, int i1, int i2, int i3) {
		super(i, i1, i2, i3);
	}

	public Slider(BoundedRangeModel boundedRangeModel) {
		super(boundedRangeModel);
	}

	private void customSetup() {
		setMinorTickSpacing(2);
		setMajorTickSpacing(10);
		setPaintTicks(true);
		setPaintLabels(true);
	}

}
