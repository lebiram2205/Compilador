package uam.compilador.analizador_sintactico;

import java.util.LinkedList;

import uam.compilador.analizador_lexico.Alex;
import uam.compilador.analizador_lexico.Token;
import uam.compilador.analizador_lexico.TokenSubType;
import uam.compilador.analizador_lexico.TokenType;

public class Parser {
	private Alex lexico;


	private LinkedList<String> e = new LinkedList<String>();
	Parser(String source){

		lexico = new Alex(source);
		System.out.println("\nINICIA EL RECONOCIMIENTO");
		PROCESS();
		System.out.println("\nTERMINA EL RECONOCIMIENTO");


	}

	/**
	 * Muestra un mensaje de error debido a un Token incorrecto
	 *
	 * @param  tt El Token que se esperaba
	 * @param  linea Linea donde se detecto el error
	 * @return ---
	 */
	private void error(TokenSubType st, int linea) {

		System.out.println("\t\tError en la linea "+linea+" se espera "+st.name());
	}

	/**
	 * Muestra un mensaje de error debido a un Token incorrecto
	 *
	 * @param  tt El Token que se esperaba
	 * @param  linea Linea donde se detecto el error
	 * @return ---
	 */
	private void error(TokenType tt, int linea) {

		System.out.println("\t\tError en la linea "+linea+" se espera "+tt.name());
	}
	
	private void error(String string) {
		System.out.println("\t\tError "+string);

	}


	/**
	 * Muestra un mensaje de error debido a un Token incorrecto
	 *
	 * @param  rw La palabra reservada que se esperaba
	 * @return ---
	 */
	private void error(TokenSubType ts) {

		System.out.println("\t\tError...se espera "+ts.name());

	}	
	private boolean se_espera(Token t, TokenType preanalisis) {

		if(t!=null) {
			if(t.getType()==preanalisis) {
				return true;
			}//else
				//sSystem.out.println("Tipo:"+t.getType()+"  Lexema:"+t.getLexeme());

		}
		return false;
	}

	private boolean se_espera(Token t, TokenSubType preanalisis) {

		if(t!=null) {
			if(t.getSubType()==preanalisis) {
				return true;
			}//else
				//System.out.println("Sub:"+t.getSubType());

		}
		return false;
	}




