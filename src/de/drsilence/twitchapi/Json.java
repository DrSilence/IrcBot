package de.drsilence.twitchapi;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lazy RFC4627 implementation. See http://tools.ietf.org/rfc/rfc4627.txt
 * 
 * @author drsilence
 * @version 2015-10-03
 */
public abstract class Json {
	
	// RFC4627 2. (JSON Grammar)
	private static final char BEGIN_ARRAY 		= '['; 
	private static final char BEGIN_OBJECT 		= '{'; 
	private static final char END_ARRAY 		= ']'; 
	private static final char END_OBCJECT 		= '}'; 
	private static final char NAME_SEPERATOR 	= ':';
	private static final char VALUE_SEPERATOR 	= ',';
	private static final char CHAR_ESCAPE		= '\\';
	private static final char CHAR_STRING		= '"';
	private static final char WHITESPACE1		= 0x20; // Space
	private static final char WHITESPACE2		= 0x09; // Tab
	private static final char WHITESPACE3		= 0x0A; // LF
	private static final char WHITESPACE4		= 0x0D; // NL/CR
	private static final String CONTROL_CHARS	= ",:\\\"}]{[";
	
	/**
	 * Default Json exception. 
	 *
	 * @author drsilence
	 * @version 2015-10-03
	 */
	public static class JsonException extends RuntimeException {
		private static final long serialVersionUID = -788693661109706440L;
		private String reason;
		private Throwable cause;
		
		public JsonException(String reason) {
			super(reason);
			this.reason = reason;
		}
		
		public JsonException(Throwable cause) {
			super(cause.getMessage());
			this.cause = cause;
		}
		
		@Override
	    public Throwable getCause() {
	        return this.cause;
	    }
	}

	/**
	 * Simple char-stream tokenizer. Each token is a char.
	 * 
	 * @author drsilence
	 * @version 2015-10-03
	 */
	public static class JsonTokenizer {
		Reader reader;
		int countChars;
		int countLines;
		char prevChar;
		boolean usePrevChar;
		boolean isEOF;
		
		/**
		 * Constructs a new instance of JsonTokener based on a Reader.
		 * 
		 * @param reader
		 * 		A Reader to read chars from.
		 */
		public JsonTokenizer(Reader reader) {
			super();
			this.reader = reader;
			this.countChars = 0;
			this.countLines = 1;
			this.prevChar = 0;
			this.usePrevChar = false;
			this.isEOF = false;
		}
		
		/**
		 * Determine if another readable char is ready to read.
		 * 
		 * @return
		 * 		true if another char can be read,
		 * 		false if EOF.
		 */
		public boolean hasNext() {
			return ( isEOF && !usePrevChar );
		}
		
		/**
		 * Get the next char from stream or the last back pushed one.
		 * 
		 * @return
		 * 		one char,
		 * 		0 if EOF.
		 * 
		 * @throws JsonException
		 * 		
		 */
		public char getNext() throws JsonException {
			int c;
			if(usePrevChar) {
				c = prevChar;
				usePrevChar = false;
			} else {
				try {
					c = reader.read();
					if(c == -1) {
						isEOF = true;
						c = 0;
					}
				} catch (IOException e) {
					throw new JsonException(e.toString());
				}
			}
			if(c == WHITESPACE4) { // NL
				countLines += 1;
				countChars  = 0;
			}
			countChars += c==0 ? 0 : 1;
			prevChar = (char) c;
//			System.out.print(prevChar);
			return prevChar;
		}
		
		/**
		 * Get the next (not whitespace) char from stream or the last back pushed one.
		 * 
		 * @return
		 * 		one char,
		 * 		0 if EOF.
		 * 
		 * @throws JsonException
		 * 
		 */
		public char getNextValid() throws JsonException {
			char c;
			while(true) {
				c = getNext();
				if((c > WHITESPACE1) || (c == 0)) {
					return c;
				}
			}
		}
		
