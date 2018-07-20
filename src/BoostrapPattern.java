import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class BoostrapPattern {
	static Properties props = PropertiesUtils.asProperties(
            "annotators", "tokenize,ssplit,pos,lemma,depparse",
            "depparse.model", DependencyParser.DEFAULT_MODEL
    );
    static AnnotationPipeline pipeline = new StanfordCoreNLP(props);
	static Map<String, String> CUSTOM_IN_CITANCE_PATTERNS;
	public static String text = "we apply the cat (=-=Bray et al., 2003-=-)";
	public static void main(String[] args) {
		
		load_patterns();
//		Sentence processed_sent = process_sent(text);
//		for(String a:processed_sent.POS)System.out.println(a);
//		for(Entry<String,String> a : CUSTOM_IN_CITANCE_PATTERNS.entrySet()) {
//			System.out.println(a.getKey()+" "+a.getValue());
//		}
//		Map<String,Integer> output = get_custom_pattern_features(processed_sent);
//		System.out.println("Output");
//		for(Entry<String,Integer> a : output.entrySet()) {
//			System.out.println(a.getKey()+" "+a.getValue());
//		}
		
		String temp = loadFileToString("/Users/bank21235/Desktop/CitationandID.txt");
		
		String[] raw = temp.split("\n");
		for(int i = 0;i<raw.length;i++) {
			String patternn = ".*=-=.*-=-.*";
			Pattern r = Pattern.compile(patternn);
			Matcher m = r.matcher(raw[i].split("\t")[1].replaceAll("\n", ""));
			if(!m.find( )) continue;
//			if(Integer.parseInt(raw[i].split("\t")[0])<2044968) {
//				System.out.println(raw[i].split("\t")[0]);
//				continue;
//			}
			
			Sentence sentence = process_sent(raw[i].split("\t")[1].replaceAll("\n", ""));
			Map<String,Integer> output = get_custom_pattern_features(sentence);
			System.out.println(raw[i].split("\t")[0]);
			String pattern = "";
			for(Entry<String,Integer> a : output.entrySet()) {
				pattern += a.getValue()+" ";
				
			}
			System.out.println(pattern);
		}
		
		
		
		
		
		
	}
	public static Map<String,Integer> get_custom_pattern_features(Sentence processed_sent) {
		
		Map<String, String> features = CUSTOM_IN_CITANCE_PATTERNS;
		Map<String,Integer> output = new TreeMap<String,Integer>();
		for(Entry<String, String> feature: features.entrySet()) {
			int pat_index = find(feature.getValue(),processed_sent,true);
			if(pat_index < 0) {
				output.put(feature.getKey(),0);
			}else {
				output.put(feature.getKey(),1);
			}			
		}
			
		return output;
	}
	
	public static Sentence process_sent(String text) {
		
		text = text.replaceAll("=-=.*-=-", " tanakitti ");
		text = text.toLowerCase();
		Annotation ann = new Annotation(text);
	    
	    pipeline.annotate(ann);
	    String omg = "";
	    List<String> lemmass = new LinkedList<String>();
	    List<String> tokens = new LinkedList<String>();
	    List<String> poss= new LinkedList<String>();
	    String[] ArgType = null;
	    int citation = 0;
	    for (CoreMap sentence : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
            if(sentence.toString().contains("tanakitti")) {
            	omg = sentence.toString();
            	for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
            		
            		if(token.toShortString("OriginalText").equals("tanakitti"))citation = token.index();
            		// tokens
            		tokens.add(token.toShortString("OriginalText"));
            		
            		//lemma
                    if(token.toShortString("OriginalText").equals("our")) lemmass.add("our");
                    else if(token.toShortString("OriginalText").contains("my")) lemmass.add("my");
                    else if(token.toShortString("OriginalText").contains("their")) lemmass.add("their");
                    else if(token.toShortString("OriginalText").contains("his")) lemmass.add("his");
                    else if(token.toShortString("OriginalText").contains("her")) lemmass.add("her");
                    else lemmass.add(token.get(LemmaAnnotation.class));
                    
                    
            		//Pos
            		poss.add(token.get(PartOfSpeechAnnotation.class));
            		
            	}
            	ArgType = new String[tokens.size()];
            	 SemanticGraph sg = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
                 for (IndexedWord iw : sg.vertexSet()) {
                	 for (SemanticGraphEdge se : sg.outgoingEdgeList(iw)) {
                		 
                         String relationName = se.getRelation().getShortName();
                         
                         if (relationName.equals("nsubj")) {
                        	 ArgType[se.getDependent().index()-1]="subj";
                        	 ArgType[iw.index()-1] = "verb";
                         } else if (relationName.equals("dobj")){
                        	 ArgType[se.getDependent().index()-1]="dobj";
                         }else {
                        	 ArgType[se.getDependent().index()-1] = "none";
                         }
                   
                	 }
                	
                 }
            }
        }
	    
	    String[]  LEMMA = new String[lemmass.size()];
	    String[]  TOKEN = new String[tokens.size()];
	    String[]  POSS = new String[poss.size()];
	    
	    for(int i = 0; i < ArgType.length;i++)if(ArgType[i]==null)ArgType[i]="root";
	    LEMMA = lemmass.toArray(LEMMA);
	    TOKEN = tokens.toArray(TOKEN);
	    POSS = poss.toArray(POSS);
		
		
		
		Sentence output = new Sentence(LEMMA,TOKEN,POSS,ArgType);
		output.Citation = citation-1;
		
		
		return output;
	}
	
	public static void load_patterns() {
		String temp = loadFileToString("./resourse/in-sent.filtered.tsv");
		String[] raw = temp.split("\n");
		CUSTOM_IN_CITANCE_PATTERNS = new TreeMap<String,String>();
		int[] count = new int[7];
		for(int j =0;j<7;j++)count[j] = 0;
		for(int i = 0;i<raw.length;i++) {
			if(!raw[i].contains("@"))continue;
			String cols[] = raw[i].split("\t");
			String pattern = cols[0].replaceAll("-lrb-", "(").replaceAll("-rrb-", ")");
			String clazz = cols[1];
			
			if(clazz.toLowerCase().equals("background")) continue;
			
			//BACKGROUND = 0
			//MOTIVATION = 1
			//USES = 2 
			//EXTENSION = 3
			//COMPARISON OR CONTRAST = 4
			//FUTURE = 5
			int num = -1 ;
//			System.out.println(clazz.toLowerCase());
			if(clazz.toLowerCase().equals("background"))num = 0;
			else if(clazz.toLowerCase().equals("motivation"))num = 1;
			else if(clazz.toLowerCase().equals("uses"))num = 2;
			else if(clazz.toLowerCase().equals("extension"))num = 3;
			else if(clazz.toLowerCase().equals("compareorcontrast"))num = 4;
			else if(clazz.toLowerCase().equals("future"))num = 5;
			else {
				num = 6;
			}
			
			
			count[num]++;
			
			CUSTOM_IN_CITANCE_PATTERNS.put(clazz+"_"+"in_cite"+"_"+leftPad(count[num],3),pattern);
			
			
			
			
			
			
		}
	}
	
	public static String leftPad(int n, int padding) {
	    return String.format("%0" + padding + "d", n);
	}
	
	private static int find(String patern,Sentence processed_sent,boolean must_have_subject) {
		
		int pat_len = patern.split(" ").length;
		String[] words_pat = patern.split(" ");
		
		for(int i = 0; i < processed_sent.TOKEN.length-pat_len+1;i++) {
			int k = 0;
			boolean match = true;
			boolean is_subj = false;
			for( int j = 0; j < pat_len; j++ ) {
				
	            if (i+j+k >= processed_sent.TOKEN.length) {
	            	System.out.println("moved beyond end of sentence");
	                match = false;
	                break;
	            }
	           
	            if (processed_sent.ARGTYPE[i+j+k].equals("subj")) {
	            	is_subj = true;
	            }
	            String pi =  words_pat[j];
	      
	            if (pi.charAt(0) == '@') {
	            	
	            	Map<String,Set<String>> lexicons = get_lexicon_ALL_ACTION_LEXICONS();
	            	Map<String,Set<String>> lexicons2 = get_lexicon_ALL_CONCEPT_LEXICONS();
	            	
	            	String word = words_pat[j].substring(1, words_pat[j].length());
	            	Set<String> lexicon = null;
	            	
	            	String required_pos =  null;
	            	String argtype_require =  null;
	            	
	            	
	            	if(lexicons.containsKey(word)) {
	            		lexicon = lexicons.get(word);
	            		
	            	}else if(lexicons2.containsKey(word)) {
	            		lexicon = lexicons2.get(word);
	            		required_pos = "V";
	            		
	            	}else {
	            		System.out.println("ERROR_find");
	            	}
	            	//System.out.println("A:"+word);
	            	int matched_phrased_length = is_in_lexicon(processed_sent,lexicon,i+j+k,argtype_require,required_pos);
	      
	                if(matched_phrased_length == 0) {
	                	match = false;
	    	            break;
	                }
	                
	                k += (matched_phrased_length - 1);
	                        
	            }else if(pi.equals("CITATION")) {
	            	if(!processed_sent.TOKEN[i+j+k].equals("tanakitti")) {
	            		match = false;
	                    break;
	            	}
	            }else if (pi.equals("CREF")) {
	            	if (!processed_sent.POS[i+j+k].equals("CD")||!processed_sent.TOKEN[i+j+k].equals("CREF")) {
	            		match = false;
	                    break;
	            	}    
	            }else if (pi.charAt(0)=='#') {
	            	if(processed_sent.POS[i+j+k].charAt(0)!=pi.charAt(1) && !(pi.charAt(1)=='J'&& processed_sent.POS[i+j+k].equals("VBN"))) {
	            		match = false;
	                    break;
	            	}
	            }else {
	            	
	            	if(!processed_sent.TOKEN[i+j+k].equals(pi)) {
	            		match = false;
	                    break;
	            	}
	            }
	   
			}
			if( match && (must_have_subject != false) && (is_subj != must_have_subject)){
            	continue;
            }
			if(match) {
			
				return i;
			}
				
		}
		return -1;
	}

	public static int is_in_lexicon(Sentence processed_sent,Set<String> lexicons,int pi,String argtype_require,String required_pos) {
		
		for(String pharse : lexicons) {
			String[] words = pharse.split(" ");
			if(pi+words.length>processed_sent.size) {
				//System.out.println("Size is exceed, Continue");
				continue;
			}else {
				boolean found = true;
				boolean found_arg = false;
				for(int i = 0; i< words.length;i++) {

					if(!(processed_sent.TOKEN[pi+i].equals(words[i])||processed_sent.LEMMA[pi+i].equals(words[i])
							||!(argtype_require == null || (processed_sent.POS[pi+i].charAt(0)+"").equals(required_pos)))) {
						
						found = false;	
						break;
						
					}
		            if(argtype_require != null && processed_sent.ARGTYPE[pi+i].equals(argtype_require)) {
		            	found_arg = true;
		            }
		                
				}
			
				if(found && (argtype_require == null || found_arg)) {
					
					return words.length;
				}
			}
		}
		return 0;
	}

	public static Map<String,Set<String>> get_lexicon_ALL_ACTION_LEXICONS(){
		
		Map<String,Set<String>> features = new TreeMap<String,Set<String>>();
		String feature_text = ALL_ACTION_LEXICONS.replaceAll("[ ]{2,}","");
		feature_text = feature_text.replaceAll("\\\\", "");
		String[] features_array = feature_text.split("\n\n");
		for(String feture:features_array) {
			String[] foo = feture.split(":");
			
			String lexKey = foo[0].trim().replace("\"", "");
			foo[1] = foo[1].trim().replaceAll("\"", "");
			
	    	foo[1] = foo[1].replaceAll("\\[", "");
	    	foo[1] = foo[1].replaceAll("\\],", "");
	    	foo[1] = foo[1].replaceAll("\\]", "");
	    	foo[1] = foo[1].replaceAll("'", "");
	    	foo[1] = foo[1].replaceAll("\n", "");
	    	foo[1] = foo[1].trim();
	    	String [] words = foo[1].split(",");
	    	for(int i = 0;i<words.length;i++) words[i] = words[i].trim();
	    	
	    	Set<String> wordlist = new TreeSet<String>(Arrays.asList(words));
	    	features.put(lexKey, wordlist);
		}
	    return features;
	}
	
	public static Map<String,Set<String>> get_lexicon_ALL_CONCEPT_LEXICONS(){
		
		Map<String,Set<String>> features = new TreeMap<String,Set<String>>();
		String feature_text = ALL_CONCEPT_LEXICONS.replaceAll("[ ]{2,}","");
		feature_text = feature_text.replaceAll("\\\\", "");
		String[] features_array = feature_text.split("\n\n");
		for(String feture:features_array) {
			String[] foo = feture.split(":");
			
			String lexKey = foo[0].trim().replace("\"", "");
			foo[1] = foo[1].trim().replaceAll("\"", "");
			
	    	foo[1] = foo[1].replaceAll("\\[", "");
	    	foo[1] = foo[1].replaceAll("\\],", "");
	    	foo[1] = foo[1].replaceAll("\\]", "");
	    	foo[1] = foo[1].replaceAll("'", "");
	    	foo[1] = foo[1].replaceAll("\n", "");
	    	foo[1] = foo[1].trim();
	    	String [] words = foo[1].split(",");
	    	for(int i = 0;i<words.length;i++) words[i] = words[i].trim();
	    	
	    	Set<String> wordlist = new TreeSet<String>(Arrays.asList(words));
	    	features.put(lexKey, wordlist);
		}
	    return features;
	}

	public static String loadFileToString(String path) {
		File file = new File(path);
		if(file.exists()) {
			String string = "";
			try {
				string = FileUtils.readFileToString(file,"UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return string;
		}
		System.out.println("Your file doesnot exit");
		return "";
	}
	
	static String ALL_ACTION_LEXICONS = "    \"AFFECT\": [\"afford\", \"believe\", \"decide\", \"feel\", \"hope\", \"imagine\", \"regard\", \"trust\", \"think\"], \n" + 
			"\n" + 
			"    \"ARGUMENTATION\": [\"agree\", \"accept\", \"advocate\", \"argue\", \"claim\", \"conclude\", \"comment\", \"defend\", \"embrace\", \"hypothesize\", \"imply\", \"insist\", \"posit\", \"postulate\", \"reason\", \"recommend\", \"speculate\", \"stipulate\", \"suspect\"],\n" + 
			"\n" + 
			"    \"AWARE\": [\"be unaware\", \"be familiar with\", \"be aware\", \"be not aware\", \"know of\"],\n" + 
			"\n" + 
			"    \"BETTER_SOLUTION\": [\"boost\", \"enhance\", \"defeat\", \"improve\", \"go beyond\", \"perform better\", \"outperform\", \"outweigh\", \"surpass\"],\n" + 
			"    \n" + 
			"    \"CHANGE\": [\"adapt\", \"adjust\", \"augment\", \"combine\", \"change\", \"decrease\", \"elaborate\", \"expand\", \"expand on\", \"extend\", \"derive\", \"incorporate\", \"increase\", \"manipulate\", \"modify\", \"optimize\", \"optimise\", \"refine\", \"render\", \"replace\", \"revise\", \"substitute\", \"tailor\", \"upgrade\"], \n" + 
			"         \n" + 
			"    \"COMPARISON\": [\"compare\", \"compete\", \"evaluate\", \"test\"],\n" + 
			"         \n" + 
			"    \"DENOTATION\": [\"be\", \"denote\", \"represent\" ],\n" + 
			"\n" + 
			"    \"INSPIRATION\": [\"inspire\", \"motivate\" ],\n" + 
			"\n" + 
			"    \"AGREE\": [\"agree with\", \"side with\" ],\n" + 
			"\n" + 
			"    \"CONTINUE\": [\"adopt\", \"base\", \"be base on\", 'base on', \"derive from\", \"originate in\",  \"borrow\", \"build on\", \"follow\", \"following\", \"originate from\", \"originate in\", 'start from', 'proceed from'],\n" + 
			"\n" + 
			"    \"CONTRAST\": [\"be different from\", \"be distinct from\", \"conflict\", \"contrast\", \"clash\", \"differ from\", \"distinguish\", \"differentiate\", \"disagree\", \"disagreeing\", \"dissent\", \"oppose\"],\n" + 
			"\n" + 
			"    \"FUTURE_INTEREST\": [ \"be interest in\", \"plan on\", \"plan to\", \"expect to\", \"intend to\", \"hope to\"],\n" + 
			"\n" + 
			"    \"HEDGING_MODALS\": [\"could\", \"might\", \"may\", \"should\" ],\n" + 
			"\n" + 
			"    \"FUTURE_MODALS\": [\"will\", \"going to\" ],\n" + 
			"\n" + 
			"    \"SHOULD\": [\"should\" ],\n" + 
			"\n" + 
			"    \"INCREASE\": [\"increase\", \"grow\", \"intensify\", \"build up\", \"explode\" ],\n" + 
			"\n" + 
			"    \"INTEREST\": [\"aim\", \"ask\", \"address\", \"attempt\", \"be concern\", \"be interest\", \"be motivat\", \"concern\", \"concern\", \"concern\", \"consider\", \"concentrate on\", \"explore\", \"focus\", \"intend to\", \"like to\", \"look at how\", \"pursue\", \"seek\", \"study\", \"try\", \"target\", \"want\", \"wish\", \"wonder\"],\n" + 
			"\n" + 
			"    \"NEED\": [\"be dependent on\", \"be reliant on\", \"depend on\", \"lack\", \"need\", \"necessitate\", \"require\", \"rely on\"],\n" + 
			"\n" + 
			"    \"PRESENTATION\": [\"describe\", \"discuss\", \"give\", \"introduce\", \"note\", \"notice\", \"point out\", \"present\", \"propose\", \"put forward\", \"recapitulate\", \"remark\", \"report\", \"say\", \"show\", \"sketch\", \"state\", \"suggest\", \"talk about\"], \n" + 
			"\n" + 
			"    \"PROBLEM\": [\"abound\", \"aggravate\", \"arise\", \"be cursed\", \"be incapable of\", \"be force to\", \"be limite to\", \"be problematic\", \"be restrict to\", \"be trouble\", \"be unable to\", \"contradict\", \"damage\", \"degrade\", \"degenerate\", \"fail\", \"fall prey\", \"fall short\", \"force\", \"force\", \"hinder\", \"impair\", \"impede\", \"inhibit\", \"misclassify\", \"misjudge\", \"mistake\", \"misuse\", \"neglect\", \"obscure\", \"overestimate\", \"over-estimate\", \"overfit\", \"over-fit\", \"overgeneralize\", \"over-generalize\", \"overgeneralise\", \"over-generalise\", \"overgenerate\", \"over-generate\", \"overlook\", \"pose\", \"plague\", \"preclude\", \"prevent\", \"remain\", \"resort to\", \"restrain\", \"run into\", \"settle for\", \"spoil\", \"suffer from\", \"threaten\", \"thwart\", \"underestimate\", \"under-estimate\", \"undergenerate\", \"under-generate\", \"violate\", \"waste\", \"worsen\"], \n" + 
			"        \n" + 
			"    \"RESEARCH\": [\"apply\", \"analyze\", \"analyse\", \"build\", \"calculate\", \"categorize\", \"categorise\", \"characterize\", \"characterise\", \"choose\", \"check\", \"classify\", \"collect\", \"compose\", \"compute\", \"conduct\", \"confirm\", \"construct\", \"count\", \"define\", \"delineate\", \"design\", \"detect\", \"determine\", \"equate\", \"estimate\", \"examine\", \"expect\", \"formalize\", \"formalise\", \"formulate\", \"gather\", \"identify\", \"implement\", \"indicate\", \"inspect\", \"integrate\", \"interpret\", \"investigate\", \"isolate\", \"maximize\", \"maximise\", \"measure\", \"minimize\", \"minimise\", \"observe\", \"predict\", \"realize\", \"realise\", \"reconfirm\", \"revalidate\", \"simulate\", \"select\", \"specify\", \"test\", \"verify\", \"work on\"], \n" + 
			"\n" + 
			"    \"SEE\": [ \"see\", \"view\", \"treat\", \"consider\" ],\n" + 
			"\n" + 
			"    \"SIMILAR\": [\"bear comparison\", \"be analogous to\", \"be alike\", \"be related to\", \"be closely relate to\", \"be reminiscent of\", \"be the same as\", \"be similar to\", \"be in a similar vein to\", \"have much in common with\", \"have a lot in common with\", \"pattern with\", \"resemble\"],\n" + 
			"\n" + 
			"    \"SOLUTION\": [\"accomplish\", \"account for\", \"achieve\", \"apply to\", \"answer\", \"alleviate\", \"allow for\", \"allow\", \"allow\", \"avoid\", \"benefit\", \"capture\", \"clarify\", \"circumvent\", \"contribute\", \"cope with\", \"cover\", \"cure\", \"deal with\", \"demonstrate\", \"develop\", \"devise\", \"discover\", \"elucidate\", \"escape\", \"explain\", \"fix\", \"gain\", \"go a long way\", \"guarantee\", \"handle\", \"help\", \"implement\", \"justify\", \"lend itself\", \"make progress\", \"manage\", \"mend\", \"mitigate\", \"model\", \"obtain\", \"offer\", \"overcome\", \"perform\", \"preserve\", \"prove\", \"provide\", \"realize\", \"realise\", \"rectify\", \"refrain from\", \"remedy\", \"resolve\", \"reveal\", \"scale up\", \"sidestep\", \"solve\", \"succeed\", \"tackle\", \"take care of\", \"take into account\", \"treat\", \"warrant\", \"work well\", \"yield\"],\n" + 
			"\n" + 
			"    \"TEXTSTRUCTURE\": [\"begin by\", \"illustrate\", \"conclude by\", \"organize\", \"organise\", \"outline\", \"return to\", \"review\", \"start by\", \"structure\", \"summarize\", \"summarise\", \"turn to\"], \n" + 
			"         \n" + 
			"    \"USE\": [\"apply\", \"employ\", \"use\", \"make use\", \"utilize\", \"implement\", 'resort to']";
	
	static String ALL_CONCEPT_LEXICONS = "\"NEGATION\": [\"no\", \"not\", \"nor\", \"non\", \"neither\", \"none\", \"never\", \"aren't\", \"can't\", \"cannot\", \"hadn't\", \"hasn't\", \"haven't\", \"isn't\", \"didn't\", \"don't\", \"doesn't\", \"n't\", \"wasn't\", \"weren't\", \"nothing\", \"nobody\", \"less\", \"least\", \"little\", \"scant\", \"scarcely\", \"rarely\", \"hardly\", \"few\", \"rare\", \"unlikely\"],\n" + 
			"\n" + 
			"    \"3RD_PERSON_PRONOUN_(NOM)\": [\"they\", \"he\", \"she\", \"theirs\", \"hers\", \"his\"],\n" + 
			"\n" + 
			"    \"OTHERS_NOM\": [\"they\", \"he\", \"she\", \"theirs\", \"hers\", \"his\"],\n" + 
			"\n" + 
			"    \"3RD_PERSON_PRONOUN_(ACC)\": [\"her\", \"him\", \"them\"],\n" + 
			"\n" + 
			"    \"OTHERS_ACC\": [\"her\", \"him\", \"them\"],\n" + 
			"\n" + 
			"    \"3RD_POSS_PRONOUN\": [\"their\", \"his\", \"her\"],\n" + 
			"\n" +  
			"    \"OTHERS_POSS\": [\"their\", \"his\", \"her\", \"they\"],\n" + 
			"\n" + 
			"    \"3RD_PERSON_REFLEXIVE\": [\"themselves\", \"himself\", \"herself\"],\n" + 
			"\n" + 
			"    \"1ST_PERSON_PRONOUN_(NOM)\": [\"we\", \"i\", \"ours\", \"mine\"],\n" + 
			"\n" + 
			"    \"SELF_NOM\": [\"we\", \"i\", \"ours\", \"mine\"],\n" + 
			"\n" + 
			"    \"1ST_PERSON_PRONOUN_(ACC)\": [\"us\", \"me\"],\n" + 
			"\n" + 
			"    \"SELF_ACC\": [\"us\", \"me\"],\n" + 
			"\n" + 
			"    \"1ST_POSS_PRONOUN\": [\"my\", \"our\"],\n" + 
			"\n" + 
			"    \"SELF_POSS\": [\"my\", \"our\"],\n" + 
			"\n" + 
			"    \"1ST_PERSON_REFLEXIVE \": [\"ourselves\", \"myself\"],\n" + 
			"\n" + 
			"    \"REFERENTIAL\": [\"this\", \"that\", \"those\", \"these\"],\n" + 
			"\n" + 
			"    \"REFLEXIVE\": [\"itself ourselves\", \"myself\", \"themselves\", \"himself\", \"herself\"],\n" + 
			"\n" + 
			"    \"QUESTION\": [\"?\", \"how\", \"why\", \"whether\", \"wonder\"],\n" + 
			"\n" + 
			"    \"GIVEN\": [\"noted\", \"mentioned\", \"addressed\", \"illustrated\", \"described\", \"discussed\", \"given\", \"outlined\", \"presented\", \"proposed\", \"reported\", \"shown\", \"taken\"],\n" + 
			"    \n" + 
			"    \"PROFESSIONALS\": [\"collegues\", \"community\", \"computer scientists\", \"computational linguists\", \"discourse analysts\", \"expert\", \"investigators\", \"linguists\", \"logicians\", \"philosophers\", \"psycholinguists\", \"psychologists\", \"researchers\", \"scholars\", \"semanticists\", \"scientists\"],\n" + 
			"\n" + 
			"    \"DISCIPLINE\": [\"computerscience\", \"computer linguistics\", \"computational linguistics\", \"discourse analysis\", \"logics\", \"linguistics\", \"psychology\", \"psycholinguistics\", \"philosophy\", \"semantics\", \"lexical semantics\", \"several disciplines\", \"various disciplines\"],\n" + 
			"    \n" + 
			"    \"TEXT_NOUN\": [\"paragraph\", \"section\", \"subsection\", \"chapter\"],\n" + 
			"    \n" + 
			"    \"SIMILAR_NOUN\": [\"analogy\", \"similarity\"],\n" + 
			"\n" + 
			"    \"SIMILAR_ADJ\": [\"similar\", \"comparable\", \"analogous\", \"kindred\"],\n" + 
			"\n" + 
			"    \"COMPARISON_NOUN\": [\"accuracy\", \"baseline\", \"comparison\", \"competition\", \"evaluation\", \"inferiority\", \"measure\", \"measurement\", \"performance\", \"precision\", \"optimum\", \"recall\", \"superiority\"],\n" + 
			"    \n" + 
			"    \"CONTRAST_NOUN\": [\"contrast\", \"conflict\", \"clash\", \"clashes\", \"difference\", \"point of departure\"],\n" + 
			"\n" + 
			"    \"AIM_NOUN\": [\"aim\", \"direction\", \"goal\", \"intention\", \"objective\", \"purpose\", \"task\", \"theme\", \"topic\"],\n" + 
			"\n" + 
			"    \"ARGUMENTATION_NOUN\": [\"assumption\", \"belief\", \"hypothesis\", \"hypotheses\", \"claim\", \"conclusion\", \"confirmation\", \"opinion\", \"recommendation\", \"stipulation\", \"view\"],\n" + 
			"\n" + 
			"    \"PROBLEM_NOUN\": [\"Achilles heel\", \"caveat\", \"challenge\", \"complication\", \"contradiction\", \"damage\", \"danger\", \"deadlock\", \"defect\", \"detriment\", \"difficulty\", \"dilemma\", \"disadvantage\", \"disregard\", \"doubt\", \"downside\", \"drawback\", \"error\", \"failure\", \"fault\", \"foil\", \"flaw\", \"handicap\", \"hindrance\", \"hurdle\", \"ill\", \"inflexibility\", \"impediment\", \"imperfection\", \"intractability\", \"inefficiency\", \"inadequacy\", \"inability\", \"lapse\", \"limitation\", \"malheur\", \"mishap\", \"mischance\", \"mistake\", \"obstacle\", \"oversight\", \"pitfall\", \"problem\", \"shortcoming\", \"threat\", \"trouble\", \"vulnerability\", \"absence\", \"dearth\", \"deprivation\", \"lack\", \"loss\", \"fraught\", \"proliferation\", \"spate\"],\n" + 
			"\n" + 
			"    \"QUESTION_NOUN\": [\"question\", \"conundrum\", \"enigma\", \"paradox\", \"phenomena\", \"phenomenon\", \"puzzle\", \"riddle\"],\n" + 
			"\n" + 
			"    \"SOLUTION_NOUN\": [\"answer\", \"accomplishment\", \"achievement\", \"advantage\", \"benefit\", \"breakthrough\", \"contribution\", \"explanation\", \"idea\", \"improvement\", \"innovation\", \"insight\", \"justification\", \"proposal\", \"proof\", \"remedy\", \"solution\", \"success\", \"triumph\", \"verification\", \"victory\"],\n" + 
			"\n" + 
			"    \"INTEREST_NOUN\": [\"attention\", \"quest\"],\n" + 
			"\n" +
			"    \"RESEARCH_NOUN\": [\"evidence\", \"experiment\", \"finding\", \"progress\", \"observation\", \"outcome\", \"result\"],\n" + 
			"\n" + 
			"    \"RESULT_NOUN\": [\"evidence\", \"experiment\", \"finding\", \"progress\", \"observation\", \"outcome\", \"result\"],\n" + 
			"\n" + 
			"    \"METRIC_NOUN\": [\"bleu\", \"F-score\", \"F1-score\", \"F score\", \"F1 score\", \"precision\", \"recall\", \"accuracy\", \"correlation\"],\n" + 
			"\n" + 
			"    \"CHANGE_NOUN\": [ \"adaptation\", \"enhancement\", \"extension\", \"generalization\", \"development\", \"modification\", \"refinement\", \"version\", \"variant\", \"variation\"],\n" + 
			"\n" + 
			"    \"PRESENTATION_NOUN\": [\"article\", \"draft\", \"manuscript\", \"paper\", \"project\", \"report\", \"study\"],\n" + 
			"    \n" + 
			"    \"NEED_NOUN\": [\"necessity\", \"motivation\"],\n" + 
			"\n" + 
			"    \"WORK_NOUN\": [\"account\", \"algorithm\", \"analysis\", \"analyses\", \"approach\", \"approaches\", \"application\", \"architecture\", \"characterization\", \"characterisation\", \"component\", \"design\", \"extension\", \"formalism\", \"formalization\", \"formalisation\", \"framework\", \"implementation\", \"investigation\", \"machinery\", \"method\", \"methodology\", \"model\", \"module\", \"moduls\", \"process\", \"procedure\", \"program\", \"prototype\", \"research\", \"researches\", \"strategy\", \"system\", \"technique\", \"theory\", \"tool\", \"treatment\", \"work\"],\n" + 
			"\n" + 
			"    \"TRADITION_NOUN\": [\"acceptance\", \"community\", \"convention\", \"disciples\", \"disciplines\", \"folklore\", \"literature\", \"mainstream\", \"school\", \"tradition\", \"textbook\"],\n" + 
			"\n" + 
			"    \"CHANGE_ADJ\": [\"alternate\", \"alternative\"],\n" + 
			"    \n" + 
			"    \"GOOD_ADJ\": [\"adequate\", \"advantageous\", \"appealing\", \"appropriate\", \"attractive\", \"automatic\", \"beneficial\", \"capable\", \"cheerful\", \"clean\", \"clear\", \"compact\", \"compelling\", \"competitive\", \"comprehensive\", \"consistent\", \"convenient\", \"convincing\", \"constructive\", \"correct\", \"desirable\", \"distinctive\", \"efficient\", \"effective\", \"elegant\", \"encouraging\", \"exact\", \"faultless\", \"favourable\", \"feasible\", \"flawless\", \"good\", \"helpful\", \"impeccable\", \"innovative\", \"insightful\", \"intensive\", \"meaningful\", \"neat\", \"perfect\", \"plausible\", \"positive\", \"polynomial\", \"powerful\", \"practical\", \"preferable\", \"precise\", \"principled\", \"promising\", \"pure\", \"realistic\", \"reasonable\", \"reliable\", \"right\", \"robust\", \"satisfactory\", \"simple\", \"sound\", \"successful\", \"sufficient\", \"systematic\", \"tractable\", \"usable\", \"useful\", \"valid\", \"unlimited\", \"well worked out\", \"well\", \"enough\", \"well-motivated\"],\n" + 
			"    \n" + 
			"    \"BAD_ADJ\": [\"absent\", \"ad-hoc\", \"adhoc\", \"ad hoc\", \"annoying\", \"ambiguous\", \"arbitrary\", \"awkward\", \"bad\", \"brittle\", \"brute-force\", \"brute force\", \"careless\", \"confounding\", \"contradictory\", \"defect\", \"defunct\", \"disturbing\", \"elusive\", \"erraneous\", \"expensive\", \"exponential\", \"false\", \"fallacious\", \"frustrating\", \"haphazard\", \"ill-defined\", \"imperfect\", \"impossible\", \"impractical\", \"imprecise\", \"inaccurate\", \"inadequate\", \"inappropriate\", \"incomplete\", \"incomprehensible\", \"inconclusive\", \"incorrect\", \"inelegant\", \"inefficient\", \"inexact\", \"infeasible\", \"infelicitous\", \"inflexible\", \"implausible\", \"inpracticable\", \"improper\", \"insufficient\", \"intractable\", \"invalid\", \"irrelevant\", \"labour-intensive\", \"laborintensive\", \"labour intensive\", \"labor intensive\", \"laborious\", \"limited-coverage\", \"limited coverage\", \"limited\", \"limiting\", \"meaningless\", \"modest\", \"misguided\", \"misleading\", \"nonexistent\", \"NP-hard\", \"NP-complete\", \"NP hard\", \"NP complete\", \"questionable\", \"pathological\", \"poor\", \"prone\", \"protracted\", \"restricted\", \"scarce\", \"simplistic\", \"suspect\", \"time-consuming\", \"time consuming\", \"toy\", \"unacceptable\", \"unaccounted for\", \"unaccounted-for\", \"unaccounted\", \"unattractive\", \"unavailable\", \"unavoidable\", \"unclear\", \"uncomfortable\", \"unexplained\", \"undecidable\", \"undesirable\", \"unfortunate\", \"uninnovative\", \"uninterpretable\", \"unjustified\", \"unmotivated\", \"unnatural\", \"unnecessary\", \"unorthodox\", \"unpleasant\", \"unpractical\", \"unprincipled\", \"unreliable\", \"unsatisfactory\", \"unsound\", \"unsuccessful\", \"unsuited\", \"unsystematic\", \"untractable\", \"unwanted\", \"unwelcome\", \"useless\", \"vulnerable\", \"weak\", \"wrong\", \"too\", \"overly\", \"only\"],\n" + 
			"\n" + 
			"    \"BEFORE_ADJ\": [\"earlier\", \"initial\", \"past\", \"previous\", \"prior\"],\n" + 
			"    \n" + 
			"    \"CONTRAST_ADJ\": [\"different\", \"distinguishing\", \"contrary\", \"competing\", \"rival\"],\n" + 
			"\n" + 
			"    \"CONTRAST_ADV\": [\"differently\", \"distinguishingly\", \"contrarily\", \"otherwise\", \"other than\", \"contrastingly\", \"imcompatibly\", \"on the other hand\", ],\n" + 
			"\n" + 
			"    \"TRADITION_ADJ\": [\"better known\", \"better-known\", \"cited\", \"classic\", \"common\", \"conventional\", \"current\", \"customary\", \"established\", \"existing\", \"extant\", \"available\", \"favourite\", \"fashionable\", \"general\", \"obvious\", \"long-standing\", \"mainstream\", \"modern\", \"naive\", \"orthodox\", \"popular\", \"prevailing\", \"prevalent\", \"published\", \"quoted\", \"seminal\", \"standard\", \"textbook\", \"traditional\", \"trivial\", \"typical\", \"well-established\", \"well-known\", \"widelyassumed\", \"unanimous\", \"usual\"],\n" + 
			"\n" + 
			"    \"MANY\": [\"a number of\", \"a body of\", \"a substantial number of\", \"a substantial body of\", \"most\", \"many\", \"several\", \"various\"],\n" + 
			"\n" + 
			"    \"HELP_NOUN\": ['help', 'aid', 'assistance', 'support' ],\n" + 
			"\n" + 
			"    \"SENSE_NOUN\": ['sense', 'spirit', ],\n" + 
			"\n" + 
			"    \"GRAPHIC_NOUN\": ['table', 'tab', 'figure', 'fig', 'example' ],\n" + 
			"    \n" + 
			"    \"COMPARISON_ADJ\": [\"evaluative\", \"superior\", \"inferior\", \"optimal\", \"better\", \"best\", \"worse\", \"worst\", \"greater\", \"larger\", \"faster\", \"weaker\", \"stronger\"],\n" + 
			"\n" + 
			"    \"PROBLEM_ADJ\": [\"demanding\", \"difficult\", \"hard\", \"non-trivial\", \"nontrivial\"],\n" + 
			"    \n" + 
			"    \"RESEARCH_ADJ\": [\"empirical\", \"experimental\", \"exploratory\", \"ongoing\", \"quantitative\", \"qualitative\", \"preliminary\", \"statistical\", \"underway\"],\n" + 
			"\n" + 
			"    \"AWARE_ADJ\": [\"unnoticed\", \"understood\", \"unexplored\"],\n" + 
			"\n" + 
			"    \"NEED_ADJ\": [\"necessary\", \"indispensable\", \"requisite\"],\n" + 
			"\n" + 
			"    \"NEW_ADJ\": [\"new\", \"novel\", \"state-of-the-art\", \"state of the art\", \"leading-edge\", \"leading edge\", \"enhanced\"],\n" + 
			"\n" + 
			"    \"FUTURE_ADJ\": [\"further\", \"future\"],\n" + 
			"\n" + 
			"    \"HEDGE_ADJ\": [ \"possible\", \"potential\", \"conceivable\", \"viable\"],\n" + 
			"    \n" + 
			"    \"MAIN_ADJ\": [\"main\", \"key\", \"basic\", \"central\", \"crucial\", \"critical\", \"essential\", \"eventual\", \"fundamental\", \"great\", \"important\", \"key\", \"largest\", \"main\", \"major\", \"overall\", \"primary\", \"principle\", \"serious\", \"substantial\", \"ultimate\"],\n" + 
			"\n" + 
			"    \"CURRENT_ADV\": [\"currently\", \"presently\", \"at present\"],\n" + 
			"\n" + 
			"    \"TEMPORAL_ADV\": [\"finally\", \"briefly\", \"next\"],\n" + 
			"\n" +
			"    \"STARSEM_NEGATION\": [  \"contrary\", \"without\", \"n't\", \"none\", \"nor\", \"nothing\", \"nowhere\", \"refused\", \"nobody\", \"means\", \"never\", \"neither\", \"absence\", \"except\", \"rather\", \"no\", \"for\", \"fail\", \"not\", \"neglected\", \"less\", \"prevent\",],\n" + 
			"\n" + 
			"    'DOWNTONERS': [ 'almost', 'barely', 'hardly', 'merely', 'mildly', 'nearly', 'only', 'partially', 'partly', 'practically', 'scarcely', 'slightly', 'somewhat', ],\n" + 
			"\n" + 
			"    'AMPLIFIERS': [ 'absolutely', 'altogether', 'completely', 'enormously', 'entirely', 'extremely', 'fully', 'greatly', 'highly', 'intensely', 'strongly', 'thoroughly', 'totally', 'utterly', 'very', ],\n" + 
			"\n" + 
			"    \n" + 
			"    'PUBLIC_VERBS': ['acknowledge', 'admit', 'agree', 'assert', 'claim', 'complain', 'declare', 'deny', 'explain', 'hint', 'insist', 'mention', 'proclaim', 'promise', 'protest', 'remark', 'reply', 'report', 'say', 'suggest', 'swear', 'write', ],\n" + 
			"    \n" + 
			"    'PRIVATE_VERBS': [ 'anticipate', 'assume', 'believe', 'conclude', 'decide', 'demonstrate', 'determine', 'discover', 'doubt', 'estimate', 'fear', 'feel', 'find', 'forget', 'guess', 'hear', 'hope', 'imagine', 'imply', 'indicate', 'infer', 'know', 'learn', 'mean', 'notice', 'prove', 'realize', 'recognize', 'remember', 'reveal', 'see', 'show', 'suppose', 'think', 'understand', ],\n" + 
			"    \n" + 
			"    'SUASIVE_VERBS': [ 'agree', 'arrange', 'ask', 'beg', 'command', 'decide', 'demand', 'grant', 'insist', 'instruct', 'ordain', 'pledge', 'pronounce', 'propose', 'recommend', 'request', 'stipulate', 'suggest', 'urge', ]\n";

}
