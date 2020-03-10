package uam.compilador.analizador_sintactico;

import java.util.LinkedList;
import java.util.TreeMap;

import uam.compilador.analizador_lexico.Alex;
import uam.compilador.analizador_lexico.Token;
import uam.compilador.analizador_lexico.TokenSubType;
import uam.compilador.analizador_lexico.TokenType;
import uam.compilador.generador_codigo.Generador;

public class Parser {
	private Alex lexico;
	private TreeMap<String, Simbolo> tablaSimbolos = new TreeMap<String, Simbolo>();
	private Generador generador = new Generador(); 
	private LinkedList<String> e = new LinkedList<String>();

	Parser(String source) {

		lexico = new Alex(source);
		System.out.println("\nINICIA EL RECONOCIMIENTO");
		//PROCESS();
		//FUNCTION();
		PROGRAM();
		System.out.println("\nTERMINA EL RECONOCIMIENTO");
		for(Simbolo s:tablaSimbolos.values()) {
			System.out.println(s);
		}
	}

	/**
	 * Muestra un mensaje de error debido a un Token incorrecto
	 *
	 * @param tt
	 *            El Token que se esperaba
	 * @param linea
	 *            Linea donde se detecto el error
	 * @return ---
	 */
	private void error(TokenSubType st, int linea) {

		System.out.println("\t\tError en la linea " + linea + " se espera " + st.name());
	}

	/**
	 * Muestra un mensaje de error debido a un Token incorrecto
	 *
	 * @param tt
	 *            El Token que se esperaba
	 * @param linea
	 *            Linea donde se detecto el error
	 * @return ---
	 */
	private void error(TokenType tt, int linea) {

		System.out.println("\t\tError en la linea " + linea + " se espera " + tt.name());
	}

	private void error(String string) {
		System.out.println("\t\tError " + string);

	}

	/**
	 * Muestra un mensaje de error debido a un Token incorrecto
	 *
	 * @param rw
	 *            La palabra reservada que se esperaba
	 * @return ---
	 */
	private void error(TokenSubType ts) {

		System.out.println("\t\tError...se espera " + ts.name());

	}

	private boolean se_espera(Token t, TokenType preanalisis) {

		if (t != null) {
			if (t.getType() == preanalisis) {
				return true;
			} // else
				// sSystem.out.println("Tipo:"+t.getType()+" Lexema:"+t.getLexeme());

		}
		return false;
	}

