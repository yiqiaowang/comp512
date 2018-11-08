// -------------------------------
// Kevin T. Manley
// CSE 593
// -------------------------------

package Server.Common;

import java.util.HashMap;

// A specialization of HashMap with some extra diagnostics
public class RMHashMap extends HashMap<String, RMItem>
{
	public RMHashMap() {
		super();
	}

	public String toString()
	{
		StringBuilder s = new StringBuilder("--- BEGIN RMHashMap ---\n");
		for (String key : keySet())
		{
			String value = get(key).toString();
			s.append("[KEY='").append(key).append("']").append(value).append("\n");
		}
		s.append("--- END RMHashMap ---");
		return s.toString();
	}

	public void dump()
	{
		System.out.println(toString());
	}

	public RMHashMap clone()
	{
		RMHashMap obj = new RMHashMap();
		for (String key : keySet())
		{
			obj.put(key, (RMItem)get(key).clone());
		}
		return obj;
	}
}

