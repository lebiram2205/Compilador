package uam.compilador.analizador_sintactico;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeMap;

import uam.compilador.analizador_lexico.Alex;
import uam.compilador.analizador_lexico.Token;
import uam.compilador.analizador_lexico.TokenSubType;
import uam.compilador.analizador_lexico.TokenType;
import uam.compilador.generador_codigo.Generador;

public class Parser {
	private Alex lexico;

	private static TreeMap<String, Simbolo> tablaSimbolos = new TreeMap<String, Simbolo>();
	private Generador generador = new Generador(); 

	private LinkedList<String> e = new LinkedList<String>();
	private LinkedList<String> code = new LinkedList<String>();
	static File file_simbolos;
	static File file_code;
	static BufferedWriter write;
	int contadoErrores=0;

	Parser(String source) throws IOException {

		lexico = new Alex(source);
		System.out.println("\nINICIA EL RECONOCIMIENTO");
		// PROCESS();
		// FUNCTION();
		PROGRAM();
		FileSimbolos();
		archivoCodigo();
		System.out.println("\nTERMINA EL RECONOCIMIENTO");

		
		/*for(Simbolo s:tablaSimbolos.values()) {

			System.out.println(s);
		}*/
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
		contadoErrores++;
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
		contadoErrores++;
		System.out.println("\t\tError en la linea " + linea + " se espera " + tt.name());
	}

