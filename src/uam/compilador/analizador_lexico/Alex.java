package uam.compilador.analizador_lexico;

/*Define nuestro analizador lexico*/
import java.io.IOException;
import java.util.ArrayList;

public class Alex implements LexicoAnalyzer {
	// recorre toda la super cadena
	private int index = 0;
	// cadena que tendra todos los simbolos de nuestro texto fuente
	String input = "";
	// lista de token
	private ArrayList<Token> tlist = null;

	// hace la lectura y concatena todo
	public Alex(String archivo) {
		String entrada = "";
		File fuente = new File();

		try {
			fuente.open(archivo);

			do {

				entrada = fuente.getLine();
				if (entrada != null)
					input = input + entrada + "\n";

			} while (entrada != null);

			fuente.close();

		} catch (IOException e) {

			e.printStackTrace();
		}
		System.out.println(input);
		// almacena los token
		tlist = new ArrayList<Token>();
		// manda a llamar para que se mande a llamar la lista de token
		createTokenList();

	}

	/**
	 * El identifica y crea un Token identificador o palabra reservada
	 * 
	 * @param lexeme
	 *            Lexema del Token
	 */
	private Token isKeyWord(String lexeme) {
		for (TokenSubType ts : TokenSubType.values()) {
			if (ts.getType().equals(lexeme))
				return new Token(TokenType.KEY_WORD, ts, lexeme);
		}

		return new Token(TokenType.IDENTIFIER, lexeme);

	}// fin del metodo

