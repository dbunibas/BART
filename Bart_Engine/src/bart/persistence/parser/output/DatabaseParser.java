// $ANTLR 3.5.1 /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g 2015-11-26 13:39:24

package bart.persistence.parser.output;

import speedy.model.expressions.Expression;
import bart.persistence.parser.operators.ParseDatabase;
import bart.persistence.parser.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


@SuppressWarnings("all")
public class DatabaseParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "DIGIT", "IDENTIFIER", "LETTER", 
		"LINE_COMMENT", "NULL", "NUMBER", "STRING", "WHITESPACE", "'('", "')'", 
		"','", "':'", "'INSTANCE:'", "'SCHEMA:'"
	};
	public static final int EOF=-1;
	public static final int T__12=12;
	public static final int T__13=13;
	public static final int T__14=14;
	public static final int T__15=15;
	public static final int T__16=16;
	public static final int T__17=17;
	public static final int DIGIT=4;
	public static final int IDENTIFIER=5;
	public static final int LETTER=6;
	public static final int LINE_COMMENT=7;
	public static final int NULL=8;
	public static final int NUMBER=9;
	public static final int STRING=10;
	public static final int WHITESPACE=11;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public DatabaseParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public DatabaseParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return DatabaseParser.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g"; }


	private static org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(DatabaseParser.class.getName());
	private ParseDatabase generator;

	private ParserSchema currentSchema;
	private ParserInstance currentInstance;
	private ParserTable currentTable;
	private ParserFact currentFact;

	public void setGenerator(ParseDatabase generator) {
	      this.generator = generator;
	}


	public static class prog_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "prog"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:40:1: prog : database ;
	public final DatabaseParser.prog_return prog() throws RecognitionException {
		DatabaseParser.prog_return retval = new DatabaseParser.prog_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope database1 =null;


		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:40:5: ( database )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:40:7: database
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_database_in_prog54);
			database1=database();
			state._fsp--;

			adaptor.addChild(root_0, database1.getTree());

			 if (logger.isDebugEnabled()) logger.debug((database1!=null?((CommonTree)database1.getTree()):null).toStringTree()); 
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


	public static class database_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "database"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:42:1: database : schema ( instance )? ;
	public final DatabaseParser.database_return database() throws RecognitionException {
		DatabaseParser.database_return retval = new DatabaseParser.database_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope schema2 =null;
		ParserRuleReturnScope instance3 =null;


		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:42:9: ( schema ( instance )? )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:42:15: schema ( instance )?
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_schema_in_database69);
			schema2=schema();
			state._fsp--;

			adaptor.addChild(root_0, schema2.getTree());

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:42:22: ( instance )?
			int alt1=2;
			int LA1_0 = input.LA(1);
			if ( (LA1_0==16) ) {
				alt1=1;
			}
			switch (alt1) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:42:22: instance
					{
					pushFollow(FOLLOW_instance_in_database71);
					instance3=instance();
					state._fsp--;

					adaptor.addChild(root_0, instance3.getTree());

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
	// $ANTLR end "database"


	public static class schema_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "schema"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:44:1: schema : 'SCHEMA:' ( relation )+ ;
	public final DatabaseParser.schema_return schema() throws RecognitionException {
		DatabaseParser.schema_return retval = new DatabaseParser.schema_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token string_literal4=null;
		ParserRuleReturnScope relation5 =null;

		CommonTree string_literal4_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:44:7: ( 'SCHEMA:' ( relation )+ )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:44:16: 'SCHEMA:' ( relation )+
			{
			root_0 = (CommonTree)adaptor.nil();


			string_literal4=(Token)match(input,17,FOLLOW_17_in_schema87); 
			string_literal4_tree = (CommonTree)adaptor.create(string_literal4);
			adaptor.addChild(root_0, string_literal4_tree);

			 currentSchema = new ParserSchema(); 
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:44:66: ( relation )+
			int cnt2=0;
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( (LA2_0==IDENTIFIER) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:44:66: relation
					{
					pushFollow(FOLLOW_relation_in_schema91);
					relation5=relation();
					state._fsp--;

					adaptor.addChild(root_0, relation5.getTree());

					}
					break;

				default :
					if ( cnt2 >= 1 ) break loop2;
					EarlyExitException eee = new EarlyExitException(2, input);
					throw eee;
				}
				cnt2++;
			}

			 generator.setSchema(currentSchema); 
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
	// $ANTLR end "schema"


	public static class relation_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "relation"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:46:1: relation : set= IDENTIFIER '(' attrName ( ',' attrName )* ')' ;
	public final DatabaseParser.relation_return relation() throws RecognitionException {
		DatabaseParser.relation_return retval = new DatabaseParser.relation_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token set=null;
		Token char_literal6=null;
		Token char_literal8=null;
		Token char_literal10=null;
		ParserRuleReturnScope attrName7 =null;
		ParserRuleReturnScope attrName9 =null;

		CommonTree set_tree=null;
		CommonTree char_literal6_tree=null;
		CommonTree char_literal8_tree=null;
		CommonTree char_literal10_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:46:9: (set= IDENTIFIER '(' attrName ( ',' attrName )* ')' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:46:11: set= IDENTIFIER '(' attrName ( ',' attrName )* ')'
			{
			root_0 = (CommonTree)adaptor.nil();


			set=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_relation103); 
			set_tree = (CommonTree)adaptor.create(set);
			adaptor.addChild(root_0, set_tree);

			 currentTable = new ParserTable(set.getText()); 
			char_literal6=(Token)match(input,12,FOLLOW_12_in_relation107); 
			char_literal6_tree = (CommonTree)adaptor.create(char_literal6);
			adaptor.addChild(root_0, char_literal6_tree);

			pushFollow(FOLLOW_attrName_in_relation109);
			attrName7=attrName();
			state._fsp--;

			adaptor.addChild(root_0, attrName7.getTree());

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:46:90: ( ',' attrName )*
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( (LA3_0==14) ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:46:91: ',' attrName
					{
					char_literal8=(Token)match(input,14,FOLLOW_14_in_relation112); 
					char_literal8_tree = (CommonTree)adaptor.create(char_literal8);
					adaptor.addChild(root_0, char_literal8_tree);

					pushFollow(FOLLOW_attrName_in_relation114);
					attrName9=attrName();
					state._fsp--;

					adaptor.addChild(root_0, attrName9.getTree());

					}
					break;

				default :
					break loop3;
				}
			}

			char_literal10=(Token)match(input,13,FOLLOW_13_in_relation118); 
			char_literal10_tree = (CommonTree)adaptor.create(char_literal10);
			adaptor.addChild(root_0, char_literal10_tree);

			 currentSchema.addTable(currentTable); 
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
	// $ANTLR end "relation"


	public static class attrName_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "attrName"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:48:1: attrName : attr= IDENTIFIER ;
	public final DatabaseParser.attrName_return attrName() throws RecognitionException {
		DatabaseParser.attrName_return retval = new DatabaseParser.attrName_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token attr=null;

		CommonTree attr_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:48:9: (attr= IDENTIFIER )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:48:11: attr= IDENTIFIER
			{
			root_0 = (CommonTree)adaptor.nil();


			attr=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_attrName129); 
			attr_tree = (CommonTree)adaptor.create(attr);
			adaptor.addChild(root_0, attr_tree);

			 currentTable.addAttribute(new ParserAttribute(attr.getText(), null));  
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
	// $ANTLR end "attrName"


	public static class instance_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "instance"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:50:1: instance : 'INSTANCE:' ( fact )+ ;
	public final DatabaseParser.instance_return instance() throws RecognitionException {
		DatabaseParser.instance_return retval = new DatabaseParser.instance_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token string_literal11=null;
		ParserRuleReturnScope fact12 =null;

		CommonTree string_literal11_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:50:9: ( 'INSTANCE:' ( fact )+ )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:50:16: 'INSTANCE:' ( fact )+
			{
			root_0 = (CommonTree)adaptor.nil();


			string_literal11=(Token)match(input,16,FOLLOW_16_in_instance143); 
			string_literal11_tree = (CommonTree)adaptor.create(string_literal11);
			adaptor.addChild(root_0, string_literal11_tree);

			 currentInstance = new ParserInstance(); 
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:50:72: ( fact )+
			int cnt4=0;
			loop4:
			while (true) {
				int alt4=2;
				int LA4_0 = input.LA(1);
				if ( (LA4_0==IDENTIFIER) ) {
					alt4=1;
				}

				switch (alt4) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:50:72: fact
					{
					pushFollow(FOLLOW_fact_in_instance147);
					fact12=fact();
					state._fsp--;

					adaptor.addChild(root_0, fact12.getTree());

					}
					break;

				default :
					if ( cnt4 >= 1 ) break loop4;
					EarlyExitException eee = new EarlyExitException(4, input);
					throw eee;
				}
				cnt4++;
			}

			 generator.setInstance(currentInstance); 
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
	// $ANTLR end "instance"


	public static class fact_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "fact"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:52:1: fact : set= IDENTIFIER '(' attrValue ( ',' attrValue )* ')' ;
	public final DatabaseParser.fact_return fact() throws RecognitionException {
		DatabaseParser.fact_return retval = new DatabaseParser.fact_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token set=null;
		Token char_literal13=null;
		Token char_literal15=null;
		Token char_literal17=null;
		ParserRuleReturnScope attrValue14 =null;
		ParserRuleReturnScope attrValue16 =null;

		CommonTree set_tree=null;
		CommonTree char_literal13_tree=null;
		CommonTree char_literal15_tree=null;
		CommonTree char_literal17_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:52:5: (set= IDENTIFIER '(' attrValue ( ',' attrValue )* ')' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:52:14: set= IDENTIFIER '(' attrValue ( ',' attrValue )* ')'
			{
			root_0 = (CommonTree)adaptor.nil();


			set=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_fact169); 
			set_tree = (CommonTree)adaptor.create(set);
			adaptor.addChild(root_0, set_tree);

			 currentFact = new ParserFact(set.getText()); 
			char_literal13=(Token)match(input,12,FOLLOW_12_in_fact173); 
			char_literal13_tree = (CommonTree)adaptor.create(char_literal13);
			adaptor.addChild(root_0, char_literal13_tree);

			pushFollow(FOLLOW_attrValue_in_fact175);
			attrValue14=attrValue();
			state._fsp--;

			adaptor.addChild(root_0, attrValue14.getTree());

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:52:92: ( ',' attrValue )*
			loop5:
			while (true) {
				int alt5=2;
				int LA5_0 = input.LA(1);
				if ( (LA5_0==14) ) {
					alt5=1;
				}

				switch (alt5) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:52:93: ',' attrValue
					{
					char_literal15=(Token)match(input,14,FOLLOW_14_in_fact178); 
					char_literal15_tree = (CommonTree)adaptor.create(char_literal15);
					adaptor.addChild(root_0, char_literal15_tree);

					pushFollow(FOLLOW_attrValue_in_fact180);
					attrValue16=attrValue();
					state._fsp--;

					adaptor.addChild(root_0, attrValue16.getTree());

					}
					break;

				default :
					break loop5;
				}
			}

			char_literal17=(Token)match(input,13,FOLLOW_13_in_fact184); 
			char_literal17_tree = (CommonTree)adaptor.create(char_literal17);
			adaptor.addChild(root_0, char_literal17_tree);

			 currentInstance.addFact(currentFact); 
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
	// $ANTLR end "fact"


	public static class attrValue_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "attrValue"
	// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:54:1: attrValue : attr= IDENTIFIER ':' val= ( NULL | STRING | NUMBER ) ;
	public final DatabaseParser.attrValue_return attrValue() throws RecognitionException {
		DatabaseParser.attrValue_return retval = new DatabaseParser.attrValue_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token attr=null;
		Token val=null;
		Token char_literal18=null;

		CommonTree attr_tree=null;
		CommonTree val_tree=null;
		CommonTree char_literal18_tree=null;

		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:54:10: (attr= IDENTIFIER ':' val= ( NULL | STRING | NUMBER ) )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:54:12: attr= IDENTIFIER ':' val= ( NULL | STRING | NUMBER )
			{
			root_0 = (CommonTree)adaptor.nil();


			attr=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_attrValue195); 
			attr_tree = (CommonTree)adaptor.create(attr);
			adaptor.addChild(root_0, attr_tree);

			char_literal18=(Token)match(input,15,FOLLOW_15_in_attrValue197); 
			char_literal18_tree = (CommonTree)adaptor.create(char_literal18);
			adaptor.addChild(root_0, char_literal18_tree);

			val=input.LT(1);
			if ( (input.LA(1) >= NULL && input.LA(1) <= STRING) ) {
				input.consume();
				adaptor.addChild(root_0, (CommonTree)adaptor.create(val));
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			 currentFact.addAttribute(new ParserAttribute(attr.getText(), val.getText()));  
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
	// $ANTLR end "attrValue"

	// Delegated rules



	public static final BitSet FOLLOW_database_in_prog54 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_schema_in_database69 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_instance_in_database71 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_17_in_schema87 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_relation_in_schema91 = new BitSet(new long[]{0x0000000000000022L});
	public static final BitSet FOLLOW_IDENTIFIER_in_relation103 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_relation107 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_attrName_in_relation109 = new BitSet(new long[]{0x0000000000006000L});
	public static final BitSet FOLLOW_14_in_relation112 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_attrName_in_relation114 = new BitSet(new long[]{0x0000000000006000L});
	public static final BitSet FOLLOW_13_in_relation118 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_attrName129 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_16_in_instance143 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_fact_in_instance147 = new BitSet(new long[]{0x0000000000000022L});
	public static final BitSet FOLLOW_IDENTIFIER_in_fact169 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_12_in_fact173 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_attrValue_in_fact175 = new BitSet(new long[]{0x0000000000006000L});
	public static final BitSet FOLLOW_14_in_fact178 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_attrValue_in_fact180 = new BitSet(new long[]{0x0000000000006000L});
	public static final BitSet FOLLOW_13_in_fact184 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IDENTIFIER_in_attrValue195 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_15_in_attrValue197 = new BitSet(new long[]{0x0000000000000700L});
	public static final BitSet FOLLOW_set_in_attrValue201 = new BitSet(new long[]{0x0000000000000002L});
}