	private void error(String string) {
		contadoErrores++;
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
		contadoErrores++;
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
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.PROCESS))
			error("El sistema debe iniciarse con un PROCESS");
		lexico.setBackToken(aux);
		PROCESS();
		if (aux != null) {
			LISTA_FUNCIONES();
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void PROCESS() {
		Token aux;
		String lex="";
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.PROCESS))
			error(TokenSubType.PROCESS, aux.getLine());
		lex=aux.getLexeme();
		code.add(generador.emitir("begin"+" "+ lex ));
		lex="";
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
		lex=aux.getLexeme();
		code.add(generador.emitir("end"+ " "+lex ));

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
		aux = lexico.getToken();
		// System.out.println("MIToken:"+aux);
		if (aux != null) {
			// Se devuelve el Token a la lista
			lexico.setBackToken(aux);

			if (aux.getSubType() != null) {
				switch (aux.getSubType()) {

				case IF:
					// Se reconoce un if. Al devolver el Token el metodo IF puede
					// indicar que espera de inicio un TokenSubType IF.
					IF(t);
					return true;
				case READ:
					READ();
					return true;
				case WRITE:
					WRITE();
					return true;
				case WHILE:
					WHILE(t);
					return true;
				case DO:
					DOWHILE(t);
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
				case CHARACTER:
					DECLARATION();
					return true;
				case FOR:
					FOR(t);
					return true;

				default:
					break;

				}
			} else {
				switch (aux.getType()) {

				case IDENTIFIER:
					ASIGNACION();
					return true;

				}

			}
		}
		System.out.println("Token No Reconocido ->  " + aux.getLexeme());
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
					aux = lexico.getToken();
				}
				if (aux == null)
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
						s=new Simbolo(nombre.getLexeme(),TokenSubType.REALNUMBER);
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
						if(!se_espera(aux,TokenSubType.REALNUMBER))
							error(TokenSubType.REALNUMBER,aux.getLine());
						//***************************
						s=new Simbolo(nombre.getLexeme(), Double.parseDouble(aux.getLexeme()),TokenSubType.REALNUMBER);

						if(!tablaSimbolos.containsKey(s.getNombre())) 
							tablaSimbolos.put(s.getNombre(), s);
						else 
							error("Error: La variable "+s.getNombre()+" ya fue declarada");
						
						//***************************************

					}
					aux = lexico.getToken();
				}
				if (aux == null)
					error(TokenSubType.SEMICOLON);

				//
				s=new Simbolo(nombre.getLexeme(),TokenSubType.REALNUMBER);
				if(!tablaSimbolos.containsKey(s.getNombre()))
					tablaSimbolos.put(s.getNombre(), s);
				//else
					//error("Error: La variable "+s.getNombre()+" ya fue declarada");

				//
			}else {error(TokenType.IDENTIFIER,aux.getLine());}
		}
		//////////////////////////////////////////////////////////////////
		if(se_espera(aux,TokenSubType.BOOLEAN)) {
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
						s=new Simbolo(nombre.getLexeme(),TokenSubType.BOOLEAN);
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
						if(!(se_espera(aux,TokenSubType.TRUE)||se_espera(aux,TokenSubType.FALSE)))
							error(TokenSubType.BOOLEAN,aux.getLine());
						//***************************
						s=new Simbolo(nombre.getLexeme(), Boolean.parseBoolean(aux.getLexeme()),TokenSubType.BOOLEAN);

						if(!tablaSimbolos.containsKey(s.getNombre())) 
							tablaSimbolos.put(s.getNombre(), s);
						else 
							error("Error: La variable "+s.getNombre()+" ya fue declarada");
						
						//***************************************

					}
					aux = lexico.getToken();
				}
				if (aux == null)
					error(TokenSubType.SEMICOLON);

				//
				s=new Simbolo(nombre.getLexeme(),TokenSubType.REALNUMBER);
				if(!tablaSimbolos.containsKey(s.getNombre()))
					tablaSimbolos.put(s.getNombre(), s);
				//else
					//error("Error: La variable "+s.getNombre()+" ya fue declarada");

				//
			}else {error(TokenType.IDENTIFIER,aux.getLine());}
		}
		////////////////////////////////////////////////////////////////
		if(se_espera(aux,TokenSubType.CHARACTER)) {
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
						s=new Simbolo(nombre.getLexeme(),TokenSubType.CHAR);
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
						if(!se_espera(aux,TokenSubType.CHAR))//no hay declaracion alguna para un caracter en tokensubtype
							error("ERROR RECIBE UN CARACTER");
						//***************************
						s=new Simbolo(nombre.getLexeme(), aux.getLexeme() ,TokenSubType.CHAR);

						if(!tablaSimbolos.containsKey(s.getNombre())) 
							tablaSimbolos.put(s.getNombre(), s);
						else 
							error("Error: La variable "+s.getNombre()+" ya fue declarada");
						
						//***************************************

					}
					aux = lexico.getToken();
				}
				if (aux == null)
					error(TokenSubType.SEMICOLON);

				//
				s=new Simbolo(nombre.getLexeme(),TokenSubType.REALNUMBER);
				if(!tablaSimbolos.containsKey(s.getNombre()))
					tablaSimbolos.put(s.getNombre(), s);
				//else
					//error("Error: La variable "+s.getNombre()+" ya fue declarada");

				//
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
			} else {
				id = aux.getLexeme();
				code.add(generador.emitir("input " + id));
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
		String cadena = "";
		String expresion = "";
		boolean salir = true;
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.WRITE))
			error(TokenSubType.WRITE, aux.getLine());

		do {

			aux = lexico.getToken();
			salir = se_espera(aux, TokenType.STRING);
			if (!salir) {

				lexico.setBackToken(aux);
				EXPRESION();
				while (!e.isEmpty())
					expresion = expresion + e.pop();
				code.add(generador.emitir("output " + expresion));
				expresion = "";
			} else {
				cadena = aux.getLexeme();
				code.add(generador.emitir("output " + cadena));
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
	// ASIGNACION
	private void ASIGNACION() {
		String expresion="";
		String id="";
		Token aux;
		aux = lexico.getToken();
		if (!se_espera(aux, TokenType.IDENTIFIER))
			error(TokenType.IDENTIFIER, aux.getLine());
		id= aux.getLexeme();
			
		aux = lexico.getToken();
		if (!se_espera(aux, TokenType.ASSIGNMENT))
			error(TokenType.ASSIGNMENT, aux.getLine());
		EXPRESION();
		while(!e.isEmpty())
			expresion=expresion+e.pop();
		code.add(generador.emitir("mov"+" "+ id +" "+ expresion));
		expresion = "";
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.SEMICOLON))
			error(TokenSubType.SEMICOLON, aux.getLine());
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
		code.add(generador.emitir(t+"cmp "+expresion+" true"));
		code.add(generador.emitir(t+"jmpc ETIQUETA"+etiqueta1));
		code.add(generador.emitir(t+"jump ETIQUETA"+etiqueta2));
		
		aux=lexico.getToken();
		
		if(!se_espera(aux,TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS);

		aux=lexico.getToken();
		if(!se_espera(aux,TokenSubType.THEN)) {
			error(TokenSubType.THEN,aux.getLine());

		}
		code.add(generador.emitir(t+"ETIQUETA"+etiqueta1+":"));
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

				code.add(generador.emitir(t+"jump ETIQUETA"+etiqueta3));
				code.add(generador.emitir(t+"ETIQUETA"+etiqueta2+":"));
				aux=lexico.getToken();
				while(!se_espera(aux,TokenSubType.ENDIF)&& aux!=null) {

					lexico.setBackToken(aux);
					aux=OPERACIONES(t+"\t");
				}
				if(aux==null)
					error(TokenSubType.ENDIF);
				else
					code.add(generador.emitir(t+"ETIQUETA"+etiqueta3+":"));
			}else
				code.add(generador.emitir(t+"ETIQUETA"+etiqueta2+":"));
		}		
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void LISTA_FUNCIONES() {
		Token aux;
		aux = lexico.getToken();
		while ((se_espera(aux, TokenSubType.INTEGER) || se_espera(aux, TokenSubType.REAL)
				|| se_espera(aux, TokenSubType.BOOLEAN) || se_espera(aux, TokenSubType.CHARACTER))) {
			lexico.setBackToken(aux);
			FUNCTION("\t");
			aux = lexico.getToken();
		}
		lexico.setBackToken(aux);

	}

	// FUNCION
	void FUNCTION(String t) {
		Token aux;
		String lex="";
		String expresion="";
		System.out.println("entre a funcion");
		
		TYPE();
		
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.FUNCTION))
			error(TokenSubType.FUNCTION);
		aux = lexico.getToken();
		if (!se_espera(aux, TokenType.IDENTIFIER))
			error(TokenType.IDENTIFIER, aux.getLine());
		lex=aux.getLexeme();
		code.add(generador.emitir("begin" + " " + lex));
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.LEFT_PARENTHESIS))
			error(TokenSubType.LEFT_PARENTHESIS);
		LISTAP();
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS);

		aux = lexico.getToken();
		while (!se_espera(aux, TokenSubType.RETURN) && aux != null) {

			lexico.setBackToken(aux);
			aux = OPERACIONES("\t");
		}
		if (aux == null)
			error(TokenSubType.RETURN);

		// aux=lexico.getToken();
		// if(!se_espera(aux,TokenSubType.RETURN))
		// error(TokenSubType.RETURN);
		EXPRESION();
		//pop es sacar
		while(!e.isEmpty())
			expresion=expresion+e.pop();
		code.add(generador.emitir(t+"¡"+expresion+"!"));
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.SEMICOLON))
			error(TokenSubType.SEMICOLON);
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.ENDFUNCTION))
			error(TokenSubType.ENDFUNCTION);
		code.add(generador.emitir("end" + " " + lex));
	}

	private void TYPE() {
		Token aux;
		aux = lexico.getToken();
		if (!(se_espera(aux, TokenSubType.INTEGER) || se_espera(aux, TokenSubType.REAL)
				|| se_espera(aux, TokenSubType.BOOLEAN) || se_espera(aux, TokenSubType.CHARACTER)))
			error(TokenType.KEY_WORD, aux.getLine());
	}

	private void LISTAP() {
		Token aux;
		aux = lexico.getToken();
		if (se_espera(aux, TokenType.IDENTIFIER)) {
			aux = lexico.getToken();
			if (se_espera(aux, TokenSubType.COMMA))
				LISTAP();
			lexico.setBackToken(aux);
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// WHILE
	private void WHILE(String t) {
		int etiqueta1, etiqueta2, etiqueta3;
		String expresion = "";
		Token aux;
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.WHILE))
			error(TokenSubType.WHILE, aux.getLine());
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.LEFT_PARENTHESIS))
			error(TokenSubType.LEFT_PARENTHESIS);
		EXPRESION();
		while (!e.isEmpty())
			expresion = expresion + e.pop();
		etiqueta1 = generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		etiqueta2 = generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		etiqueta3 = generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		
		code.add(generador.emitir("ETIQUETA" + etiqueta3 + ":"));
	
		code.add(generador.emitir(t+"cmp " + expresion + " verdadero"));
		code.add(generador.emitir(t+"jmpc ETIQUETA" + etiqueta1));
		code.add(generador.emitir(t+"jump ETIQUETA" + etiqueta2));
		
		
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS);
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.DO))
			error(TokenSubType.DO);
		code.add(generador.emitir("ETIQUETA" + etiqueta1 + ":"));
		aux = lexico.getToken();
		while (!se_espera(aux, TokenSubType.ENDWHILE) && aux != null) {
			lexico.setBackToken(aux);
			aux = OPERACIONES("\t");
		}
		code.add(generador.emitir(t+"jump ETIQUETA" + etiqueta3));
		code.add(generador.emitir("ETIQUETA" + etiqueta2 + ":"));
		if (aux == null)
			error(TokenSubType.ENDWHILE);

	}

	// CORREGIDO
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// DOWHILE
	private void DOWHILE(String t) {
		int etiqueta1;
		String expresion = "";
		Token aux;
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.DO))
			error(TokenSubType.DO);
		etiqueta1=generador.getNumeroEtiqueta();
		code.add(generador.emitir("ETIQUETA" + etiqueta1 + ":"));
		aux = lexico.getToken();
		while (!se_espera(aux, TokenSubType.WHILE) && aux != null) {
			lexico.setBackToken(aux);
			aux = OPERACIONES("\t");
		}
		if (aux == null) {
			error(TokenSubType.WHILE);
		}
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.LEFT_PARENTHESIS))
			error(TokenSubType.LEFT_PARENTHESIS);
		EXPRESION();
		
		while (!e.isEmpty())
			expresion = expresion + e.pop();
		code.add(generador.emitir(t+"cmp " + expresion + " verdadero"));
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS);
		code.add(generador.emitir(t+"jmpc ETIQUETA" + etiqueta1));
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.SEMICOLON))
			error(TokenSubType.SEMICOLON);
		
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// FOR
	private void FOR(String t) {
		int etiqueta1, etiqueta2, etiqueta3, etiqueta4;
		Token aux;
		String id1, id2, id3, id4;
		
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.FOR))
			error(TokenSubType.FOR);
		aux = lexico.getToken();
		if (!se_espera(aux, TokenType.IDENTIFIER))
			error(TokenType.IDENTIFIER, aux.getLine());
		id1 = aux.getLexeme();
		aux = lexico.getToken();
		if (!se_espera(aux, TokenType.ASSIGNMENT))
			error(TokenType.ASSIGNMENT, aux.getLine());
		aux = lexico.getToken();
		if (!(se_espera(aux, TokenSubType.INTEGERNUMBER) || se_espera(aux, TokenType.IDENTIFIER)))/////////////////
			error("ERROR EN EL VALOR INGRESADO");
