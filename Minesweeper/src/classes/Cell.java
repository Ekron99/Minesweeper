package classes;

import javax.swing.JButton;


public class Cell {
	private JButton button;
	private int row;
	private int column;
	private int action = -1;
	private boolean empty = false;
	private boolean flagged;
	private boolean hasMine;
	private boolean searched = false;
	
	public Cell(JButton pButton, int pRow, int pColumn, boolean pHasMine) {
		setButton(pButton);
		setRow(pRow);
		setColumn(pColumn);
		setMine(pHasMine);
	}
	
	public Cell(JButton pButton, int pRow, int pColumn) {
		setButton(pButton);
		setRow(pRow);
		setColumn(pColumn);		
	}
	
	public Cell(int pRow, int pColumn, boolean pHasMine) {
		setRow(pRow);
		setColumn(pColumn);	
		setMine(pHasMine);
	}
	
	public Cell(int pRow, int pColumn) {
		setRow(pRow);
		setColumn(pColumn);
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	public boolean isFlagged() {
		return flagged;
	}

	public void setFlag(boolean flagged) {
		this.flagged = flagged;
	}

	public JButton getButton() {
		return button;
	}

	public void setButton(JButton button) {
		this.button = button;
	}

	public boolean isMine() {
		return hasMine;
	}

	public void setMine(boolean hasMine) {
		this.hasMine = hasMine;
	}
	
	public String toString() {
		if (isMine()) {
			return "*";
		} else {
			return "-";
		}
	}

	public boolean isSearched() {
		return searched;
	}

	public void setSearched(boolean searched) {
		this.searched = searched;
	}
	
	public int getAction() {
		return action;
	}
	
	public void setAction(int pAction) {
		action = pAction;
	}

}
