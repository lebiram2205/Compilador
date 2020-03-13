package uam.compilador.analizador_lexico;

/*
 * La clase TokenType define los patrones de los lenguajes a reconocer.
 * Todas las palabras se reconocen como identificadores y despues se 
 * identifica si es (o no) una palabra reservada.
 * */
/*mis constantes que definen los token*/
public enum TokenType{
	COMMENT("|//.*|/\\*.*\\*/|"),
	NUMBER("[0-9]+\\.[0-9]+|[0-9]+"), 
	PUNCTUATION(",|;|:"), 
	STRING("\"(.*?)\""),
	ARITHMETIC_OPERATOR("\\*|/|\\+|-|%"),
	WHITE_SPACE("[\t|\f|\r|\n]+"),
	IDENTIFIER("[_[a-zA-Z]][_[a-zA-Z]\\d]*"),
	RELATIONAL_OPERATOR("<|>!=|<=|>=|>"),
	COMPARISON_OPERATOR("!=|=="),
	LOGICAL_OPERATOR("[&&|\\|||!]"),
	PARENTHESIS("(|)"),
	ASSIGNMENT("[=]"),
	KEY_WORD("process|endprocess|function|endfunction|return|integer|real|boolean|character|false|true|read|white|if|then|else|endif|switch|do|break|default|endswitch|while|endwhile|for|until|with|step|endfor");
	
	private final String pattern;
	private TokenType(String pattern) {
		this.pattern = pattern;
	}
	
	public String getType() {	
		return pattern;
	}
	
}
