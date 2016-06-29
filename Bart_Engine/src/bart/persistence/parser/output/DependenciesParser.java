// $ANTLR 3.5.1 /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g 2016-02-24 12:11:48

package bart.persistence.parser.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import bart.model.dependency.*;
import speedy.model.expressions.Expression;
import bart.persistence.parser.operators.ParseDependencies;
import bart.BartConstants;
import bart.utility.BartUtility;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


@SuppressWarnings("all")
public class DependenciesParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "DIGIT", "EXPRESSION", "IDENTIFIER", 
		"LETTER", "LINE_COMMENT", "NULL", "NUMBER", "OPERATOR", "STRING", "WHITESPACE", 
		"'#fail'", "'('", "')'", "','", "'->'", "'.'", "':'", "'DCs:'", "'\\$'", 
		"'and not exists'"
	};
	public static final int EOF=-1;
	public static final int T__14=14;
	public static final int T__15=15;
	public static final int T__16=16;
	public static final int T__17=17;
	public static final int T__18=18;
	public static final int T__19=19;
	public static final int T__20=20;
	public static final int T__21=21;
	public static final int T__22=22;
	public static final int T__23=23;
	public static final int DIGIT=4;
	public static final int EXPRESSION=5;
	public static final int IDENTIFIER=6;
	public static final int LETTER=7;
	public static final int LINE_COMMENT=8;
	public static final int NULL=9;
	public static final int NUMBER=10;
	public static final int OPERATOR=11;
	public static final int STRING=12;
	public static final int WHITESPACE=13;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public DependenciesParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public DependenciesParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return DependenciesParser.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g"; }


	private static Logger logger = LoggerFactory.getLogger(DependenciesParser.class);

	private ParseDependencies generator = new ParseDependencies();

	private Stack<IFormula> formulaStack = new Stack<IFormula>();

	private Dependency dependency;
	private IFormula formulaWN;
	private PositiveFormula positiveFormula;
	private IFormulaAtom atom;
	private FormulaAttribute attribute;
	private StringBuilder expressionString;
	private String leftVariableId;
	private String rightVariableId;
	private String leftConstant;
	private String rightConstant;
	private int counter;

	public void setGenerator(ParseDependencies generator) {
	      this.generator = generator;
	}



	public static class prog_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "prog"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:56:1: prog : dependencies ;
	public final DependenciesParser.prog_return prog() throws RecognitionException {
		DependenciesParser.prog_return retval = new DependenciesParser.prog_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependencies1 =null;


		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:56:5: ( dependencies )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:56:7: dependencies
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_dependencies_in_prog54);
			dependencies1=dependencies();
			state._fsp--;

			adaptor.addChild(root_0, dependencies1.getTree());

			 if (logger.isDebugEnabled()) logger.debug((dependencies1!=null?((CommonTree)dependencies1.getTree()):null).toStringTree()); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "prog"


	public static class dependencies_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "dependencies"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:58:1: dependencies : ( 'DCs:' ( dc )+ )? ;
	public final DependenciesParser.dependencies_return dependencies() throws RecognitionException {
		DependenciesParser.dependencies_return retval = new DependenciesParser.dependencies_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token string_literal2=null;
		ParserRuleReturnScope dc3 =null;

		CommonTree string_literal2_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:58:13: ( ( 'DCs:' ( dc )+ )? )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:59:11: ( 'DCs:' ( dc )+ )?
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:59:11: ( 'DCs:' ( dc )+ )?
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==21) ) {
				alt2=1;
			}
			switch (alt2) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:59:12: 'DCs:' ( dc )+
					{
					string_literal2=(Token)match(input,21,FOLLOW_21_in_dependencies80); 
					string_literal2_tree = (CommonTree)adaptor.create(string_literal2);
					adaptor.addChild(root_0, string_literal2_tree);

					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:59:19: ( dc )+
					int cnt1=0;
					loop1:
					while (true) {
						int alt1=2;
						int LA1_0 = input.LA(1);
						if ( (LA1_0==IDENTIFIER) ) {
							alt1=1;
						}

						switch (alt1) {
						case 1 :
							// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:59:19: dc
							{
							pushFollow(FOLLOW_dc_in_dependencies82);
							dc3=dc();
							state._fsp--;

							adaptor.addChild(root_0, dc3.getTree());

							}
							break;

						default :
							if ( cnt1 >= 1 ) break loop1;
							EarlyExitException eee = new EarlyExitException(1, input);
							throw eee;
						}
						cnt1++;
					}

					 counter = 0;
					}
					break;

			}

			 generator.processDependencies(); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "dependencies"


	public static class dc_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "dc"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:62:1: dc : dependency ;
	public final DependenciesParser.dc_return dc() throws RecognitionException {
		DependenciesParser.dc_return retval = new DependenciesParser.dc_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope dependency4 =null;


		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:62:3: ( dependency )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:62:8: dependency
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_dependency_in_dc103);
			dependency4=dependency();
			state._fsp--;

			adaptor.addChild(root_0, dependency4.getTree());

			  dependency.setType(BartConstants.DC); dependency.setId("d" + counter++); generator.addDC(dependency); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "dc"


	public static class dependency_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "dependency"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:64:1: dependency : (id= IDENTIFIER ':' )? positiveFormula ( negatedFormula )* '->' ( '#fail' | conclusionFormula ) '.' ;
	public final DependenciesParser.dependency_return dependency() throws RecognitionException {
		DependenciesParser.dependency_return retval = new DependenciesParser.dependency_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token id=null;
		Token char_literal5=null;
		Token string_literal8=null;
		Token string_literal9=null;
		Token char_literal11=null;
		ParserRuleReturnScope positiveFormula6 =null;
		ParserRuleReturnScope negatedFormula7 =null;
		ParserRuleReturnScope conclusionFormula10 =null;

		CommonTree id_tree=null;
		CommonTree char_literal5_tree=null;
		CommonTree string_literal8_tree=null;
		CommonTree string_literal9_tree=null;
		CommonTree char_literal11_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:64:11: ( (id= IDENTIFIER ':' )? positiveFormula ( negatedFormula )* '->' ( '#fail' | conclusionFormula ) '.' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:64:14: (id= IDENTIFIER ':' )? positiveFormula ( negatedFormula )* '->' ( '#fail' | conclusionFormula ) '.'
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:64:14: (id= IDENTIFIER ':' )?
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0==IDENTIFIER) ) {
				int LA3_1 = input.LA(2);
				if ( (LA3_1==20) ) {
					alt3=1;
				}
			}
			switch (alt3) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:64:15: id= IDENTIFIER ':'
					{
					id=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_dependency119); 
					id_tree = (CommonTree)adaptor.create(id);
					adaptor.addChild(root_0, id_tree);

					char_literal5=(Token)match(input,20,FOLLOW_20_in_dependency120); 
					char_literal5_tree = (CommonTree)adaptor.create(char_literal5);
					adaptor.addChild(root_0, char_literal5_tree);

					}
					break;

			}

			  dependency = new Dependency(); 
			                    formulaWN = new FormulaWithNegations(); 
			                    formulaStack.push(formulaWN);
			                    dependency.setPremise(formulaWN);
			                    if(id!=null) dependency.setId(id.getText()); 
			pushFollow(FOLLOW_positiveFormula_in_dependency129);
			positiveFormula6=positiveFormula();
			state._fsp--;

			adaptor.addChild(root_0, positiveFormula6.getTree());

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:69:21: ( negatedFormula )*
			loop4:
			while (true) {
				int alt4=2;
				int LA4_0 = input.LA(1);
				if ( (LA4_0==23) ) {
					alt4=1;
				}

				switch (alt4) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:69:23: negatedFormula
					{
					pushFollow(FOLLOW_negatedFormula_in_dependency134);
					negatedFormula7=negatedFormula();
					state._fsp--;

					adaptor.addChild(root_0, negatedFormula7.getTree());

					}
					break;

				default :
					break loop4;
				}
			}

			string_literal8=(Token)match(input,18,FOLLOW_18_in_dependency141); 
			string_literal8_tree = (CommonTree)adaptor.create(string_literal8);
			adaptor.addChild(root_0, string_literal8_tree);

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:70:4: ( '#fail' | conclusionFormula )
			int alt5=2;
			int LA5_0 = input.LA(1);
			if ( (LA5_0==14) ) {
				alt5=1;
			}
			else if ( ((LA5_0 >= EXPRESSION && LA5_0 <= IDENTIFIER)||LA5_0==NUMBER||LA5_0==STRING||LA5_0==22) ) {
				alt5=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 5, 0, input);
				throw nvae;
			}

			switch (alt5) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:70:5: '#fail'
					{
					string_literal9=(Token)match(input,14,FOLLOW_14_in_dependency148); 
					string_literal9_tree = (CommonTree)adaptor.create(string_literal9);
					adaptor.addChild(root_0, string_literal9_tree);

					  formulaStack.clear(); 
					                    dependency.setConclusion(NullFormula.getInstance());
					}
					break;
				case 2 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:74:4: conclusionFormula
					{
					  formulaStack.clear(); 
					pushFollow(FOLLOW_conclusionFormula_in_dependency185);
					conclusionFormula10=conclusionFormula();
					state._fsp--;

					adaptor.addChild(root_0, conclusionFormula10.getTree());

					}
					break;

			}

			char_literal11=(Token)match(input,19,FOLLOW_19_in_dependency188); 
			char_literal11_tree = (CommonTree)adaptor.create(char_literal11);
			adaptor.addChild(root_0, char_literal11_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "dependency"


	public static class positiveFormula_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "positiveFormula"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:77:1: positiveFormula : relationalAtom ( ',' atom )* ;
	public final DependenciesParser.positiveFormula_return positiveFormula() throws RecognitionException {
		DependenciesParser.positiveFormula_return retval = new DependenciesParser.positiveFormula_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token char_literal13=null;
		ParserRuleReturnScope relationalAtom12 =null;
		ParserRuleReturnScope atom14 =null;

		CommonTree char_literal13_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:77:16: ( relationalAtom ( ',' atom )* )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:77:18: relationalAtom ( ',' atom )*
			{
			root_0 = (CommonTree)adaptor.nil();


			  positiveFormula = new PositiveFormula(); 
			                    positiveFormula.setFather(formulaStack.peek()); 
			                    formulaStack.peek().setPositiveFormula(positiveFormula); 
			pushFollow(FOLLOW_relationalAtom_in_positiveFormula242);
			relationalAtom12=relationalAtom();
			state._fsp--;

			adaptor.addChild(root_0, relationalAtom12.getTree());

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:80:34: ( ',' atom )*
			loop6:
			while (true) {
				int alt6=2;
				int LA6_0 = input.LA(1);
				if ( (LA6_0==17) ) {
					alt6=1;
				}

				switch (alt6) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:80:35: ',' atom
					{
					char_literal13=(Token)match(input,17,FOLLOW_17_in_positiveFormula245); 
					char_literal13_tree = (CommonTree)adaptor.create(char_literal13);
					adaptor.addChild(root_0, char_literal13_tree);

					pushFollow(FOLLOW_atom_in_positiveFormula247);
					atom14=atom();
					state._fsp--;

					adaptor.addChild(root_0, atom14.getTree());

					}
					break;

				default :
					break loop6;
				}
			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "positiveFormula"


	public static class negatedFormula_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "negatedFormula"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:82:1: negatedFormula : 'and not exists' '(' ( positiveFormula ( negatedFormula )* ) ')' ;
	public final DependenciesParser.negatedFormula_return negatedFormula() throws RecognitionException {
		DependenciesParser.negatedFormula_return retval = new DependenciesParser.negatedFormula_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token string_literal15=null;
		Token char_literal16=null;
		Token char_literal19=null;
		ParserRuleReturnScope positiveFormula17 =null;
		ParserRuleReturnScope negatedFormula18 =null;

		CommonTree string_literal15_tree=null;
		CommonTree char_literal16_tree=null;
		CommonTree char_literal19_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:82:15: ( 'and not exists' '(' ( positiveFormula ( negatedFormula )* ) ')' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:82:18: 'and not exists' '(' ( positiveFormula ( negatedFormula )* ) ')'
			{
			root_0 = (CommonTree)adaptor.nil();


			  formulaWN = new FormulaWithNegations(); 
					    formulaWN.setFather(formulaStack.peek());
					    formulaStack.peek().addNegatedFormula(formulaWN);
			                    formulaStack.push(formulaWN); 
			string_literal15=(Token)match(input,23,FOLLOW_23_in_negatedFormula278); 
			string_literal15_tree = (CommonTree)adaptor.create(string_literal15);
			adaptor.addChild(root_0, string_literal15_tree);

			char_literal16=(Token)match(input,15,FOLLOW_15_in_negatedFormula279); 
			char_literal16_tree = (CommonTree)adaptor.create(char_literal16);
			adaptor.addChild(root_0, char_literal16_tree);

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:86:38: ( positiveFormula ( negatedFormula )* )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:86:40: positiveFormula ( negatedFormula )*
			{
			pushFollow(FOLLOW_positiveFormula_in_negatedFormula283);
			positiveFormula17=positiveFormula();
			state._fsp--;

			adaptor.addChild(root_0, positiveFormula17.getTree());

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:86:56: ( negatedFormula )*
			loop7:
			while (true) {
				int alt7=2;
				int LA7_0 = input.LA(1);
				if ( (LA7_0==23) ) {
					alt7=1;
				}

				switch (alt7) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:86:58: negatedFormula
					{
					pushFollow(FOLLOW_negatedFormula_in_negatedFormula287);
					negatedFormula18=negatedFormula();
					state._fsp--;

					adaptor.addChild(root_0, negatedFormula18.getTree());

					}
					break;

				default :
					break loop7;
				}
			}

			}

			char_literal19=(Token)match(input,16,FOLLOW_16_in_negatedFormula294); 
			char_literal19_tree = (CommonTree)adaptor.create(char_literal19);
			adaptor.addChild(root_0, char_literal19_tree);

			  formulaStack.pop(); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "negatedFormula"


	public static class conclusionFormula_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "conclusionFormula"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:89:1: conclusionFormula : atom ( ',' atom )* ;
	public final DependenciesParser.conclusionFormula_return conclusionFormula() throws RecognitionException {
		DependenciesParser.conclusionFormula_return retval = new DependenciesParser.conclusionFormula_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token char_literal21=null;
		ParserRuleReturnScope atom20 =null;
		ParserRuleReturnScope atom22 =null;

		CommonTree char_literal21_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:89:18: ( atom ( ',' atom )* )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:89:20: atom ( ',' atom )*
			{
			root_0 = (CommonTree)adaptor.nil();


			  positiveFormula = new PositiveFormula(); 
			                      dependency.setConclusion(positiveFormula); 
			pushFollow(FOLLOW_atom_in_conclusionFormula340);
			atom20=atom();
			state._fsp--;

			adaptor.addChild(root_0, atom20.getTree());

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:91:24: ( ',' atom )*
			loop8:
			while (true) {
				int alt8=2;
				int LA8_0 = input.LA(1);
				if ( (LA8_0==17) ) {
					alt8=1;
				}

				switch (alt8) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:91:25: ',' atom
					{
					char_literal21=(Token)match(input,17,FOLLOW_17_in_conclusionFormula343); 
					char_literal21_tree = (CommonTree)adaptor.create(char_literal21);
					adaptor.addChild(root_0, char_literal21_tree);

					pushFollow(FOLLOW_atom_in_conclusionFormula345);
					atom22=atom();
					state._fsp--;

					adaptor.addChild(root_0, atom22.getTree());

					}
					break;

				default :
					break loop8;
				}
			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "conclusionFormula"


	public static class atom_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "atom"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:93:1: atom : ( relationalAtom | builtin | comparison );
	public final DependenciesParser.atom_return atom() throws RecognitionException {
		DependenciesParser.atom_return retval = new DependenciesParser.atom_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope relationalAtom23 =null;
		ParserRuleReturnScope builtin24 =null;
		ParserRuleReturnScope comparison25 =null;


		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:93:6: ( relationalAtom | builtin | comparison )
			int alt9=3;
			switch ( input.LA(1) ) {
			case IDENTIFIER:
				{
				alt9=1;
				}
				break;
			case EXPRESSION:
				{
				alt9=2;
				}
				break;
			case NUMBER:
			case STRING:
			case 22:
				{
				alt9=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 9, 0, input);
				throw nvae;
			}
			switch (alt9) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:93:9: relationalAtom
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_relationalAtom_in_atom358);
					relationalAtom23=relationalAtom();
					state._fsp--;

					adaptor.addChild(root_0, relationalAtom23.getTree());

					}
					break;
				case 2 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:93:26: builtin
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_builtin_in_atom362);
					builtin24=builtin();
					state._fsp--;

					adaptor.addChild(root_0, builtin24.getTree());

					}
					break;
				case 3 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:93:36: comparison
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_comparison_in_atom366);
					comparison25=comparison();
					state._fsp--;

					adaptor.addChild(root_0, comparison25.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "atom"


	public static class relationalAtom_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "relationalAtom"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:95:1: relationalAtom : name= IDENTIFIER '(' attribute ( ',' attribute )* ')' ;
	public final DependenciesParser.relationalAtom_return relationalAtom() throws RecognitionException {
		DependenciesParser.relationalAtom_return retval = new DependenciesParser.relationalAtom_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token name=null;
		Token char_literal26=null;
		Token char_literal28=null;
		Token char_literal30=null;
		ParserRuleReturnScope attribute27 =null;
		ParserRuleReturnScope attribute29 =null;

		CommonTree name_tree=null;
		CommonTree char_literal26_tree=null;
		CommonTree char_literal28_tree=null;
		CommonTree char_literal30_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:95:15: (name= IDENTIFIER '(' attribute ( ',' attribute )* ')' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:95:18: name= IDENTIFIER '(' attribute ( ',' attribute )* ')'
			{
			root_0 = (CommonTree)adaptor.nil();


			name=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_relationalAtom377); 
			name_tree = (CommonTree)adaptor.create(name);
			adaptor.addChild(root_0, name_tree);

			 atom = new RelationalAtom(name.getText()); 
			char_literal26=(Token)match(input,15,FOLLOW_15_in_relationalAtom381); 
			char_literal26_tree = (CommonTree)adaptor.create(char_literal26);
			adaptor.addChild(root_0, char_literal26_tree);

			pushFollow(FOLLOW_attribute_in_relationalAtom383);
			attribute27=attribute();
			state._fsp--;

			adaptor.addChild(root_0, attribute27.getTree());

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:95:95: ( ',' attribute )*
			loop10:
			while (true) {
				int alt10=2;
				int LA10_0 = input.LA(1);
				if ( (LA10_0==17) ) {
					alt10=1;
				}

				switch (alt10) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:95:96: ',' attribute
					{
					char_literal28=(Token)match(input,17,FOLLOW_17_in_relationalAtom386); 
					char_literal28_tree = (CommonTree)adaptor.create(char_literal28);
					adaptor.addChild(root_0, char_literal28_tree);

					pushFollow(FOLLOW_attribute_in_relationalAtom388);
					attribute29=attribute();
					state._fsp--;

					adaptor.addChild(root_0, attribute29.getTree());

					}
					break;

				default :
					break loop10;
				}
			}

			char_literal30=(Token)match(input,16,FOLLOW_16_in_relationalAtom392); 
			char_literal30_tree = (CommonTree)adaptor.create(char_literal30);
			adaptor.addChild(root_0, char_literal30_tree);

			  positiveFormula.addAtom(atom); atom.setFormula(positiveFormula); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "relationalAtom"


	public static class builtin_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "builtin"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:98:1: builtin : expression= EXPRESSION ;
	public final DependenciesParser.builtin_return builtin() throws RecognitionException {
		DependenciesParser.builtin_return retval = new DependenciesParser.builtin_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token expression=null;

		CommonTree expression_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:98:9: (expression= EXPRESSION )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:98:12: expression= EXPRESSION
			{
			root_0 = (CommonTree)adaptor.nil();


			expression=(Token)match(input,EXPRESSION,FOLLOW_EXPRESSION_in_builtin408); 
			expression_tree = (CommonTree)adaptor.create(expression);
			adaptor.addChild(root_0, expression_tree);

			  atom = new BuiltInAtom(positiveFormula, new Expression(generator.clean(expression.getText()))); 
			                    positiveFormula.addAtom(atom);  
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "builtin"


	public static class comparison_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "comparison"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:102:1: comparison : leftargument oper= OPERATOR rightargument ;
	public final DependenciesParser.comparison_return comparison() throws RecognitionException {
		DependenciesParser.comparison_return retval = new DependenciesParser.comparison_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token oper=null;
		ParserRuleReturnScope leftargument31 =null;
		ParserRuleReturnScope rightargument32 =null;

		CommonTree oper_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:102:12: ( leftargument oper= OPERATOR rightargument )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:102:15: leftargument oper= OPERATOR rightargument
			{
			root_0 = (CommonTree)adaptor.nil();


			   expressionString = new StringBuilder(); 
					     leftVariableId = null;
					     rightVariableId = null;
					     leftConstant = null;
					     rightConstant = null;
			pushFollow(FOLLOW_leftargument_in_comparison467);
			leftargument31=leftargument();
			state._fsp--;

			adaptor.addChild(root_0, leftargument31.getTree());

			oper=(Token)match(input,OPERATOR,FOLLOW_OPERATOR_in_comparison489); 
			oper_tree = (CommonTree)adaptor.create(oper);
			adaptor.addChild(root_0, oper_tree);

			 expressionString.append(" ").append(oper.getText()); 
			pushFollow(FOLLOW_rightargument_in_comparison510);
			rightargument32=rightargument();
			state._fsp--;

			adaptor.addChild(root_0, rightargument32.getTree());

			  Expression expression = new Expression(expressionString.toString()); 
			                    atom = new ComparisonAtom(positiveFormula, expression, leftVariableId, rightVariableId, leftConstant, rightConstant, oper.getText()); 
			                    positiveFormula.addAtom(atom); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "comparison"


	public static class leftargument_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "leftargument"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:114:1: leftargument : ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) ) ;
	public final DependenciesParser.leftargument_return leftargument() throws RecognitionException {
		DependenciesParser.leftargument_return retval = new DependenciesParser.leftargument_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token var=null;
		Token constant=null;
		Token char_literal33=null;

		CommonTree var_tree=null;
		CommonTree constant_tree=null;
		CommonTree char_literal33_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:114:13: ( ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) ) )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:114:16: ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) )
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:114:16: ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) )
			int alt11=2;
			int LA11_0 = input.LA(1);
			if ( (LA11_0==22) ) {
				alt11=1;
			}
			else if ( (LA11_0==NUMBER||LA11_0==STRING) ) {
				alt11=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 11, 0, input);
				throw nvae;
			}

			switch (alt11) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:114:17: '\\$' var= IDENTIFIER
					{
					char_literal33=(Token)match(input,22,FOLLOW_22_in_leftargument540); 
					char_literal33_tree = (CommonTree)adaptor.create(char_literal33);
					adaptor.addChild(root_0, char_literal33_tree);

					var=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_leftargument543); 
					var_tree = (CommonTree)adaptor.create(var);
					adaptor.addChild(root_0, var_tree);

					 leftVariableId = var.getText(); expressionString.append(var.getText()); 
					}
					break;
				case 2 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:115:18: constant= ( STRING | NUMBER )
					{
					constant=input.LT(1);
					if ( input.LA(1)==NUMBER||input.LA(1)==STRING ) {
						input.consume();
						adaptor.addChild(root_0, (CommonTree)adaptor.create(constant));
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					 leftConstant = constant.getText(); expressionString.append(constant.getText());
					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "leftargument"


	public static class rightargument_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "rightargument"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:118:1: rightargument : ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) ) ;
	public final DependenciesParser.rightargument_return rightargument() throws RecognitionException {
		DependenciesParser.rightargument_return retval = new DependenciesParser.rightargument_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token var=null;
		Token constant=null;
		Token char_literal34=null;

		CommonTree var_tree=null;
		CommonTree constant_tree=null;
		CommonTree char_literal34_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:118:14: ( ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) ) )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:118:17: ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) )
			{
			root_0 = (CommonTree)adaptor.nil();


			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:118:17: ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) )
			int alt12=2;
			int LA12_0 = input.LA(1);
			if ( (LA12_0==22) ) {
				alt12=1;
			}
			else if ( (LA12_0==NUMBER||LA12_0==STRING) ) {
				alt12=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 12, 0, input);
				throw nvae;
			}

			switch (alt12) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:118:18: '\\$' var= IDENTIFIER
					{
					char_literal34=(Token)match(input,22,FOLLOW_22_in_rightargument621); 
					char_literal34_tree = (CommonTree)adaptor.create(char_literal34);
					adaptor.addChild(root_0, char_literal34_tree);

					var=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_rightargument624); 
					var_tree = (CommonTree)adaptor.create(var);
					adaptor.addChild(root_0, var_tree);

					 rightVariableId = var.getText(); expressionString.append(var.getText()); 
					}
					break;
				case 2 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:119:18: constant= ( STRING | NUMBER )
					{
					constant=input.LT(1);
					if ( input.LA(1)==NUMBER||input.LA(1)==STRING ) {
						input.consume();
						adaptor.addChild(root_0, (CommonTree)adaptor.create(constant));
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					  rightConstant = constant.getText(); expressionString.append(constant.getText());
					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "rightargument"


	public static class attribute_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "attribute"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:122:1: attribute : attr= IDENTIFIER ':' value ;
	public final DependenciesParser.attribute_return attribute() throws RecognitionException {
		DependenciesParser.attribute_return retval = new DependenciesParser.attribute_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token attr=null;
		Token char_literal35=null;
		ParserRuleReturnScope value36 =null;

		CommonTree attr_tree=null;
		CommonTree char_literal35_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:122:10: (attr= IDENTIFIER ':' value )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:122:13: attr= IDENTIFIER ':' value
			{
			root_0 = (CommonTree)adaptor.nil();


			attr=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_attribute686); 
			attr_tree = (CommonTree)adaptor.create(attr);
			adaptor.addChild(root_0, attr_tree);

			char_literal35=(Token)match(input,20,FOLLOW_20_in_attribute688); 
			char_literal35_tree = (CommonTree)adaptor.create(char_literal35);
			adaptor.addChild(root_0, char_literal35_tree);

			 attribute = new FormulaAttribute(attr.getText()); 
			pushFollow(FOLLOW_value_in_attribute692);
			value36=value();
			state._fsp--;

			adaptor.addChild(root_0, value36.getTree());

			 ((RelationalAtom)atom).addAttribute(attribute); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "attribute"


	public static class value_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "value"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:125:1: value : ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) |nullValue= NULL |expression= EXPRESSION );
	public final DependenciesParser.value_return value() throws RecognitionException {
		DependenciesParser.value_return retval = new DependenciesParser.value_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token var=null;
		Token constant=null;
		Token nullValue=null;
		Token expression=null;
		Token char_literal37=null;

		CommonTree var_tree=null;
		CommonTree constant_tree=null;
		CommonTree nullValue_tree=null;
		CommonTree expression_tree=null;
		CommonTree char_literal37_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:125:7: ( '\\$' var= IDENTIFIER |constant= ( STRING | NUMBER ) |nullValue= NULL |expression= EXPRESSION )
			int alt13=4;
			switch ( input.LA(1) ) {
			case 22:
				{
				alt13=1;
				}
				break;
			case NUMBER:
			case STRING:
				{
				alt13=2;
				}
				break;
			case NULL:
				{
				alt13=3;
				}
				break;
			case EXPRESSION:
				{
				alt13=4;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 13, 0, input);
				throw nvae;
			}
			switch (alt13) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:125:10: '\\$' var= IDENTIFIER
					{
					root_0 = (CommonTree)adaptor.nil();


					char_literal37=(Token)match(input,22,FOLLOW_22_in_value707); 
					char_literal37_tree = (CommonTree)adaptor.create(char_literal37);
					adaptor.addChild(root_0, char_literal37_tree);

					var=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_value710); 
					var_tree = (CommonTree)adaptor.create(var);
					adaptor.addChild(root_0, var_tree);

					 attribute.setValue(new FormulaVariableOccurrence(new AttributeRef(((RelationalAtom)atom).getTableName(), attribute.getAttributeName()), var.getText())); 
					}
					break;
				case 2 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:126:18: constant= ( STRING | NUMBER )
					{
					root_0 = (CommonTree)adaptor.nil();


					constant=input.LT(1);
					if ( input.LA(1)==NUMBER||input.LA(1)==STRING ) {
						input.consume();
						adaptor.addChild(root_0, (CommonTree)adaptor.create(constant));
						state.errorRecovery=false;
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					 attribute.setValue(new FormulaConstant(BartUtility.cleanConstantValue(constant.getText()))); 
					}
					break;
				case 3 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:127:18: nullValue= NULL
					{
					root_0 = (CommonTree)adaptor.nil();


					nullValue=(Token)match(input,NULL,FOLLOW_NULL_in_value766); 
					nullValue_tree = (CommonTree)adaptor.create(nullValue);
					adaptor.addChild(root_0, nullValue_tree);

					 attribute.setValue(new FormulaConstant(nullValue.getText(), true)); 
					}
					break;
				case 4 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:128:18: expression= EXPRESSION
					{
					root_0 = (CommonTree)adaptor.nil();


					expression=(Token)match(input,EXPRESSION,FOLLOW_EXPRESSION_in_value791); 
					expression_tree = (CommonTree)adaptor.create(expression);
					adaptor.addChild(root_0, expression_tree);

					 attribute.setValue(new FormulaExpression(new Expression(generator.clean(expression.getText())))); 
					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "value"

	// Delegated rules



	public static final BitSet FOLLOW_dependencies_in_prog54 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_21_in_dependencies80 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_dc_in_dependencies82 = new BitSet(new long[]{0x0000000000000042L});
	public static final BitSet FOLLOW_dependency_in_dc103 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_dependency119 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_20_in_dependency120 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_positiveFormula_in_dependency129 = new BitSet(new long[]{0x0000000000840000L});
	public static final BitSet FOLLOW_negatedFormula_in_dependency134 = new BitSet(new long[]{0x0000000000840000L});
	public static final BitSet FOLLOW_18_in_dependency141 = new BitSet(new long[]{0x0000000000405460L});
	public static final BitSet FOLLOW_14_in_dependency148 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_conclusionFormula_in_dependency185 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_19_in_dependency188 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_relationalAtom_in_positiveFormula242 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_17_in_positiveFormula245 = new BitSet(new long[]{0x0000000000401460L});
	public static final BitSet FOLLOW_atom_in_positiveFormula247 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_23_in_negatedFormula278 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_15_in_negatedFormula279 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_positiveFormula_in_negatedFormula283 = new BitSet(new long[]{0x0000000000810000L});
	public static final BitSet FOLLOW_negatedFormula_in_negatedFormula287 = new BitSet(new long[]{0x0000000000810000L});
	public static final BitSet FOLLOW_16_in_negatedFormula294 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_atom_in_conclusionFormula340 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_17_in_conclusionFormula343 = new BitSet(new long[]{0x0000000000401460L});
	public static final BitSet FOLLOW_atom_in_conclusionFormula345 = new BitSet(new long[]{0x0000000000020002L});
	public static final BitSet FOLLOW_relationalAtom_in_atom358 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_builtin_in_atom362 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_comparison_in_atom366 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_relationalAtom377 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_15_in_relationalAtom381 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_attribute_in_relationalAtom383 = new BitSet(new long[]{0x0000000000030000L});
	public static final BitSet FOLLOW_17_in_relationalAtom386 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_attribute_in_relationalAtom388 = new BitSet(new long[]{0x0000000000030000L});
	public static final BitSet FOLLOW_16_in_relationalAtom392 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EXPRESSION_in_builtin408 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_leftargument_in_comparison467 = new BitSet(new long[]{0x0000000000000800L});
	public static final BitSet FOLLOW_OPERATOR_in_comparison489 = new BitSet(new long[]{0x0000000000401400L});
	public static final BitSet FOLLOW_rightargument_in_comparison510 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_22_in_leftargument540 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_IDENTIFIER_in_leftargument543 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_leftargument568 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_22_in_rightargument621 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_IDENTIFIER_in_rightargument624 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_rightargument649 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_attribute686 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_20_in_attribute688 = new BitSet(new long[]{0x0000000000401620L});
	public static final BitSet FOLLOW_value_in_attribute692 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_22_in_value707 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_IDENTIFIER_in_value710 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_value735 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NULL_in_value766 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EXPRESSION_in_value791 = new BitSet(new long[]{0x0000000000000002L});
}
