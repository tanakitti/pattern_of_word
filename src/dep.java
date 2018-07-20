import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

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
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;

public class dep {
	static Map<String,Double[]> WORD_TO_VEC = new TreeMap<String,Double[]>();
	static Map<String, TreeMap<String, LinkedList<String>>> GOLD_PATH = new TreeMap<String,TreeMap<String,LinkedList<String>>>();
	static Map<String, TreeMap<String,Double[]>> GOLD_VEC = new TreeMap<String,TreeMap<String,Double[]>>();
	public static String text = "He is walking on the street =-=(14)-=-";
	static Map<String, TreeMap<String, LinkedList<Double>>> labled_path_to_scores = new TreeMap<String,TreeMap<String,LinkedList<Double>>>();
	public static String text2 = "gh space overhead. The suffix tree in MUMmer, e.g. uses 37n bytes of "
			+ "memory, where n is the input length (Delcher et al., 1999), although with careful implementation, "
			+ "this can be reduced to 8n. AVID (=-=Bray et al., 2003-=-) handles mismatches and gaps by using "
			+ "a variant of the Smith–Waterman algorithm once the anchors have been selected with the help of suffix trees. "
			+ "CHAOS (Brudno and Morgenstern, 2002) indexes k-mers ";
	public static void main(String[] args) {
		load_WORD_TO_VEC();
		load_path_data();
//		
//		System.out.println(GOLD_PATH.get("inv-advcl nsubj"));
//		System.out.println(Arrays.toString(WORD_TO_VEC.get("literature")));
//		System.out.println(Arrays.toString(WORD_TO_VEC.get("figure")));
//		System.out.println(Arrays.toString(WORD_TO_VEC.get("number")));
//		System.out.println(Arrays.toString(WORD_TO_VEC.get("that")));
//		System.out.println(Arrays.toString(WORD_TO_VEC.get("we")));
//		System.out.println(Arrays.toString(WORD_TO_VEC.get("it")));
		
		
		
		String temp = loadFileToString("/Users/bank21235/Desktop/CitationandID.txt");
		String[] raw = temp.split("\n");
		for(int i = 0;i<raw.length;i++) {
			System.out.println(raw[i].split("\t")[0]);
			String patternn = ".*=-=.*-=-.*";
			Pattern r = Pattern.compile(patternn);
			Matcher m = r.matcher(raw[i].split("\t")[1].replaceAll("\n", " "));
			if(!m.find( )) continue;
//			if(!raw[i].split("\t")[0].equals("81958538")) {
////				System.out.println(raw[i].split("\t")[0]);
//				continue;
//			}
			
			
			Map<String,Double> output = get_dependency_features(raw[i].split("\t")[1].replaceAll("\n", ""));
			
			if(!output.containsKey("Background"))output.put("Background", 0.0);
			if(!output.containsKey("Motivation"))output.put("Motivation", 0.0);
			if(!output.containsKey("Uses"))output.put("Uses", 0.0);
			if(!output.containsKey("Future"))output.put("Future", 0.0);
			if(!output.containsKey("CompareOrContrast"))output.put("CompareOrContrast", 0.0);
			if(!output.containsKey("Extends"))output.put("Extends", 0.0);
			if(!output.containsKey("Prior"))output.put("Prior", 0.0);
			
			for(Entry<String,Double> a : output.entrySet()) {
				System.out.println(a.getKey()+" "+a.getValue());
			}
			if(output.get("Prior")>0.0) break;
		}
		
		
	}
	
	
	
