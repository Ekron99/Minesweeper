package classes;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class Minesweeper extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static int DEFAULT_MINE_CHANCE = 15;
	Icon one = new ImageIcon("Icons\\Number one.png");
	Icon two = new ImageIcon("Icons\\Number two.png");
	Icon three = new ImageIcon("Icons\\Number three.png");
	Icon four = new ImageIcon("Icons\\Number four.png");
	Icon five = new ImageIcon("Icons\\Number five.png");
	Icon six = new ImageIcon("Icons\\Number six.png");
	Icon seven = new ImageIcon("Icons\\Number seven.png");
	Icon eight = new ImageIcon("Icons\\Number eight.png");
	Icon flag = new ImageIcon("Icons\\Flag.png");
	Icon empty = new ImageIcon("Icons\\Empty.png");
	Icon mine = new ImageIcon("Icons\\Mine.png");
	private Cell[][] grid = new Cell[20][20];
	int gridRows = 19;
	int gridColumns = 19;
	Random rand = new Random();
	private ArrayList<Cell> mineList = new ArrayList<Cell>();
	private int numOfMines = 0;
	private int tilesLeft = 20 * 20;
	private int mineChance = DEFAULT_MINE_CHANCE;
	public Queue<Cell> cellsToSearch = new LinkedList<Cell>();
	public ArrayList<Cell> searchedCells = new ArrayList<Cell>();
	public JButton clickedButton;
	private boolean gameRunning = true;
	private boolean isWinner = false;
	int cellsSearched = 0;
	int sleepTime = 100;
	private ArrayList<JButton> buttons = new ArrayList<JButton>();

	public void run() {
		this.setVisible(true);
	}

	public Minesweeper() {
		super("Minesweeper - Gabe Vaught");
		setSize(600, 600);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new GridLayout(20, 20));

		fillGrid();
		System.out.println("Number of mines: " + numOfMines);
		System.out.println("Number of tiles: " + tilesLeft);
//		tilesLeft = tilesLeft - numOfMines;
		System.out.println("Tiles left: " + tilesLeft);

	}
	
	private void fillGrid() {
		int counter = 0;
		for (int n = 0; n < grid.length; n++) {
			for (int i = 0; i < grid.length; i++) {
				JButton button = new JButton();
				button.setActionCommand(String.valueOf(counter++));
				button.addActionListener(this);
				button.setBackground(Color.LIGHT_GRAY);
				button.setBorder(BorderFactory.createRaisedBevelBorder());
				button.setIcon(empty);
				button.addMouseListener(new MouseAdapter() {
					SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

						@Override
						protected Void doInBackground() throws Exception {
							cellsSearched = 0;
							searchForMine(getCell(clickedButton));
							return null;
						}

					};

					public void mouseReleased(MouseEvent e) {
						if (gameRunning) {
							if (SwingUtilities.isRightMouseButton(e)) {
								// set or de-set Flag
								setFlag(getCell((JButton) e.getSource()));
							} else {
								// search for mines
								clickedButton = (JButton) e.getSource();
								worker.execute();
							}
						}
					}
				});
				add(button);
				if (isMine()) {
					Cell cell = new Cell(button, n, i, true);
					grid[n][i] = cell;
					mineList.add(cell);
					numOfMines++;
				} else {
					grid[n][i] = new Cell(button, n, i, false);
				}
			}
		}
		
