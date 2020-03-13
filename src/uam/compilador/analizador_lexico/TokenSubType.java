package uam.compilador.analizador_lexico;

public enum TokenSubType {

	REALNUMBER("[0-9]+\\.[0-9]+"),
	INTEGERNUMBER("[0-9]+"), 
	CHAR("\'(.*?)\'"),
	COMMA(","), 
	SEMICOLON(";"), 
	COLON(":"), 
	ARITHMETIC_ADD("+|-"),
	ARITHMETIC_MUL("*|/||%"),
	SPACE("[\t\f\r]+"),
	LINE_BREAK("\n"),
	AND("[&|\\|]"),
	//error tenia ! 
	OR("||"),
	AND2("&&"),
	NEGATION("!"),
	LEFT_PARENTHESIS("("),
	RIGHT_PARENTHESIS(")"),
//asignacion es en tokentype
	//ASSIGNMENT("[=]"),
	PROCESS("process"),ENDPROCESS("endprocess"),READ("read"),WRITE("write"),
	IF("if"),ELSE("else"),FUNCTION("function"),ENDFUNCTION("endfunction"),
	RETURN("return"),INTEGER("integer"),REAL("real"),BOOLEAN("boolean"),
	CHARACTER("character"),FALSE("false"),TRUE("true"),THEN("then"),ENDIF("endif"),
	SWITCH("switch"),DO("do"),WHILE("while"),BREAK("break"),DEFAULT("default"),
	ENDSWITCH("endswich"),ENDWHILE("endwhile"),FOR("for"),UNTIL("until"),WITH("with"),STEP("step"),ENDFOR("endfor"),DECLARATION("declaration"),ASIGNACION("asignacion");
	private final String pattern;
	private TokenSubType(String pattern) {
		this.pattern = pattern;
	}
	
	public String getType() {	
		return pattern;
	}
}