	public static Map<String,Double> get_dependency_features(String sentence) {
		Map<String, TreeMap<String, LinkedList<Double>>> labled_path_to_scores = new TreeMap<String,TreeMap<String,LinkedList<Double>>>();
		sentence = sentence.replaceAll("=-=.*-=-", " tanakitti ");
		sentence = sentence.replaceAll("([^a-zA-Z]\\[[0-9]*\\]$|[^a-zA-Z]\\[[0-9]*\\][^a-zA-Z]|\\[[0-9]*\\][^a-zA-Z])", " CITANCE ");
		sentence = sentence.replaceAll("([^a-zA-Z]\\[[0-9]+,([0-9]*,)*[0-9]+\\]$"
				+ "|[^a-zA-Z]\\[[0-9]+,([0-9]*,)*[0-9]+\\][^a-zA-Z]"
				+ "|\\[[0-9]+,([0-9]*,)*[0-9]+\\][^a-zA-Z])", " ");
		
		Sentence processet_sent  = process_sent(sentence);
		Map<String,Double> output = new TreeMap<String,Double>();
		   
//		for(Entry<String, List<String>> a : processet_sent.path_to_leaves.entrySet()) {
//		    	System.out.println(a.getKey());
//		    	for(String b:a.getValue()) {
//		    		System.out.println("\t"+b);
//		    	}
//		    }
		
		for(Entry<String,List<String>> path:processet_sent.path_to_leaves.entrySet()) {
			
			if(!GOLD_VEC.containsKey(path.getKey())) continue;
			
			for(Entry<String,Double[]> label : GOLD_VEC.get(path.getKey()).entrySet()) {
				
				for(String filler:path.getValue()) {
					
					if(!WORD_TO_VEC.containsKey(filler))continue;
					//System.out.println(filler+" "+Arrays.toString(WORD_TO_VEC.get(filler)));
					Double[] filler_vec = WORD_TO_VEC.get(filler);
//					System.out.println(label.getKey()+" "+"label: "+Arrays.toString(label.getValue())+"\nfiller_vec: "+Arrays.toString(filler_vec));
					Double vec_sim = cosineSimilarity(label.getValue(),filler_vec);
//					System.out.println(vec_sim);
					if(labled_path_to_scores.containsKey(label.getKey())) {
						TreeMap<String, LinkedList<Double>> label_leave = labled_path_to_scores.get(label.getKey());
						if(label_leave.containsKey(path.getKey())) {
							
							label_leave.get(path.getKey()).add(vec_sim);
						}else {
//							System.out.println("vec:"+vec_sim);
							TreeMap<String, LinkedList<Double>> add_path = new TreeMap<String, LinkedList<Double>>();
							LinkedList<Double> add_list = new LinkedList<Double>();
							add_list.add(vec_sim);
							add_path.put(path.getKey(), add_list);
							labled_path_to_scores.put(label.getKey(), add_path);
							
						}
					}else {
						
						TreeMap<String, LinkedList<Double>> add_path = new TreeMap<String, LinkedList<Double>>();
						LinkedList<Double> add_list = new LinkedList<Double>();
						add_list.add(vec_sim);
						add_path.put(path.getKey(), add_list);
						labled_path_to_scores.put(label.getKey(), add_path);
					}
				}	
			}
		}
		
//		for(Entry<String, TreeMap<String, LinkedList<Double>>> label_score:labled_path_to_scores.entrySet()) {
//			System.out.println(label_score.getKey());
//			for(Entry<String, LinkedList<Double>> path_score:label_score.getValue().entrySet()){
//				System.out.println("\t"+path_score.getKey());
//				for(Double socre:path_score.getValue()) {
//					System.out.println("\t\t"+socre);	
//				}
//			}
//		}
		
		
		for(Entry<String, TreeMap<String, LinkedList<Double>>> label_score:labled_path_to_scores.entrySet()) {
			double score_sum = 0;
			double num_score = 0;
			for(Entry<String, LinkedList<Double>> path_score:label_score.getValue().entrySet()){
				for(Double socre:path_score.getValue()) {
					score_sum +=socre;
					num_score +=1;
				}
			}
			if(num_score>0) {
				output.put(label_score.getKey(), score_sum/num_score);
			}
		}
		return output;
	}

