package uam.compilador.analizador_lexico;
//define las operaciones del analizador lexico se puede implementar mas de una forma
public interface LexicoAnalyzer {

	void createTokenList();//crear la lista de token
	Token getToken();//saca el token y se lo da a alguien
	void setBackToken(Token t);//en algunos casos se devuelve el token a la lista
}
