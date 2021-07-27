import java_cup.runtime.*;

%%
/* ----------------- Options and Declarations Section----------------- */

%class Scanner


%line
%column


%cup
%unicode

/*
  Declarations
*/

%{
    /**
        The following two methods create java_cup.runtime.Symbol objects
    **/
    StringBuffer stringBuffer = new StringBuffer();
    private Symbol symbol(int type) {
       return new Symbol(type, yyline, yycolumn);
    }
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

/*
  Macro Declarations
*/

/* A line terminator is a \r (carriage return), \n (line feed), or \r\n.*/
LineTerminator = \r|\n|\r\n

/* White space is a line terminator, space, tab, or line feed. */
WhiteSpace     = {LineTerminator} | [ \t\f]

/* Identifier */
 Letter = [A-Za-z]
 Digit = [0-9]
 Identifier = {Letter}({Letter} | {Digit} | _ )*


%state STRING

%%
/* ------------------------Lexical Rules Section---------------------- */

<YYINITIAL> {
 "+"            { return symbol(sym.PLUS); }
 "if"           { return symbol(sym.IF); }
 "else"         { return symbol(sym.ELSE); }
 "suffix"       { return symbol(sym.SUFFIX); }
 "prefix"       { return symbol(sym.PREFIX); }
 "("            { return symbol(sym.LP); }
 ")"            { return symbol(sym.RP); }
 "{"            { return symbol(sym.LBR); }
 "}"            { return symbol(sym.RBR); }
 ","            { return symbol(sym.COMMA); }
 {Identifier}   { return symbol(sym.ID, yytext()); }
 \"             { stringBuffer.setLength(0); yybegin(STRING); }
 {WhiteSpace}   { /* just skip what was found, do nothing */ }
}

<STRING> {
      \"                             { yybegin(YYINITIAL);
                                       return symbol(sym.STRING_LITERAL, stringBuffer.toString()); }
      [^\n\r\"\\]+                   { stringBuffer.append( yytext() ); }
      \\t                            { stringBuffer.append('\t'); }
      \\n                            { stringBuffer.append('\n'); }
      \\r                            { stringBuffer.append('\r'); }
      \\\"                           { stringBuffer.append('\"'); }
      \\                             { stringBuffer.append('\\'); }
}

/* No token was found for the input so throw an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^]                    { throw new Error("Illegal character <"+yytext()+">"); }