	private boolean se_espera(Token t, TokenSubType preanalisis) {

		if (t != null) {
			if (t.getSubType() == preanalisis) {
				return true;
			} // else
				// System.out.println("Sub:"+t.getSubType());

		}
		return false;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void PROGRAM() {
		Token aux;
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.PROCESS))
			error("El sistema debe iniciarse con un PROCESS");
		lexico.setBackToken(aux);
		PROCESS();
		aux=lexico.getToken();
		if(aux!=null) {
			lexico.setBackToken(aux);
			LISTA_FUNCIONES();
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void PROCESS() {
		Token aux;
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.PROCESS))
			error(TokenSubType.PROCESS, aux.getLine());
		aux = lexico.getToken();
		if (!se_espera(aux, TokenType.IDENTIFIER))
			error(TokenType.IDENTIFIER, aux.getLine());
		aux = lexico.getToken();
		while (!se_espera(aux, TokenSubType.ENDPROCESS) && aux != null) {
			lexico.setBackToken(aux);
			aux = OPERACIONES("\t");
		}
		if (aux == null)
			error(TokenSubType.ENDPROCESS);

	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Identifica que tipo de operacion a reconocer. Observe que en esta version
	 * solo se reconoce una operacion por estructura de control. Ademas, observe que
	 * el el Token leido (if, read, etc.) se devuelve a la lista de Tokens a fin que
	 * la estructura a reconocer no tenga problemas.
	 */
	private boolean OPERACION(String t) {
		Token aux;
		aux=lexico.getToken();
		//System.out.println("MIToken:"+aux);
		if(aux!=null) {
			//Se devuelve el Token a la lista
			lexico.setBackToken(aux);

			if(aux.getSubType()!=null) {
				switch(aux.getSubType()) {

				case IF:
					//Se reconoce un if. Al devolver el Token el metodo IF puede
					//indicar que espera de inicio un TokenSubType IF.
					IF(t);
					return true;	
				case READ:
					READ();
					return  true;
				case WRITE:
					WRITE();
					return  true;
				case WHILE:
					WHILE();
					return  true;
				case DO:
					DOWHILE();
					return true;
				case INTEGER:
					DECLARATION();
					return true;
				case REAL:
					DECLARATION();
					return true;
				case BOOLEAN:
					DECLARATION();
					return true;
				case FOR:
					FOR();
					return true;
	
				default:break;	


				}
			}else {
				switch(aux.getType()) {

				case IDENTIFIER:
					ASIGNACION();
					return true;

				}
				
				
			}
		}
		System.out.println("Token No Reconocido ->  "+aux.getLexeme());
		return false;

	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	Token OPERACIONES(String t) {
		Token aux;
		boolean f;
		f = OPERACION(t);
		if (!f) {
			aux = lexico.getToken();
			error("Error en linea " + aux.getLine() + " se recibio: " + aux.getLexeme());
			aux = lexico.getToken();
		} else
			aux = lexico.getToken();
		return aux;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// DECLARACIONES
	private void DECLARATION() {
		Token aux;
		Simbolo s=null;
		TokenSubType tr;
		Token tipo;
		Token nombre;
		
		aux=lexico.getToken();
		
		if(se_espera(aux,TokenSubType.INTEGER)) {
			aux=lexico.getToken();
			if(se_espera(aux,TokenType.IDENTIFIER)) {
				//
				nombre=aux;
				//
				aux=lexico.getToken();
				while(!se_espera(aux,TokenSubType.SEMICOLON)&& aux!=null) {
					lexico.setBackToken(aux);
					aux=lexico.getToken();
					
					if(se_espera(aux,TokenSubType.COMMA)) {
						//********
						 //SE CREA UN Simbolo CON EL LEXEMA Y EL TIPO (INTEGER)
						s=new Simbolo(nombre.getLexeme(),TokenSubType.INTEGERNUMBER);
						if(!tablaSimbolos.containsKey(s.getNombre()))
							tablaSimbolos.put(s.getNombre(), s);
						else//SI LA VARIABLE YA ESTA DECLARADA, HAY UN ERROR
							error("Error: La variable "+s.getNombre()+" ya fue declarada");
						 //********
						aux=lexico.getToken();
						if(!se_espera(aux,TokenType.IDENTIFIER))
							error(TokenType.IDENTIFIER,aux.getLine());
						//******
						nombre=aux;
						//******
					}else if(se_espera(aux,TokenType.ASSIGNMENT)) {
						aux=lexico.getToken();
						if(!se_espera(aux,TokenSubType.INTEGERNUMBER))
							error(TokenSubType.INTEGERNUMBER,aux.getLine());
						//***************************
						s=new Simbolo(nombre.getLexeme(), Integer.parseInt(aux.getLexeme()),TokenSubType.INTEGERNUMBER);

						if(!tablaSimbolos.containsKey(s.getNombre())) 
							tablaSimbolos.put(s.getNombre(), s);
						else 
							error("Error: La variable "+s.getNombre()+" ya fue declarada");
						
						//***************************************
					}
					aux=lexico.getToken();
				}
				if(aux==null) 
					error(TokenSubType.SEMICOLON);
				//
				s=new Simbolo(nombre.getLexeme(),TokenSubType.INTEGERNUMBER);
				if(!tablaSimbolos.containsKey(s.getNombre()))
					tablaSimbolos.put(s.getNombre(), s);
				//else
					//error("Error: La variable "+s.getNombre()+" ya fue declarada");

				//
			}else {error(TokenType.IDENTIFIER,aux.getLine());}
		}
		///////////////////////////////////////////////////////////////
		if(se_espera(aux,TokenSubType.REAL)) {
			aux=lexico.getToken();
			if(se_espera(aux,TokenType.IDENTIFIER)) {
				aux=lexico.getToken();
				while(!se_espera(aux,TokenSubType.SEMICOLON)&& aux!=null) {
					lexico.setBackToken(aux);
					aux=lexico.getToken();
					if(se_espera(aux,TokenSubType.COMMA)) {
						aux=lexico.getToken();
						if(!se_espera(aux,TokenType.IDENTIFIER))
							error(TokenType.IDENTIFIER,aux.getLine());
					}else if(se_espera(aux,TokenType.ASSIGNMENT)) {
						aux=lexico.getToken();
						if(!se_espera(aux,TokenSubType.REALNUMBER))
							error(TokenSubType.REALNUMBER,aux.getLine());
					}
					aux=lexico.getToken();
				}
				if(aux==null) 
					error(TokenSubType.SEMICOLON);
			}else {error(TokenType.IDENTIFIER,aux.getLine());}
		}
		//////////////////////////////////////////////////////////////////
		if(se_espera(aux,TokenSubType.BOOLEAN)) {
			aux=lexico.getToken();
			if(se_espera(aux,TokenType.IDENTIFIER)) {
				aux=lexico.getToken();
				while(!se_espera(aux,TokenSubType.SEMICOLON)&& aux!=null) {
					lexico.setBackToken(aux);
					aux=lexico.getToken();
					if(se_espera(aux,TokenSubType.COMMA)) {
						aux=lexico.getToken();
						if(!se_espera(aux,TokenType.IDENTIFIER))
							error(TokenType.IDENTIFIER,aux.getLine());
					}else if(se_espera(aux,TokenType.ASSIGNMENT)) {
						aux=lexico.getToken();
						if(!(se_espera(aux,TokenSubType.TRUE)||se_espera(aux,TokenSubType.FALSE)))
							error(TokenSubType.BOOLEAN,aux.getLine());
					}
					aux=lexico.getToken();
				}
				if(aux==null) 
					error(TokenSubType.SEMICOLON);
			}else {error(TokenType.IDENTIFIER,aux.getLine());}
		}
		////////////////////////////////////////////////////////////////
		if(se_espera(aux,TokenSubType.CHARACTER)) {
			aux=lexico.getToken();
			if(se_espera(aux,TokenType.IDENTIFIER)) {
				aux=lexico.getToken();
				while(!se_espera(aux,TokenSubType.SEMICOLON)&& aux!=null) {
					lexico.setBackToken(aux);
					aux=lexico.getToken();
					if(se_espera(aux,TokenSubType.COMMA)) {
						aux=lexico.getToken();
						if(!se_espera(aux,TokenType.IDENTIFIER))
							error(TokenType.IDENTIFIER,aux.getLine());
					}else if(se_espera(aux,TokenType.ASSIGNMENT)) {
						aux=lexico.getToken();
						if(!se_espera(aux,TokenSubType.CHARACTER))//no hay declaracion alguna para un caracter en tokensubtype
							error(TokenSubType.CHARACTER,aux.getLine());
					}
					aux=lexico.getToken();
				}
				if(aux==null) 
					error(TokenSubType.SEMICOLON);
			}else {error(TokenType.IDENTIFIER,aux.getLine());}
		}
	}
	

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// READ
	private void READ() {
		Token aux;
		String id;
		boolean salir = true;
		aux = lexico.getToken();

		if (!se_espera(aux, TokenSubType.READ))
			error(TokenSubType.READ, aux.getLine());

		do {
			aux = lexico.getToken();
			if (!se_espera(aux, TokenType.IDENTIFIER)) {
				error(TokenType.IDENTIFIER, aux.getLine());
			}
			aux = lexico.getToken();
			salir = se_espera(aux, TokenSubType.COMMA);
			if (!salir) {
				if (!se_espera(aux, TokenSubType.SEMICOLON)) {
					error(TokenSubType.SEMICOLON, aux.getLine());

				} else {
					salir = false;
				}
			}

		} while (salir && aux != null);

	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// WRITE
	private void WRITE() {
		Token aux;

		boolean salir=true;
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.WRITE))
			error(TokenSubType.WRITE,aux.getLine());

		do {

			aux=lexico.getToken();
			salir=se_espera(aux,TokenType.STRING);
			if(!salir) {

				lexico.setBackToken(aux);
				EXPRESION();

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

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ASIGNACION
	private void ASIGNACION() {
	Token aux;
	aux = lexico.getToken();
	if (!se_espera(aux, TokenType.IDENTIFIER))
		error(TokenType.IDENTIFIER, aux.getLine());
	aux = lexico.getToken();
	if (!se_espera(aux,TokenType.ASSIGNMENT))
		error(TokenType.ASSIGNMENT,aux.getLine());
	//<-------------------------------------------------------------------------------------
	EXPRESION();
	aux = lexico.getToken();
	if (!se_espera(aux,TokenSubType.SEMICOLON))
		error(TokenSubType.SEMICOLON,aux.getLine());
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// IF
	/**
	 * Inicia el reconocimiento de un SI.Observe que si la estructura Si no tiene un
	 * Sino
	 */
	private void IF(String t) {
		Token aux; 
		int etiqueta1,etiqueta2,etiqueta3;
		String expresion="";
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.IF))
			error(TokenSubType.IF);

		aux=lexico.getToken();
		
		if(!se_espera(aux,TokenSubType.LEFT_PARENTHESIS))
			error(TokenSubType.LEFT_PARENTHESIS);

		EXPRESION();
		while(!e.isEmpty())
			expresion=expresion+e.pop();

		etiqueta1=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		etiqueta2=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		etiqueta3=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		generador.emitir(t+"cmp "+expresion+" true");
		generador.emitir(t+"jmpc ETIQUETA"+etiqueta1);
		generador.emitir(t+"jump ETIQUETA"+etiqueta2);
		
		aux=lexico.getToken();
		
		if(!se_espera(aux,TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS);

		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.THEN)) {
			error(TokenSubType.THEN,aux.getLine());

		}
		generador.emitir(t+"ETIQUETA"+etiqueta1+":");
		aux=lexico.getToken();
		while(!se_espera(aux,TokenSubType.ELSE)&&
				!se_espera(aux,TokenSubType.ENDIF)&& aux!=null) {
			lexico.setBackToken(aux);
			aux=OPERACIONES(t+"\t");

		}
		if(aux==null)
			error(TokenSubType.ENDIF);
		else {

			if(aux.getSubType()==TokenSubType.ELSE) {

				generador.emitir(t+"jump ETIQUETA"+etiqueta3);
				generador.emitir(t+"ETIQUETA"+etiqueta2+":");
				aux=lexico.getToken();
				while(!se_espera(aux,TokenSubType.ENDIF)&& aux!=null) {

					lexico.setBackToken(aux);
					aux=OPERACIONES(t+"\t");
				}
				if(aux==null)
					error(TokenSubType.ENDIF);
				else
					generador.emitir(t+"ETIQUETA"+etiqueta3+":");
			}else
				generador.emitir(t+"ETIQUETA"+etiqueta2+":");
		}		
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void LISTA_FUNCIONES() {
		Token aux;
		aux=lexico.getToken();
		while(se_espera(aux,TokenSubType.FUNCTION)) {
			lexico.setBackToken(aux);
			FUNCTION();
			aux=lexico.getToken();
		}lexico.setBackToken(aux);
		
	}
	// FUNCION
	void FUNCTION() {
		Token aux;
		//aux=lexico.getToken();
		//if(!se_espera(aux,TokenSubType.INTEGER))
			//error(TokenSubType.INTEGER);
		TYPE();
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.FUNCTION))
			error(TokenSubType.FUNCTION);
		aux=lexico.getToken();
		if(!se_espera(aux,TokenType.IDENTIFIER))
			error(TokenType.IDENTIFIER,aux.getLine());
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.LEFT_PARENTHESIS))
			error(TokenSubType.LEFT_PARENTHESIS);
		LISTAP();
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS);
		
		aux = lexico.getToken();
		while (!se_espera(aux, TokenSubType.RETURN) && aux != null) {

			lexico.setBackToken(aux);
			aux = OPERACIONES("\t");
		}
		if (aux == null)
			error(TokenSubType.RETURN);

		
		//aux=lexico.getToken();
		//if(!se_espera(aux,TokenSubType.RETURN))
			//error(TokenSubType.RETURN);
		EXPRESION();
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.SEMICOLON))
			error(TokenSubType.SEMICOLON);
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.ENDFUNCTION))
			error(TokenSubType.ENDFUNCTION);
	}
	
	private void TYPE() {
		Token aux;
		aux=lexico.getToken();
		if(!(se_espera(aux,TokenSubType.INTEGER)||se_espera(aux,TokenSubType.REAL)||se_espera(aux,TokenSubType.BOOLEAN)||se_espera(aux,TokenSubType.CHARACTER)))
			error(TokenType.KEY_WORD,aux.getLine());
	}
	
	private void LISTAP() {
		Token aux;
		aux=lexico.getToken();
		if(se_espera(aux,TokenType.IDENTIFIER)) {
			aux=lexico.getToken();
			if(se_espera(aux,TokenSubType.COMMA))
				LISTAP();
			lexico.setBackToken(aux);
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// WHILE
	private void WHILE() {
		Token aux;
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.WHILE))
			error(TokenSubType.WHILE, aux.getLine());
		
		EXPRESION();
		aux = lexico.getToken();
		while (!se_espera(aux, TokenSubType.ENDWHILE) && aux != null) {
			lexico.setBackToken(aux);
			aux = OPERACIONES("\t");
		}
		if (aux == null)
			error(TokenSubType.ENDWHILE);

	}
	//CORREGIDO
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// DOWHILE
	private void DOWHILE() {
		Token aux;
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.DO))
			error(TokenSubType.DO);
		aux=lexico.getToken();
		while(!se_espera(aux,TokenSubType.WHILE) && aux!=null) {
			lexico.setBackToken(aux);
			aux=OPERACIONES("\t");
		}if(aux==null) {
			error(TokenSubType.WHILE);
		}
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.LEFT_PARENTHESIS))
			error(TokenSubType.LEFT_PARENTHESIS);
		EXPRESION();
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS);
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.SEMICOLON))
			error(TokenSubType.SEMICOLON);
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// FOR
	private void FOR() {
		Token aux;
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.FOR))
			error(TokenSubType.FOR);
		aux=lexico.getToken();
		if(!se_espera(aux,TokenType.IDENTIFIER))
			error(TokenType.IDENTIFIER,aux.getLine());
		aux=lexico.getToken();
		if(!se_espera(aux,TokenType.ASSIGNMENT))
			error(TokenType.ASSIGNMENT,aux.getLine());
		aux=lexico.getToken();
		if(!(se_espera(aux,TokenSubType.INTEGERNUMBER)||se_espera(aux,TokenType.IDENTIFIER)))/////////////////
			error("ERROR EN EL VALOR INGRESADO");
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.UNTIL))
			error(TokenSubType.UNTIL);
		aux=lexico.getToken();
		if(!(se_espera(aux,TokenSubType.INTEGERNUMBER)||se_espera(aux,TokenType.IDENTIFIER)))////////////////////
			error("ERROR EN EL VALOR INGRESADO");
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.LEFT_PARENTHESIS))
			error(TokenSubType.LEFT_PARENTHESIS);
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.WITH))
			error(TokenSubType.WITH);
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.STEP))
			error(TokenSubType.STEP);
		aux=lexico.getToken();
		if(!(se_espera(aux,TokenSubType.INTEGERNUMBER)||se_espera(aux,TokenType.IDENTIFIER)))/////////////////////////
			error("ERROR EN EL VALOR INGRESADO");
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS);
		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.DO))
			error(TokenSubType.DO);
		aux=lexico.getToken();
		while(!se_espera(aux,TokenSubType.ENDFOR) && aux!=null) {
			lexico.setBackToken(aux);
			aux=OPERACIONES("\t");
		}if(aux==null) {
			error(TokenSubType.ENDFOR);
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// EXPRESIONES
	private void EXPRESION() {
		O();
	}
	private void O() {
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
		if(aux!=null) {
			if(aux.getLexeme().equals("&&")) {
				C();
				YP();
			}else {
				lexico.setBackToken(aux);
			}}	
	}
	private void OP() {
		Token aux;
		aux = lexico.getToken();
		if(aux!=null) {
		if (aux.getLexeme().equals("||")) {
			Y();
			OP();
		} else {
			lexico.setBackToken(aux);
		}
		}
	}

	private void C() {
		R();
		CP();
	}

	private void CP() {
		Token aux;
		aux = lexico.getToken();
		if(aux!=null) {
		if (aux.getLexeme().equals("==")||aux.getLexeme().equals("!=")) {
			R();
			CP();
		} else {
			lexico.setBackToken(aux);
		}}

	}

	private void R() {
		E();
		RP();
	}

	private void RP() {
		Token aux;
		aux = lexico.getToken();
		if(aux!=null) {
		if (aux.getLexeme().equals("<")||aux.getLexeme().equals(">")
				||aux.getLexeme().equals(">=")||aux.getLexeme().equals("<=")) {
			E();
			RP();
		} else {
			lexico.setBackToken(aux);
		}}
	}// fin RP

	private void E() {
		T();
		EP();
	}

	private void EP() {
		Token aux;
		aux=lexico.getToken();
		if(aux!=null)
			if(aux.getLexeme().equals("+")||aux.getLexeme().equals("-")) {
				//e.add(aux.getLexeme()+"");
				T();
				EP();
			}else {
				lexico.setBackToken(aux);
			}
	}

	private void T() {

		N();
		TP();

	}

	private void TP() {
		Token aux;
		aux=lexico.getToken();
		if(aux!=null)
			if(aux.getLexeme().equals("*")||aux.getLexeme().equals("/")||aux.getLexeme().equals("%")) {
				//e.add(aux.getLexeme()+"");
				N();
				TP();
			}else {

				lexico.setBackToken(aux);
			}

	}

	private void N() {

		Token aux;
		aux=lexico.getToken();
		if(aux!=null)
			if(aux.getLexeme().equals("!")) {
				//e.add(aux.getLexeme()+"");
				F();
			}else {
				lexico.setBackToken(aux);
				F();
			}
	}


		
	private void F() {
		Token aux;
		aux=lexico.getToken();
		if(!(se_espera(aux,TokenType.IDENTIFIER)|| 
				se_espera(aux,TokenSubType.INTEGERNUMBER) || 
				se_espera(aux,TokenSubType.REALNUMBER) )) {

			if(!(se_espera(aux,TokenSubType.LEFT_PARENTHESIS))){

				error("Error en linea "+aux.getLine()+" se espera numero o identificador");				
			}else {
				//e.add(aux.getLexeme()+"");
				EXPRESION();
				aux=lexico.getToken();
				if(!(se_espera(aux,TokenSubType.RIGHT_PARENTHESIS))){
					error("Error en linea "+aux.getLine()+" se espera )");				

				}//else
					//e.add(aux.getLexeme()+"");

			}

		}
//		else
//			e.add(aux.getLexeme()+"");
		//System.out.println("\t   Operador Expresion:"+aux.getData());
	}
	public static void main(String[] args) {
		//new Parser("ejemplo.txt");
		//new Parser("programa1.txt");
		new Parser("ejemploprofe.txt");
		

	}

}