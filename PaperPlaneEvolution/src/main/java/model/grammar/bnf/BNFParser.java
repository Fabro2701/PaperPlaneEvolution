package model.grammar.bnf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;



public class BNFParser {
	protected String _string;
	protected BNFTokenizer _tokenizer;
	protected JSONObject _lookahead;
	public BNFParser() {
		_string = new String();
		_tokenizer = new BNFTokenizer();
	}
	public JSONObject parse(String string){
		_string = string;
		_tokenizer.init(string);
		
		
		this._lookahead = this._tokenizer.getNextToken();
		return this.Program();
	}
	protected JSONObject Program() {
		return new JSONObject().put("type", "Grammar").put("rules", this.StatementList());
	}
	protected JSONArray StatementList() {
		JSONArray arr = new JSONArray();
		
		while(this._lookahead != null) {
			arr.put(this.Statement());
		}
		return arr;
	}
	protected JSONObject Statement() {
		return this.RuleStatement();
	}
	/**
	 * RuleStatement ::= <NTSymbol>  '->'  <ProductionList> '.'
	 * @return
	 */
	protected JSONObject RuleStatement() {
		JSONObject key = this.NTSymbol();
		_eat("->");
		JSONArray productions = this.ProductionList();
		_eat(".");
		return new JSONObject().put("name", key)
							   .put("productions", productions);
	}
	/**
	 * ProductionList ::= <Production> '|' <Production> '|' <Production>
	 * @return
	 */
	protected JSONArray ProductionList() {
		JSONArray arr = new JSONArray();
		
		while(this._lookahead != null && !this._lookahead.getString("type").equals(".")) {
			arr.put(this.Production());
			if(this._lookahead.getString("type").equals("|")) _eat("|");
		}
		return arr;
	}
	protected JSONArray Production() {
		JSONArray arr = new JSONArray();
		
		while(this._lookahead != null && !(this._lookahead.getString("type").equals("|")||this._lookahead.getString("type").equals("."))) {
			arr.put(this.Symbol());			
		}
		return arr;
	}
	protected JSONObject Symbol() {
		if(this._lookahead.getString("type").equals("NTSYMBOL")) {
			return this.NTSymbol();
		}
		else {
			return this.TSymbol();
		}
	}
	protected JSONObject TSymbol() {
		String literal = _eat("TSYMBOL").getString("value");
		if(literal.charAt(0)=='\'') {
			return new JSONObject().put("type", "Terminal").put("id", literal.substring(1, literal.length()-1));
		}
		return new JSONObject().put("type", "Terminal").put("id", literal);
	}
	protected JSONObject NTSymbol() {
		String literal = _eat("NTSYMBOL").getString("value");
		return new JSONObject().put("type", "NonTerminal").put("id", literal.substring(1, literal.length()-1));
	}
	protected JSONObject _eat(String type) {
		JSONObject token=_lookahead;
		if(this._lookahead==null) {
			System.err.println("unex end of input");
			return null;
		}
		if(!this._lookahead.getString("type").equals(type)) {
			System.err.println("unexpected "+this._lookahead.getString("type")+" expected "+type);
			return null;
		}
		this._lookahead=_tokenizer.getNextToken();
		return token;
	}
	public static void main(String args[]) {
		String e1 ="<OBS> -> 'r_f_d'|'l_f_d'|'u_f_d'|'d_f_d'.\n"
				+ "<OP> -> '<'|'>'|'<='|'>='.\n"
				+ "<AR> -> '+'|'*'|'-'.\n"
				+ "<CODE> -> <LINE>|<CODE> <LINE>.\n"
				+ "<ACTION> -> 'RIGHT'|'LEFT'|'UP'|'DOWN'|'NEUTRAL'.\n"
				+ "<V> -> '0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'.\n"
				+ "<AREXP> -> <OBS>|<V>|<AREXP> <AR> <AREXP>.\n"
				+ "<LINE> -> <ACTION> ';'|<IF>.\n"
				+ "<Pi> -> '0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'.\n"
				+ "<IF> -> 'if' <COND> '{' <LINE> '}' 'else' '{' <LINE> '}'|'if' <COND> '{' <LINE> '}'.\n"
				+ "<COND> -> '(' <OBS> ')' | '(' <OBS> <OP> <OBS> ')' | '(' '(' <AREXP> ')' <OP> '(' <AREXP> ')' ')'.\n"
				+ "<LOGOP> -> '||' | '&&'.";
		String e2 = "<A>->'a'|<B>."
				  + "<B>->'b'|'a'.";
		
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("resources/loads/grammars/default.bnf")));
			String aux = reader.readLine();
			while(aux!=null) {
				sb.append(aux);
				aux = reader.readLine();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String e3 = sb.toString();
		BNFParser parser = new BNFParser();
		System.out.println(parser.parse(e3).toString(4));
		
		
		
		//Pattern p = Pattern.compile("^\\blet\\b");
		//Matcher m = p.matcher(" let x =12;"); 
		//System.out.println(m.find());
		//System.out.println(m.end());
	}
}
