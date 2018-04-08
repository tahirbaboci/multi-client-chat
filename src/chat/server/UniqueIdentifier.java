package chat.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UniqueIdentifier {

	public static List<Integer> ids = new ArrayList<Integer>();
	public static final int RANGE = 100;
	
	public static int index = 0;
	
	// bu class Static: Metodu Static yaptık cunku bu sınıfta varolmasını istiyoruz, yani cagirmak zorunda kalmak istemiyoruz
	static {
		for(int i = 0; i < RANGE; i++)
		{
			ids.add(i);
		}
		Collections.shuffle(ids);
	}
	
	
	public UniqueIdentifier() {
		
	}
	
	public static int getIdentifier() {
		if(index > ids.size() - 1) index = 0;
		
		return ids.get(index++);
	}
	
}