	public static double cosineSimilarity(Double[] filler_vec, Double[] filler_vec2) {
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < filler_vec.length; i++) {
	        dotProduct += filler_vec[i] * filler_vec2[i];
	        normA += Math.pow(filler_vec[i], 2);
	        normB += Math.pow(filler_vec2[i], 2);
	    }   
	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}
	
	public static void load_WORD_TO_VEC() {
		File file = new File("/Users/bank21235/Documents/glove.840B.300d.txt");
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;
			int stop = 0;
		    while ((line = br.readLine()) != null) {
		    	String[] cols = line.split(" ");
		    	Double[] vector = new Double[300];
		    	for(int i = 0; i<300;i++) {
		    		vector[i] = Double.parseDouble(cols[i+1]);
		    	}
		    	WORD_TO_VEC.put(cols[0], vector);
		    	//System.out.println(cols[0]+" "+Arrays.toString(WORD_TO_VEC.get(cols[0])));
		    	stop++;
		    	if(stop==10000)break;
		    }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void load_path_data() {
		String temp = loadFileToString("./resourse/dependency-contexts.tsv");
		String[] raw = temp.split("\n");
		
		
		for(String a:raw) {
			String[] cols = a.split("\t");
			String fname = cols[0];
			String label = cols[1];
			String[] path_elements = cols[2].split(" ");
			String path = "";
			String leaf = "";
			if(path_elements.length==3) {
				path = path_elements[0];
				leaf = path_elements[1];
			}else {
				path = path_elements[0]+" "+path_elements[1];
				leaf = path_elements[2];
			}
			if(path.contains("punct")||path.contains("det")) {
				continue;
			}
			
			if(GOLD_PATH.containsKey(path)) {
				Map<String, LinkedList<String>> Gold_Label = GOLD_PATH.get(path);
				if(Gold_Label.containsKey(label)) {
					Gold_Label.get(label).add(leaf);
				}else {
					LinkedList<String> leaves = new LinkedList<String>();
					leaves.add(leaf);
					Gold_Label.put(label, leaves);
				}
			}else {
				TreeMap<String, LinkedList<String>> Gold_Label = new TreeMap<String, LinkedList<String>>() ;
				LinkedList<String> leaves = new LinkedList<String>();
				leaves.add(leaf);
				Gold_Label.put(label, leaves);
				GOLD_PATH.put(path, Gold_Label);
			}	
		}
		
		//test
//		for(Entry<String, TreeMap<String, LinkedList<String>>> test:GOLD_PATH.entrySet()) {
//			System.out.println(test.getKey());
//			for(Entry<String, LinkedList<String>> test2 : test.getValue().entrySet()) {
//				System.out.println("  "+test2.getKey());
//				for(String test3 : test2.getValue()) {
//					System.out.println("    "+test3);
//				}
//			}
//		}
		
		
		for(Entry<String, TreeMap<String, LinkedList<String>>> path : GOLD_PATH.entrySet()) {
			for(Entry<String,LinkedList<String>> label : path.getValue().entrySet()) {
				LinkedList<String> leaves = label.getValue();
				Double[] avg_vec = new Double[300];
				boolean add = false;
				
				for(int i = 0;i<300;i++)avg_vec[i] = 0.0;
				
				for(String leave : leaves) {
					if(WORD_TO_VEC.containsKey(leave)) {
						
						for(int i = 0;i<300;i++) {
							
							avg_vec[i]+=WORD_TO_VEC.get(leave)[i];
							
						}
						add = true;
					}
					
				}
				if(add) {
					TreeMap<String,Double[]> label1 = new TreeMap<String,Double[]>();
					label1.put(label.getKey(), avg_vec);
 					GOLD_VEC.put(path.getKey(), label1);
				}
			}
		}
		
//		for(Entry<String, TreeMap<String,Double[]>> oh : GOLD_VEC.entrySet()) {
//			System.out.println(oh.getKey());
//			for(Entry<String,Double[]> ah : oh.getValue().entrySet()) {
//				System.out.println(" "+ah.getKey()+" :"+Arrays.toString(ah.getValue()));
//			}
//		}
	}
	
	
	public static Sentence process_sent(String text) {
		text = text.replaceAll("=-=.*-=-", " tanakitti ");
		text = text.toLowerCase();
		Annotation ann = new Annotation(text);
	    Properties props = PropertiesUtils.asProperties(
	            "annotators", "tokenize,ssplit,pos,lemma,depparse",
	            "depparse.model", DependencyParser.DEFAULT_MODEL
	    );
	    AnnotationPipeline pipeline = new StanfordCoreNLP(props);
	    pipeline.annotate(ann);
	    String omg = "";
	    List<String> lemmass = new LinkedList<String>();
	    List<String> tokens = new LinkedList<String>();
	    List<String> poss= new LinkedList<String>();
	    String[] ArgType = null;
	    int root_index = -1;
	    
	    Map<Integer, List<String[]>> tree = new TreeMap<Integer,List<String[]>>();
	    Map<Integer, List<String[]>> intree = new TreeMap<Integer,List<String[]>>();
	    
	    
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
            	 //System.out.println(sg.toString(SemanticGraph.OutputFormat.LIST));
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
                 Collection<IndexedWord> roots = sg.getRoots();
                 if(roots.size()==1) {
                	 for(IndexedWord root:roots) {
                		 root_index = root.index()-1;
                	 }
                	 for (IndexedWord iw : sg.vertexSet()) {
                		 for (SemanticGraphEdge se : sg.outgoingEdgeList(iw)) {
                			 
                			 String re_name = se.getRelation().getShortName();
                			 int dep_id = se.getDependent().index()-1;
                			 int gov_id = iw.index()-1;
                			
                			 if(tree.containsKey(gov_id)) {
                				 
                				 String[] add = new String[2];
                				 add[0] = re_name;
                				 add[1] = dep_id+"";
                				 tree.get(gov_id).add(add);
                			 }else {
                				 
                				 List<String[]> addlist =  new LinkedList<String[]>();
                				 String[] add = new String[2];
                				 add[0] = re_name;
                				 add[1] = dep_id+"";
                				 addlist.add(add);
                				 tree.put(gov_id, addlist);
                			 }
                			 
                			 if(intree.containsKey(dep_id)) {
                				 String[] add = new String[2];
                				 add[0] = "inv-"+re_name;
                				 add[1] = gov_id+"";
                				 intree.get(dep_id).add(add);
                			 }else {
                				 List<String[]> addlist =  new LinkedList<String[]>();
                				 String[] add = new String[2];
                				 add[0] = "inv-"+re_name;
                				 add[1] = gov_id+"";
                				 addlist.add(add);
                				 intree.put(dep_id, addlist);
                			 }
                		 }
                     }
                 }else {
                	 System.out.println("WTFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
                 }
            }
        }
	    
	    
//	    for(Entry<Integer, List<String[]>> a : tree.entrySet()) {
//	    	System.out.println(a.getKey());
//	    	for(String[] b:a.getValue()) {
//	    		System.out.println(Arrays.toString(b));
//	    	}
//	    }
//	    for(Entry<Integer, List<String[]>> a : intree.entrySet()) {
//	    	System.out.println(a.getKey());
//	    	for(String[] b:a.getValue()) {
//	    		System.out.println(Arrays.toString(b));
//	    	}
//	    }
	    
	    
	    
	    
	    String[]  LEMMA = new String[lemmass.size()];
	    String[]  TOKEN = new String[tokens.size()];
	    String[]  POSS = new String[poss.size()];
	    
	    for(int i = 0; i < ArgType.length;i++)if(ArgType[i]==null)ArgType[i]="root";
	    LEMMA = lemmass.toArray(LEMMA);
	    TOKEN = tokens.toArray(TOKEN);
	    POSS = poss.toArray(POSS);
		
	    Map<String,List<String>> path_to_leaves = new TreeMap<String,List<String>>();
	    if(tree.get(citation-1)!=null) {
	    	for(String[] a : tree.get(citation-1)) {
		    	String dep = a[0];
		    	int index = Integer.parseInt(a[1]);
		    	String path = dep;
		    	if(path_to_leaves.containsKey(path)) {
					 String leave = LEMMA[index];
					 path_to_leaves.get(path).add(leave);
				 }else {
					 List<String> addlist =  new LinkedList<String>();
					 String leave = LEMMA[index];
					 addlist.add(leave);
					 path_to_leaves.put(path, addlist);
				 }
		    	
		    	if(tree.containsKey(index)) {
		    		for(String[] b:tree.get(index)) {
		    			String dep2 = b[0];
		    	    	int index2 = Integer.parseInt(b[1]);
		    	    	String path2 = dep2;
		    	    	
		    	    	if(path_to_leaves.containsKey(path+" "+path2)) {
		   				 	String leave = LEMMA[index2];
		   				 	path_to_leaves.get(path+" "+path2).add(leave);
		   			 	}else {
		   			 		List<String> addlist =  new LinkedList<String>();
		   			 		String leave = LEMMA[index2];
		   			 		addlist.add(leave);
		   			 		path_to_leaves.put(path+" "+path2, addlist);
		   			 	}
		    		}
		    	}
		    	
		    	if(intree.containsKey(index)) {
		    		for(String[] b:intree.get(index)) {
		    			String dep2 = b[0];
		    	    	int index2 = Integer.parseInt(b[1]);
		    	    	String path2 = dep2;
		    	    	
		    	    	if(index2==(citation-1))continue;
		    	    	
		    	    	if(path_to_leaves.containsKey(path+" "+path2)) {
		   				 	String leave = LEMMA[index2];
		   				 	path_to_leaves.get(path+" "+path2).add(leave);
		   			 	}else {
		   			 		List<String> addlist =  new LinkedList<String>();
		   			 		String leave = LEMMA[index2];
		   			 		addlist.add(leave);
		   			 		path_to_leaves.put(path+" "+path2, addlist);
		   			 	}
		    		}
		    	}
		    }
	    }
	    
	    if(intree.get(citation-1)!=null) {
	    	for(String[] a : intree.get(citation-1)) {
		    	String dep = a[0];
		    	int index = Integer.parseInt(a[1]);
		    	String path = dep;
		    	if(path_to_leaves.containsKey(path)) {
					 String leave = LEMMA[index];
					 path_to_leaves.get(path).add(leave);
				 }else {
					 List<String> addlist =  new LinkedList<String>();
					 String leave = LEMMA[index];
					 addlist.add(leave);
					 path_to_leaves.put(path, addlist);
				 }
		    	
		    	if(tree.containsKey(index)) {
		    		for(String[] b:tree.get(index)) {
		    			String dep2 = b[0];
		    	    	int index2 = Integer.parseInt(b[1]);
		    	    	String path2 = dep2;
		    	    	
		    	    	if(index2==(citation-1))continue;
		    	    	
		    	    	if(path_to_leaves.containsKey(path+" "+path2)) {
		   				 	String leave = LEMMA[index2];
		   				 	path_to_leaves.get(path+" "+path2).add(leave);
		   			 	}else {
		   			 		List<String> addlist =  new LinkedList<String>();
		   			 		String leave = LEMMA[index2];
		   			 		addlist.add(leave);
		   			 		path_to_leaves.put(path+" "+path2, addlist);
		   			 	}
		    		}
		    	}
		    	
		    	if(intree.containsKey(index)) {
		    		for(String[] b:intree.get(index)) {
		    			String dep2 = b[0];
		    	    	int index2 = Integer.parseInt(b[1]);
		    	    	String path2 = dep2;
		    	    	
		    	    	
		    	    	
		    	    	if(path_to_leaves.containsKey(path+" "+path2)) {
		   				 	String leave = LEMMA[index2];
		   				 	path_to_leaves.get(path+" "+path2).add(leave);
		   			 	}else {
		   			 		List<String> addlist =  new LinkedList<String>();
		   			 		String leave = LEMMA[index2];
		   			 		addlist.add(leave);
		   			 		path_to_leaves.put(path+" "+path2, addlist);
		   			 	}
		    		}
		    	}
		    }
	    }
	    
	    
//	    for(Entry<String, List<String>> a : path_to_leaves.entrySet()) {
//	    	System.out.println(a.getKey());
//	    	for(String b:a.getValue()) {
//	    		System.out.println("\t"+b);
//	    	}
//	    }
	    
		Sentence output = new Sentence(LEMMA,TOKEN,POSS,ArgType);
		output.Citation = citation-1;
		output.path_to_leaves = path_to_leaves;
		System.out.println(text);
		return output;
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
}

