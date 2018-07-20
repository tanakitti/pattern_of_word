import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class BankMapKey {
	Map<String, List<String>> path_to_leaves;
	Set<String> path;
	Set<String> labels;
	BankMapKey(){
		this.path_to_leaves = new TreeMap<String,List<String>>();
		this.path = new TreeSet<String>();
		this.labels = new TreeSet<String>();
	}
	public void put(String[] key,String leave){
		
		this.path.add(key[0]);
		this.labels.add(key[1]);
		
		if(!path.contains(key[0])||!labels.contains(key[1])) {
			List<String> leaves = new LinkedList<String>();
			leaves.add(leave);
			path_to_leaves.put(key[0]+" "+key[1], leaves);
		}else {
			path_to_leaves.get(key[0]+" "+key[1]).add(leave);
		}	
	}
}
