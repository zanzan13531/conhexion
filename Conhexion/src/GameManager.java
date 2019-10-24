import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.util.Scanner;

public class GameManager
{
	// Width of the canvas, measured in number of pixels.
	private final int CANVAS_WIDTH_PIXELS = 1000;

	// Distance from center of game piece to corner
	private final double RADIUS = 1;

	// Distance from center to midpoint of an edge
	private final double TO_EDGE = RADIUS * Math.cos(Math.PI * 1.0 / 6.0);

	// Length of each edge of each hexagon location
	private final double EDGE_LENGTH = RADIUS * (Math.sin(Math.PI * 5.0 / 6.0) - Math.sin(Math.PI * 7.0 / 6.0));

	// Height, in pixels, of the text that indicates the status (whose turn it is, or
	// who won the game).
	private static final int STATUS_TEXT_HEIGHT_PIXELS = 40;

	// Colors of the dots inside the spaces which indicate whether the hexagon is connected to a side
	// (Array indexed by player number.)
	private static final Color[] CONNECTION_GLYPH_COLORS = {StdDraw.BLACK, new Color(255, 100, 50), new Color(50, 250, 255)};

	// Colors to paint the hexagon pieces
	// (Array indexed by player number.)
	private static final Color[] FILL_COLORS = {StdDraw.LIGHT_GRAY, new Color(200, 0, 0), new Color(0, 0, 200)};

	// If you only want to see the status text (whose turn it is), and the board is too
	// big to draw efficiently, set this to false for faster run times
	private final boolean DRAW_BOARD = true;

	private final double[] spaceXOffsets;
	private final double[] spaceYOffsets;
	private final double width;

	private boolean initialPiecesPlaced;
	private double height;
	private final double bufferX;
	private final double bufferY;
	private final double borderThickness; 

	private final int rows;
	private final int columns;

	private final double statusTextX;
	private final double statusTextY;

	private Board board;

	// Either PLAYER_1 or PLAYER_2
	private int curPlayer;

	public GameManager(Board board)
	{
		initialPiecesPlaced = false;

		this.rows = board.getRows();
		this.columns = board.getColumns();
		this.board = board;
		this.curPlayer = Board.PLAYER_1;

		int biggerDim = Math.max(rows, columns);

		width = getRowLeftBuffer(rows - 1) + (biggerDim + 1) * 2 * TO_EDGE - 1;
		height = ((biggerDim + 2) / 2) * 2 * RADIUS + ((biggerDim + 1) / 2) * EDGE_LENGTH;

		bufferX = (width * 0.5) / (TO_EDGE * 34 - 1);
		bufferY = (height * 0.25) / ((TO_EDGE * 34 - 1) / 1.5);

		int heightPixels = (int) (CANVAS_WIDTH_PIXELS * ( height / width )) + STATUS_TEXT_HEIGHT_PIXELS;
		height = heightPixels * (width / CANVAS_WIDTH_PIXELS);
		StdDraw.setCanvasSize(CANVAS_WIDTH_PIXELS, heightPixels);
		StdDraw.setXscale(0.0, width);
		StdDraw.setYscale(0.0, height);
		StdDraw.clear();
		for (Frame frame : Frame.getFrames())
		{
			if (frame.isVisible())
			{
				frame.setTitle("Conhexion");
			}
		}

		spaceXOffsets = new double[6];
		spaceYOffsets = new double[6];

		for (int i=0; i < 6; i++)
		{
			spaceXOffsets[i] = Math.cos((2 * i + 1) / 6.0 * Math.PI);
			spaceYOffsets[i] = Math.sin((2 * i + 1) / 6.0 * Math.PI);
		}

		double lengthBasedThickness = EDGE_LENGTH * 0.75;
		double constantThickness = (width * EDGE_LENGTH * 1.5) / (TO_EDGE * 34 - 1);

		borderThickness = Math.max(lengthBasedThickness, constantThickness);

		Point2D spaceNearStatusText = getSpaceCenter(rows - 1, 0);
		statusTextX = spaceNearStatusText.x();
		statusTextY = spaceNearStatusText.y() - RADIUS - borderThickness - 7 * (width / CANVAS_WIDTH_PIXELS);

		StdDraw.show(0);

		drawBoard();
	}

	private double getRowLeftBuffer(int row)
	{
		return row * TO_EDGE;
	}

