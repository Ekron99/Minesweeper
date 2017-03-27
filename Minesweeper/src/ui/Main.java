package ui;

import classes.Minesweeper;


public class Main {
	
	public static void main(String[] args) {
		Minesweeper sweeper = new Minesweeper();
		sweeper.setSleepTime(20);
		sweeper.run();
	}
	
}