package model.plane.shapes;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class ShapeTokenizer{
	private int _cursor;
	private String _string;
	static String [][]Spec= new String[][]{
		  {"^\\s+",null},
   		  {"^;",";"},
   		  {"^\\.","."},
   		  {"^plane","PLANE"},
   		  {"^tri","TRI"},
   		  {"^cuad","CUAD"},
   		  {"^startBase|middleBase|endBase|upperRightCorner|upperMiddleRight|upperLeftCorner|upperMiddleLeft","BASE_POINT"},
   		  {"^maxX|maxY|maxZ|minX|minY|minZ","GENERAL_POINT"},
   		  {"^N[(]","NATURAL_POINT"},
   		  {"^[(]","("},
   		  {"^[)]",")"},
   		  {"^[,]",","},
		  {"^[0-9.]+f?d?","NUMBER"},
   		  {"^[+-]","ADDITIVE_OPERATOR"},
   		  {"^[*/]","MULTIPLICATIVE_OPERATOR"},
		  {"^\"[^\"]*\"","STRING"}
   		  };
	public ShapeTokenizer() {
		
	}
	public void init(String string) {
		_cursor=0;
		_string=string;
		
	}
	public boolean isEOF() {
		return this._cursor == this._string.length();
	}
	public boolean hasMoreTokens() {
		return this._cursor < this._string.length();
	}
	boolean debug = false;
	public JSONObject getNextToken() {
		if(!this.hasMoreTokens()) {
			//System.err.println("No more tokens");
			return null;
		}
		
		String string = this._string.substring(_cursor);
		String regexp=null;
		String tokenType=null;
		String tokenValue=null;
		for(int i=0;i<Spec.length;i++) {
			regexp=Spec[i][0];
			tokenType=Spec[i][1];
			
			tokenValue = this._match(regexp,string);
			
			
			if(tokenValue==null) {
				if(debug)System.out.println(0);
				continue;
			}
			if(tokenType == null) {
				if(debug)System.out.println(1);
				return this.getNextToken();
			}

			if(debug)System.out.println("2  "+tokenValue);
			return new JSONObject().put("type", tokenType).put("value",tokenValue);
		}
		System.err.println("Unexpected token "+string.charAt(0));
		return null;
	}
	/**
	 * Return the substring that matched with the RE or null otherwise
	 * @param regexp
	 * @param string
	 * @return
	 */
	private String _match(String regexp, String string) {
		if(debug)System.out.println(regexp+ "   -   < "+string+" >");
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(string); 
		if(!m.find()||m.start()!=0) {
			return null;
		}
		this._cursor+=m.end();
		return string.substring(0, m.end());
	}
	
}
