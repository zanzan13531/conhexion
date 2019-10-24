
public class PlayerInteractive implements Player
{
	private GameManager gameManager;

	public PlayerInteractive(GameManager gameManagerP)
	{
		gameManager = gameManagerP;
	}

	@Override
	public Location getNextMove(Board board, int player)
	{
		while (true) 
		{
			if (StdDraw.mousePressed())
			{
				// screen coordinates
				double x = StdDraw.mouseX();
				double y = StdDraw.mouseY();

				Location location = gameManager.locationFromMouseCoordinates(x, y);

				if (location != null && board.getPlayer(location) == Board.PLAYER_NONE)
				{
					return location;
				}
			}

			StdDraw.show(20);
		}
	}
}
