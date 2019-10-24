
public class PlayerRandom implements Player
{
	public Location getNextMove(Board board, int player)
	{
		while (true)
		{
			int row = (int) (Math.random() * board.getRows());
			int column = (int) (Math.random() * board.getColumns());
			int existingPlayer = board.getPlayer(new Location(row, column));
			if (existingPlayer == Board.PLAYER_NONE)
			{
				return new Location(row, column);
			}
		}
	}
}
