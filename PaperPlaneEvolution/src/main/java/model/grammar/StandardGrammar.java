package model.grammar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import model.grammar.bnf.BNFParser;

/**
 * 
 * @author fabrizioortega
 *
 */
public class StandardGrammar extends AbstractGrammar{


	public StandardGrammar() {
		super();
		
	}

	public LinkedList<Symbol> mapGrammar(Chromosome<Chromosome.Codon> c){
		Symbol t = this.getInitial();
		List<Production> ps;
		LinkedList<Symbol> q = new LinkedList<Symbol>();
		LinkedList<Symbol> terminals = new LinkedList<Symbol>();
		// = new ArrayList<Production>(); 
		// ps.addAll(0,g.productions.get(init));
		int limit=100;
		int i=0;
		int cont=0;
		int calls=0;
		while(true) {
			ps = this.getRule(t);
			int m = ps.size();
			//int r = Util.toInt(codons.get(i).bits.get(0, Util.log2(m)));
			
			int r = ((Chromosome.Codon) c.getCodon(i)).getIntValue() % m;
			((Chromosome.Codon) c.getCodon(i)).setMod(r);
			q.addAll(0, ps.get(r));
			
			//terminals.add(g.new Terminal("("));
			calls++;
			while(!q.isEmpty() && q.getFirst().getType()==AbstractGrammar.SymbolType.Terminal) {
				if(!q.getFirst().toString().equals(")"))cont++;
				terminals.add(q.pop());
			}
			
			if(q.isEmpty())break;
			
			t = q.pop();
			//q.add(0, g.new Terminal(")"));
			i++;
			i %= c.getLength();
			if(calls>=limit)return null;
		}
		c.setUsedCodons(calls);
		//terminals.add(g.new Symbol(")",Grammar.SymbolType.Terminal));

		
		return terminals;
	}
	
	@Override
	public void parseBNF(String filename) {
		StringBuilder sb = new StringBuilder();
		//try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("resources/loads/grammars/"+filename+".bnf")));){
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));){
			String aux = reader.readLine();
			while(aux!=null) {
				sb.append(aux);
				aux = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String string = sb.toString();
		
		BNFParser parser = new BNFParser();
		JSONObject o = parser.parse(string);
		
		JSONArray rules = o.getJSONArray("rules");
		for(int i=0;i<rules.length();i++) {
			JSONObject rule = rules.getJSONObject(i);
			
			String name = rule.getJSONObject("name").getString("id");
			Symbol nameS = new Symbol(name,AbstractGrammar.SymbolType.NTerminal);
			
			if(i==0)this.setInitial(nameS);
			
			JSONArray productions = rule.getJSONArray("productions");
			List<Production> ps = new ArrayList<Production>();
			for(int j=0;j<productions.length();j++) {
				JSONArray p = productions.getJSONArray(j);
				Production production = new Production();
				for(int k=0; k<p.length();k++) {
					JSONObject s = p.getJSONObject(k);
					if(s.getString("type").equals("Terminal")) {
						Symbol inS = new Symbol(s.getString("id"),AbstractGrammar.SymbolType.Terminal);
						production.add(inS);
					}
					else {
						Symbol inS = new Symbol(s.getString("id"),AbstractGrammar.SymbolType.NTerminal);
						production.add(inS);
					}
				}
				ps.add(production);
			}
			Rule r = new Rule();
			r.set_symbol(nameS);
			r.addAll(ps);
			addRule(nameS,r);
		}
	}
	public void addRule(Symbol s, Rule r) {
		_rulesProductions.put(s, r);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Symbol nt : _rulesProductions.keySet()) {
			sb.append(nt + " -> ");
			for (Production p : _rulesProductions.get(nt)) {
				sb.append(p);
				sb.append(" | ");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
			sb.append('\n');
		}
		return sb.toString();
	}
	public static void main(String args[]) {
		StandardGrammar g = new StandardGrammar();
		g.parseBNF("default");
		System.out.println(g);

	}
}
