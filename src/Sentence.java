import java.util.List;
import java.util.Map;

public class Sentence {
	public String[] LEMMA;
	public String[] TOKEN;
	public String[] POS;
	public String[] ARGTYPE;
	public int size;
	public int Citation;
	public Map<String,List<String>> path_to_leaves;
	public Sentence(String[] lemma,String[] token,String[] pos,String[] argtype) {
		this.LEMMA = lemma;
		this.TOKEN = token;
		this.POS = pos;
		this.ARGTYPE = argtype;
		this.size = LEMMA.length;
	}
}
