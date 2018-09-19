// -------------------------------
// Kevin T. Manley
// CSE 593
// -------------------------------

package Server.Common;

public class Flight extends ReservableItem
{
	public Flight(int flightNum, int flightSeats, int flightPrice)
	{
		super(Integer.toString(flightNum), flightSeats, flightPrice);
	}

	public String getKey()
	{
		return Flight.getKey(Integer.parseInt(getLocation()));
	}

	public static String getKey(int flightNum)
	{
		String s = "flight-" + flightNum;
		return s.toLowerCase();
	}
}

