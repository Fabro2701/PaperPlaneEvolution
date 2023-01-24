package model.grammar.bnf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;



public class BNFMeritParser extends BNFParser{


	/**
	 * ProductionList ::= <MeritStatement> <Production> '|' <MeritStatement> <Production> '|' <MeritStatement> <Production>
	 * @return
	 */
	protected JSONArray ProductionList() {
		JSONArray arr = new JSONArray();
		
		while(this._lookahead != null && !this._lookahead.getString("type").equals(".")) {
			arr.put(this.MeritProduction());
			if(this._lookahead.getString("type").equals("|")) _eat("|");
		}
		return arr;
	}
	/**
	 * MeritProduction ::= <MeritStatement> <Symbol> <Symbol> <Symbol> <Symbol>
	 */
	protected JSONObject MeritProduction() {
		JSONArray arr = new JSONArray();
		JSONObject merit = this.MeritStatement();
		while(this._lookahead != null && !(this._lookahead.getString("type").equals("|")||this._lookahead.getString("type").equals("."))) {
			arr.put(this.Symbol());			
		}
		return new JSONObject().put("symbols", arr).put("merit",merit.getString("value").substring(1, merit.getString("value").length()-1));
	}
	protected JSONObject MeritStatement() {
		return _eat("MERIT");
	}
	
	public static void main(String args[]) {
	
		
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("resources/loads/grammars/defaultBias.bnf")));
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
		BNFMeritParser parser = new BNFMeritParser();
		System.out.println(parser.parse(e3).toString(4));
		
	}
}
