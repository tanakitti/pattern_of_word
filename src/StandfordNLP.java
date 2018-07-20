import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.patterns.surface.Token;
import edu.stanford.nlp.ie.util.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;

import java.util.*;

public class StandfordNLP {
	static String LEXICONS_TEXT = "    \"NEGATION\": [\"no\", \"not\", \"nor\", \"non\", \"neither\", \"none\", \"never\", \"aren't\", \"can't\", \"cannot\", \"hadn't\", \"hasn't\", \"haven't\", \"isn't\", \"didn't\", \"don't\", \"doesn't\", \"n't\", \"wasn't\", \"weren't\", \"nothing\", \"nobody\", \"less\", \"least\", \"little\", \"scant\", \"scarcely\", \"rarely\", \"hardly\", \"few\", \"rare\", \"unlikely\"],\n" + 
			"    \"3RD_PERSON_PRONOUN_(NOM)\": [\"they\", \"he\", \"she\", \"theirs\", \"hers\", \"his\"],\n" + 
			"    \"OTHERS_NOM\": [\"they\", \"he\", \"she\", \"theirs\", \"hers\", \"his\"],\n" + 
			"    \"3RD_PERSON_PRONOUN_(ACC)\": [\"her\", \"him\", \"them\"], \n" + 
			"    \"OTHERS_ACC\": [\"her\", \"him\", \"them\"], \n" + 
			"    \"3RD_POSS_PRONOUN\": [\"their\", \"his\", \"her\"]";
	
	static String PATERN = "'US_AGENT': [ '@SELF_NOM',\n" + 
			"'@SELF_POSS #JJ @WORK_NOUN',\n" + 
			"'@SELF_POSS #JJ @PRESENTATION_NOUN',\n" + 
			"'@SELF_POSS #JJ @ARGUMENTATION_NOUN',\n" + 
			"'@SELF_POSS #JJ @SOLUTION_NOUN',\n" + 
			"'@SELF_POSS #JJ @RESULT_NOUN',\n" + 
			"'@SELF_POSS @WORK_NOUN',\n" + 
			"'@SELF_POSS @PRESENTATION_NOUN']";
	 public static String text = "their c approaches, in contrast, tend to lend themselves well to "
	 		+ "the generalisation problem, rapidly producing effective generalisations as the "
	 		+ "discovery of dependancies proceeds (see, for example, [79] or =-=[98]-=-). However, "
	 		+ "the results from a subsymbolic learner are difficult to analyse in order to produce "
	 		+ "a human comprehensible form. Once again, additional processing stages are required "
	 		+ "if this problem is to";
	 
	 public static String text2 = "tin components (19, including the histones), proteins involved in"
	 		+ " ubiquitination and proteasome-mediated degradation (11) and components of the endoplasmatic"
	 		+ " reticulum and related membrane structures =-=(14)-=-. DISCUSSION We describe a versatile "
	 		+ "method which allows for detailed analysis of the length of the poly(A) tail. It detects changes "
	 		+ "in poly(A) tail length and allows for separation of the RNAs into m";
	 
