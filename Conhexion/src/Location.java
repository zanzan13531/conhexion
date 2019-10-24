public class Location 
{
	private int row;
	private int column;
	
	// Constructs a new Location object with the specified row and column
	public Location(int rowP, int columnP)
	{
		row = rowP;
		column = columnP;
	}

	// Returns the row integer that was passed to the constructor
	public int getRow()
	{
		return (row);
	}

	// Returns the column integer that was passed to the constructor
	public int getColumn()
	{
		return (column);
	}
}