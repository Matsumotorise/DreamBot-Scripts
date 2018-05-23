package Craft;

public enum Product {
	RING(1592, 7), NECKLACE(1597, 21), AMULET(1595, 34);

	private final int mouldID, childID;

	Product(int mouldId, int childId) {
		this.mouldID = mouldId;
		this.childID = childId;
	}

	public int getMouldID() {
		return mouldID;
	}

	public int getChildID() {
		return childID;
	}
}
