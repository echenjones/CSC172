import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;

public class Project1 {
	
	protected static int count = 0;
	protected static int max = 0;
	protected static boolean maybeQuit = false;
	protected static boolean maybeRestart = false;

	/** Prints the board in correct format 
	 * @param b a 2D array with an element for each space on the board */
	public static void printBoard(int[][] b) {
		System.out.println("-" + "\t" + "-" + "\t" + "-" + "\t" + "-" + "\t" + "-" + "\t" + "-");
		for (int[] i : b) {
			System.out.print("|" + "\t");
			for (int j : i) {
				if (j == 0) {
					System.out.print("." + "\t");
				}
				else {
					System.out.print(j + "\t");
				}
			}
			System.out.println("|");
		}
		System.out.println("-" + "\t" + "-" + "\t" + "-" + "\t" + "-" + "\t" + "-" + "\t" + "-");
	}
	
	/** Shifts the numbers on one line to the right where there is a 0
	 * @param line a sub int array from the larger 2D array, one line on the board
	 * @param slide the number of spaces to slide 
	 * @return the number of spaces to slide */
	public static int shift(int[] line, int slide) {
		int endpoint = line.length - 1;
		while (slide < endpoint && line[slide + 1] == 0) {
			line[slide + 1] = line[slide];
			line[slide] = 0;
			slide++;
		}
		return slide;
	}
	
	/** Takes a line from 2D array and adds numbers to another array, flipping them to read from left to right
	 * @param b the 2D array, the board
	 * @param index the index of the row or column in the 2D array
	 * @param row specifies whether the line to flip is a row or a column 
	 * @param reverse whether or not the numbers need to be flipped 
	 * @return the flipped line */
	public static int[] getLine(int[][] b, int index, boolean row, boolean reverse) {
		int[] line = {0, 0, 0, 0};
		int opp = line.length - 1;
		if (row) {
			if (reverse) {
				for (int i = 0; i < b.length; i++) {
					line[i] = b[index][opp];
					opp--;
				}
			}
			else {
				for (int i = 0; i < b.length; i++) {
					line[i] = b[index][i];
				}
			}
		}
		else {
			if (reverse) {
				for (int i = 0; i < b.length; i++) {
					line[i] = b[opp][index];
					opp--;
				}
			}
			else {
				for (int i = 0; i < b.length; i++) {
					line[i] = b[i][index];
				}
			}
		}
		return line;
	}
	
	/** Takes a line and calls shift to push all numbers down, then adds numbers when applicable
	 * @param arr a single line from the board */
	public static void processLine(int[] arr) {
		int endpoint = arr.length - 1;
		int focus = endpoint - 1;
		while (focus >= 0) {
			int pos = shift(arr, focus);
			if (pos < endpoint && arr[pos] == arr[pos + 1] && arr[pos] != 0) {
				arr[pos + 1] += arr[pos];
				arr[pos] = 0;
				endpoint = pos;
			}
			focus--;
		}
	}
	
	/** Takes a line and flips it back into correct order
	 * @param b the 2D array, the board
	 * @param line a single line from the board
	 * @param index the index of the line in the 2D array
	 * @param row specifies whether the line to change back is a row or a column 
	 * @param reverse whether or not the numbers were flipped */
	public static void setLine(int[][] b, int[] line, int index, boolean row, boolean reverse) {
		int opp = line.length - 1;
		if (row) {
			if (reverse) {
				for (int i = 0; i < line.length; i++) {
					b[index][i] = line[opp];
					opp--;
				}
			}
			else {
				for (int i = 0; i < line.length; i++) {
					b[index][i] = line[i];
				}
			}
		}
		else {
			if (reverse) {
				for (int i = 0; i < line.length; i++) {
					b[i][index] = line[opp];
					opp--;
				}
			}
			else {
				for (int i = 0; i < line.length; i++) {
					b[i][index] = line[i];
				}
			}
		}
	}
	
	/** Finds the maximum number in a 2D array
	 * @param b the 2D array, the board
	 * @return the maximum number */
	public static int maxNumber(int[][] b) {
		int max = 0;
		for (int i[] : b) {
			for (int j : i) {
				if (j > max) {
					max = j;
				}
			}
		}
		return max;
	}
	
	/** Adds a random number (2 or 4) to a 2D array, where possible
	 * @param b the 2D array, the board 
	 * @return the new 2D array/board */
	public static int[][] addTile(int[][] b) {
		Random rand = new Random();
		int r1 = rand.nextInt(100);
		int r2 = rand.nextInt(4);
		int r3 = rand.nextInt(4);
		while(b[r2][r3]	!= 0) {
			r2 = rand.nextInt(4);
			r3 = rand.nextInt(4);
		}
		if (r1 < 20) {
			b[r2][r3] = 4;
		}
		else {
			b[r2][r3] = 2;
		}
		return b;
	}
	
