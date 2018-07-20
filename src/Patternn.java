import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class Patternn {
	public static String text = "gh space overhead. The suffix tree in MUMmer, e.g. uses 37n bytes of "
			+ "memory, where n is the input length (Delcher et al., 1999), although with careful implementation, "
			+ "this can be reduced to 8n. AVID (=-=Bray et al., 2003-=-) handles mismatches and gaps by using "
			+ "a variant of the Smith–Waterman algorithm once the anchors have been selected with the help of suffix trees. "
			+ "CHAOS (Brudno and Morgenstern, 2002) indexes k-mers ";
	
	public static String text2 = "ny complex-valued single-variable problems. The complex univariate "
			+ "problems are solved as two-variable nonconvex problems over the real domain using "
			+ "Davidon-Fletcher-Powell optimization algorithm. In =-=[26]-=- an iterative algorithm "
			+ "of quasi-Newton form is applied for the RSTLS problem for reflexive boundary conditions "
			+ "that exploits the diagonalization properties of the associated matrices. The work [28] e";
	
	public static String text3 = "the first author SELF_POSS evidence =-=[26]-=- alternate none of these algorithms no good algorithms" ;
	
	static String PATERN = "'US_AGENT': [ '@SELF_NOM']";
	
	static String LEXICONS_TEXT = "    \"NEGATION\": [\"no\", \"not\", \"nor\", \"non\", \"neither\", \"none\", \"never\", \"aren't\", \"can't\", \"cannot\", \"hadn't\", \"hasn't\", \"haven't\", \"isn't\", \"didn't\", \"don't\", \"doesn't\", \"n't\", \"wasn't\", \"weren't\", \"nothing\", \"nobody\", \"less\", \"least\", \"little\", \"scant\", \"scarcely\", \"rarely\", \"hardly\", \"few\", \"rare\", \"unlikely\"],\n" + 
			"    \"3RD_PERSON_PRONOUN_(NOM)\": [\"they\", \"he\", \"she\", \"theirs\", \"hers\", \"his\"],\n" + 
			"    \"OTHERS_NOM\": [\"they\", \"he\", \"she\", \"theirs\", \"hers\", \"his\"],\n" + 
			"    \"3RD_PERSON_PRONOUN_(ACC)\": [\"her\", \"him\", \"them\"], \n" + 
			"    \"OTHERS_ACC\": [\"her\", \"him\", \"them\"], \n" + 
			"    \"3RD_POSS_PRONOUN\": [\"their\", \"his\", \"her\"],\n"+ 
			"	 \"SELF_NOM\": [\"we\", \"i\", \"ours\", \"mine\"]";
	
	static String FORMULAIC_PATTERNS = "'GENERAL_FORMULAIC': [ 'in @TRADITION_ADJ #JJ @WORK_NOUN',\n" + 
			"                           'in @TRADITION_ADJ used @WORK_NOUN',\n" + 
			"                           'in @TRADITION_ADJ @WORK_NOUN',\n" + 
			"                           'in @MANY #JJ @WORK_NOUN',\n" + 
			"                           'in @MANY @WORK_NOUN',\n" + 
			"                           'in @BEFORE_ADJ #JJ @WORK_NOUN',\n" + 
			"                           'in @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                           'in other #JJ @WORK_NOUN',\n" + 
			"                           'in such @WORK_NOUN' \n" + 
			"                           ],\n" + 
			"\n" + 
			"    'THEM_FORMULAIC': [ 'according to CITATION',\n" + 
			"                        'like CITATION',\n" + 
			"                        'CITATION style',\n" + 
			"                        'a la CITATION',\n" + 
			"                        'CITATION - style' ],\n" + 
			"\n" + 
			"    'US_PREVIOUS_FORMULAIC': [ '@SELF_NOM have previously',\n" + 
			"                               '@SELF_NOM have earlier'\n" + 
			"                               '@SELF_NOM have elsewhere',\n" + 
			"                               '@SELF_NOM elsewhere',\n" + 
			"                               '@SELF_NOM previously',\n" + 
			"                               '@SELF_NOM earlier',\n" + 
			"                               'elsewhere @SELF_NOM',\n" + 
			"                               'elswhere @SELF_NOM',\n" + 
			"                               'elsewhere , @SELF_NOM',\n" + 
			"                               'elswhere , @SELF_NOM',\n" + 
			"                               'presented elswhere',\n" + 
			"                               'presented elsewhere',\n" + 
			"                               '@SELF_NOM have @ARGUMENTATION elsewhere',\n" + 
			"                               '@SELF_NOM have @SOLUTION elsewhere',\n" + 
			"                               '@SELF_NOM have argue elsewhere',\n" + 
			"                               '@SELF_NOM have show elswhere_NOM',\n" + 
			"                               '@SELF_NOM have argue elswhere_NOM',\n" + 
			"                               '@SELF_NOM will show elsewhere',\n" + 
			"                               '@SELF_NOM will show elswhere',\n" + 
			"                               '@SELF_NOM will argue elsewhere',\n" + 
			"                               '@SELF_NOM will argue elswhere',\n" + 
			"                               'elsewhere SELFCITE',\n" + 
			"                               'elswhere SELFCITE',\n" + 
			"                               'in a @BEFORE_ADJ @PRESENTATION_NOUN',\n" + 
			"                               'in an earlier @PRESENTATION_NOUN',\n" + 
			"                               'another @PRESENTATION_NOUN' ],\n" + 
			"                               \n" + 
			"    'TEXTSTRUCTURE_FORMULAIC': [ 'then @SELF_NOM describe',\n" + 
			"                                 'then , @SELF_NOM describe',\n" + 
			"                                 'next @SELF_NOM describe',\n" + 
			"                                 'next , @SELF_NOM describe',\n" + 
			"                                 'finally @SELF_NOM describe',\n" + 
			"                                 'finally , @SELF_NOM describe',\n" + 
			"                                 'then @SELF_NOM present',\n" + 
			"                                 'then , @SELF_NOM present',\n" + 
			"                                 'next @SELF_NOM present',\n" + 
			"                                 'next , @SELF_NOM present',\n" + 
			"                                 'finally @SELF_NOM present',\n" + 
			"                                 'finally , @SELF_NOM present',\n" + 
			"                                 'briefly describe',\n" + 
			"                                 'briefly introduce',\n" + 
			"                                 'briefly present',\n" + 
			"                                 'briefly discuss' ],\n" + 
			"\n" + 
			"    'HERE_FORMULAIC': [ 'in this @PRESENTATION_NOUN',\n" + 
			"                        'the present @PRESENTATION_NOUN',\n" + 
			"                        '@SELF_NOM here',\n" + 
			"                        'here @SELF_NOM',\n" + 
			"                        'here , @SELF_NOM',\n" + 
			"                        '@GIVEN here',\n" + 
			"                        '@SELF_NOM now',\n" + 
			"                        'now @SELF_NOM',\n" + 
			"                        'now , @SELF_NOM',\n" + 
			"                        '@GIVEN now',\n" + 
			"                        'herein' ],\n" + 
			"\n" + 
			"    'METHOD_FORMULAIC': [ 'a new @WORK_NOUN',\n" + 
			"                          'a novel @WORK_NOUN',\n" + 
			"                          'a @WORK_NOUN of',\n" + 
			"                          'an @WORK_NOUN of',\n" + 
			"                          'a [JJ @WORK_NOUN of',\n" + 
			"                          'an #JJ @WORK_NOUN of',\n" + 
			"                          'a #NN @WORK_NOUN of',\n" + 
			"                          'an #NN @WORK_NOUN of',\n" + 
			"                          'a #JJ #NN @WORK_NOUN of',\n" + 
			"                          'an #JJ #NN @WORK_NOUN of',\n" + 
			"                          'a @WORK_NOUN for',\n" + 
			"                          'an @WORK_NOUN for',\n" + 
			"                          'a #JJ @WORK_NOUN for',\n" + 
			"                          'an #JJ @WORK_NOUN for',\n" + 
			"                          'a #NN @WORK_NOUN for',\n" + 
			"                          'an #NN @WORK_NOUN for',\n" + 
			"                          'a #JJ #NN @WORK_NOUN for',\n" + 
			"                          'an #JJ #NN @WORK_NOUN for',\n" + 
			"                          '@WORK_NOUN design to #VV',\n" + 
			"                          '@WORK_NOUN intend for',\n" + 
			"                          '@WORK_NOUN for #VV',\n" + 
			"                          '@WORK_NOUN for the #NN',\n" + 
			"                          '@WORK_NOUN design to #VV',\n" + 
			"                          '@WORK_NOUN to the #NN',\n" + 
			"                          '@WORK_NOUN to #NN',\n" + 
			"                          '@WORK_NOUN to #VV',\n" + 
			"                          '@WORK_NOUN for #JJ #VV',\n" + 
			"                          '@WORK_NOUN for the #JJ #NN,'\n" + 
			"                          '@WORK_NOUN to the #JJ #NN',\n" + 
			"                          '@WORK_NOUN to #JJ #VV',\n" + 
			"                          'the problem of #RB #VV',\n" + 
			"                          'the problem of #VV',\n" + 
			"                          'the problem of how to'], \n" + 
			"\n" + 
			"    'CONTINUE_FORMULAIC': [ 'follow CITATION',\n" + 
			"                            'follow the @WORK_NOUN of CITATION',\n" + 
			"                            'follow the @WORK_NOUN give in CITATION',\n" + 
			"                            'follow the @WORK_NOUN present in CITATION',\n" + 
			"                            'follow the @WORK_NOUN propose in CITATION',\n" + 
			"                            'follow the @WORK_NOUN discuss in CITATION',\n" + 
			"                            'base on CITATION',\n" + 
			"                            '@CONTINUE CITATION',\n" + 
			"                            '@CONTINUE the @WORK_NOUN',\n" + 
			"                            '@CONTINUE a @WORK_NOUN',\n" + 
			"                            '@CONTINUE an @WORK_NOUN',\n" + 
			"                            '@CONTINUE @OTHERS_POSS @WORK_NOUN',\n" + 
			"                            '@CONTINUE @SELF_POSS @WORK_NOUN',\n" + 
			"                            '@AGREE CITATION',\n" + 
			"                            '@AGREE the @WORK_NOUN',\n" + 
			"                            '@AGREE a @WORK_NOUN',\n" + 
			"                            '@AGREE an @WORK_NOUN',\n" + 
			"                            '@AGREE @OTHERS_POSS @WORK_NOUN',\n" + 
			"                            '@AGREE @SELF_POSS @WORK_NOUN',\n" + 
			"                            'base on the @WORK_NOUN of CITATION',\n" + 
			"                            'base on the @WORK_NOUN give in CITATION',\n" + 
			"                            'base on the @WORK_NOUN present in CITATION',\n" + 
			"                            'base on the @WORK_NOUN propose in CITATION',\n" + 
			"                            'base on the @WORK_NOUN discuss in CITATION',\n" + 
			"                            'adopt CITATION',\n" + 
			"                            'start point for @REFERENTIAL @WORK_NOUN',\n" + 
			"                            'start point for @SELF_POSS @WORK_NOUN',\n" + 
			"                            'as a start point',\n" + 
			"                            'as start point',\n" + 
			"                            'use CITATION',\n" + 
			"                            'base @SELF_POSS',\n" + 
			"                            'support @SELF_POSS',\n" + 
			"                            'support @OTHERS_POSS',\n" + 
			"                            'lend support to @SELF_POSS',\n" + 
			"                            'lend support to @OTHERS_POSS',\n" + 
			"                            '@CONTINUE the @WORK_NOUN of',\n" + 
			"                            '@AGREE the @WORK_NOUN of'\n" + 
			"                            ],\n" + 
			"\n" + 
			"    'DISCOURSE_CONTRAST_FORMULAIC': [ 'however',\n" + 
			"                                      'unfortunately', \n" + 
			"                                      'whereas' \n" + 
			"                                      ],\n" + 
			"\n" + 
			"    'GRAPHIC_FORMULAIC': [ '@GRAPHIC_NOUN #CD' ],\n" + 
			"\n" + 
			"    'CONTRAST2_FORMULAIC': [ 'this @WORK_NOUN @CONTRAST',\n" + 
			"                            '@SELF_POSS @WORK_NOUN @CONTRAST',\n" + 
			"                            'this @PRESENTATION_NOUN @CONTRAST',\n" + 
			"                            '@SELF_POSS @PRESENTATION_NOUN @CONTRAST',\n" + 
			"                            'compare to @OTHERS_POSS @WORK_NOUN',\n" + 
			"                            'compare to @OTHERS_POSS @PRESENTATION_NOUN',\n" + 
			"                            '@OTHERS_POSS @WORK_NOUN @CONTRAST',\n" + 
			"                            'that @WORK_NOUN @CONTRAST',\n" + 
			"                            'that @PRESENTATION_NOUN @CONTRAST',\n" + 
			"                            '@OTHERS_POSS @PRESENTATION_NOUN @CONTRAST' ],\n" + 
			"\n" + 
			"    'COMPARISON_FORMULAIC': [ 'in @COMPARISON with',\n" + 
			"                              'in @COMPARISON to',\n" + 
			"                              '@GIVEN #NN @SIMILAR',\n" + 
			"                              '@SELF_POSS #NN',\n" + 
			"                              '@SELF_POSS @PRESENTATION @SIMILAR',\n" + 
			"                              'a @SELF_POSS @PRESENTATION @SIMILAR',\n" + 
			"                              'a @SIMILAR_ADJ @WORK_NOUN is',\n" + 
			"                              'be closely relate to',\n" + 
			"                              'be @SIMILAR_ADJ to',\n" + 
			"                              'along the line of CITATION' ],\n" + 
			"    \n" + 
			"    'CONTRAST_FORMULAIC': [ 'against CITATATION',\n" + 
			"                              'against @SELF_ACC',\n" + 
			"                              'against @SELF_POSS',\n" + 
			"                              'against @OTHERS_ACC',\n" + 
			"                              'against @OTHERS_POSS',\n" + 
			"                              'against @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'against @MANY @WORK_NOUN',\n" + 
			"                              'against @TRADITION_ADJ @WORK_NOUN',\n" + 
			"                              'than CITATATION',\n" + 
			"                              'than @SELF_ACC',\n" + 
			"                              'than @SELF_POSS',\n" + 
			"                              'than @OTHERS_ACC',\n" + 
			"                              'than @OTHERS_POSS',\n" + 
			"                              'than @TRADITION_ADJ @WORK_NOUN',\n" + 
			"                              'than @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'than @MANY @WORK_NOUN',\n" + 
			"                              'point of departure from @SELF_POSS',\n" + 
			"                              'points of departure from @OTHERS_POSS',\n" + 
			"                              'advantage over @OTHERS_ACC',\n" + 
			"                              'advantage over @TRADITION_ADJ',\n" + 
			"                              'advantage over @MANY @WORK_NOUN',\n" + 
			"                              'advantage over @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'advantage over @OTHERS_POSS',\n" + 
			"                              'advantage over CITATATION',\n" + 
			"                              'advantage to @OTHERS_ACC',\n" + 
			"                              'advantage to @OTHERS_POSS',\n" + 
			"                              'advantage to CITATATION',\n" + 
			"                              'advantage to @TRADITION_ADJ',\n" + 
			"                              'advantage to @MANY @WORK_NOUN',\n" + 
			"                              'advantage to @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'benefit over @OTHERS_ACC',\n" + 
			"                              'benefit over @OTHERS_POSS',\n" + 
			"                              'benefit over CITATATION',\n" + 
			"                              'benefit over @TRADITION_ADJ',\n" + 
			"                              'benefit over @MANY @WORK_NOUN',\n" + 
			"                              'benefit over @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'difference to CITATATION',\n" + 
			"                              'difference to @TRADITION_ADJ',\n" + 
			"                              'difference to CITATATION',\n" + 
			"                              'difference to @TRADITION_ADJ',\n" + 
			"                              'difference to @MANY @WORK_NOUN',\n" + 
			"                              'difference to @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'difference to @OTHERS_ACC',\n" + 
			"                              'difference to @OTHERS_POSS',\n" + 
			"                              'difference to @SELF_ACC',\n" + 
			"                              'difference to @SELF_POSS',\n" + 
			"                              'difference between CITATATION',\n" + 
			"                              'difference between @TRADITION_ADJ',\n" + 
			"                              'difference between @MANY @WORK_NOUN',\n" + 
			"                              'difference between @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'difference between @OTHERS_ACC',\n" + 
			"                              'difference between @OTHERS_POSS',\n" + 
			"                              'difference between @SELF_ACC',\n" + 
			"                              'difference between @SELF_POSS',\n" + 
			"                              'contrast with CITATATION',\n" + 
			"                              'contrast with @TRADITION_ADJ',\n" + 
			"                              'contrast with @MANY @WORK_NOUN',\n" + 
			"                              'contrast with @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'contrast with @OTHERS_ACC',\n" + 
			"                              'contrast with @OTHERS_POSS',\n" + 
			"                              'contrast with @SELF_ACC',\n" + 
			"                              'contrast with @SELF_POSS',\n" + 
			"                              'unlike @SELF_ACC',\n" + 
			"                              'unlike @SELF_POSS',\n" + 
			"                              'unlike CITATATION',\n" + 
			"                              'unlike @TRADITION_ADJ',\n" + 
			"                              'unlike @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'unlike @MANY @WORK_NOUN',\n" + 
			"                              'unlike @OTHERS_ACC',\n" + 
			"                              'unlike @OTHERS_POSS',\n" + 
			"                              'in contrast to @SELF_ACC',\n" + 
			"                              'in contrast to @SELF_POSS',\n" + 
			"                              'in contrast to CITATATION',\n" + 
			"                              'in contrast to @TRADITION_ADJ',\n" + 
			"                              'in contrast to @MANY @WORK_NOUN',\n" + 
			"                              'in contrast to @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'in contrast to @OTHERS_ACC',\n" + 
			"                              'in contrast to @OTHERS_POSS',\n" + 
			"                              'as oppose to @SELF_ACC',\n" + 
			"                              'as oppose to @SELF_POSS',\n" + 
			"                              'as oppose to CITATATION',\n" + 
			"                              'as oppose to @TRADITION_ADJ',\n" + 
			"                              'as oppose to @MANY @WORK_NOUN',\n" + 
			"                              'as oppose to @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'as oppose to @OTHERS_ACC',\n" + 
			"                              'as oppose to @OTHERS_POSS',\n" + 
			"                              'contrary to @SELF_ACC',\n" + 
			"                              'contrary to @SELF_POSS',\n" + 
			"                              'contrary to CITATATION',\n" + 
			"                              'contrary to @TRADITION_ADJ',\n" + 
			"                              'contrary to @MANY @WORK_NOUN',\n" + 
			"                              'contrary to @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'contrary to @OTHERS_ACC',\n" + 
			"                              'contrary to @OTHERS_POSS',\n" + 
			"                              'whereas @SELF_ACC',\n" + 
			"                              'whereas @SELF_POSS',\n" + 
			"                              'whereas CITATATION',\n" + 
			"                              'whereas @TRADITION_ADJ',\n" + 
			"                              'whereas @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'whereas @MANY @WORK_NOUN',\n" + 
			"                              'whereas @OTHERS_ACC',\n" + 
			"                              'whereas @OTHERS_POSS',\n" + 
			"                              'compare to @SELF_ACC',\n" + 
			"                              'compare to @SELF_POSS',\n" + 
			"                              'compare to CITATATION',\n" + 
			"                              'compare to @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'compare to @MANY @WORK_NOUN',\n" + 
			"                              'compare to @OTHERS_ACC',\n" + 
			"                              'compare to @OTHERS_POSS',\n" + 
			"                              'in comparison to @SELF_ACC',\n" + 
			"                              'in comparison to @SELF_POSS',\n" + 
			"                              'in comparison to CITATATION',\n" + 
			"                              'in comparison to @TRADITION_ADJ',\n" + 
			"                              'in comparison to @MANY @WORK_NOUN',\n" + 
			"                              'in comparison to @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'in comparison to @OTHERS_ACC',\n" + 
			"                              'in comparison to @OTHERS_POSS',\n" + 
			"                              'while @SELF_NOM',\n" + 
			"                              'while @SELF_POSS',\n" + 
			"                              'while CITATATION',\n" + 
			"                              'while @TRADITION_ADJ',\n" + 
			"                              'while @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                              'while @MANY @WORK_NOUN',\n" + 
			"                              'while @OTHERS_NOM',\n" + 
			"                              'while @OTHERS_POSS',\n" + 
			"                              'this @WORK_NOUN @COMPARISON',\n" + 
			"                              '@SELF_POSS @WORK_NOUN @COMPARISON',\n" + 
			"                              'this @PRESENTATION_NOUN @COMPARISON',\n" + 
			"                              '@SELF_POSS @PRESENTATION_NOUN @COMPARISON',\n" + 
			"                              'compare to @OTHERS_POSS @WORK_NOUN',\n" + 
			"                              'compare to @OTHERS_POSS @PRESENTATION_NOUN',\n" + 
			"                              '@OTHERS_POSS @WORK_NOUN @COMPARISON',\n" + 
			"                              'that @WORK_NOUN @COMPARISON',\n" + 
			"                              'that @PRESENTATION_NOUN @COMPARISON',\n" + 
			"                              '@OTHERS_POSS @PRESENTATION_NOUN @COMPARISON' ],\n" +
			"\n" +
			"    'ALIGN_FORMULAIC': ['in the @SENSE_NOUN of CITATION'],\n" + 
			"\n" + 
			"    'AFFECT_FORMULAIC': [ 'hopefully','thankfully','fortunately','unfortunately' ],\n" + 
			"\n" + 
			"    'GOOD_FORMULAIC': [ '@GOOD_ADJ' ],\n" + 
			"\n" + 
			"    'TRADITION_FORMULAIC': [ '@TRADITION_ADJ' ],\n" + 
			"\n" + 
			"    'IN_ORDER_TO_FORMULAIC': [ 'in order to' ],\n" + 
			"\n" + 
			"    'DETAIL_FORMULAIC': ['@SELF_NOM have also',\n" + 
			"                         '@SELF_NOM also',\n" + 
			"                         'this @PRESENTATION_NOUN also',\n" + 
			"                         'this @PRESENTATION_NOUN has also' ],\n" + 
			"    \n" + 
			"    'NO_TEXTSTRUCTURE_FORMULAIC': [ '( @TEXT_NOUN CREF )',\n" + 
			"                                    'as explain in @TEXT_NOUN CREF',\n" + 
			"                                    'as explain in the @BEFORE_ADJ @TEXT_NOUN',\n" + 
			"                                    'as @GIVEN early in this @TEXT_NOUN',\n" + 
			"                                    'as @GIVEN below',\n" + 
			"                                    'as @GIVEN in @TEXT_NOUN CREF',\n" + 
			"                                    'as @GIVEN in the @BEFORE_ADJ @TEXT_NOUN',\n" + 
			"                                    'as @GIVEN in the next @TEXT_NOUN',\n" + 
			"                                    '#NN @GIVEN in @TEXT_NOUN CREF',\n" + 
			"                                    '#NN @GIVEN in the @BEFORE_ADJ @TEXT_NOUN',\n" + 
			"                                    '#NN @GIVEN in the next @TEXT_NOUN',\n" + 
			"                                    '#NN @GIVEN below',\n" + 
			"                                    'cf. @TEXT_NOUN CREF',\n" + 
			"                                    'cf. @TEXT_NOUN below',\n" + 
			"                                    'cf. the @TEXT_NOUN below',\n" + 
			"                                    'cf. the @BEFORE_ADJ @TEXT_NOUN',\n" + 
			"                                    'cf. @TEXT_NOUN above',\n" + 
			"                                    'cf. the @TEXT_NOUN above',\n" + 
			"                                    'cfXXX @TEXT_NOUN CREF',\n" + 
			"                                    'cfXXX @TEXT_NOUN below',\n" + 
			"                                    'cfXXX the @TEXT_NOUN below',\n" + 
			"                                    'cfXXX the @BEFORE_ADJ @TEXT_NOUN',\n" + 
			"                                    'cfXXX @TEXT_NOUN above',\n" + 
			"                                    'cfXXX the @TEXT_NOUN above',\n" + 
			"                                    'e. g. , @TEXT_NOUN CREF',\n" + 
			"                                    'e. g , @TEXT_NOUN CREF',\n" + 
			"                                    'e. g. @TEXT_NOUN CREF',\n" + 
			"                                    'e. g @TEXT_NOUN CREF',\n" + 
			"                                    'e.g., @TEXT_NOUN CREF',\n" + 
			"                                    'e.g. @TEXT_NOUN CREF',\n" + 
			"                                    'compare @TEXT_NOUN CREF',\n" + 
			"                                    'compare @TEXT_NOUN below',\n" + 
			"                                    'compare the @TEXT_NOUN below',\n" + 
			"                                    'compare the @BEFORE_ADJ @TEXT_NOUN', \n" + 
			"                                    'compare @TEXT_NOUN above',\n" + 
			"                                    'compare the @TEXT_NOUN above',\n" + 
			"                                    'see @TEXT_NOUN CREF',\n" + 
			"                                    'see the @BEFORE_ADJ @TEXT_NOUN',\n" + 
			"                                    'recall from the @BEFORE_ADJ @TEXT_NOUN',\n" + 
			"                                    'recall from the @TEXT_NOUN above',\n" + 
			"                                    'recall from @TEXT_NOUN CREF',\n" + 
			"                                    '@SELF_NOM shall see below',\n" + 
			"                                    '@SELF_NOM will see below',\n" + 
			"                                    '@SELF_NOM shall see in the next @TEXT_NOUN',\n" + 
			"                                    '@SELF_NOM will see in the next @TEXT_NOUN',\n" + 
			"                                    '@SELF_NOM shall see in @TEXT_NOUN CREF',\n" + 
			"                                    '@SELF_NOM will see in @TEXT_NOUN CREF',\n" + 
			"                                    'example in @TEXT_NOUN CREF',\n" + 
			"                                    'example CREF in @TEXT_NOUN CREF',\n" + 
			"                                    'example CREF and CREF in @TEXT_NOUN CREF',\n" + 
			"                                    'example in @TEXT_NOUN CREF' ],\n" + 
			"\n" + 
			"    'USE_FORMULAIC': [ '@SELF_NOM @USE',\n" + 
			"                       '@SELF_NOM @RESEARCH',\n" +
			"                       '@SOLUTION with the @HELP_NOUN of',\n" + 
			"                       '@SOLUTION with the @WORK_NOUN of' ],\n" + 
			"\n" + 
			"    'FUTURE_WORK_FORMULAIC': [ '@FUTURE_ADJ @WORK_NOUN',\n" + 
			"                               '@FUTURE_ADJ @AIM_NOUN',\n" + 
			"                               '@FUTURE_ADJ @CHANGE_NOUN',\n" + 
			"                               'a @HEDGE_ADJ @AIM_NOUN',\n" + 
			"                               'one @HEDGE_ADJ @AIM_NOUN',\n" + 
			"                               '#NN be also @HEDGE_ADJ',\n" + 
			"                               'in the future',\n" + 
			"                               '@SELF_NOM @FUTURE_INTEREST' ],\n" + 
			"\n" + 
			"    'HEDGING_FORMULAIC': [ '@HEDGING_MODALS be @RESEARCH',\n" + 
			"                           '@HEDGING_MODALS be @CHANGE',\n" + 
			"                           '@HEDGING_MODALS be @SOLUTION' ],\n" + 
			"\n" + 
			"    'PRESENT_WORK_FORMULAIC': [ '@SELF_NOM be @CURRENT_ADV @RESEARCH',\n" + 
			"                                '@SELF_NOM be @RESEARCH @CURRENT_ADV' ],\n" + 
			"\n" + 
			"    'EXTENDING_WORK_FORMULAIC': [ '@CHANGE the @WORK_NOUN',\n" + 
			"                                  '@CHANGE this @WORK_NOUN',\n" + 
			"                                  '@SELF_POSS @WORK_NOUN be @CHANGE',\n" + 
			"                                  '@SELF_POSS #JJ @WORK_NOUN be @CHANGE',\n" + 
			"                                  '@SELF_POSS @WORK_NOUN @CHANGE',\n" + 
			"                                  '@SELF_POSS #JJ @WORK_NOUN @CHANGE',\n" + 
			"                                  '@CHANGE the #JJ @WORK_NOUN',\n" + 
			"                                  '@SELF_NOM @CHANGE' ],\n" + 
			"\n" + 
			"    'EXTENDING_WORK2_FORMULAIC': [ '@SELF_NOM @CHANGE #DD @WORK_NOUN',\n" + 
			"                                   '@SELF_POSS @WORK_NOUN @CHANGE',\n" + 
			"                                   '@CHANGE from CITATION',\n" + 
			"                                   '@CHANGE from #NN of CITATION',\n" + 
			"                                   '@SELF_POSS @CHANGE_NOUN of CITATION',\n" + 
			"                                   '@SELF_POSS @WORK_NOUN @CONTINUE',\n" + 
			"                                   '@SELF_POSS @WORK_NOUN be #DD @CHANGE_NOUN',\n" + 
			"                                   '@SELF_POSS @WORK_NOUN be #VV #DD @CHANGE_NOUN',\n" + 
			"                                   '#NN be #DD @CHANGE_NOUN of',\n" + 
			"                                   '#NN be #DD #JJ @CHANGE_NOUN of',\n" + 
			"                                   '#DD #NN @DENOTATION #DD @CHANGE_NOUN of',\n" + 
			"                                   '@TEXT_NOUN @CONTINUE CITATION',\n" + 
			"                                   '#NN @CONTINUE #NN of CITATION',\n" + 
			"                                   'be @SEE as an @CHANGE_NOUN',\n" + 
			"                                   '@CHANGE #DD #NN of CITATION' ],\n" + 
			"\n" + 
			"    'USEFUL_FORMULAIC': [ 'have shown @GOOD_ADJ for' ],\n" + 
			"\n" + 
			"    'MOTIVATING_FORMULAIC': [ 'as @PRESENTATION in CITATION',\n" + 
			"                              'as @PRESENTATION by CITATION',\n" + 
			"                              'this be a #JJ convention',\n" + 
			"                              'this be a #RB #JJ convention',\n" + 
			"                              '@CONTINUE the #NN result',\n" + 
			"                              '@CONTINUE the #JJ result',\n" + 
			"                              '@AGREE the #NN result',\n" + 
			"                              '@AGREE the #JJ result',\n" + 
			"                              '@INSPIRATION by',\n" + 
			"                              'CITATION have @PRESENTATION that',\n" + 
			"                              'have remain a @PROBLEM_NOUN',\n" +  
			"                              'their importance have @INCREASE',\n" + 
			"                              '#NN be @MAIN_ADJ in',\n" + 
			"                              '#NN be @MAIN_ADJ for',\n" + 
			"                              'it be @MAIN_ADJ not to',\n" + 
			"                              'from CITATION , @SELF_NOM',\n" + 
			"                              '@CONTINUE CITATION, @SELF_NOM',\n" + 
			"                              '@AGREE CITATION, @SELF_NOM',\n" + 
			"                              '@RESEARCH in @DISCIPLINE @PRESENTATION',\n" + 
			"                              '@RESEARCH in #NN @PRESENTATION',\n" + 
			"                              '@RESEARCH in #NN #NN @PRESENTATION',\n" + 
			"                              '@RESEARCH in #JJ #NN @PRESENTATION',\n" + 
			"                              'negative @RESULT_NOUN for',\n" + 
			"                              'negative @RESULT_NOUN that',\n" + 
			"                              'it be well document',\n" + 
			"                              'it have be well document',\n" + 
			"                              '#NN need to @USE',\n" + 
			"                              'CITATION have @RESEARCH it',\n" + 
			"                              'CITATION have @PRESENTATION that',\n" + 
			"                              'CITATATION @PRESENTATION that',\n" + 
			"                              'CITATATION #RB @PRESENTATION that',\n" + 
			"                              'prove to be @GOOD_ADJ in',\n" + 
			"                              '@PRESENTATION to be @GOOD_ADJ in',\n" + 
			"                              'prove to be @GOOD_ADJ for',\n" + 
			"                              '@PRESENTATION to be @GOOD_ADJ for' ],\n" + 
			"\n" + 
			"    'PRIOR_WORK_FORMULAIC': [ '@BEFORE_ADJ @PRESENTATION @SELF_NOM',\n" + 
			"                              '@BEFORE_ADJ @PRESENTATION , @SELF_NOM',\n" + 
			"                              'a @BEFORE_ADJ @PRESENTATION @SELF_NOM',\n" + 
			"                              'a @BEFORE_ADJ @PRESENTATION , @SELF_NOM',\n" + 
			"                              '@SELF_POSS @BEFORE_ADJ @PRESENTATION @SELF_NOM',\n" + 
			"                              '@SELF_POSS @BEFORE_ADJ @PRESENTATION , @SELF_NOM',\n" + 
			"                              '@SELF_POSS @BEFORE_ADJ @PRESENTATION CITATION',\n" + 
			"                              '@SELF_POSS @BEFORE_ADJ @PRESENTATION SELFCITATION',\n" + 
			"                              '@BEFORE_ADJ @PRESENTATION CITATION @SELF_NOM',\n" + 
			"                              '@BEFORE_ADJ @PRESENTATION CITATION , @SELF_NOM',\n" + 
			"                              'a @BEFORE_ADJ @PRESENTATION CITATION @SELF_NOM',\n" + 
			"                              'a @BEFORE_ADJ @PRESENTATION CITATION , @SELF_NOM',\n" + 
			"                              'first @PRESENTATION in CITATION',\n" + 
			"                              '@PRESENTATION #RR in CITATION',\n" + 
			"                              '@PRESENTATION #JJ in CITATION',\n" + 
			"                              '@BEFORE_ADJ @CHANGE_NOUN of @SELF_POSS @WORK_NOUN',\n" + 
			"                              '@CHANGE on @BEFORE_ADJ @PRESENTATION @PRESENTATION in SELFCITATION',\n" + 
			"                              '@CHANGE @BEFORE_ADJ @PRESENTATION @PRESENTATION in SELFCITATION',\n" + 
			"                              '@CHANGE @BEFORE_ADJ @PRESENTATION @PRESENTATION in SELFCITATION',\n" + 
			"                              '@CHANGE @BEFORE_ADJ @PRESENTATION @PRESENTATION SELFCITATION',\n" + 
			"                              '@CHANGE on @SELF_POSS @BEFORE_ADJ @PRESENTATION @PRESENTATION in SELFCITATION',\n" + 
			"                              '@CHANGE @SELF_POSS @BEFORE_ADJ @PRESENTATION @PRESENTATION in SELFCITATION',\n" + 
			"                              '@CHANGE @SELF_POSS @BEFORE_ADJ @PRESENTATION @PRESENTATION in SELFCITATION',\n" + 
			"                              '@CHANGE @SELF_POSS @BEFORE_ADJ @PRESENTATION @PRESENTATION SELFCITATION',\n" + 
			"                              'in @SELF_POSS @BEFORE_ADJ @PRESENTATION CITATION' ],";
	
	static String AGENT_PATTERN = "'US_AGENT': [ '@SELF_NOM',\n" + 
			"                  '@SELF_POSS #JJ @WORK_NOUN',\n" + 
			"                  '@SELF_POSS #JJ @PRESENTATION_NOUN',\n" + 
			"                  '@SELF_POSS #JJ @ARGUMENTATION_NOUN',\n" + 
			"                  '@SELF_POSS #JJ @SOLUTION_NOUN',\n" + 
			"                  '@SELF_POSS #JJ @RESULT_NOUN',\n" + 
			"                  'the first author',\n" + 
			"                  '@SELF_POSS @PRESENTATION_NOUN',\n" + 
			"                  '@SELF_POSS @ARGUMENTATION_NOUN',\n" + 
			"                  '@SELF_POSS @SOLUTION_NOUN',\n" + 
			"                  'SELF_POSS @RESULT_NOUN',\n" + 
			"                  '@WORK_NOUN @GIVEN here',\n" + 
			"                  'WORK_NOUN @GIVEN below',\n" + 
			"                  '@WORK_NOUN @GIVEN in this @PRESENTATION_NOUN',\n" + 
			"                  '@WORK_NOUN @GIVEN in @SELF_POSS @PRESENTATION_NOUN',\n" + 
			"                  'the @SOLUTION_NOUN @GIVEN here',\n" + 
			"                  'the @SOLUTION_NOUN @GIVEN in this @PRESENTATION_NOUN',\n" + 
			"                  'the first author',\n" + 
			"                  'the second author',\n" + 
			"                  'the third author',\n" + 
			"                  'one of the authors',\n" + 
			"                  'one of us' ],\n" + 
			"\n" + 
			"    'REF_US_AGENT': [ 'this @PRESENTATION_NOUN',\n" + 
			"                      'the present @PRESENTATION_NOUN',\n" + 
			"                      'the current @PRESENTATION_NOUN',\n" + 
			"                      'the present #JJ @PRESENTATION_NOUN',\n" + 
			"                      'the current #JJ @PRESENTATION_NOUN',\n" + 
			"                      'the @WORK_NOUN @GIVEN' ],\n" + 
			"\n" + 
			"    'OUR_AIM_AGENT': [ '@SELF_POSS @AIM_NOUN',\n" + 
			"                       'the point of this @PRESENTATION_NOUN',\n" + 
			"                       'the @AIM_NOUN of this @PRESENTATION_NOUN',\n" + 
			"                       'the @AIM_NOUN of the @GIVEN @WORK_NOUN',\n" + 
			"                       'the @AIM_NOUN of @SELF_POSS @WORK_NOUN',\n" + 
			"                       'the @AIM_NOUN of @SELF_POSS @PRESENTATION_NOUN',\n" + 
			"                       'the most @MAIN_ADJ feature of @SELF_POSS @WORK_NOUN',\n" + 
			"                       'contribution of this @PRESENTATION_NOUN',\n" + 
			"                       'contribution of the @GIVEN @WORK_NOUN',\n" + 
			"                       'contribution of @SELF_POSS @WORK_NOUN',\n" + 
			"                       'the question @GIVEN in this PRESENTATION_NOUN',\n" + 
			"                       'the question @GIVEN here',\n" + 
			"                       '@SELF_POSS @MAIN_ADJ @AIM_NOUN',\n" + 
			"                       '@SELF_POSS @AIM_NOUN in this @PRESENTATION_NOUN',\n" + 
			"                       '@SELF_POSS @AIM_NOUN here',\n" + 
			"                       'the #JJ point of this @PRESENTATION_NOUN',\n" + 
			"                       'the #JJ purpose of this @PRESENTATION_NOUN',\n" + 
			"                       'the #JJ @AIM_NOUN of this @PRESENTATION_NOUN',\n" + 
			"                       'the #JJ @AIM_NOUN of the @GIVEN @WORK_NOUN',\n" + 
			"                       'the #JJ @AIM_NOUN of @SELF_POSS @WORK_NOUN',\n" + 
			"                       'the #JJ @AIM_NOUN of @SELF_POSS @PRESENTATION_NOUN',\n" + 
			"                       'the #JJ question @GIVEN in this PRESENTATION_NOUN',\n" + 
			"                       'the #JJ question @GIVEN here' ],\n" + 
			"\n" + 
			"    'AIM_REF_AGENT':  [ 'its @AIM_NOUN',\n" + 
			"                        'its #JJ @AIM_NOUN',\n" + 
			"                        '@REFERENTIAL #JJ @AIM_NOUN',\n" + 
			"                        'contribution of this @WORK_NOUN',\n" + 
			"                        'the most important feature of this @WORK_NOUN',\n" + 
			"                        'feature of this @WORK_NOUN',\n" + 
			"                        'the @AIM_NOUN',\n" + 
			"                        'the #JJ @AIM_NOUN' ],\n" + 
			"                        \n" + 
			"    'US_PREVIOUS_AGENT': [ 'SELFCITATION',\n" + 
			"                           'this @BEFORE_ADJ @PRESENTATION_NOUN',\n" + 
			"                           '@SELF_POSS @BEFORE_ADJ @PRESENTATION_NOUN',\n" + 
			"                           '@SELF_POSS @BEFORE_ADJ @WORK_NOUN',\n" + 
			"                           'in CITATION , @SELF_NOM',\n" + 
			"                           'in CITATION @SELF_NOM',\n" + 
			"                           'the @WORK_NOUN @GIVEN in SELFCITATION',\n" + 
			"                           'in @BEFORE_ADJ @PRESENTATION CITATION @SELF_NOM',\n" + 
			"                           'in @BEFORE_ADJ @PRESENTATION CITATION , @SELF_NOM',\n" + 
			"                           'in a @BEFORE_ADJ @PRESENTATION CITATION @SELF_NOM',\n" + 
			"                           'in a @BEFORE_ADJ @PRESENTATION CITATION , @SELF_NOM',\n" + 
			"                           ],\n" + 
			"\n" + 
			"    'REF_AGENT': [ '@REFERENTIAL #JJ @WORK_NOUN',\n" + 
			"                   '@REFERENTIAL @WORK_NOUN',\n" + 
			"                   'this sort of @WORK_NOUN',\n" + 
			"                   'this kind of @WORK_NOUN',\n" + 
			"                   'this type of @WORK_NOUN',\n" + 
			"                   'the current #JJ @WORK_NOUN',\n" + 
			"                   'the current @WORK_NOUN',\n" + 
			"                   'the @WORK_NOUN',\n" + 
			"                   'the @PRESENTATION_NOUN',\n" + 
			"                   'the author',\n" + 
			"                   'the authors' ],\n" + 
			"\n" + 
			"    'THEM_PRONOUN_AGENT': [ '@OTHERS_NOM' ],    \n" + 
			"\n" + 
			"    'THEM_ACTIVE_AGENT' : [ 'CITATION @PRESENTATION' ],\n" + 
			"\n" + 
			"    'THEM_AGENT': [ 'CITATION',\n" + 
			"                    'CITATION \\'s #NN',\n" + 
			"                    'CITATION \\'s @PRESENTATION_NOUN',\n" + 
			"                    'CITATION \\'s @WORK_NOUN',\n" + 
			"                    'CITATION \\'s @ARGUMENTATION_NOUN',\n" + 
			"                    'CITATION \\'s #JJ @PRESENTATION_NOUN',\n" + 
			"                    'CITATION \\'s #JJ @WORK_NOUN',\n" + 
			"                    'CITATION \\'s #JJ @ARGUMENTATION_NOUN',\n" + 
			"                    'the CITATION @WORK_NOUN',\n" + 
			"                    'the @WORK_NOUN @GIVEN in CITATION',\n" + 
			"                    'the @WORK_NOUN of CITATION',\n" + 
			"                    '@OTHERS_POSS @PRESENTATION_NOUN',\n" + 
			"                    '@OTHERS_POSS @WORK_NOUN',\n" + 
			"                    '@OTHERS_POSS @RESULT_NOUN',\n" + 
			"                    '@OTHERS_POSS @ARGUMENTATION_NOUN',\n" + 
			"                    '@OTHERS_POSS @SOLUTION_NOUN',\n" + 
			"                    '@OTHERS_POSS #JJ @PRESENTATION_NOUN',\n" + 
			"                    '@OTHERS_POSS #JJ @WORK_NOUN',\n" + 
			"                    '@OTHERS_POSS #JJ @RESULT_NOUN',\n" + 
			"                    '@OTHERS_POSS #JJ @ARGUMENTATION_NOUN',\n" + 
			"                    '@OTHERS_POSS #JJ @SOLUTION_NOUN' ],\n" + 
			"\n" + 
			"    'GAP_AGENT':  [ 'none of these @WORK_NOUN',\n" + 
			"                    'none of those @WORK_NOUN',\n" + 
			"                    'no @WORK_NOUN',\n" + 
			"                    'no #JJ @WORK_NOUN',\n" + 
			"                    'none of these @PRESENTATION_NOUN',\n" + 
			"                    'none of those @PRESENTATION_NOUN',\n" + 
			"                    'no @PRESENTATION_NOUN',\n" + 
			"                    'no #JJ @PRESENTATION_NOUN' ],\n" + 
			"\n" + 
			"    'GENERAL_AGENT': [ '@TRADITION_ADJ #JJ @WORK_NOUN',\n" + 
			"                       '@TRADITION_ADJ use @WORK_NOUN',\n" + 
			"                       '@TRADITION_ADJ @WORK_NOUN',\n" + 
			"                       '@MANY #JJ @WORK_NOUN',\n" + 
			"                       '@MANY @WORK_NOUN',\n" + 
			"                       '@BEFORE_ADJ #JJ @WORK_NOUN',\n" + 
			"                       '@BEFORE_ADJ @WORK_NOUN',\n" + 
			"                       '@BEFORE_ADJ #JJ @PRESENTATION_NOUN',\n" + 
			"                       '@BEFORE_ADJ @PRESENTATION_NOUN',\n" + 
			"                       'other #JJ @WORK_NOUN',\n" + 
			"                       'other @WORK_NOUN',\n" + 
			"                       'such @WORK_NOUN',\n" + 
			"                       'these #JJ @PRESENTATION_NOUN',\n" + 
			"                       'these @PRESENTATION_NOUN',\n" + 
			"                       'those #JJ @PRESENTATION_NOUN',\n" + 
			"                       'those @PRESENTATION_NOUN',\n" + 
			"                       '@REFERENTIAL authors',\n" + 
			"                       '@MANY author',\n" + 
			"                       'researcher in @DISCIPLINE',\n" + 
			"                       '@PROFESSIONALS' ],\n" + 
			"\n" + 
			"    'PROBLEM_AGENT': [ '@REFERENTIAL #JJ @PROBLEM_NOUN',\n" + 
			"                       '@REFERENTIAL @PROBLEM_NOUN',\n" + 
			"                       'the @PROBLEM_NOUN' ],\n" + 
			"\n" + 
			"    'SOLUTION_AGENT': [ '@REFERENTIAL #JJ @SOLUTION_NOUN',\n" + 
			"                       '@REFERENTIAL @SOLUTION_NOUN',\n" + 
			"                       'the @SOLUTION_NOUN',\n" + 
			"                       'the #JJ @SOLUTION_NOUN' ],\n" + 
			"\n" + 
			"    'TEXTSTRUCTURE_AGENT': [ '@TEXT_NOUN CREF',\n" + 
			"                             '@TEXT_NOUN CREF and CREF',\n" + 
			"                             'this @TEXT_NOUN',\n" + 
			"                             'next @TEXT_NOUN',\n" + 
			"                             'next #CD @TEXT_NOUN',\n" + 
			"                             'concluding @TEXT_NOUN',\n" + 
			"                             '@BEFORE_ADJ @TEXT_NOUN',\n" + 
			"                             '@TEXT_NOUN above',\n" + 
			"                             '@TEXT_NOUN below',\n" + 
			"                             'following @TEXT_NOUN',\n" + 
			"                             'remaining @TEXT_NOUN',\n" + 
			"                             'subsequent @TEXT_NOUN',\n" + 
			"                             'following #CD @TEXT_NOUN',\n" + 
			"                             'remaining #CD @TEXT_NOUN',\n" + 
			"                             'subsequent #CD @TEXT_NOUN',\n" + 
			"                             '@TEXT_NOUN that follow',\n" + 
			"                             'rest of this @PRESENTATION_NOUN',\n" + 
			"                             'remainder of this @PRESENTATION_NOUN',\n" + 
			"                             'in @TEXT_NOUN CREF , @SELF_NOM',\n" + 
			"                             'in this @TEXT_NOUN , @SELF_NOM',\n" + 
			"                             'in the next @TEXT_NOUN , @SELF_NOM',\n" + 
			"                             'in @BEFORE_ADJ @TEXT_NOUN , @SELF_NOM',\n" + 
			"                             'in the @BEFORE_ADJ @TEXT_NOUN , @SELF_NOM',\n" + 
			"                             'in the @TEXT_NOUN above , @SELF_NOM',\n" + 
			"                             'in the @TEXT_NOUN below , @SELF_NOM',\n" + 
			"                             'in the following @TEXT_NOUN , @SELF_NOM',\n" + 
			"                             'in the remaining @TEXT_NOUN , @SELF_NOM',\n" + 
			"                             'in the subsequent @TEXT_NOUN , @SELF_NOM',\n" + 
			"                             'in the @TEXT_NOUN that follow , @SELF_NOM',\n" + 
			"                             'in the rest of this @PRESENTATION_NOUN , @SELF_NOM',\n" + 
			"                             'in the remainder of this @PRESENTATION_NOUN , @SELF_NOM',\n" + 
			"                             'below , @SELF_NOM',\n" + 
			"                             'the @AIM_NOUN of this @TEXT_NOUN' ],";
	
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
			"    # Not sure if this one is used\n" + 
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
	
	static Properties props = PropertiesUtils.asProperties(
            "annotators", "tokenize,ssplit,pos,lemma,depparse",
            "depparse.model", DependencyParser.DEFAULT_MODEL
    );
    static AnnotationPipeline pipeline = new StanfordCoreNLP(props);
	public static void main(String[] args) {
//		System.out.println(text3);
//		Sentence sentence = process_sent(text3);
//		Map<String, Integer> a = get_formulaic_features(sentence);
//		Map<String, Integer> aa = get_agent_features(sentence);
//		for(Entry<String, Integer> b:aa.entrySet()) {
//			String pattern = b.getKey()+"\t"+b.getValue();
//			System.out.println(pattern);
//		}
//		for(Entry<String, Integer> b:a.entrySet()) {
//			String pattern = b.getKey()+"\t"+b.getValue();
//			System.out.println(pattern);
//		}
	
		
		String temp = loadFileToString("/Users/bank21235/Desktop/CitationandID.txt");
		File outfile = new File("/Users/bank21235/Desktop/OMG.txt");
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
			Map<String, Integer> a = get_agent_features(sentence);
			Map<String, Integer> aa = get_formulaic_features(sentence);
			try {
				System.out.println("Process....."+i+"/"+raw.length);
				String pattern = "";
				for(Entry<String, Integer> b:a.entrySet()) {
					pattern += b.getValue()+" ";
					
//					FileUtils.write(outfile,raw[i].split("\t")[0]+"\t"+pattern+"\n", "UTF-8",true);
					
				}
				System.out.println(pattern);
				String pattern2="";
				for(Entry<String, Integer> b:aa.entrySet()) {
					pattern2 += b.getValue()+" ";
					
//					FileUtils.write(outfile,raw[i].split("\t")[0]+"\t"+pattern+"\n", "UTF-8",true);
					
				}
				System.out.println(pattern2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
//		Map<String, Integer> a = get_agent_features(process_sent(text3));
//		
//		for(Entry<String, Integer> b:a.entrySet()) {
//			System.out.println("Pattern: "+b.getKey()+" Value: "+b.getValue());
//		}
//		
//		Map<String,Set<String>> a = get_feture_list_FORMULAIC_PATTERNS();
//		for(Entry<String, Set<String>> b:a.entrySet()) {
//			System.out.println("Pattern: "+b.getKey());
//			for(String c:b.getValue())System.out.println(c);
//			
//		}
//		
		
	
		
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
	
	public static Map<String,Set<String>> get_feture_list_AGENT_PATTERN(){
		
		Map<String,Set<String>> features = new TreeMap<String,Set<String>>();
		String feature_text = AGENT_PATTERN.replaceAll("[ ]{2,}","");
		feature_text = feature_text.replaceAll("\\\\", "");
		String[] features_array = feature_text.split("\n\n");
		for(String feture:features_array) {
			String[] foo = feture.split(":");
			
			String lexKey = foo[0].trim().replace("\'", "");
	    	foo[1] = foo[1].replaceAll("\n","");
	    	
	    	String [] words = foo[1].split("','");
	    	words[0] = words[0].replaceAll("\\[", "");
	    	words[0] = words[0].trim();
	    	if(words[0].charAt(0)=='\'') words[0] = words[0].substring(1, words[0].length());
	    	
	    	words[words.length-1] = words[words.length-1].replaceAll("\\],", "");
	    	words[words.length-1] = words[words.length-1].trim();
	    	
	    	if(words[words.length-1].charAt(words[words.length-1].length()-1)=='\'')words[words.length-1] = words[words.length-1].substring(0, words[words.length-1].length()-1);
	    	
	    	
	    	Set<String> wordlist = new TreeSet<String>(Arrays.asList(words));
	    	features.put(lexKey, wordlist);
//	    	for(String a : wordlist)System.out.println(a);
	    	
		}
	    return features;
	}
	
	public static Map<String,Set<String>> get_feture_list_FORMULAIC_PATTERNS(){
		
		Map<String,Set<String>> features = new TreeMap<String,Set<String>>();
		String feature_text = FORMULAIC_PATTERNS.replaceAll("[ ]{2,}","");
		feature_text = feature_text.replaceAll("\\\\", "");
		String[] features_array = feature_text.split("\n\n");
		for(String feture:features_array) {
			String[] foo = feture.split(":");
			
			String lexKey = foo[0].trim().replace("\'", "");
	    	foo[1] = foo[1].replaceAll("\n","");
	    	
	    	String [] words = foo[1].split("','");
	    	words[0] = words[0].replaceAll("\\[", "");
	    	words[0] = words[0].trim();
	    	if(words[0].charAt(0)=='\'') words[0] = words[0].substring(1, words[0].length());
	    	
	    	words[words.length-1] = words[words.length-1].replaceAll("\\],", "");
	    	words[words.length-1] = words[words.length-1].trim();
	    	
	    	if(words[words.length-1].charAt(words[words.length-1].length()-1)=='\'')words[words.length-1] = words[words.length-1].substring(0, words[words.length-1].length()-1);
	    	
	    	
	    	Set<String> wordlist = new TreeSet<String>(Arrays.asList(words));
	    	features.put(lexKey, wordlist);
//	    	for(String a : wordlist)System.out.println(a);
	    	
		}
	    return features;
	}

	public static Map<String,Integer> get_agent_features(Sentence processed_sent) {
		
		Map<String,Set<String>> features = get_feture_list_AGENT_PATTERN();
		Map<String,Integer> output = new TreeMap<String,Integer>();
		

		for(Map.Entry<String, Set<String>> feature: features.entrySet()) {
			for(String patern:feature.getValue()) {
				int pat_index = find(patern,processed_sent,true);

				if(pat_index >= 0) {
					
					int offset;
					if(processed_sent.Citation < pat_index) {
						offset = pat_index - processed_sent.Citation;
						
					}else {
						int len = patern.split(" ").length;
						offset = (pat_index)- processed_sent.Citation;
						
					}
					offset = Math.abs(offset);
					output.put("Agent_Features"+"\t"+feature.getKey()+"\t"+patern, offset);
				}else {
					output.put("Formulaic_Features"+"\t"+feature.getKey()+"\t"+patern, -1);
				}
			}
			
		}
		return output;
	}
	
	public static Map<String,Integer> get_formulaic_features(Sentence processed_sent){
		Map<String,Set<String>> features = get_feture_list_AGENT_PATTERN();
		Map<String,Set<String>> features2 = get_feture_list_FORMULAIC_PATTERNS();
		Map<String,Integer> output = new TreeMap<String,Integer>();
		
		for(Map.Entry<String, Set<String>> feature: features2.entrySet()) {
			for(String patern:feature.getValue()) {
				int pat_index = find(patern,processed_sent,false);

				if(pat_index >= 0) {
					
					int offset;
					if(processed_sent.Citation < pat_index) {
						offset = pat_index - processed_sent.Citation;
						
					}else {
						int len = patern.split(" ").length;
						offset = (pat_index)- processed_sent.Citation;
						
					}
					offset = Math.abs(offset);
					output.put("Formulaic_Features"+"\t"+feature.getKey()+"\t"+patern, offset);
				}else {
					output.put("Formulaic_Features"+"\t"+feature.getKey()+"\t"+patern, -1);
				}
			}
			
		}
		
		for(Map.Entry<String, Set<String>> feature: features.entrySet()) {
			for(String patern:feature.getValue()) {
				int pat_index = find(patern,processed_sent,false);

				if(pat_index >= 0) {
					
					int offset;
					if(processed_sent.Citation < pat_index) {
						offset = pat_index - processed_sent.Citation;
						
					}else {
						int len = patern.split(" ").length;
						//System.out.println("pattern: "+pat_index+"_"+len+"_"+processed_sent.Citation);
						offset = (pat_index)- processed_sent.Citation;
						
					}
					offset = Math.abs(offset);
					output.put("Formulaic_Features"+"\t"+feature.getKey()+"\t"+patern, offset);
				}else {
					output.put("Formulaic_Features"+"\t"+feature.getKey()+"\t"+patern, -1);
				}
			}
			
		}
		return output;
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
//	            	System.out.println(word);
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
