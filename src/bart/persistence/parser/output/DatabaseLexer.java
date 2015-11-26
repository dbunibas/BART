// $ANTLR 3.5.1 /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g 2015-11-26 13:39:24

package bart.persistence.parser.output;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class DatabaseLexer extends Lexer {
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


	public void emitErrorMessage(String msg) {
		throw new speedy.exceptions.ParserException(msg);
	}


	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public DatabaseLexer() {} 
	public DatabaseLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public DatabaseLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "/Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g"; }

	// $ANTLR start "T__12"
	public final void mT__12() throws RecognitionException {
		try {
			int _type = T__12;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:12:7: ( '(' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:12:9: '('
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
	// $ANTLR end "T__12"

	// $ANTLR start "T__13"
	public final void mT__13() throws RecognitionException {
		try {
			int _type = T__13;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:13:7: ( ')' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:13:9: ')'
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
	// $ANTLR end "T__13"

	// $ANTLR start "T__14"
	public final void mT__14() throws RecognitionException {
		try {
			int _type = T__14;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:14:7: ( ',' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:14:9: ','
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
	// $ANTLR end "T__14"

	// $ANTLR start "T__15"
	public final void mT__15() throws RecognitionException {
		try {
			int _type = T__15;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:15:7: ( ':' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:15:9: ':'
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
	// $ANTLR end "T__15"

	// $ANTLR start "T__16"
	public final void mT__16() throws RecognitionException {
		try {
			int _type = T__16;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:16:7: ( 'INSTANCE:' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:16:9: 'INSTANCE:'
			{
			match("INSTANCE:"); 

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
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:17:7: ( 'SCHEMA:' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:17:9: 'SCHEMA:'
			{
			match("SCHEMA:"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__17"

	// $ANTLR start "IDENTIFIER"
	public final void mIDENTIFIER() throws RecognitionException {
		try {
			int _type = IDENTIFIER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:56:13: ( ( LETTER ) ( LETTER | DIGIT | '_' | '.' )* )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:56:17: ( LETTER ) ( LETTER | DIGIT | '_' | '.' )*
			{
			if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:56:26: ( LETTER | DIGIT | '_' | '.' )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0=='.'||(LA1_0 >= '0' && LA1_0 <= '9')||(LA1_0 >= 'A' && LA1_0 <= 'Z')||LA1_0=='_'||(LA1_0 >= 'a' && LA1_0 <= 'z')) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:
					{
					if ( input.LA(1)=='.'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
					break loop1;
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
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:58:9: ( '\"' ( LETTER | DIGIT | '-' | '.' | '@' | ' ' )+ '\"' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:58:13: '\"' ( LETTER | DIGIT | '-' | '.' | '@' | ' ' )+ '\"'
			{
			match('\"'); 
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:58:17: ( LETTER | DIGIT | '-' | '.' | '@' | ' ' )+
			int cnt2=0;
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( (LA2_0==' '||(LA2_0 >= '-' && LA2_0 <= '.')||(LA2_0 >= '0' && LA2_0 <= '9')||(LA2_0 >= '@' && LA2_0 <= 'Z')||(LA2_0 >= 'a' && LA2_0 <= 'z')) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:
					{
					if ( input.LA(1)==' '||(input.LA(1) >= '-' && input.LA(1) <= '.')||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= '@' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
					if ( cnt2 >= 1 ) break loop2;
					EarlyExitException eee = new EarlyExitException(2, input);
					throw eee;
				}
				cnt2++;
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
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:59:8: ( ( '-' )? ( DIGIT )+ ( '.' ( DIGIT )+ )? )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:59:11: ( '-' )? ( DIGIT )+ ( '.' ( DIGIT )+ )?
			{
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:59:11: ( '-' )?
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0=='-') ) {
				alt3=1;
			}
			switch (alt3) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:59:12: '-'
					{
					match('-'); 
					}
					break;

			}

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:59:18: ( DIGIT )+
			int cnt4=0;
			loop4:
			while (true) {
				int alt4=2;
				int LA4_0 = input.LA(1);
				if ( ((LA4_0 >= '0' && LA4_0 <= '9')) ) {
					alt4=1;
				}

				switch (alt4) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:
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
					if ( cnt4 >= 1 ) break loop4;
					EarlyExitException eee = new EarlyExitException(4, input);
					throw eee;
				}
				cnt4++;
			}

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:59:25: ( '.' ( DIGIT )+ )?
			int alt6=2;
			int LA6_0 = input.LA(1);
			if ( (LA6_0=='.') ) {
				alt6=1;
			}
			switch (alt6) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:59:26: '.' ( DIGIT )+
					{
					match('.'); 
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:59:30: ( DIGIT )+
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
							// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:
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
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:60:9: ( '#NULL#' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:60:17: '#NULL#'
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
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:61:16: ( '0' .. '9' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:
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
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:62:17: ( 'a' .. 'z' | 'A' .. 'Z' )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:
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
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:63:12: ( ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+ )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:63:15: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
			{
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:63:15: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
			int cnt7=0;
			loop7:
			while (true) {
				int alt7=2;
				int LA7_0 = input.LA(1);
				if ( ((LA7_0 >= '\t' && LA7_0 <= '\n')||(LA7_0 >= '\f' && LA7_0 <= '\r')||LA7_0==' ') ) {
					alt7=1;
				}

				switch (alt7) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:
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
					if ( cnt7 >= 1 ) break loop7;
					EarlyExitException eee = new EarlyExitException(7, input);
					throw eee;
				}
				cnt7++;
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
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:64:14: ( '//' (~ ( '\\r' | '\\n' ) )* )
			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:64:17: '//' (~ ( '\\r' | '\\n' ) )*
			{
			match("//"); 

			// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:64:22: (~ ( '\\r' | '\\n' ) )*
			loop8:
			while (true) {
				int alt8=2;
				int LA8_0 = input.LA(1);
				if ( ((LA8_0 >= '\u0000' && LA8_0 <= '\t')||(LA8_0 >= '\u000B' && LA8_0 <= '\f')||(LA8_0 >= '\u000E' && LA8_0 <= '\uFFFF')) ) {
					alt8=1;
				}

				switch (alt8) {
				case 1 :
					// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:
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
					break loop8;
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

	@Override
	public void mTokens() throws RecognitionException {
		// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:1:8: ( T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | IDENTIFIER | STRING | NUMBER | NULL | WHITESPACE | LINE_COMMENT )
		int alt9=12;
		switch ( input.LA(1) ) {
		case '(':
			{
			alt9=1;
			}
			break;
		case ')':
			{
			alt9=2;
			}
			break;
		case ',':
			{
			alt9=3;
			}
			break;
		case ':':
			{
			alt9=4;
			}
			break;
		case 'I':
			{
			int LA9_5 = input.LA(2);
			if ( (LA9_5=='N') ) {
				int LA9_13 = input.LA(3);
				if ( (LA9_13=='S') ) {
					int LA9_15 = input.LA(4);
					if ( (LA9_15=='T') ) {
						int LA9_17 = input.LA(5);
						if ( (LA9_17=='A') ) {
							int LA9_19 = input.LA(6);
							if ( (LA9_19=='N') ) {
								int LA9_21 = input.LA(7);
								if ( (LA9_21=='C') ) {
									int LA9_23 = input.LA(8);
									if ( (LA9_23=='E') ) {
										int LA9_25 = input.LA(9);
										if ( (LA9_25==':') ) {
											alt9=5;
										}

										else {
											alt9=7;
										}

									}

									else {
										alt9=7;
									}

								}

								else {
									alt9=7;
								}

							}

							else {
								alt9=7;
							}

						}

						else {
							alt9=7;
						}

					}

					else {
						alt9=7;
					}

				}

				else {
					alt9=7;
				}

			}

			else {
				alt9=7;
			}

			}
			break;
		case 'S':
			{
			int LA9_6 = input.LA(2);
			if ( (LA9_6=='C') ) {
				int LA9_14 = input.LA(3);
				if ( (LA9_14=='H') ) {
					int LA9_16 = input.LA(4);
					if ( (LA9_16=='E') ) {
						int LA9_18 = input.LA(5);
						if ( (LA9_18=='M') ) {
							int LA9_20 = input.LA(6);
							if ( (LA9_20=='A') ) {
								int LA9_22 = input.LA(7);
								if ( (LA9_22==':') ) {
									alt9=6;
								}

								else {
									alt9=7;
								}

							}

							else {
								alt9=7;
							}

						}

						else {
							alt9=7;
						}

					}

					else {
						alt9=7;
					}

				}

				else {
					alt9=7;
				}

			}

			else {
				alt9=7;
			}

			}
			break;
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'J':
		case 'K':
		case 'L':
		case 'M':
		case 'N':
		case 'O':
		case 'P':
		case 'Q':
		case 'R':
		case 'T':
		case 'U':
		case 'V':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
		case 'a':
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
			alt9=7;
			}
			break;
		case '\"':
			{
			alt9=8;
			}
			break;
		case '-':
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
			alt9=9;
			}
			break;
		case '#':
			{
			alt9=10;
			}
			break;
		case '\t':
		case '\n':
		case '\f':
		case '\r':
		case ' ':
			{
			alt9=11;
			}
			break;
		case '/':
			{
			alt9=12;
			}
			break;
		default:
			NoViableAltException nvae =
				new NoViableAltException("", 9, 0, input);
			throw nvae;
		}
		switch (alt9) {
			case 1 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:1:10: T__12
				{
				mT__12(); 

				}
				break;
			case 2 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:1:16: T__13
				{
				mT__13(); 

				}
				break;
			case 3 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:1:22: T__14
				{
				mT__14(); 

				}
				break;
			case 4 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:1:28: T__15
				{
				mT__15(); 

				}
				break;
			case 5 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:1:34: T__16
				{
				mT__16(); 

				}
				break;
			case 6 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:1:40: T__17
				{
				mT__17(); 

				}
				break;
			case 7 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:1:46: IDENTIFIER
				{
				mIDENTIFIER(); 

				}
				break;
			case 8 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:1:57: STRING
				{
				mSTRING(); 

				}
				break;
			case 9 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:1:64: NUMBER
				{
				mNUMBER(); 

				}
				break;
			case 10 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:1:71: NULL
				{
				mNULL(); 

				}
				break;
			case 11 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:1:76: WHITESPACE
				{
				mWHITESPACE(); 

				}
				break;
			case 12 :
				// /Users/donatello/Projects/BART/src/bart/persistence/parser/Database.g:1:87: LINE_COMMENT
				{
				mLINE_COMMENT(); 

				}
				break;

		}
	}



}