	/**
	 * Identifica que tipo de operacion a reconocer. Observe
	 * que en esta version solo se reconoce una operacion por
	 * estructura de control. Ademas, observe que el 
	 * el Token leido (if, read, etc.) se devuelve a la lista de Tokens a fin que 
	 * la estructura a reconocer  no  tenga problemas.
	 */
	private boolean OPERACION() {
		Token aux;
		aux=lexico.getToken();
		if(aux!=null) {
			//Se devuelve el Token a la lista
			lexico.setBackToken(aux);

			switch(aux.getSubType()) {

			case IF:
				//Se reconoce un if. Al devolver el Token el metodo IF puede
				//indicar que espera de inicio un TokenSubType IF.
				IF();
				return true;	
			case READ:
				READ();
				return  true;						
			default:break;	

			}

		}
		System.out.println("Token No Reconocido ->  "+aux.getLexeme());
		return false;

	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void EXPRESION() {
		Y();
		OP();
	}
	
	private void Y() {
		C();
		YP();
	}
	
	private void YP() {
		Token aux;
		aux=lexico.getToken();
		if(aux!=null)
			//if(aux.getLexema().equals("&&")) {
			if(aux.getLexeme() == "&&") {
				C();
				YP();
			}else {
				lexico.setBackToken(aux);
			}

	}
	
	private void OP() {
		Token aux;
		aux=lexico.getToken();
		if(aux != null)
			//if(aux.getLexema().equals("||")) {
			if(aux.getLexeme() == "||") {
				Y();
				OP();
			}else {
				lexico.setBackToken(aux);
			}
	}
	
	private void C() {
		R();
		CP();
	}

	private void CP() {

		Token aux;
		aux=lexico.getToken();
		if(aux!=null)
			//if(aux.getLexema().equals("==")||aux.getLexema().equals("!=")
			if(aux.getLexeme() == "==" || aux.getLexeme() == "!=" ) {
				R();
				CP();
			}else {
				lexico.setBackToken(aux);
			}

	}


	private void R() {
		E();
		RP();
	}

	private void RP() {
		Token aux;
		aux=lexico.getToken();
		if(aux!=null)
//			if(aux.getLexema().equals("<")||aux.getLexema().equals(">")
//					||aux.getLexema().equals("<=")||aux.getLexema().equals(">=")) {
			if(aux.getLexeme() == "<" || aux.getLexeme() == ">" 
					|| aux.getLexeme() == "<=" || aux.getLexeme() == ">=") {
				E();
				RP();
			}else {
				lexico.setBackToken(aux);
			}
	}


	private void E() {
		T();
		EP();
	}
	private void EP() {
		Token aux;
		aux=lexico.getToken();
		if(aux!=null)
			//if(aux.getLexema().equals("+")||aux.getLexema().equals("-")) {
			if(aux.getLexeme() == "+" || aux.getLexeme() == "-") {
				T();
				EP();
			}else {
				lexico.setBackToken(aux);
			}
	}
	private void T() {

		F();
		TP();

	}

	private void TP() {
		Token aux;
		aux=lexico.getToken();
		if(aux!=null)
			if(aux.getLexeme() == "*" ||aux.getLexeme() == "/" || aux.getLexeme() == "%") {
				F();
				TP();
			}else {
				lexico.setBackToken(aux);
			}

	}



	private void F() {
//		Token aux;
//		aux=lexico.getToken();
//		if(!(se_espera(aux,TokenType.IDENTIFIER)|| 
//				se_espera(aux,TokenSubType.INTEGERNUMBER) || 
//				se_espera(aux,TokenSubType.REALNUMBER) )) {
//
//			if(!(se_espera(aux,TokenSubType.LEFT_PARENTHESIS))){
//
//				errores.error("Error en linea "+aux.getLinea()+" se espera numero o identificador");				
//			}else {
//				EXPRESION();
//				aux=lexico.getToken();
//				if(!(se_espera(aux,TokenSubType.RIGHT_PARENTHESIS))){
//					errores.error("Error en linea "+aux.getLinea()+" se espera )");				
//
//				}
//
//			}
//
//		}		

	}

	/**
	 * Inicia el reconocimiento de un SI.Observe que si la estructura
	 * Si no tiene un Sino
	 */
	private void IF() {
		Token aux; 
		

		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.IF))
			error(TokenSubType.IF);
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.LEFT_PARENTHESIS))
			error(TokenSubType.LEFT_PARENTHESIS);
			
		EXPRESION();
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS);
		
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.THEN)) {
			error(TokenSubType.THEN,aux.getLine());

		}
		aux=lexico.getToken();
		while(!se_espera(aux,TokenSubType.ELSE)&&
				!se_espera(aux,TokenSubType.ENDIF)&& aux!=null) {
			lexico.setBackToken(aux);
			aux=OPERACIONES();

		}
		if(aux==null)
			error(TokenSubType.ENDIF);
		else {

			if(aux.getSubType()==TokenSubType.ELSE) {

				aux=lexico.getToken();
				while(!se_espera(aux,TokenSubType.ENDIF)&& aux!=null) {

					lexico.setBackToken(aux);
					aux=OPERACIONES();
				}
				if(aux==null)
					error(TokenSubType.ENDIF);
			}
		}		
	}

	Token OPERACIONES() {
		Token aux;
		boolean f;
		f=OPERACION();
		if(!f) {
			aux=lexico.getToken();
			error("Error en linea "+aux.getLine()+" se recibio: "+aux.getLexeme());
			aux=lexico.getToken();
		}else
			aux=lexico.getToken();
		return aux;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void WHILE() {
		
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void WRITE() {
		
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void READ() {
		Token aux;
		String id;
		boolean salir=true;
		aux=lexico.getToken();

		if(!se_espera(aux,TokenSubType.READ))
			error(TokenSubType.READ,aux.getLine());

		do {
			aux=lexico.getToken();
			if(!se_espera(aux,TokenType.IDENTIFIER)){
				error(TokenType.IDENTIFIER,aux.getLine()); 
			}
			aux=lexico.getToken();
			salir=se_espera(aux,TokenSubType.COMMA);
			if(!salir) {
				if(!se_espera(aux,TokenSubType.SEMICOLON)){
					error(TokenSubType.SEMICOLON,aux.getLine());

				}else {
					salir=false;
				}	
			}

		}while(salir && aux!=null);	


	}

	void PROCESS() {
		Token aux;
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.PROCESS))
			error(TokenSubType.PROCESS,aux.getLine());
		aux=lexico.getToken();
		if(!se_espera(aux,TokenType.IDENTIFIER))
			error(TokenType.IDENTIFIER,aux.getLine());	
		aux=lexico.getToken();
		while(!se_espera(aux,TokenSubType.ENDPROCESS)&& aux!=null) {
			lexico.setBackToken(aux);
			aux=OPERACIONES();
		}
		if(aux==null) 
			error(TokenSubType.ENDPROCESS);

	}

	public static void main(String[] args) {
		new Parser("ejemplo.txt");


	}

}