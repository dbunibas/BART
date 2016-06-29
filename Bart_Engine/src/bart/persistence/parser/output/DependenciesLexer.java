// $ANTLR 3.5.1 /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g 2016-02-24 12:11:49

package bart.persistence.parser.output;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class DependenciesLexer extends Lexer {
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


	public void emitErrorMessage(String msg) {
		throw new speedy.exceptions.ParserException(msg);
	}


	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public DependenciesLexer() {} 
	public DependenciesLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public DependenciesLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "/Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g"; }

	// $ANTLR start "T__14"
	public final void mT__14() throws RecognitionException {
		try {
			int _type = T__14;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:12:7: ( '#fail' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:12:9: '#fail'
			{
			match("#fail"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__14"

	// $ANTLR start "T__15"
	public final void mT__15() throws RecognitionException {
		try {
			int _type = T__15;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:13:7: ( '(' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:13:9: '('
			{
			match('('); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__15"

	// $ANTLR start "T__16"
	public final void mT__16() throws RecognitionException {
		try {
			int _type = T__16;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:14:7: ( ')' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:14:9: ')'
			{
			match(')'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__16"

	// $ANTLR start "T__17"
	public final void mT__17() throws RecognitionException {
		try {
			int _type = T__17;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:15:7: ( ',' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:15:9: ','
			{
			match(','); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__17"

	// $ANTLR start "T__18"
	public final void mT__18() throws RecognitionException {
		try {
			int _type = T__18;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:16:7: ( '->' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:16:9: '->'
			{
			match("->"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__18"

	// $ANTLR start "T__19"
	public final void mT__19() throws RecognitionException {
		try {
			int _type = T__19;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:17:7: ( '.' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:17:9: '.'
			{
			match('.'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__19"

	// $ANTLR start "T__20"
	public final void mT__20() throws RecognitionException {
		try {
			int _type = T__20;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:18:7: ( ':' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:18:9: ':'
			{
			match(':'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__20"

	// $ANTLR start "T__21"
	public final void mT__21() throws RecognitionException {
		try {
			int _type = T__21;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:19:7: ( 'DCs:' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:19:9: 'DCs:'
			{
			match("DCs:"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__21"

	// $ANTLR start "T__22"
	public final void mT__22() throws RecognitionException {
		try {
			int _type = T__22;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:20:7: ( '\\$' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:20:9: '\\$'
			{
			match('$'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__22"

	// $ANTLR start "T__23"
	public final void mT__23() throws RecognitionException {
		try {
			int _type = T__23;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:21:7: ( 'and not exists' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:21:9: 'and not exists'
			{
			match("and not exists"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__23"

	// $ANTLR start "OPERATOR"
	public final void mOPERATOR() throws RecognitionException {
		try {
			int _type = OPERATOR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:130:9: ( '==' | '!=' | '>' | '<' | '>=' | '<=' )
			int alt1=6;
			switch ( input.LA(1) ) {
			case '=':
				{
				alt1=1;
				}
				break;
			case '!':
				{
				alt1=2;
				}
				break;
			case '>':
				{
				int LA1_3 = input.LA(2);
				if ( (LA1_3=='=') ) {
					alt1=5;
				}

				else {
					alt1=3;
				}

				}
				break;
			case '<':
				{
				int LA1_4 = input.LA(2);
				if ( (LA1_4=='=') ) {
					alt1=6;
				}

				else {
					alt1=4;
				}

				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 1, 0, input);
				throw nvae;
			}
			switch (alt1) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:130:12: '=='
					{
					match("=="); 

					}
					break;
				case 2 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:130:19: '!='
					{
					match("!="); 

					}
					break;
				case 3 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:130:26: '>'
					{
					match('>'); 
					}
					break;
				case 4 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:130:32: '<'
					{
					match('<'); 
					}
					break;
				case 5 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:130:38: '>='
					{
					match(">="); 

					}
					break;
				case 6 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:130:45: '<='
					{
					match("<="); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OPERATOR"

	// $ANTLR start "IDENTIFIER"
	public final void mIDENTIFIER() throws RecognitionException {
		try {
			int _type = IDENTIFIER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:132:13: ( ( LETTER ) ( LETTER | DIGIT | '_' )* )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:132:18: ( LETTER ) ( LETTER | DIGIT | '_' )*
			{
			if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:132:27: ( LETTER | DIGIT | '_' )*
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( ((LA2_0 >= '0' && LA2_0 <= '9')||(LA2_0 >= 'A' && LA2_0 <= 'Z')||LA2_0=='_'||(LA2_0 >= 'a' && LA2_0 <= 'z')) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop2;
				}
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "IDENTIFIER"

	// $ANTLR start "STRING"
	public final void mSTRING() throws RecognitionException {
		try {
			int _type = STRING;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:135:9: ( '\"' (~ ( '\\r' | '\\n' | '\"' ) )* '\"' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:135:19: '\"' (~ ( '\\r' | '\\n' | '\"' ) )* '\"'
			{
			match('\"'); 
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:135:23: (~ ( '\\r' | '\\n' | '\"' ) )*
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( ((LA3_0 >= '\u0000' && LA3_0 <= '\t')||(LA3_0 >= '\u000B' && LA3_0 <= '\f')||(LA3_0 >= '\u000E' && LA3_0 <= '!')||(LA3_0 >= '#' && LA3_0 <= '\uFFFF')) ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop3;
				}
			}

			match('\"'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "STRING"

	// $ANTLR start "NUMBER"
	public final void mNUMBER() throws RecognitionException {
		try {
			int _type = NUMBER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:136:8: ( ( '-' )? ( DIGIT )+ ( '.' ( DIGIT )+ )? )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:136:12: ( '-' )? ( DIGIT )+ ( '.' ( DIGIT )+ )?
			{
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:136:12: ( '-' )?
			int alt4=2;
			int LA4_0 = input.LA(1);
			if ( (LA4_0=='-') ) {
				alt4=1;
			}
			switch (alt4) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:136:13: '-'
					{
					match('-'); 
					}
					break;

			}

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:136:19: ( DIGIT )+
			int cnt5=0;
			loop5:
			while (true) {
				int alt5=2;
				int LA5_0 = input.LA(1);
				if ( ((LA5_0 >= '0' && LA5_0 <= '9')) ) {
					alt5=1;
				}

				switch (alt5) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt5 >= 1 ) break loop5;
					EarlyExitException eee = new EarlyExitException(5, input);
					throw eee;
				}
				cnt5++;
			}

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:136:26: ( '.' ( DIGIT )+ )?
			int alt7=2;
			int LA7_0 = input.LA(1);
			if ( (LA7_0=='.') ) {
				alt7=1;
			}
			switch (alt7) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:136:27: '.' ( DIGIT )+
					{
					match('.'); 
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:136:31: ( DIGIT )+
					int cnt6=0;
					loop6:
					while (true) {
						int alt6=2;
						int LA6_0 = input.LA(1);
						if ( ((LA6_0 >= '0' && LA6_0 <= '9')) ) {
							alt6=1;
						}

						switch (alt6) {
						case 1 :
							// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt6 >= 1 ) break loop6;
							EarlyExitException eee = new EarlyExitException(6, input);
							throw eee;
						}
						cnt6++;
					}

					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NUMBER"

	// $ANTLR start "NULL"
	public final void mNULL() throws RecognitionException {
		try {
			int _type = NULL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:137:9: ( '#NULL#' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:137:18: '#NULL#'
			{
			match("#NULL#"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NULL"

	// $ANTLR start "DIGIT"
	public final void mDIGIT() throws RecognitionException {
		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:138:15: ( '0' .. '9' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:
			{
			if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DIGIT"

	// $ANTLR start "LETTER"
	public final void mLETTER() throws RecognitionException {
		try {
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:139:16: ( 'a' .. 'z' | 'A' .. 'Z' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:
			{
			if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LETTER"

	// $ANTLR start "WHITESPACE"
	public final void mWHITESPACE() throws RecognitionException {
		try {
			int _type = WHITESPACE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:140:12: ( ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+ )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:140:16: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
			{
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:140:16: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
			int cnt8=0;
			loop8:
			while (true) {
				int alt8=2;
				int LA8_0 = input.LA(1);
				if ( ((LA8_0 >= '\t' && LA8_0 <= '\n')||(LA8_0 >= '\f' && LA8_0 <= '\r')||LA8_0==' ') ) {
					alt8=1;
				}

				switch (alt8) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt8 >= 1 ) break loop8;
					EarlyExitException eee = new EarlyExitException(8, input);
					throw eee;
				}
				cnt8++;
			}

			 skip(); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WHITESPACE"

	// $ANTLR start "LINE_COMMENT"
	public final void mLINE_COMMENT() throws RecognitionException {
		try {
			int _type = LINE_COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:141:14: ( '//' (~ ( '\\r' | '\\n' ) )* )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:141:18: '//' (~ ( '\\r' | '\\n' ) )*
			{
			match("//"); 

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:141:23: (~ ( '\\r' | '\\n' ) )*
			loop9:
			while (true) {
				int alt9=2;
				int LA9_0 = input.LA(1);
				if ( ((LA9_0 >= '\u0000' && LA9_0 <= '\t')||(LA9_0 >= '\u000B' && LA9_0 <= '\f')||(LA9_0 >= '\u000E' && LA9_0 <= '\uFFFF')) ) {
					alt9=1;
				}

				switch (alt9) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop9;
				}
			}

			 skip(); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LINE_COMMENT"

	// $ANTLR start "EXPRESSION"
	public final void mEXPRESSION() throws RecognitionException {
		try {
			int _type = EXPRESSION;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:142:11: ( '{' ( . )* '}' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:142:18: '{' ( . )* '}'
			{
			match('{'); 
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:142:21: ( . )*
			loop10:
			while (true) {
				int alt10=2;
				int LA10_0 = input.LA(1);
				if ( (LA10_0=='}') ) {
					alt10=2;
				}
				else if ( ((LA10_0 >= '\u0000' && LA10_0 <= '|')||(LA10_0 >= '~' && LA10_0 <= '\uFFFF')) ) {
					alt10=1;
				}

				switch (alt10) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:142:22: .
					{
					matchAny(); 
					}
					break;

				default :
					break loop10;
				}
			}

			match('}'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EXPRESSION"

	@Override
	public void mTokens() throws RecognitionException {
		// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:8: ( T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | OPERATOR | IDENTIFIER | STRING | NUMBER | NULL | WHITESPACE | LINE_COMMENT | EXPRESSION )
		int alt11=18;
		switch ( input.LA(1) ) {
		case '#':
			{
			int LA11_1 = input.LA(2);
			if ( (LA11_1=='f') ) {
				alt11=1;
			}
			else if ( (LA11_1=='N') ) {
				alt11=15;
			}

			else {
				int nvaeMark = input.mark();
				try {
					input.consume();
					NoViableAltException nvae =
						new NoViableAltException("", 11, 1, input);
					throw nvae;
				} finally {
					input.rewind(nvaeMark);
				}
			}

			}
			break;
		case '(':
			{
			alt11=2;
			}
			break;
		case ')':
			{
			alt11=3;
			}
			break;
		case ',':
			{
			alt11=4;
			}
			break;
		case '-':
			{
			int LA11_5 = input.LA(2);
			if ( (LA11_5=='>') ) {
				alt11=5;
			}
			else if ( ((LA11_5 >= '0' && LA11_5 <= '9')) ) {
				alt11=14;
			}

			else {
				int nvaeMark = input.mark();
				try {
					input.consume();
					NoViableAltException nvae =
						new NoViableAltException("", 11, 5, input);
					throw nvae;
				} finally {
					input.rewind(nvaeMark);
				}
			}

			}
			break;
		case '.':
			{
			alt11=6;
			}
			break;
		case ':':
			{
			alt11=7;
			}
			break;
		case 'D':
			{
			int LA11_8 = input.LA(2);
			if ( (LA11_8=='C') ) {
				int LA11_21 = input.LA(3);
				if ( (LA11_21=='s') ) {
					int LA11_23 = input.LA(4);
					if ( (LA11_23==':') ) {
						alt11=8;
					}

					else {
						alt11=12;
					}

				}

				else {
					alt11=12;
				}

			}

			else {
				alt11=12;
			}

			}
			break;
		case '$':
			{
			alt11=9;
			}
			break;
		case 'a':
			{
			int LA11_10 = input.LA(2);
			if ( (LA11_10=='n') ) {
				int LA11_22 = input.LA(3);
				if ( (LA11_22=='d') ) {
					int LA11_24 = input.LA(4);
					if ( (LA11_24==' ') ) {
						alt11=10;
					}

					else {
						alt11=12;
					}

				}

				else {
					alt11=12;
				}

			}

			else {
				alt11=12;
			}

			}
			break;
		case '!':
		case '<':
		case '=':
		case '>':
			{
			alt11=11;
			}
			break;
		case 'A':
		case 'B':
		case 'C':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'I':
		case 'J':
		case 'K':
		case 'L':
		case 'M':
		case 'N':
		case 'O':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
		case 'T':
		case 'U':
		case 'V':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			{
			alt11=12;
			}
			break;
		case '\"':
			{
			alt11=13;
			}
			break;
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			{
			alt11=14;
			}
			break;
		case '\t':
		case '\n':
		case '\f':
		case '\r':
		case ' ':
			{
			alt11=16;
			}
			break;
		case '/':
			{
			alt11=17;
			}
			break;
		case '{':
			{
			alt11=18;
			}
			break;
		default:
			NoViableAltException nvae =
				new NoViableAltException("", 11, 0, input);
			throw nvae;
		}
		switch (alt11) {
			case 1 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:10: T__14
				{
				mT__14(); 

				}
				break;
			case 2 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:16: T__15
				{
				mT__15(); 

				}
				break;
			case 3 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:22: T__16
				{
				mT__16(); 

				}
				break;
			case 4 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:28: T__17
				{
				mT__17(); 

				}
				break;
			case 5 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:34: T__18
				{
				mT__18(); 

				}
				break;
			case 6 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:40: T__19
				{
				mT__19(); 

				}
				break;
			case 7 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:46: T__20
				{
				mT__20(); 

				}
				break;
			case 8 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:52: T__21
				{
				mT__21(); 

				}
				break;
			case 9 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:58: T__22
				{
				mT__22(); 

				}
				break;
			case 10 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:64: T__23
				{
				mT__23(); 

				}
				break;
			case 11 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:70: OPERATOR
				{
				mOPERATOR(); 

				}
				break;
			case 12 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:79: IDENTIFIER
				{
				mIDENTIFIER(); 

				}
				break;
			case 13 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:90: STRING
				{
				mSTRING(); 

				}
				break;
			case 14 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:97: NUMBER
				{
				mNUMBER(); 

				}
				break;
			case 15 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:104: NULL
				{
				mNULL(); 

				}
				break;
			case 16 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:109: WHITESPACE
				{
				mWHITESPACE(); 

				}
				break;
			case 17 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:120: LINE_COMMENT
				{
				mLINE_COMMENT(); 

				}
				break;
			case 18 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Dependencies.g:1:133: EXPRESSION
				{
				mEXPRESSION(); 

				}
				break;

		}
	}



}