	private void drawBoard()
	{
		StdDraw.setPenRadius(0.008);
		StdDraw.picture(width / 2, height / 2, "woodgrain.jpg", width * 1.25, height * 1.25);

		if (DRAW_BOARD)
		{
			drawBorders();

			for (int row=0; row < rows; row++)
			{
				for (int col=0; col < columns; col++)
				{
					drawSpace(row, col);
				}
			}
		}

		StdDraw.setPenColor(StdDraw.YELLOW);
		StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 32));
		String statusText = null;
		if (!initialPiecesPlaced)
		{
			statusText = "Placing initial pieces...";
		}
		else if (board.getCurrentWinner() == Board.PLAYER_1)
		{
			statusText = "Winner: Player 1";
		}
		else if (board.getCurrentWinner() == Board.PLAYER_2)
		{
			statusText = "Winner: Player 2";
		}
		else
		{
			statusText = "Ready player " + curPlayer;
		}

		StdDraw.textLeft(statusTextX, statusTextY, statusText);
		StdDraw.show(0);
	}

	private void drawBorders()
	{
		Point2D topLeftCenter = getSpaceCenter(0, 0);
		Point2D topRightCenter = getSpaceCenter(0, columns - 1);
		Point2D bottomLeftCenter = getSpaceCenter(rows - 1, 0);
		Point2D bottomRightCenter = getSpaceCenter(rows - 1, columns - 1);

		double labelOffset = RADIUS + borderThickness / 5;

		{
			// LEFT: Player 2 (BLUE)

			double[] xPolygon = new double[rows * 2 + 2];
			double[] yPolygon = new double [rows * 2 + 2];

			for (int row = 0; row < rows; row++)
			{
				Point2D space = getSpaceCenter(row, 0);
				xPolygon[row*2] = space.x() + spaceXOffsets[2];
				yPolygon[row*2] = space.y() + spaceYOffsets[2];
				xPolygon[row*2 + 1] = space.x() + spaceXOffsets[3];
				yPolygon[row*2 + 1] = space.y() + spaceYOffsets[3];
			}

			xPolygon[rows*2] = bottomLeftCenter.x() + spaceXOffsets[3] - borderThickness;
			yPolygon[rows*2] = bottomLeftCenter.y() + spaceYOffsets[3];
			xPolygon[rows*2 + 1] = topLeftCenter.x() + spaceXOffsets[2] - borderThickness;
			yPolygon[rows*2 + 1] = topLeftCenter.y() + spaceYOffsets[2];

			StdDraw.setPenColor(FILL_COLORS[Board.PLAYER_2]);
			StdDraw.filledPolygon(xPolygon, yPolygon);

			// Row labels
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.setFont();
			for (int row = 0; row < rows; row++)
			{
				Point2D space = getSpaceCenter(row, 0);
				StdDraw.text(space.x() - labelOffset, space.y(), "" + row);
			}
		}

		{
			// RIGHT: Player 2 (BLUE)

			double[] xPolygon = new double[rows * 2 + 2];
			double[] yPolygon = new double [rows * 2 + 2];

			for (int row = 0; row < rows; row++) 
			{
				Point2D space = getSpaceCenter(row, columns - 1);
				xPolygon[row*2] = space.x() + spaceXOffsets[0];
				yPolygon[row*2] = space.y() + spaceYOffsets[0];
				xPolygon[row*2 + 1] = space.x() + spaceXOffsets[5];
				yPolygon[row*2 + 1] = space.y() + spaceYOffsets[5];
			}

			xPolygon[rows*2] = bottomRightCenter.x() + spaceXOffsets[5] + borderThickness;
			yPolygon[rows*2] = bottomRightCenter.y() + spaceYOffsets[5];
			xPolygon[rows*2 + 1] = topRightCenter.x() + spaceXOffsets[0] + borderThickness;
			yPolygon[rows*2 + 1] = topRightCenter.y() + spaceYOffsets[0];

			StdDraw.setPenColor(FILL_COLORS[Board.PLAYER_2]);
			StdDraw.filledPolygon(xPolygon, yPolygon);

			// Row labels
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.setFont();
			for (int row = 0; row < rows; row++)
			{
				Point2D space = getSpaceCenter(row, columns - 1);
				StdDraw.text(space.x() + labelOffset, space.y(), "" + row);
			}
		}
		{
			// TOP: Player 1 (RED)

			double[] xPolygon = new double[columns * 2 + 3];
			double[] yPolygon = new double [columns * 2 + 3];

			for (int col = 0; col < columns; col++)
			{
				Point2D space = getSpaceCenter(0, col);
				xPolygon[col*2] = space.x() + spaceXOffsets[2];
				yPolygon[col*2] = space.y() + spaceYOffsets[2];
				xPolygon[col*2 + 1] = space.x() + spaceXOffsets[1];
				yPolygon[col*2 + 1] = space.y() + spaceYOffsets[1];
			}

			xPolygon[columns*2] = topRightCenter.x() + spaceXOffsets[0];
			yPolygon[columns*2] = topRightCenter.y() + spaceYOffsets[0];
			xPolygon[columns*2 + 1] = topRightCenter.x() + spaceXOffsets[0];
			yPolygon[columns*2 + 1] = topRightCenter.y() + spaceYOffsets[0] + borderThickness;
			xPolygon[columns*2 + 2] = topLeftCenter.x() + spaceXOffsets[2];
			yPolygon[columns*2 + 2] = topLeftCenter.y() + spaceYOffsets[2] + borderThickness;

			StdDraw.setPenColor(FILL_COLORS[Board.PLAYER_1]);
			StdDraw.filledPolygon(xPolygon, yPolygon);

			// Column labels
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.setFont();
			for (int col = 0; col < columns; col++)
			{
				Point2D space = getSpaceCenter(0, col);
				StdDraw.text(space.x(), space.y() + labelOffset, "" + col);
			}
		}
		{
			// BOTTOM: Player 1 (RED)

			double[] xPolygon = new double[columns * 2 + 3];
			double[] yPolygon = new double [columns * 2 + 3];

			for (int col = 0; col < columns; col++)
			{
				Point2D space = getSpaceCenter(rows - 1, col);
				xPolygon[col*2] = space.x() + spaceXOffsets[3];
				yPolygon[col*2] = space.y() + spaceYOffsets[3];
				xPolygon[col*2 + 1] = space.x() + spaceXOffsets[4];
				yPolygon[col*2 + 1] = space.y() + spaceYOffsets[4];
			}

			xPolygon[columns*2] = bottomRightCenter.x() + spaceXOffsets[5];
			yPolygon[columns*2] = bottomRightCenter.y() + spaceYOffsets[5];
			xPolygon[columns*2 + 1] = bottomRightCenter.x() + spaceXOffsets[5];
			yPolygon[columns*2 + 1] = bottomRightCenter.y() + spaceYOffsets[5] - borderThickness;
			xPolygon[columns*2 + 2] = bottomLeftCenter.x() + spaceXOffsets[3];
			yPolygon[columns*2 + 2] = bottomLeftCenter.y() + spaceYOffsets[3] - borderThickness;

			StdDraw.setPenColor(FILL_COLORS[Board.PLAYER_1]);
			StdDraw.filledPolygon(xPolygon, yPolygon);

			// Column labels
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.setFont();
			for (int col = 0; col < columns; col++)
			{
				Point2D space = getSpaceCenter(rows - 1, col);
				StdDraw.text(space.x(), space.y() - labelOffset, "" + col);
			}
		}
	}

	private Point2D getSpaceCenter(int row, int col)
	{
		return new Point2D(
				row * TO_EDGE + col * (2 * TO_EDGE) + RADIUS + bufferX,
				height - row * (RADIUS + 0.5 * EDGE_LENGTH) - RADIUS - bufferY);
	}

	// Can be used from other classes, like PlayerInteractive, to figure
	// out which space has been clicked on
	public Location locationFromMouseCoordinates(double x, double y)
	{
		// Calculate row first
		Point2D topLeftSpace = getSpaceCenter(0, 0);
		double yTopMost = topLeftSpace.y() + TO_EDGE;
		int row = (int) ((yTopMost - y) / (RADIUS + 0.5 * EDGE_LENGTH));

		// Calculate column
		Point2D leftMostSpaceInRow = getSpaceCenter(row, 0);
		double xLeftMostInColumn = leftMostSpaceInRow.x() - TO_EDGE;
		int col = (int) ((x - xLeftMostInColumn) / (2 * TO_EDGE));

		if (row < 0 || row >= rows || col < 0 || col >= columns)
		{
			return null;
		}

		// If click is too far from the center, it could be misinterpreted, since
		// the spaces are not perfect squares.  Throw away boundary clicks
		Point2D clickedSpaceCenter = getSpaceCenter(row, col);
		if (clickedSpaceCenter.distanceTo(new Point2D(x, y)) >= TO_EDGE)
		{
			return null;
		}

		return new Location(row, col);
	}


	private void drawSpace(int row, int column)
	{
		Point2D center = getSpaceCenter(row, column);
		double[] xs = new double[6];
		double[] ys = new double[6];
		for (int i=0; i < 6; i++)
		{
			xs[i] = center.x() + spaceXOffsets[i];
			ys[i] = center.y() + spaceYOffsets[i];
		}

		StdDraw.setPenColor(getFillColor(row, column));
		StdDraw.filledPolygon(xs, ys);

		StdDraw.setPenColor(Color.BLACK);
		StdDraw.polygon(xs, ys);

		// Add a glyph to indicate whether the space is connected
		int playerConnection = board.getSideConnection(new Location(row, column));
		if (playerConnection == Board.PLAYER_NONE)
		{
			return;
		}

		Color connectionGlyphColor = CONNECTION_GLYPH_COLORS[playerConnection];
		StdDraw.setPenColor(connectionGlyphColor);
		StdDraw.filledCircle(center.x(), center.y(), RADIUS / 4);
	}

	private Color getFillColor(int row, int column)
	{
		return FILL_COLORS[board.getPlayer(new Location(row, column))];
	}

	public void placeInitialPieces(In in)
	{
		Scanner console = new Scanner(System.in);

		drawBoard();

		// This variable indicates which line of the test file is being
		// sent to your setPlayer method.  You may find this variable
		// useful when creating breakpoints that are hit when a certain
		// line of the test file is read.
		// (Starting at line 2, since line 1 was the board dimensions.)
		int lineNumber = 2;

		// repeatedly read in moves and draw resulting board
		while (!in.isEmpty()) 
		{
			// Let the user step through the file by tapping enter
			System.out.print("Press enter to apply line #" + lineNumber + "...");
			console.nextLine();
			
			int row = in.readInt();
			int col = in.readInt();
			int player = in.readInt();
			System.out.println("Adding to board: row=" + row + ", column=" + col + ", player=" + player);
			board.setPlayer(new Location(row, col), player);
			drawBoard();
			lineNumber++;
		}

		initialPiecesPlaced = true;
		drawBoard();
	}

	public void play(Player player1, Player player2)
	{
		// Set this to true, and the game will be recorded into a test file with the
		// name "board-recorded.txt" located in the project directory in your workspace
		final boolean record = false;

		Out out = null;
		if (record)
		{
			out = new Out("board-recorded.txt");
			out.print(rows + " " + columns + "\n");
		}

		Player[] players = new Player[] { player1, player2 };

		while (board.getCurrentWinner() == Board.PLAYER_NONE) 
		{
			Player player = players[curPlayer - 1];
			
			// Ask player for its move
			Location nextMove = player.getNextMove(new Board(board), curPlayer);
			
			// Is the move legal?
			int currentOccupant = board.getPlayer(nextMove);
			if (currentOccupant != Board.PLAYER_NONE)
			{
				throw new UnsupportedOperationException("Player # " + curPlayer + " attempted an illegal move in row " +
						nextMove.getRow() + ", column " + nextMove.getColumn() + 
						", which is already occupied by player " + currentOccupant);
			}

			// Apply the move to the Board
			board.setPlayer(nextMove, curPlayer);

			if (record)
			{
				out.print(nextMove.getRow() + " " + nextMove.getColumn() + " " + curPlayer + "\n");
			}

			// Toggle to the next player
			curPlayer = 3 - curPlayer;
			
			drawBoard();
		}

		if (record)
		{
			out.close();
		}
	}

	public static void main(String[] args)
	{
		// HEY YOU!  Modify this string to read in other input files
		In in = new In("testInput/board-11x11-empty.txt");

		// Read in board size and create board
		int rows = in.readInt();
		int columns = in.readInt();
		Board board = new Board(rows, columns);

		GameManager gm = new GameManager(board);
		
		// Read and apply any moves from the input file
		gm.placeInitialPieces(in);
		in.close();

		System.out.println("Finished reading input file");

		// HEY YOU!  Modify the parameters to change who plays the game
		gm.play(
				new PlayerInteractive(gm),	// player 1
				new PlayerInteractive(gm)	// player 2
				);
	}		
}
