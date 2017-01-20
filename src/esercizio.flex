/* JFlex example: part of Java language lexer specification */
import java_cup.runtime.*;
import java_cup.*;
import supportItems.*;
/**
* This class is a simple example lexer.
*/
%%
%class Lexer
%unicode
%line
%column
%cup
%{
private TableOfSymbols table = new TableOfSymbols();

int comments_level;
  StringBuffer string = new StringBuffer();


  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }

  private Symbol symbol(Object value, int type) {

    java.util.Map.Entry entry = table.addLexem(value.toString());
    table.addAttribute(value.toString(), new Attributo("class", type + ""));

    return new Symbol(type, yyline, yycolumn, value);
  }
%}

EndProgram = "."

/* Relational Operators */
LessThen = "<"
GreaterThen = ">"
LessEquals = "<="
GreaterEquals = ">="
EqualEqual = "=="
NotEqual = "!="
RelOp = {LessThen} | {GreaterThen} | {LessEquals} | {GreaterEquals} | {EqualEqual} | {NotEqual}
Not = "not"

Read = "<-"
Write = "->"

Multiplication = "*" | "&&"
Division = "/"
MultiOperators = {Multiplication} | {Division}

Plus = "+" | "||"
Minus = "-"

AddOperators = {Plus} | {Minus}

Assignement = ":="

/* separators */
Lpar = "("
Rpar = ")"

Semi = ";"
Comma = ","
Colon = ":"

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n']
Character = \'{InputCharacter}\'
WhiteSpace = {LineTerminator} | [ \t\f]
/* integer literals */
Number = 0 | [1-9][0-9]*
NumberFloat =[0-9]+ \. [0-9]*

/* comments */
TraditionalCommentInit = "/*"
TraditionalCommentEnd = "*/"
TraditionalCommentBody = [^"/*"]*

NoTraditionalComment = {EndOfLineComment} | {DocumentationComment}

CommentContent = ( [^*] | \*+ [^/*] )*
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/**" {CommentContent} "*"+ "/"
/*end comments*/

If          =   "if"
Else        =   "else"
Then        =   "then"
Begin       =   "begin"
End         =   "end"
Var         =   "var"
While       =   "while"
Procedure   =   "procedure"
Program     =   "program"
Boolean     =   "boolean"
True        =   "true"
Integer     =   "integer"
False       =   "false"
Do          =   "do"
for         =   "for"
Switch      =   "switch"
Case        =   "case"
Break       =   "break"
Default      =   "default"




/*identifier*/
Identifier = [:jletter:] [:jletterdigit:]*
%state STRING, COMMENT
%%

<YYINITIAL> {

/* keywords */
{If}                            {return symbol(sym.IF); }
{Else}                          {return symbol(sym.ELSE); }
{Then}                          {return symbol(sym.THEN); }
{Begin}                         {return symbol(sym.BEGIN); }
{End}                           {return symbol(sym.END); }
{Var}                           {return symbol(sym.VAR); }
{While}                         {return symbol(sym.WHILE); }
{Procedure}                     {return symbol(sym.PROCEDURE);}
{Program}                       {return symbol(sym.PROGRAM);}
{Boolean}                       {return symbol(sym.BOOLEAN);}
{True}                          {return symbol(sym.TRUE);}
{False}                         {return symbol(sym.FALSE);}
{Integer}                       {return symbol(sym.INTEGER);}
{Do}                            {return symbol(sym.DO);}
{Not}                           {return symbol(sym.NOT);}
{for}                           {return symbol(sym.FOR);}
{Semi}                          {return symbol("INSTRUCTION_SEPARATOR",sym.INSTRUCTION_SEPARATOR);}
{Switch}                        {return symbol(sym.SWITCH);}
{Case}                          {return symbol(sym.CASE);}
{Break}                         {return symbol(sym.BREAK);}
{Default}                       {return symbol(sym.DEFAULT);}
{Colon}                         {return symbol(sym.COLON);}





/* identifiers */
{Identifier}                    { return symbol(yytext(), sym.IDENTIFIER); }
\"                              { string.setLength(0); yybegin(STRING); }
/* operators */
{Assignement}                   { return symbol(sym.ASSIGN); }

{RelOp}                         { return symbol(yytext(), sym.RELOP);}

{Read}                          {return symbol("READ",sym.READ);}
{Write}                         {return symbol("WRITE",sym.WRITE);}

{Minus}                         { return symbol("MINUS", sym.MINUS); }
{AddOperators}                  { return symbol(yytext(), sym.ADD_OPERATOR); }

{MultiOperators}                { return symbol(yytext(), sym.MULT_OPERATOR); }
/* separators */
{Lpar}                          { return symbol("LPAREN",sym.LPAR); }
{Rpar}                          { return symbol("RPAREN",sym.RPAR); }
{Comma}                         { return symbol("SEPARATOR",sym.SEPARATOR); }
{EndProgram}                    { return symbol("END_PROGRAM",sym.END_PROGRAM); }
/* numeric literals */
{Number}                        {return symbol(new Integer(yytext()).toString(), sym.INTEGER_CONSTANT); }

/* whitespace */
{WhiteSpace}                    { /* ignore */ }
{TraditionalCommentInit}        {comments_level ++; yybegin(COMMENT); }
{NoTraditionalComment}          {/*Ignore if is a EndOfLineComment or a DocumentationComment*/}

/* character */
{Character} { return symbol(yytext(), sym.CHARACTER_CONSTANT);}
}
<STRING> {
<<EOF>>                         { throw new Error("Fine file raggiunto"); }
\"                              { yybegin(YYINITIAL); return symbol("\""+ string.toString() + "\"", sym.STRING_CONSTANT); }
[^\n\r\"\\]+                    { string.append( yytext() ); }
\\t                             { string.append('\t'); }
\\n                             { string.append('\n'); }
\\r                             { string.append('\r'); }
\\\"                            { string.append('\"'); }
\\                              { string.append('\\'); }
}
<COMMENT> {

{TraditionalCommentEnd}         { if( -- comments_level == 0) yybegin(YYINITIAL); }
{TraditionalCommentInit}        { comments_level ++; }
{TraditionalCommentBody}        { /* ignore */}

<<EOF>>                         { throw new Error("Fine file raggiunto"); }
}
/* error fallback */
[^]                             { throw new Error("Illegal character <"+ yytext()+">"); }