	@Override
	// crea la lista de token
	public void createTokenList() {

		char caracter;
		boolean valor;
		String lexeme = "";
		int linea = 1;
		Token aux;

		while (index < input.length()) {
			// ver cada paso de como se va ejecutando
			// ESPERAR 1000 MILISEGUNDOS (1 SEGUNDO)
			/*
			 * try { Thread.sleep(1000); } catch (InterruptedException e) {
			 * e.printStackTrace(); }
			 */
			caracter = input.charAt(index);
			/////////////////////////////////////////////////////////////////////////////////////////////////
			if (caracter == '(') {

				lexeme = "";
				lexeme = lexeme + caracter;
				aux = new Token(TokenType.PARENTHESIS, TokenSubType.LEFT_PARENTHESIS, lexeme);
				aux.setLine(linea);
				tlist.add(aux);
				index++;
			} else if (caracter == ')') {

				lexeme = "";
				lexeme = lexeme + caracter;
				aux = new Token(TokenType.PARENTHESIS, TokenSubType.RIGHT_PARENTHESIS, lexeme);
				aux.setLine(linea);
				tlist.add(aux);
				index++;
			}
			///////////////////////////////////////////////////////////////////////////////////////////////// )

			else if (caracter == '<' || caracter == '>') {
				lexeme = "";
				lexeme = lexeme + caracter;
				index++;
				caracter = input.charAt(index);
				if (caracter == '=') {
					lexeme = lexeme + caracter;
					index++;
				}
				aux = new Token(TokenType.RELATIONAL_OPERATOR, lexeme);
				aux.setLine(linea);
				tlist.add(aux);
			}
			/////////////////////////////////////////////////////////////////////////////////////////////////
			else if (caracter == '=') {
				lexeme = "";
				lexeme = lexeme + caracter;
				index++;
				caracter = input.charAt(index);
				if (caracter == '=') {
					lexeme = lexeme + caracter;
					index++;
					aux = new Token(TokenType.COMPARISON_OPERATOR, lexeme);
					aux.setLine(linea);
					tlist.add(aux);
				} else {
					aux = new Token(TokenType.ASSIGNMENT, lexeme);
					aux.setLine(linea);
					tlist.add(aux);
				}
			}
			///////////////////////////////////////////////////////////////////////////////////////////////// )
			else if (caracter == '!') {
				lexeme = "";
				lexeme = lexeme + caracter;
				index++;
				caracter = input.charAt(index);
				if (caracter == '=') {
					lexeme = lexeme + caracter;
					index++;
					aux = new Token(TokenType.COMPARISON_OPERATOR, lexeme);
					aux.setLine(linea);
					tlist.add(aux);
				} else {
					aux = new Token(TokenType.LOGICAL_OPERATOR, TokenSubType.NEGATION, lexeme);
					aux.setLine(linea);
					tlist.add(aux);
				}
			}
			///////////////////////////////////////////////////////////////////////////////////////////////// )
			else if (caracter == '&') {
				lexeme = "";
				lexeme = lexeme + caracter;
				index++;
				caracter = input.charAt(index);
				if (caracter == '&') {
					lexeme = lexeme + caracter;
					index++;
					aux = new Token(TokenType.LOGICAL_OPERATOR, TokenSubType.AND, lexeme);
					aux.setLine(linea);
					tlist.add(aux);
				}
			}

			///////////////////////////////////////////////////////////////////////////////////////////////// )
			else if (caracter == '|') {
				lexeme = "";
				lexeme = lexeme + caracter;
				index++;
				caracter = input.charAt(index);
				if (caracter == '|') {
					lexeme = lexeme + caracter;
					index++;
					aux = new Token(TokenType.LOGICAL_OPERATOR, TokenSubType.OR, lexeme);
					aux.setLine(linea);
					tlist.add(aux);
				}
			}
			///////////////////////////////////////////////////////////////////////////////////////////////// )

			else if (Character.isDigit(caracter)) {// Determina si el caracter es un digito

				lexeme = "";
				lexeme = lexeme + caracter;
				index++;
				valor = true;
				while (index < input.length() && valor) {
					caracter = input.charAt(index);
					valor = Character.isDigit(caracter);
					if (valor) {
						lexeme = lexeme + caracter;
						index++;
					}
				}
				if (caracter == '.') {

					lexeme = lexeme + caracter;
					valor = true;
					index++;
					caracter = input.charAt(index);
					if (index < input.length() && Character.isDigit(caracter)) {
						while (index < input.length() && valor) {
							caracter = input.charAt(index);
							valor = Character.isDigit(caracter);
							if (valor) {
								lexeme = lexeme + caracter;

								index++;
							}
						}
						aux = new Token(TokenType.NUMBER, TokenSubType.REALNUMBER, lexeme);
						aux.setLine(linea);
						tlist.add(aux);

					}
				} else {
					aux = new Token(TokenType.NUMBER, TokenSubType.INTEGERNUMBER, lexeme);
					aux.setLine(linea);
					tlist.add(aux);
				}
			}

			///////////////////////////////////////////////////////////////////////////////////////////////// )

			else if (caracter == '"') {
				lexeme = "";
				lexeme = lexeme + caracter;
				index++;
				while (index < input.length()) {
					caracter = input.charAt(index);
					lexeme = lexeme + caracter;
					if (caracter == '"') {
						lexeme = lexeme + caracter;
						index++;
						aux = new Token(TokenType.STRING, lexeme);
						aux.setLine(linea);
						tlist.add(aux);
						break;
					} else
						index++;

				}
			}
			///////////////////////////////////////////////////////////////////////////////////////////////// )
			else if (caracter == '+' || caracter == '-') {
				lexeme = "";
				lexeme = lexeme + caracter;
				aux = new Token(TokenType.ARITHMETIC_OPERATOR, TokenSubType.ARITHMETIC_ADD, lexeme);
				aux.setLine(linea);
				tlist.add(aux);
				index++;

			} else if (caracter == '%' || caracter == '*') {
				lexeme = "";
				lexeme = lexeme + caracter;
				aux = new Token(TokenType.ARITHMETIC_OPERATOR, TokenSubType.ARITHMETIC_MUL, lexeme);
				aux.setLine(linea);
				tlist.add(aux);
				index++;
			}
			///////////////////////////////////////////////////////////////////////////////////////////////// )
			else if (caracter == ',') {
				lexeme = "";
				lexeme = lexeme + caracter;
				aux = new Token(TokenType.PUNCTUATION, TokenSubType.COMMA, lexeme);
				aux.setLine(linea);
				tlist.add(aux);
				index++;
			}
			///////////////////////////////////////////////////////////////////////////////////////////////// )
			else if (caracter == ';') {
				lexeme = "";
				lexeme = lexeme + caracter;
				aux = new Token(TokenType.PUNCTUATION, TokenSubType.SEMICOLON, lexeme);
				aux.setLine(linea);
				tlist.add(aux);
				index++;

			}
			//// /////////////////////////////////////////////////////////////////////////////////////////////
			//// )
			else if (caracter == ':') {
				lexeme = "";
				lexeme = lexeme + caracter;
				aux = new Token(TokenType.PUNCTUATION, TokenSubType.COLON, lexeme);
				aux.setLine(linea);
				tlist.add(aux);
				index++;
			}
			//// /////////////////////////////////////////////////////////////////////////////////////////////
			//// )

			else if (Character.isWhitespace(caracter)) {
				if (caracter == '\n')
					linea++;
				index++;
			}
			// else if (caracter == '\n' || caracter == '\t' || caracter == '\f' || caracter
			// == '\r') {
			// lexeme = "";
			// lexeme = lexeme + caracter;
			// aux = new Token(TokenType.WHITE_SPACE, TokenSubType.SPACE, lexeme);
			// aux.setLine(linea);
			// //tlist.add(aux);
			// index++;
			// }
			///////////////////////////////////////////////////////////////////////////////////////////// )
			else if (caracter == '/') {
				lexeme = "";
				lexeme = lexeme + caracter;
				valor = true;
				index++;
				caracter = input.charAt(index);
				if (caracter == '/') {
					//poner un while caracter no sea whitespace
					lexeme = lexeme + caracter;
					index++;
					aux = new Token(TokenType.COMMENT, lexeme);
					aux.setLine(linea);
					//tlist.add(aux);// Poner que el lexema incremente hasta hallar \n
				} else if (caracter == '*') {
					index++;
					while (index < input.length()) {
						caracter = input.charAt(index);
						// lexeme = lexeme + caracter;
						if (caracter == '*') {
							index++;
							caracter = input.charAt(index);
							if (caracter == '/') {
								aux = new Token(TokenType.COMMENT, lexeme);
								aux.setLine(linea);
								//tlist.add(aux);
								break;
							}

						}
					}
				} else {
					aux = new Token(TokenType.ARITHMETIC_OPERATOR, TokenSubType.ARITHMETIC_MUL, lexeme);
					aux.setLine(linea);
					tlist.add(aux);
				}

			}
			///////////////////////////////////////////////////////////////////////////////////////////// )
			else if (caracter == '_' || Character.isLetter(caracter)) {
				lexeme = "";
				lexeme = lexeme + caracter;
				valor = true;
				index++;
				while (index < input.length() && valor) {

					caracter = input.charAt(index);
					valor = (caracter == '_' || Character.isLetter(caracter) || Character.isDigit(caracter));
					if (valor) {

						lexeme = lexeme + caracter;
						index++;
					}

				}

				// si es una palabra recervada o no
				aux = isKeyWord(lexeme);
				aux.setLine(linea);
				tlist.add(aux);

			}

			///////////////////////////////////////////////////////////////////////////////////////////// )
			else {
				System.out.println("Caracter no reconocido: " + caracter);
				index++;
			}

		} // fin de while
		System.out.println("########token list###########");
		for (Token aux1 : tlist)
			System.out.println("Token:" + aux1);

	}// fin de token list

	/**
	 * Recupera un Token de la lista creada mediante createTokenList
	 *
	 * @param ---
	 * @return Un Token de ls lista
	 * @see metodo createTokenList
	 */

	public Token getToken() {

		if (!tlist.isEmpty())
			return tlist.remove(0);
		return null;
	}

	/**
	 * Devuelve un Token de la lista creada mediante createTokenList
	 *
	 * @param t
	 *            El Token a devolver a la lista
	 * @return ---
	 * @see metodo createTokenList
	 */
	public void setBackToken(Token t) {

		tlist.add(0, t);
	}

	public static void main(String[] args) {
		new Alex("ejemplo.txt");

	}

}// fin de la clase