//		else {
			id2 = aux.getLexeme();
			code.add(generador.emitir(t+"mv"+"  " +id1+"  "+ id2));
//		}
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.UNTIL))
			error(TokenSubType.UNTIL);
		aux = lexico.getToken();
		if (!(se_espera(aux, TokenSubType.INTEGERNUMBER) || se_espera(aux, TokenType.IDENTIFIER)))////////////////////
			error("ERROR EN EL VALOR INGRESADO");
		id3=aux.getLexeme();
		etiqueta1=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		etiqueta2=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		etiqueta3=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		code.add(generador.emitir("ETIQUETA" + etiqueta3 + ":"));
		code.add(generador.emitir(t+"cmp"+"  " +id1+"  "+ id3));
		code.add(generador.emitir(t+"jmpc ETIQUETA"+etiqueta1));
		code.add(generador.emitir(t+"jump ETIQUETA"+etiqueta2));
		
		
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.LEFT_PARENTHESIS))
			error(TokenSubType.LEFT_PARENTHESIS);
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.WITH))
			error(TokenSubType.WITH);
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.STEP))
			error(TokenSubType.STEP);
		aux = lexico.getToken();
		if (!(se_espera(aux, TokenSubType.INTEGERNUMBER) || se_espera(aux, TokenType.IDENTIFIER)))/////////////////////////
			error("ERROR EN EL VALOR INGRESADO");
		id4 = aux.getLexeme();
		etiqueta4=generador.getNumeroEtiqueta();
		generador.incrementaNumeroEtiqueta();
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.RIGHT_PARENTHESIS))
			error(TokenSubType.RIGHT_PARENTHESIS);
		aux = lexico.getToken();
		if (!se_espera(aux, TokenSubType.DO))
			error(TokenSubType.DO);
		code.add(generador.emitir("ETIQUETA"+ etiqueta1 + ":"));
		aux = lexico.getToken();
		while (!se_espera(aux, TokenSubType.ENDFOR) && aux != null) {
			lexico.setBackToken(aux);
			aux = OPERACIONES("\t");
		}
		code.add(generador.emitir(t+"add " + id1 +" "+ id4));
		code.add(generador.emitir(t+"jump ETIQUETA"+etiqueta3));
		if (aux == null) {
			error(TokenSubType.ENDFOR);
		}
		code.add(generador.emitir("ETIQUETA"+ etiqueta2 + ":"));
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
		aux = lexico.getToken();
		if (aux != null) {
			if (aux.getLexeme().equals("&&")) {
				e.add(aux.getLexeme()+"");
				C();
				YP();
				
			} else {
				lexico.setBackToken(aux);
			}
		}
	}

	private void OP() {
		Token aux;
		aux = lexico.getToken();
		if (aux != null) {
			if (aux.getLexeme().equals("||")) {
				e.add(aux.getLexeme()+"");
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
		if (aux != null) {
			if (aux.getLexeme().equals("==") || aux.getLexeme().equals("!=")) {
				e.add(aux.getLexeme()+"");
				R();
				CP();
			} else {
				lexico.setBackToken(aux);
			}
		}

	}

	private void R() {
		E();
		RP();
	}

	private void RP() {
		Token aux;
		aux = lexico.getToken();
		if (aux != null) {
			if (aux.getLexeme().equals("<") || aux.getLexeme().equals(">") || aux.getLexeme().equals(">=")
					|| aux.getLexeme().equals("<=")) {
				e.add(aux.getLexeme()+"");
				E();
				RP();
			} else {
				lexico.setBackToken(aux);
			}
		}
	}// fin RP

	private void E() {
		T();
		EP();
	}

	private void EP() {
		Token aux;
		aux = lexico.getToken();
		if (aux != null)
			if (aux.getLexeme().equals("+") || aux.getLexeme().equals("-")) {
				 e.add(aux.getLexeme()+"");
				T();
				EP();
			} else {
				lexico.setBackToken(aux);
			}
	}

	private void T() {

		N();
		TP();

	}

	private void TP() {
		Token aux;
		aux = lexico.getToken();
		if (aux != null)
			if (aux.getLexeme().equals("*") || aux.getLexeme().equals("/") || aux.getLexeme().equals("%")) {
				e.add(aux.getLexeme()+"");
				N();
				TP();
			} else {

				lexico.setBackToken(aux);
			}

	}

	private void N() {

		Token aux;
		aux = lexico.getToken();
		if (aux != null)
			if (aux.getLexeme().equals("!")) {
				e.add(aux.getLexeme()+"");
				F();
			} else {
				lexico.setBackToken(aux);
				F();
			}
	}

	private void F() {
		Token aux;
		aux = lexico.getToken();
		if (!(se_espera(aux, TokenType.IDENTIFIER) || se_espera(aux, TokenSubType.INTEGERNUMBER)
				|| se_espera(aux, TokenSubType.REALNUMBER))) {

			if (!(se_espera(aux, TokenSubType.LEFT_PARENTHESIS))) {

				error("Error en linea " + aux.getLine() + " se espera numero o identificador");
			} else {
				e.add(aux.getLexeme()+"");
				EXPRESION();
				aux = lexico.getToken();
				if (!(se_espera(aux, TokenSubType.RIGHT_PARENTHESIS))) {
					error("Error en linea " + aux.getLine() + " se espera )");

				} else
					e.add(aux.getLexeme()+"");

			}

		}
		else
		e.add(aux.getLexeme()+"");
		// System.out.println("\t Operador Expresion:"+aux.getData());
	}

	
	
	///////////////////////////////////////
	//////////////////////////////////////
	
	private void FileSimbolos() throws IOException{
		String aux = "";
		//int errores=0;
		file_simbolos = new File("src/file_simbolos.txt");
		if (contadoErrores==0) {
			System.out.println("\nSe creo el archivo txt");


			// Escribimos en el archivo con el metodo write
			write = new BufferedWriter(new FileWriter(file_simbolos));
			write.write("Tabla de simbolos: \n");
			for (Simbolo s : tablaSimbolos.values()) {
				aux = s.toString();
				write.write("\n" + aux);
			}
			write.close();

		} else {
			System.out.println("\nNo se pudo crear el archivo porque hay ERRORES");
		}
	}
	
	
	/////////////////////////////////////
	////////////////////////////////////////
	
	private void archivoCodigo() throws IOException{
		file_code = new File("src/file_code.txt");
		if (contadoErrores == 0) {
			System.out.println("\nSe creo el archivo txt");
			//System.out.println("\nSe creo el archivo generador.txt");
			
				write = new BufferedWriter(new FileWriter(file_code));
				write.write("Generador: \n");
				for (String string : code) {
					write.write("\n"+string);
				}

				write.close();
			
				
			
		} else {
			System.out.println("\nNo se pudo crear el archivo porque hay ERRORES");
			
		}	
	}
	
/////////////////////////////////////
////////////////////////////////////////
	
	public static void main(String[] args) throws IOException {
		new Parser("ejemplo.txt");
		//new Parser("programa1.txt");
		//new Parser("ejemploprofe.txt");
		
		


	}
	

}