		/**
		 * Read a String on Stream until the end-char " is hit.
		 * Based on RFC4627 escaped chars will be substituted.
		 * No .trim() will be performed. 
		 * 
		 * @return
		 * 		Parsed chars as String (not including ending quote).
		 * 
		 * @throws JsonException
		 * 
		 */
		public String getString() throws JsonException {
			StringBuilder sb = new StringBuilder();
			char c;
			while(true) {
				c = getNext();
				switch(c) {
					case 0:
					case WHITESPACE3:
					case WHITESPACE4:
						throw new JsonException("Unterminated string.");
					case CHAR_ESCAPE:
						c = getNext();
						switch(c) {
							case 't':
								sb.append('\t');
								break;
							case 'r':
								sb.append('\r');
								break;
							case 'n':
								sb.append('\n');
								break;
							case 'b':
								sb.append('\b');
								break;
							case 'f':
								sb.append('\f');
								break;
							case '/':
							case '\\':
							case '"':
								sb.append(c);
								break;
							case 'u':
								char t[] = new char[4];
								t[0] = getNext();
								t[1] = getNext();
								t[2] = getNext();
								t[3] = getNext();
								if( (t[0]==0) || (t[1]==0) || (t[2]==0) || (t[3]==0) ) {
									throw new JsonException("Illegal unicode.");
								}
								sb.append((char)Integer.parseInt(new String(t), 16));
								break;
							default:
								throw new JsonException("Illegal escape.");
						}//switch(c)
					default:
						if(c == CHAR_STRING) {
							return sb.toString();
						} else {
							sb.append(c);
						}
				}//switch(c)
			}//while(true)
		}//getString()
		
		/**
		 * Push last read char back, so it can be red again.
		 * Only one push back is allowed.
		 * 
		 * @throws JsonException
		 * 		On any attempt to push more than one char back.
		 */
		public void back() throws JsonException {
			if(usePrevChar) {
				throw new JsonException("Stepping two back is not implemented yet.");
			}
			usePrevChar = true;
			countChars -=1;
			if(prevChar == WHITESPACE4) {
				countLines -= 1;
				countChars  = 0;
			}
		}
		
		public Object getNextValue() throws JsonException {
			char c = getNextValid();
			switch(c) {
				case BEGIN_ARRAY:
					back();
					return new JsonArray(this);
				case BEGIN_OBJECT:
					back();
					return new JsonObject(this);
				case CHAR_STRING:
					return getString();
			}
			
			//handle true,false,null,numbers
			
			StringBuilder sb = new StringBuilder();
			while( (c>=WHITESPACE1) &&  (CONTROL_CHARS.indexOf(c)<0) ) {
				sb.append(c);
				c=getNext();
			}
			back();
			
			String s = sb.toString().trim();
			if( "".equals(s) ) {
				return "";
			} else if( "true".equalsIgnoreCase(s) ) {
				return Boolean.TRUE;
			} else if( "false".equalsIgnoreCase(s) ) {
				return Boolean.FALSE;
			} else if( "null".equalsIgnoreCase(s) ) {
				return null;
			} else {
				try {
					char b = s.charAt(0);
					if ((b >= '0' && b <= '9') || b == '-') {
						//double!?
						if( (s.indexOf('.')>-1) || 
							(s.indexOf('e')>-1) || 
							(s.indexOf('E')>-1) )
						{
							Double d = Double.parseDouble(s);
							if (!d.isInfinite() && !d.isNaN()) {
		                        return d;
		                    }
						} else {
							//long or int !?
							Long myLong = new Long(s);
		                    if (s.equals(myLong.toString())) {
		                        if (myLong == myLong.intValue()) {
		                            return myLong.intValue();
		                        } else {
		                            return myLong;
		                        }
		                    }
						}
					}
				} catch(NumberFormatException ignore) {
					// just ignore since we deliver atm unparsed string.
				}
			}
			return s;
		}//getNextObject()
		
		/**
		 * Generates a String that somewhat describes the current Reader read position.
		 * 
		 * @return
		 * 		A String in form "... at line <%d> char <%d> actual char is '<%c>'"
		 */
		public String getPosition() {
			return new String("JsonTokenizer at line "+countLines+" char "+countChars+" actual char is '"+prevChar+"'.");
		}
		
	}//class JsonTokenizer
	
	
	/**
	 * This class represents a Json object.
	 * 
	 * @author drsilence
	 * @version 2015-10-03
	 */
	public static class JsonObject {
		private Map<String, Object> pairs; 
		
		/**
		 * Constructs an empty instance.
		 * 
		 */
		public JsonObject() {
			super();
			this.pairs = new HashMap<String, Object>();
		}
		