	/** Checks if a 2D array is full
	 * @param b the 2D array, the board 
	 * @return whether or not it is full */
	public static boolean boardFull(int[][] b) {
		for (int[] i : b) {
			for (int j : i) {
				if (j == 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	/** Checks if a 2D array is the same as another 2D array
	 * @param b the first 2D array to compare
	 * @param temp the second 2D array to compare
	 * @return whether or not their contents are the same */
	public static boolean boardsEqual(int[][] b, int[][] temp) {
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b.length; j++) {
				if (b[i][j] != temp[i][j]) {
					return false;
				}
			}
		}
		return true;
	}
	
	/** Plays a game of 2048, calling methods when necessary, using a key listener to get user input */
	public static void playGame() {
		int[][] b = new int[4][4];
		addTile(b);
		addTile(b);
		printBoard(b);
		
		KeyListener kl = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				int[][] temp = new int[4][4];
				for (int i = 0; i < b.length; i++) {
					for (int j = 0; j < b.length; j++) {
						temp[i][j] = b[i][j];
					}
				}
				if (key == KeyEvent.VK_W || key == KeyEvent.VK_S || key == KeyEvent.VK_A || key == KeyEvent.VK_D) {
					if (key == KeyEvent.VK_W) {
						System.out.println("W is a valid key. Shift up.");
						for (int i = 0; i < b.length; i++) {
							int[] line = getLine(b, i, false, true);
							processLine(line);
							setLine(b, line, i, false, true);
						}
					}
					else if (key == KeyEvent.VK_S) {
						System.out.println("S is a valid key. Shift down.");
						for (int i = 0; i < b.length; i++) {
							int[] line = getLine(b, i, false, false);
							processLine(line);
							setLine(b, line, i, false, false);
						}
					}
					else if (key == KeyEvent.VK_A) {
						System.out.println("A is a valid key. Shift left.");
						for (int i = 0; i < b.length; i++) {
							int[] line = getLine(b, i, true, true);
							processLine(line);
							setLine(b, line, i, true, true);
						}
					}
					else if (key == KeyEvent.VK_D) {
						System.out.println("D is a valid key. Shift right.");
						for (int i = 0; i < b.length; i++) {
							int[] line = getLine(b, i, true, false);
							processLine(line);
							setLine(b, line, i, true, false);
						}
					}
					
					boolean equal = boardsEqual(b, temp);
					if (equal) {
						boolean full = boardFull(b);
						if (full) {
							System.out.println("Board is full. Game over.");
							System.out.println("Number of valid moves: " + count);
							System.out.println("Highest number on board: " + max);
							System.exit(0);
						}
						System.out.println("Move not possible. Try another direction.");
					}
					else {
						count++;
						System.out.println("Number of valid moves: " + count);
						System.out.println("\n\n\n\n\n");
						addTile(b);
						max = maxNumber(b);
						System.out.println("Highest number on board: " + max);
						printBoard(b);
					}
				}
				else if (key == KeyEvent.VK_Q) {
					System.out.println("Do you want to quit? (y/n)");
					maybeQuit = true;
				}
				else if (key == KeyEvent.VK_R) {
					System.out.println("Do you want to restart? (y/n)");
					maybeRestart = true;
				}
				else if (key == KeyEvent.VK_Y) {
					if (maybeQuit == true) {
						System.exit(0);
					}
					else if (maybeRestart == true) {
						maybeRestart = false;
						System.out.println("Restarting game...");
						System.out.println("\n\n\n\n\n");
						playGame();
					}
					else {
						System.out.println("Invalid key.");
					}
				}
				else if (key == KeyEvent.VK_N) {
					if (maybeQuit == true) {
						maybeQuit = false;
						System.out.println("\n\n\n\n\n");
						printBoard(b);
						return;
					}
					else if (maybeRestart == true) {
						maybeRestart = false;
						System.out.println("\n\n\n\n\n");
						printBoard(b);
						return;
					}
					else {
						System.out.println("Invalid key.");
					}
				}
				else {
					System.out.println("Invalid key.");
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
			}
		};
		
		JFrame frame = new JFrame();
		JButton button = new JButton("Play 2048");
		button.addKeyListener(kl);
		frame.add(button);
		frame.setSize(100, 100);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		playGame();
	}
}