	 public static String text3 = "He is walking on the street" ;
		  public static void main(String[] args) {
		    
//		    Properties props = new Properties();
//		    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse");
//		    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//		    CoreDocument document = new CoreDocument(text);
//		    pipeline.annotate(document);
		    
		    
		    String[] LEXICONS = LEXICONS_TEXT.split("\n");
		    Map<String,Set<String>> lexs = new TreeMap<String,Set<String>>();
		    for(String lex : LEXICONS) {
		    	String[] foo = lex.split(":");
		    	
		    	
		    	String lexKey = foo[0].trim().replace("\"", "");
		    	
		    	foo[1] = foo[1].trim().replaceAll("\"", "");
		    	foo[1] = foo[1].replaceAll("\\[", "");
		    	foo[1] = foo[1].replaceAll("\\]", "");
		    	foo[1] = foo[1].replaceAll(" ", "");
		    	String [] words = foo[1].split(",");
		    	Set<String> wordlist = new TreeSet<String>(Arrays.asList(words));
		    	lexs.put(lexKey, wordlist);
		    	
		    }
		    
		    Map<String,Set<String>> pats = new TreeMap<String,Set<String>>();
		    String[] PATERNS = PATERN.replaceAll("\n","").split(":");
		    
		    String key = PATERNS[0].replaceAll("'", "");
		    PATERNS[1] = PATERNS[1].replaceAll("\\[", "");
		    PATERNS[1] = PATERNS[1].replaceAll("\\]", "");
		    PATERNS[1] = PATERNS[1].replaceAll("'", "");
		    
		    String[] bar = PATERNS[1].split(",");
		    Set<String> patlist = new TreeSet<String>();
		    for(String p : bar) {
		    	patlist.add(p.trim());
		    }
		    
		    pats.put(key, patlist);
		    
		    for(Map.Entry<String, Set<String>> lex: pats.entrySet()){
		    	System.out.println(lex.getKey());
		    	for(String word : lex.getValue()) System.out.println("\t"+word);
		    }
		  
		    text = text.replaceAll("=-=.*-=-", "TANAKITTI");
			  	Annotation ann = new Annotation(text3);
			    Properties props = PropertiesUtils.asProperties(
			            "annotators", "tokenize,ssplit,pos,lemma,depparse",
			            "depparse.model", DependencyParser.DEFAULT_MODEL
			    );

			    AnnotationPipeline pipeline = new StanfordCoreNLP(props);
			    pipeline.annotate(ann);
			    
			    List<String> lemmass = new LinkedList<String>();
			    List<String> tokens = new LinkedList<String>();
			    List<String> poss= new LinkedList<String>();
			    String[] ArgType = null;
			    
			    
			    for (CoreMap sentence : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
		            if(!sentence.toString().contains("TANAKITTI")) {
		            	for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
		            		
		            		// tokens
		            		tokens.add(token.toString());
		            		
		            		//lemma
		                    if(token.toShortString("OriginalText").equals("our")) lemmass.add("our");
		                    else if(token.toShortString("OriginalText").contains("my")) lemmass.add("my");
		                    else if(token.toShortString("OriginalText").contains("their")) lemmass.add("their");
		                    else if(token.toShortString("OriginalText").contains("his")) lemmass.add("his");
		                    else if(token.toShortString("OriginalText").contains("her")) lemmass.add("her");
		                    else lemmass.add(token.get(LemmaAnnotation.class));
		                    
		            		//Pos
		            		poss.add(token.get(PartOfSpeechAnnotation.class));
		            		
//		            		System.out.println("Token: "+token.toShortString("OriginalText"));
//		                    System.out.println("Lem: "+token.get(LemmaAnnotation.class));
//		                    System.out.println("POS: "+token.get(PartOfSpeechAnnotation.class));
//		                    System.out.println();
		            	}
		            	ArgType = new String[tokens.size()];
		            	 SemanticGraph sg = sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class);
		            	 int i = 0;
		                 for (IndexedWord iw : sg.vertexSet()) {
		                	 System.out.println(i++);
		                	 for (SemanticGraphEdge se : sg.outgoingEdgeList(iw)) {
		                		 
		                		 
		                         String dependentWord = se.getDependent().word();
		                         String relationName = se.getRelation().getShortName();
		                         System.out.println(relationName.equals("nsubj"));
		                         if (relationName.equals("nsubj")) {
		                        	 ArgType[se.getDependent().index()-1]="subj";
		                        	 ArgType[iw.index()-1] = "verb";
		                         } else if (relationName.equals("dobj")){
		                        	 ArgType[se.getDependent().index()-1]="dobj";
		                         }else {
		                        	 ArgType[se.getDependent().index()-1] = "none";
		                         }
		                         
		                         System.out.println(relationName+"\t"+iw+"\t"+dependentWord);
		                         System.out.println("Index  "+ (se.getDependent().index()-1));
		                         System.out.println();
		                    
		                	 }
		                	
		                 }
		                 
		                 System.out.println(sg.toString(SemanticGraph.OutputFormat.LIST));
		            	
		            	
		            	
		            }
		        }
			    
			    String[]  LEMMA = new String[lemmass.size()];
			    String[]  TOKEN = new String[tokens.size()];
			    String[]  POSS = new String[poss.size()];
			    
			    LEMMA = lemmass.toArray(LEMMA);
			    TOKEN = tokens.toArray(TOKEN);
			    POSS = poss.toArray(POSS);
			  
			    for(Map.Entry<String, Set<String>> pat: pats.entrySet()){
			    	for(String a : pat.getValue()) {
			    		String[] pattern = a.split(" ");
			    		for(int i = 0; i<TOKEN.length-pattern.length-1;i++) {
			    			 for(int j = 0; j< pattern.length;j++) {
			    				 String pi =  pattern[j].charAt(0)+"";
			    				 
			    				 if(pi.equals("@")) {
			    					if( lexs.containsKey(pattern[j].substring(1,pattern[j].length()+1))) {
			    						
			    					}else {
			    						System.out.println("ERROR");
			    						
			    					}
			    					 
			    					 
			    					 
			    				 }
			    				 
			    				 
			    				 
			    				 
			    				 
			    				 
			    				 
			    				 
			    				 
			    				 
			    				 
			    			 }
			    		}
			    		
			    	}
			    }
			  
			  
		   
		   
		    
		    
		    
		    
		    
		  
		  }
		  
		  public static void find(String patern,boolean must_have_subject) {
			 
			  
			  
		  }
		  
		  
		  public static void process_sent(String text) {
			  
			  	Annotation ann = new Annotation(text);
			    Properties props = PropertiesUtils.asProperties(
			            "annotators", "tokenize,ssplit,pos,depparse",
			            "depparse.model", DependencyParser.DEFAULT_MODEL
			    );

			    AnnotationPipeline pipeline = new StanfordCoreNLP(props);
			    pipeline.annotate(ann);
			   
			    HashMap<String, HashMap<String, String>> placeHolderEdges = new HashMap<String, HashMap<String, String>>();
		        placeHolderEdges.put("out", new HashMap<String, String>());
		        placeHolderEdges.put("in", new HashMap<String, String>());

		        // get the edges going into and leaving @placeholder
		        for (CoreMap sentence : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
		            SemanticGraph sg = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
		            Collection<IndexedWord> iw2 = sg.getRoots();
		            System.out.println("Size: "+iw2.size());
		            
		            for (IndexedWord iw : sg.vertexSet()) {
		                System.out.println(iw.toString());
		            	 for (SemanticGraphEdge se : sg.incomingEdgeList(iw)) {
		                     String governorWord = se.getGovernor().word();
		                     System.out.println(governorWord);
		                 }
		                
		            }
		        }
			  
		        
		        for (CoreMap sent : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
		            SemanticGraph sg = sent.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
		            System.out.println(sg.toString(SemanticGraph.OutputFormat.LIST));
		          }
			  
			  
		  }

}