//		showGrid();
	}

	private void showGrid() {
		for (int n = 0; n <= gridRows; n++) {
			for (int i = 0; i <= gridColumns; i++) {
				if (i == gridColumns) {
					System.out.println(grid[n][i]);
				} else {
					System.out.print(grid[n][i] + " ");
				}
			}
		}

	}

	private boolean isMine() {
		int chance = rand.nextInt(100) + 1;
		if (chance <= mineChance) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getNumberOfMines() {
		return numOfMines;
	}

	private Cell getCell(JButton pButton) {
		for (int n = 0; n < grid.length; n++) {
			for (int i = 0; i < grid.length; i++) {
				if (grid[n][i].getButton().getActionCommand()
						.equals(pButton.getActionCommand().toString())) {
					return grid[n][i];
				}
			}
		}
		return null;
	}
	
	public void setSleepTime(int pTime) {
		sleepTime = pTime;
	}

	private void disableButton(JButton pButton) {
		pButton.setEnabled(false);
		pButton.setBorder(BorderFactory.createLoweredBevelBorder());
		pButton.setBackground(Color.white);
	}

	private void setFlag(Cell pCell) {
		if (!pCell.getButton().isEnabled()
				|| (!pCell.getButton().getIcon().equals(empty) && !pCell
						.getButton().getIcon().equals(flag))) {
			return;
		}
		if (pCell.isFlagged()) {
			pCell.getButton().setIcon(empty);
			pCell.setFlag(false);
		} else {
			pCell.getButton().setIcon(flag);
			pCell.setFlag(true);
			checkForWin();
		}

	}

	private void checkForWin() {
		for (Cell c : mineList) {
			if ((c.isMine() && !c.isFlagged()) || !c.getButton().isEnabled()) {
				return;
			}
		}
			gameWon();
	}

	private void gameWon() {
		System.out.println("Number of tiles left once game is done: " + (tilesLeft - numOfMines));
		JOptionPane.showMessageDialog(this, "You've Won! Thanks for playing.");
		System.exit(0);
	}

	private void searchForMine(Cell cell) {
		gameRunning = false;
		Cell currentCell;
//		JButton button;
		if (cell.isMine()) {
			blowUp();
			return;
		}
		cellsToSearch.clear();
		searchedCells.clear();
		cellsToSearch.add(cell);
		searchedCells.add(cell);
		
		do {
			currentCell = cellsToSearch.poll();
//			button = currentCell.getButton();
			currentCell.setAction(getSurroundingMines(currentCell));
			cellsSearched++;
			tilesLeft--;
		} while (!cellsToSearch.isEmpty());
		activateCells();
		System.out.println("Tiles left: " + tilesLeft);
		checkForWin();
		gameRunning = true;
	}

	private void clearSearched() {
		for (int n = 0; n <= gridRows; n++) {
			for (int i = 0; i <= gridColumns; i++) {
				grid[n][i].setSearched(false);
			}
		}
	}

	private void activateCells() {
		Cell cell;
		do {
			cell = searchedCells.get(0);
			searchedCells.remove(0);
			cell.getButton().setBorder(BorderFactory.createLoweredBevelBorder());
			switch (cell.getAction()) {
			case 0:
				cell.getButton().setIcon(empty);
				disableButton(cell.getButton());
				break;
			case 1:
				cell.getButton().setIcon(one);
				break;
			case 2:
				cell.getButton().setIcon(two);
				break;
			case 3:
				cell.getButton().setIcon(three);
				break;
			case 4:
				cell.getButton().setIcon(four);
				break;
			case 5:
				cell.getButton().setIcon(five);
				break;
			case 6:
				cell.getButton().setIcon(six);
				break;
			case 7:
				cell.getButton().setIcon(seven);
				break;
			case 8:
				cell.getButton().setIcon(eight);
				break;
			default:
				break;
			}
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (!searchedCells.isEmpty());
	}

	private int getSurroundingMines(Cell cell) {
		if (cell == null) {
			return -1;
		}
		int mineAmount = 0;
		int row = cell.getRow();
		int column = cell.getColumn();
		ArrayList<Cell> possibleCells = new ArrayList<Cell>();

		if (row == gridRows) {
			if (column == gridColumns) {
				// up
				if (grid[row - 1][column].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row - 1][column]);
				}
				// up-left
				if (grid[row - 1][column - 1].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row - 1][column - 1]);
				}
				// left
				if (grid[row][column - 1].isMine()) {
					mineAmount++;

				} else {
					possibleCells.add(grid[row][column - 1]);
				}
			} else if (column == 0) {
				// up
				if (grid[row - 1][column].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row - 1][column]);
				}
				// up-right
				if (grid[row - 1][column + 1].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row - 1][column + 1]);
				}
				// right
				if (grid[row][column + 1].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row][column + 1]);
				}
			} else {
				// up
				if (grid[row - 1][column].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row - 1][column]);
				}
				// up-left
				if (grid[row - 1][column - 1].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row - 1][column - 1]);
				}
				// up-right
				if (grid[row - 1][column + 1].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row - 1][column + 1]);
				}
				// left
				if (grid[row][column - 1].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row][column - 1]);
				}
				// right
				if (grid[row][column + 1].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row][column + 1]);
				}
			}

		} else if (row == 0) {
			if (column == gridRows) {
				// down
				if (grid[row + 1][column].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row + 1][column]);
				}
				// down-left
				if (grid[row + 1][column - 1].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row + 1][column - 1]);
				}
				// left
				if (grid[row][column - 1].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row][column - 1]);
				}
			} else if (column == 0) {
				// down
				if (grid[row + 1][column].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row + 1][column]);
				}
				// down-right
				if (grid[row + 1][column + 1].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row + 1][column + 1]);
				}
				// right
				if (grid[row][column + 1].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row][column + 1]);
				}
			} else {
				// down
				if (grid[row + 1][column].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row + 1][column]);
				}
				// down-left
				if (grid[row + 1][column - 1].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row + 1][column - 1]);
				}
				// down-right
				if (grid[row + 1][column + 1].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row + 1][column + 1]);
				}
				// left
				if (grid[row][column - 1].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row][column - 1]);
				}
				// right
				if (grid[row][column + 1].isMine()) {
					mineAmount++;
				} else {
					possibleCells.add(grid[row][column + 1]);
				}
			}

		} else if (column == gridColumns) {
			// up
			if (grid[row - 1][column].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row - 1][column]);
			}
			// up-left
			if (grid[row - 1][column - 1].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row - 1][column - 1]);
			}
			// left
			if (grid[row][column - 1].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row][column - 1]);
			}
			// down
			if (grid[row + 1][column].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row + 1][column]);
			}
			// down-left
			if (grid[row + 1][column - 1].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row + 1][column - 1]);
			}
		} else if (column == 0) {
			// up
			if (grid[row - 1][column].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row - 1][column]);
			}
			// up-right
			if (grid[row - 1][column + 1].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row - 1][column + 1]);
			}
			// right
			if (grid[row][column + 1].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row][column + 1]);
			}
			// down-right
			if (grid[row + 1][column + 1].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row + 1][column + 1]);
			}
			// down
			if (grid[row + 1][column].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row + 1][column]);
			}
		} else {
			// up
			if (grid[row - 1][column].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row - 1][column]);
			}
			// up-left
			if (grid[row - 1][column - 1].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row - 1][column - 1]);
			}
			// up-right
			if (grid[row - 1][column + 1].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row - 1][column + 1]);
			}
			// down
			if (grid[row + 1][column].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row + 1][column]);
			}
			// down-left
			if (grid[row + 1][column - 1].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row + 1][column - 1]);
			}
			// down-right
			if (grid[row + 1][column + 1].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row + 1][column + 1]);
			}
			// left
			if (grid[row][column - 1].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row][column - 1]);
			}
			// right
			if (grid[row][column + 1].isMine()) {
				mineAmount++;
			} else {
				possibleCells.add(grid[row][column + 1]);
			}
		}
		if (mineAmount == 0) {
			for (Cell c : possibleCells) {
				if (!searchedCells.contains(c)) {
					cellsToSearch.add(c);
					searchedCells.add(c);
					c.setSearched(true);
				}
			}
		}
		return mineAmount;
	}

	private void blowUp() {
		gameRunning = false;
		for (int n = 0; n <= gridRows; n++) {
			for (int i = 0; i <= gridColumns; i++) {
				if (!grid[n][i].isMine())
					grid[n][i].getButton().setEnabled(false);
			}
		}
		for (Cell c : mineList) {
			c.getButton().setIcon(mine);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		JOptionPane.showMessageDialog(this, "You've lost! Try again!");
		System.exit(0);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
//		System.out.println("Clicked Cell Location: " + arg0.getActionCommand());

	}
}
