import java.util.Arrays;

public class Board {
	
	public static final int PLAYER_NONE = 0;
	public static final int PLAYER_1 = 1;
	public static final int PLAYER_2 = 2;
	private int[][][] boardy;

	// Constructs a new board with the specified number of rows and columns
	public Board(int rows, int columns) {
		
		boardy = new int[rows][columns][1];
		
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < rows; y++) {
				boardy[x][y][0] = 0;
			}
		}
		
		
	}

	// Constructs a new Board that clones the state of the specified Board
	public Board(Board original) {
		throw new UnsupportedOperationException();
	}

	// Returns the total number of rows in this Board
	public int getRows() {
		return (boardy.length);
	}

	// Returns the total number of columns in this Board
	public int getColumns() {
		return (boardy[0].length);
	}

	// Returns one of the three "player" ints defined on this class
	// representing which player, if any, occupies the specified
	// location on the board
	public int getPlayer(Location location) {
		return (boardy[location.getRow()][location.getColumn()][0]);
	}

	// Places a game piece from the specified player (represented by
	// one of the three "player" ints defined on this class) into the
	// specified location on the board
	public void setPlayer(Location location, int player) {
		boardy[location.getRow()][location.getColumn()][0] = player;
	}

	// Although the GameManager does not need to call this method, the
	// tests will call it to help verify that you're using the
	// union-find data structure correctly
	public boolean isConnected(Location location1, Location location2) {
		throw new UnsupportedOperationException();
	}

	// Returns whether the specified location on the board contains
	// a game piece that is connected to one of the corresponding
	// player's sides.
	public int getSideConnection(Location location) {
		throw new UnsupportedOperationException();
	}

	// Returns one of the three "player" ints indicating who is the winner
	// of the current Board.  PLAYER_NONE indicates no one has won yet.
	public int getCurrentWinner() {
		throw new UnsupportedOperationException();
	}
}
