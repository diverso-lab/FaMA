// $ANTLR : "Analex.g" -> "Analex.java"$

	package es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.plain;	

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

public class Analex extends antlr.CharScanner implements AnalexTokenTypes, TokenStream
 {
public Analex(InputStream in) {
	this(new ByteBuffer(in));
}
public Analex(Reader in) {
	this(new CharBuffer(in));
}
public Analex(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public Analex(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = true;
	setCaseSensitive(true);
	literals = new Hashtable();
	literals.put(new ANTLRHashString("IFF", this), new Integer(34));
	literals.put(new ANTLRHashString("IMPLIES", this), new Integer(35));
	literals.put(new ANTLRHashString("OR", this), new Integer(36));
	literals.put(new ANTLRHashString("REQUIRES", this), new Integer(33));
	literals.put(new ANTLRHashString("NOT", this), new Integer(38));
	literals.put(new ANTLRHashString("AND", this), new Integer(37));
	literals.put(new ANTLRHashString("EXCLUDES", this), new Integer(32));
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				switch ( LA(1)) {
				case '\t':  case '\n':  case '\r':  case ' ':
				{
					mBLANCO(true);
					theRetToken=_returnToken;
					break;
				}
				case '.':
				{
					mPUNTO(true);
					theRetToken=_returnToken;
					break;
				}
				case '#':
				{
					mCOMMENT_LIN(true);
					theRetToken=_returnToken;
					break;
				}
				case 'A':  case 'B':  case 'C':  case 'D':
				case 'E':  case 'F':  case 'G':  case 'H':
				case 'I':  case 'J':  case 'K':  case 'L':
				case 'M':  case 'N':  case 'O':  case 'P':
				case 'Q':  case 'R':  case 'S':  case 'T':
				case 'U':  case 'V':  case 'W':  case 'X':
				case 'Y':  case 'Z':  case '_':  case 'a':
				case 'b':  case 'c':  case 'd':  case 'e':
				case 'f':  case 'g':  case 'h':  case 'i':
				case 'j':  case 'k':  case 'l':  case 'm':
				case 'n':  case 'o':  case 'p':  case 'q':
				case 'r':  case 's':  case 't':  case 'u':
				case 'v':  case 'w':  case 'x':  case 'y':
				case 'z':
				{
					mIDENT(true);
					theRetToken=_returnToken;
					break;
				}
				case '"':
				{
					mLIT_STRING(true);
					theRetToken=_returnToken;
					break;
				}
				case '0':  case '1':  case '2':  case '3':
				case '4':  case '5':  case '6':  case '7':
				case '8':  case '9':
				{
					mNUMERO(true);
					theRetToken=_returnToken;
					break;
				}
				case ',':
				{
					mCOMA(true);
					theRetToken=_returnToken;
					break;
				}
				case ';':
				{
					mPyC(true);
					theRetToken=_returnToken;
					break;
				}
				case ':':
				{
					mDOSPUNTOS(true);
					theRetToken=_returnToken;
					break;
				}
				case '(':
				{
					mPARENTESIS_ABRIR(true);
					theRetToken=_returnToken;
					break;
				}
				case ')':
				{
					mPARENTESIS_CERRAR(true);
					theRetToken=_returnToken;
					break;
				}
				case '{':
				{
					mLLAVE_ABRIR(true);
					theRetToken=_returnToken;
					break;
				}
				case '}':
				{
					mLLAVE_CERRAR(true);
					theRetToken=_returnToken;
					break;
				}
				case '[':
				{
					mCORCHETE_ABRIR(true);
					theRetToken=_returnToken;
					break;
				}
				case ']':
				{
					mCORCHETE_CERRAR(true);
					theRetToken=_returnToken;
					break;
				}
				default:
					if ((LA(1)=='%') && (LA(2)=='R')) {
						mSECCION_RELACIONES(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='%') && (LA(2)=='C')) {
						mSECCION_CONSTRAINTS(true);
						theRetToken=_returnToken;
					}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	protected final void mSALTO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SALTO;
		int _saveIndex;
		
		switch ( LA(1)) {
		case '\r':
		{
			match("\r\n");
			break;
		}
		case '\n':
		{
			match('\n');
			newline();
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mBLANCO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BLANCO;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case '\n':  case '\r':
		{
			mSALTO(false);
			break;
		}
		case ' ':
		{
			match(' ');
			break;
		}
		case '\t':
		{
			match('\t');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		_ttype = Token.SKIP;
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mLETRA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LETRA;
		int _saveIndex;
		
		switch ( LA(1)) {
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':  case 'g':  case 'h':
		case 'i':  case 'j':  case 'k':  case 'l':
		case 'm':  case 'n':  case 'o':  case 'p':
		case 'q':  case 'r':  case 's':  case 't':
		case 'u':  case 'v':  case 'w':  case 'x':
		case 'y':  case 'z':
		{
			matchRange('a','z');
			break;
		}
		case 'A':  case 'B':  case 'C':  case 'D':
		case 'E':  case 'F':  case 'G':  case 'H':
		case 'I':  case 'J':  case 'K':  case 'L':
		case 'M':  case 'N':  case 'O':  case 'P':
		case 'Q':  case 'R':  case 'S':  case 'T':
		case 'U':  case 'V':  case 'W':  case 'X':
		case 'Y':  case 'Z':
		{
			matchRange('A','Z');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mBARRA_BAJA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BARRA_BAJA;
		int _saveIndex;
		
		match('_');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mDIGITO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DIGITO;
		int _saveIndex;
		
		matchRange('0','9');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCOMILLA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMILLA;
		int _saveIndex;
		
		match('\'');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPUNTO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PUNTO;
		int _saveIndex;
		
		match('.');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mALMOADILLA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ALMOADILLA;
		int _saveIndex;
		
		match('#');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCOMMENT_LIN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMMENT_LIN;
		int _saveIndex;
		
		mALMOADILLA(false);
		{
		_loop830:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				{
				match(_tokenSet_0);
				}
			}
			else {
				break _loop830;
			}
			
		} while (true);
		}
		_ttype = Token.SKIP;
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mIDENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = IDENT;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case 'A':  case 'B':  case 'C':  case 'D':
		case 'E':  case 'F':  case 'G':  case 'H':
		case 'I':  case 'J':  case 'K':  case 'L':
		case 'M':  case 'N':  case 'O':  case 'P':
		case 'Q':  case 'R':  case 'S':  case 'T':
		case 'U':  case 'V':  case 'W':  case 'X':
		case 'Y':  case 'Z':  case 'a':  case 'b':
		case 'c':  case 'd':  case 'e':  case 'f':
		case 'g':  case 'h':  case 'i':  case 'j':
		case 'k':  case 'l':  case 'm':  case 'n':
		case 'o':  case 'p':  case 'q':  case 'r':
		case 's':  case 't':  case 'u':  case 'v':
		case 'w':  case 'x':  case 'y':  case 'z':
		{
			mLETRA(false);
			break;
		}
		case '_':
		{
			mBARRA_BAJA(false);
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		{
		_loop834:
		do {
			switch ( LA(1)) {
			case 'A':  case 'B':  case 'C':  case 'D':
			case 'E':  case 'F':  case 'G':  case 'H':
			case 'I':  case 'J':  case 'K':  case 'L':
			case 'M':  case 'N':  case 'O':  case 'P':
			case 'Q':  case 'R':  case 'S':  case 'T':
			case 'U':  case 'V':  case 'W':  case 'X':
			case 'Y':  case 'Z':  case 'a':  case 'b':
			case 'c':  case 'd':  case 'e':  case 'f':
			case 'g':  case 'h':  case 'i':  case 'j':
			case 'k':  case 'l':  case 'm':  case 'n':
			case 'o':  case 'p':  case 'q':  case 'r':
			case 's':  case 't':  case 'u':  case 'v':
			case 'w':  case 'x':  case 'y':  case 'z':
			{
				mLETRA(false);
				break;
			}
			case '_':
			{
				mBARRA_BAJA(false);
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				mDIGITO(false);
				break;
			}
			default:
			{
				break _loop834;
			}
			}
		} while (true);
		}
		_ttype = testLiteralsTable(_ttype);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLIT_STRING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LIT_STRING;
		int _saveIndex;
		
		match('"');
		{
		_loop837:
		do {
			if ((_tokenSet_1.member(LA(1)))) {
				matchNot('"');
			}
			else {
				break _loop837;
			}
			
		} while (true);
		}
		match('"');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNUMERO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NUMERO;
		int _saveIndex;
		
		{
		int _cnt840=0;
		_loop840:
		do {
			if (((LA(1) >= '0' && LA(1) <= '9'))) {
				mDIGITO(false);
			}
			else {
				if ( _cnt840>=1 ) { break _loop840; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt840++;
		} while (true);
		}
		_ttype = LIT_ENTERO;
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCOMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMA;
		int _saveIndex;
		
		match(',');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPyC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PyC;
		int _saveIndex;
		
		match(';');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDOSPUNTOS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DOSPUNTOS;
		int _saveIndex;
		
		match(':');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPARENTESIS_ABRIR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PARENTESIS_ABRIR;
		int _saveIndex;
		
		match('(');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPARENTESIS_CERRAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PARENTESIS_CERRAR;
		int _saveIndex;
		
		match(')');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLLAVE_ABRIR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LLAVE_ABRIR;
		int _saveIndex;
		
		match('{');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLLAVE_CERRAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LLAVE_CERRAR;
		int _saveIndex;
		
		match('}');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCORCHETE_ABRIR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CORCHETE_ABRIR;
		int _saveIndex;
		
		match('[');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCORCHETE_CERRAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CORCHETE_CERRAR;
		int _saveIndex;
		
		match(']');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSECCION_RELACIONES(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SECCION_RELACIONES;
		int _saveIndex;
		
		match("%Relationships");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSECCION_CONSTRAINTS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SECCION_CONSTRAINTS;
		int _saveIndex;
		
		match("%Constraints");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[260];
		data[0]=-9224L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		for (int i = 64; i<=127; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = new long[260];
		data[0]=-17179869192L;
		for (int i = 1; i<=3; i++) { data[i]=-1L; }
		for (int i = 64; i<=127; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	
	}
