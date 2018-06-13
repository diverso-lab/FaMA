/*
	This file is part of FaMaTS.

    FaMaTS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FaMaTS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.

 */
header{
	package es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.plain;	
}

class Analex extends Lexer;

options{
	charVocabulary = '\3'..'\377' | '\u1000'..'\u1fff';
	k = 2;
	importVocab = Anasint; 	
	testLiterals=false;
}

tokens{
	REQUIRES = "REQUIRES";
	EXCLUDES = "EXCLUDES";
}

//protected TANTO_POR: '%';
protected SALTO: "\r\n" | '\n'{newline();};
BLANCO: (SALTO | ' ' | '\t') {$setType(Token.SKIP);};
protected LETRA: 'a'..'z' | 'A'..'Z';
protected BARRA_BAJA: '_';
protected DIGITO: '0'..'9';
protected COMILLA: '\'';
//protected AT: '@';
PUNTO: '.';
protected ALMOADILLA: '#';

//Comentarios de linea
COMENT_LIN: ALMOADILLA (('\r')+ ~('\n') | ~('\r') )* "\r\n" {newline();$setType(Token.SKIP);} ;

IDENT options {testLiterals=true;}: (LETRA | BARRA_BAJA) (LETRA | BARRA_BAJA | DIGITO)*;
//ATT : IDENT (PUNTO IDENT)?; 
//ATT : IDENT PUNTO IDENT;
//CUIDADO CON LOS VALORES NEGATIVOS, AHORA MISMO NO ESTAN CONTEMPLADOS
//LIT_ENTERO: (DIGITO)+;
//LIT_REAL: (DIGITO)+ PUNTO (DIGITO)+;
LIT_STRING: '"' (~'"')* '"';

NUMERO : (DIGITO)+ {$setType(LIT_ENTERO);};


COMA : ',';
PyC : ';';
DOSPUNTOS: ':';
PARENTESIS_ABRIR:'(';
PARENTESIS_CERRAR: ')';
LLAVE_ABRIR: '{';
LLAVE_CERRAR: '}';
CORCHETE_ABRIR: '[';
CORCHETE_CERRAR: ']';

//SECCIONES
SECCION_RELACIONES: "%Relationships";
SECCION_CONSTRAINTS: "%Constraints";
