grammar Dependencies;

options {
output=AST;
ASTLabelType=CommonTree; // type of $stat.tree ref etc...
}

@lexer::header {
package bart.persistence.parser.output;
}

@header {
package bart.persistence.parser.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import bart.model.dependency.*;
import speedy.model.expressions.Expression;
import bart.persistence.parser.operators.ParseDependencies;
import bart.BartConstants;
import bart.utility.BartUtility;
}

@members {
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

}
@lexer::members {

public void emitErrorMessage(String msg) {
	throw new speedy.exceptions.ParserException(msg);
}
}

prog: dependencies { if (logger.isDebugEnabled()) logger.debug($dependencies.tree.toStringTree()); }  ;

dependencies:    
	         ('DCs:' dc+ { counter = 0;} )?
		 { generator.processDependencies(); };

dc:	 	 dependency {  dependency.setType(BartConstants.DC); dependency.setId("d" + counter++); generator.addDC(dependency); } ;

dependency:	 (id = IDENTIFIER':')? {  dependency = new Dependency(); 
                    formulaWN = new FormulaWithNegations(); 
                    formulaStack.push(formulaWN);
                    dependency.setPremise(formulaWN);
                    if(id!=null) dependency.setId(id.getText()); }
		 positiveFormula  ( negatedFormula   )* '->' 
		 ('#fail' 
		 {  formulaStack.clear(); 
                    dependency.setConclusion(NullFormula.getInstance());}
		 |
		 {  formulaStack.clear(); } 
                  conclusionFormula) '.' ;  
                  		    
positiveFormula: {  positiveFormula = new PositiveFormula(); 
                    positiveFormula.setFather(formulaStack.peek()); 
                    formulaStack.peek().setPositiveFormula(positiveFormula); }
                  relationalAtom (',' atom )* ;

negatedFormula:  {  formulaWN = new FormulaWithNegations(); 
		    formulaWN.setFather(formulaStack.peek());
		    formulaStack.peek().addNegatedFormula(formulaWN);
                    formulaStack.push(formulaWN); }
                 'and not exists''(' ( positiveFormula ( negatedFormula )* ) ')'
                 {  formulaStack.pop(); };

conclusionFormula: {  positiveFormula = new PositiveFormula(); 
                      dependency.setConclusion(positiveFormula); }
                  atom (',' atom )* ;

atom	:	 relationalAtom | builtin | comparison;	

relationalAtom:	 name=IDENTIFIER { atom = new RelationalAtom(name.getText()); } '(' attribute (',' attribute)* ')'
		 {  positiveFormula.addAtom(atom); atom.setFormula(positiveFormula); };

builtin	:	 expression=EXPRESSION  
                 {  atom = new BuiltInAtom(positiveFormula, new Expression(generator.clean(expression.getText()))); 
                    positiveFormula.addAtom(atom);  } ;         

comparison :	 {   expressionString = new StringBuilder(); 
		     leftVariableId = null;
		     rightVariableId = null;
		     leftConstant = null;
		     rightConstant = null;}
                 leftargument 
                 oper=OPERATOR { expressionString.append(" ").append(oper.getText()); }
                 rightargument 
                 {  Expression expression = new Expression(expressionString.toString()); 
                    atom = new ComparisonAtom(positiveFormula, expression, leftVariableId, rightVariableId, leftConstant, rightConstant, oper.getText()); 
                    positiveFormula.addAtom(atom); } ;

leftargument:	 ('\$'var=IDENTIFIER { leftVariableId = var.getText(); expressionString.append(var.getText()); } |
                 constant=(STRING | NUMBER) { leftConstant = constant.getText(); expressionString.append(constant.getText());}
                 );
                 
rightargument:	 ('\$'var=IDENTIFIER { rightVariableId = var.getText(); expressionString.append(var.getText()); } |
                 constant=(STRING | NUMBER) {  rightConstant = constant.getText(); expressionString.append(constant.getText());}
                 );

attribute:	 attr=IDENTIFIER ':' { attribute = new FormulaAttribute(attr.getText()); } value
		 { ((RelationalAtom)atom).addAttribute(attribute); } ;

value	:	 '\$'var=IDENTIFIER { attribute.setValue(new FormulaVariableOccurrence(new AttributeRef(((RelationalAtom)atom).getTableName(), attribute.getAttributeName()), var.getText())); } |
                 constant=(STRING | NUMBER) { attribute.setValue(new FormulaConstant(BartUtility.cleanConstantValue(constant.getText()))); } |
                 nullValue=NULL { attribute.setValue(new FormulaConstant(nullValue.getText(), true)); } |
                 expression=EXPRESSION { attribute.setValue(new FormulaExpression(new Expression(generator.clean(expression.getText())))); };

OPERATOR:	 '==' | '!=' | '>' | '<' | '>=' | '<=';

IDENTIFIER  :    (LETTER) (LETTER | DIGIT | '_')*;

//STRING  :  	 '"' (LETTER | DIGIT| '-' | '.' | ' ' | '_' | '*' | '/' )+ '"';
STRING  :         '"' ~('\r' | '\n' | '"')* '"';
NUMBER	: 	 ('-')? DIGIT+ ('.' DIGIT+)?;
NULL    :        '#NULL#';
fragment DIGIT:  '0'..'9' ;
fragment LETTER: 'a'..'z'|'A'..'Z' ;
WHITESPACE : 	 ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ { skip(); } ;
LINE_COMMENT :   '//' ~( '\r' | '\n' )* { skip(); } ;
EXPRESSION:      '{'(.)*'}';