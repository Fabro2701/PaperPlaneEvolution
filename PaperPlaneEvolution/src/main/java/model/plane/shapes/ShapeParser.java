package model.plane.shapes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import model.grammar.Chromosome;
import model.grammar.StandardGrammar;
import model.grammar.AbstractGrammar.Symbol;
import util.RandomSingleton;



public class ShapeParser {
	protected String _string;
	public ShapeTokenizer _tokenizer;
	protected JSONObject _lookahead;
	public ShapeParser() {
		_tokenizer = new ShapeTokenizer();
	}
	public JSONObject parse(String string){
		_string = string;
		_tokenizer.init(string);
		
		this._lookahead = this._tokenizer.getNextToken();
		return this.Program();
	}

	protected JSONObject Program() {
		JSONArray planeSpecs = this.PlaneSpecs();
		JSONArray shapes = this.Shapes();
		return new JSONObject().put("plane", planeSpecs)
							   .put("shapes", shapes);
	}
	private JSONArray PlaneSpecs() {
		this._eat("PLANE");
		this._eat("(");
		JSONArray arr = new JSONArray();
		arr.put(this._eat("NUMBER"));
		this._eat(",");
		arr.put(this._eat("NUMBER"));
		this._eat(",");
		arr.put(this._eat("NUMBER"));
		this._eat(",");
		arr.put(this._eat("NUMBER"));
		this._eat(",");
		arr.put(this._eat("NUMBER"));
		this._eat(")");
		this._eat(";");
		return arr;
	}
	private JSONArray Shapes() {
		JSONArray arr = new JSONArray();
		while(this._lookahead!=null){
			arr.put(this.Shape());
			this._eat(";");
		}
		return arr;
	}
	private JSONObject Shape() {
		switch(this._lookahead.getString("type")) {
		case "TRI": 
			this._eat("TRI");
			this._eat("(");
			JSONObject aux = this.Polygon(3);
			this._eat(")");
			return aux;
		case "CUAD": 
			this._eat("CUAD");
			this._eat("(");
			JSONObject aux2 = this.Polygon(4);
			this._eat(")");
			return aux2;
		default:
			break;
		}
		return null;
	}
	private JSONObject Polygon(int n) {
		JSONArray arr = new JSONArray();
		for(int i=0;i<n;i++) {
			arr.put(this.Point());
			if(i!=n-1)this._eat(",");
		}
		return new JSONObject().put("type", "PolygonShape")
							   .put("points", arr);
	}
	private JSONObject Point() {
		switch(this._lookahead.getString("type")) {
		case "ADDITIVE_OPERATOR": 
			JSONObject op = this._eat("ADDITIVE_OPERATOR");
			this._eat("(");
			JSONObject l = this.Point();
			this._eat(",");
			JSONObject r = this.Point();
			this._eat(")");
			return new JSONObject().put("type", "OperationPoint")
								   .put("op", op.getString("value"))
								   .put("left", l)
								   .put("right", r);
		case "BASE_POINT": 
			JSONObject v = this._eat("BASE_POINT");
			return new JSONObject().put("type", "BasePoint")
					   			   .put("value", v.getString("value"));
		case "GENERAL_POINT": 
			JSONObject v2 = this._eat("GENERAL_POINT");
			return new JSONObject().put("type", "GeneralPoint")
					   			   .put("value", v2.getString("value"));
		case "NATURAL_POINT": 
			//this._eat("(");
			this._eat("NATURAL_POINT");
			JSONObject x = this._eat("NUMBER");
			this._eat(",");
			JSONObject y = this._eat("NUMBER");
			this._eat(",");
			JSONObject z = this._eat("NUMBER");
			this._eat(")");
			return new JSONObject().put("type", "NaturalPoint")
					   			   .put("x", x)
					   			   .put("y", y)
					   			   .put("z", z);
		default:
			break;
		}
		return null;
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
		RandomSingleton.setSeed(40L);
		Chromosome<Chromosome.Codon> c = new Chromosome<>(500, Chromosome.Codon::new);
		StandardGrammar grammar = new StandardGrammar();
		grammar.parseBNF("resources/grammar/default.bnf");
		LinkedList<Symbol>ss = (LinkedList<Symbol>) grammar.mapChromosome(c);
		StringBuilder sb = new StringBuilder();
		for(Symbol symb:ss)sb.append(symb.getName());
		System.out.println(sb.toString());
		
		ShapeParser parser = new ShapeParser();
		JSONObject r = parser.parse(sb.toString());
		System.out.println(r.toString(4));
	}
}