		/**
		 * Builds a JsonObject based on json-data in stream.
		 * 
		 * @param t
		 * 		A JsonTokenizer to read from.
		 * 
		 * @throws JsonException
		 * 		If no valid json-object can be read from stream.
		 * 
		 */
		public JsonObject(JsonTokenizer t) throws JsonException {
			this();
			char c;
			c = t.getNextValid();
			if( c != BEGIN_OBJECT ) {
				throw new JsonException("An JsonObject must begin with '"+BEGIN_OBJECT+"'.");
			}
			c = t.getNextValid();
			if( c == END_OBCJECT ) {
				//empty object
				return ;
			}
			t.back();
			while(true) {
				c = t.getNextValid();
				if( c != CHAR_STRING ) {
					throw new JsonException("JsonObject is missing a key string.");
				}
				String key = t.getString().trim();
				if("".equals(key)) {
					throw new JsonException("JsonObject is has an empty key string.");
				}
				c = t.getNextValid();
				if( c != NAME_SEPERATOR ) {
					throw new JsonException("JsonObject key string must be followed by a '"+
							NAME_SEPERATOR+"'.");
				}
				
				Object o = t.getNextValue();
				pairs.put(key, o);
				
				c = t.getNextValid();
				if( c == VALUE_SEPERATOR ) {
					//parse next key/value pair
					continue;
				} else if( c == END_OBCJECT ) {
					//end of object parsing
					return;
				} else {
					throw new JsonException("JsonObject seperates key/value pairs with '"+
							VALUE_SEPERATOR+"' or ends with '"+END_OBCJECT+"'. ");
				}
			}
		}
		
		@Override
		public String toString() {
			return toString("\t");
		}
		
		public String toString(String c) {
			//TODO:
			StringBuilder sb = new StringBuilder();
			sb.append(BEGIN_OBJECT);
			for(Map.Entry<String, Object> e : pairs.entrySet()) {
				String k = e.getKey();
				Object o = e.getValue();
				
				sb.append(WHITESPACE4); //NL
				sb.append(c);
				sb.append(k);
				sb.append(" "+NAME_SEPERATOR+" ");
				String os="";
				if( o == null ) {
					sb.append("null");
				} else if(o instanceof Number) {
					os = ((Number)o).toString();
				} else if( o instanceof JsonObject ) {
					os = ((JsonObject)o).toString(c+c);
				} else if( o instanceof JsonArray ) {
//					os = ((JsonArray)o).toString(c+c);
				} else if( o instanceof String ){
					os = CHAR_STRING + ((String)o).toString() + CHAR_STRING;
				} else {
					os = o.toString();
				}
				sb.append(os);
				sb.append(VALUE_SEPERATOR);
				
			}
			sb.setLength(sb.length()-1);
			sb.append(WHITESPACE4); // NL
			sb.append(c);
			sb.append(END_OBCJECT);
			return sb.toString();
		}
		
	}//class JsonObject
	
	/**
	 * This class represents a Json-array.
	 * 
	 * @author drsilence
	 * @version 2015-10-03
	 */
	public static class JsonArray {
		private List<Object> list;
		
		/**
		 * Constructs an empty instance.
		 * 
		 */
		public JsonArray() {
			super();
			this.list = new ArrayList<Object>();
//			throw new JsonException("Not implementet yet.");
		}
		
		/**
		 * Builds a JsonArray based on json-data in stream.
		 * 
		 * @param t
		 * 		A JsonTokenizer to read from.
		 * 
		 * @throws JsonException
		 * 		If no valid json-array can be read from stream.
		 * 
		 */
		public JsonArray(JsonTokenizer t) throws JsonException {
			this();
			char c;
			c = t.getNextValid();
			if( c != BEGIN_ARRAY ) {
				throw new JsonException("An JsonArray must begin with '"+BEGIN_ARRAY+"'.");
			}
			c = t.getNextValid();
			if( c == END_ARRAY ) {
				//empty array
				return ;
			}
			t.back();
			
			while(true) {
				Object o = t.getNextValue();
				list.add(o);
				c = t.getNextValid();
				if( c == VALUE_SEPERATOR ) {
					//parse next array element
					continue;
				} else if( c == END_ARRAY ){
					//end of array parsing
					return ;
				} else {
					throw new JsonException("JsonArray seperates elements with '"+
							VALUE_SEPERATOR+"' or ends with '"+END_ARRAY+"'.");
				}
			}
		}
		
	}//class JsonArray
	
	
	
	
	
	
	
	
	
	public static Object parse(Reader reader) throws JsonException {
		JsonTokenizer t = new JsonTokenizer(reader);
		return t.getNextValue();
	}
	
	public static Object parse(String s) throws JsonException {
		JsonTokenizer t = new JsonTokenizer(new StringReader(s));
		return t.getNextValue();
	}

}
