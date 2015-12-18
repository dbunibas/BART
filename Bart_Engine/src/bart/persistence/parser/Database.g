grammar Database;

options {
output=AST;
ASTLabelType=CommonTree; // type of $stat.tree ref etc...
}

@lexer::header {
package bart.persistence.parser.output;
}

@header {
package bart.persistence.parser.output;

import speedy.model.expressions.Expression;
import bart.persistence.parser.operators.ParseDatabase;
import bart.persistence.parser.*;
}

@members {
private static org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(DatabaseParser.class.getName());
private ParseDatabase generator;

private ParserSchema currentSchema;
private ParserInstance currentInstance;
private ParserTable currentTable;
private ParserFact currentFact;

public void setGenerator(ParseDatabase generator) {
      this.generator = generator;
}
}
@lexer::members {

public void emitErrorMessage(String msg) {
	throw new speedy.exceptions.ParserException(msg);
}
}

prog: database { if (logger.isDebugEnabled()) logger.debug($database.tree.toStringTree()); }  ;

database:     schema instance? ;

schema:	       'SCHEMA:' { currentSchema = new ParserSchema(); } relation+ { generator.setSchema(currentSchema); };

relation:	set=IDENTIFIER { currentTable = new ParserTable(set.getText()); } '(' attrName (',' attrName)* ')' { currentSchema.addTable(currentTable); };

attrName:	attr=IDENTIFIER { currentTable.addAttribute(new ParserAttribute(attr.getText(), null));  };

instance:      'INSTANCE:' { currentInstance = new ParserInstance(); } fact+ { generator.setInstance(currentInstance); }   ;

fact:	       set=IDENTIFIER { currentFact = new ParserFact(set.getText()); } '(' attrValue (',' attrValue)* ')' { currentInstance.addFact(currentFact); };

attrValue:	attr=IDENTIFIER ':' val=(NULL | STRING | NUMBER) { currentFact.addAttribute(new ParserAttribute(attr.getText(), val.getText()));  };

IDENTIFIER  :   (LETTER) (LETTER | DIGIT | '_' | '.' )*;

STRING  :  	'"' (LETTER | DIGIT| '-' | '.' | '@' | ' ')+ '"';
NUMBER	: 	('-')? DIGIT+ ('.' DIGIT+)?;
NULL    :       '#NULL#';
fragment DIGIT	: '0'..'9' ;
fragment LETTER	: 'a'..'z'|'A'..'Z' ;
WHITESPACE : 	( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ { skip(); } ;
LINE_COMMENT :  '//' ~( '\r' | '\n' )* { skip(); } ;