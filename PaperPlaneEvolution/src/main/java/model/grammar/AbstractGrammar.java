package model.grammar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


import simulator.model.entity.individuals.Chromosome;
import simulator.model.entity.individuals.Chromosome.Codon;
import simulator.model.entity.individuals.Mapper;


public abstract class AbstractGrammar implements Mapper{
	protected Symbol initial;
	HashMap<Symbol,Rule>_rulesProductions;

	public AbstractGrammar() {
		_rulesProductions = new HashMap<Symbol,Rule>();
	}
	@Override
	public Object mapChromosome(Chromosome<?> c) {
		return mapGrammar((Chromosome<Codon>) c);
	}
	public abstract LinkedList<Symbol> mapGrammar(Chromosome<Chromosome.Codon> c);
	public abstract void parseBNF(String filename);
	public static enum SymbolType{NTerminal,Terminal}
	public class Symbol {
		String name;
		SymbolType type;
		public Symbol(String name, SymbolType type) {
			this.type = type;
			this.name = name;
		}
		public SymbolType getType() {return type;}
		@Override
		public String toString() {
			return type==SymbolType.NTerminal?"<"+name+">":name;
		}
		public String getName() {
			return name;
		}
		@Override
		public int hashCode() {
			return name.hashCode();
		}
		
		public boolean equals(String s) {
			return this.name.equals(s);
		}
		public boolean equals(Symbol s) {
			return this.name.equals(s.name)&&this.type==s.type;
		}
		@Override
		public boolean equals(Object s) {
			return equals((Symbol)s);
		}
		
	}
	public class Production extends ArrayList<Symbol>{
		protected int _minimumDepth;
		int _minimumExp;
		boolean _recursive;
		public Production() {
			super();
		}
		public int get_minimumDepth() {
			return _minimumDepth;
		}
		public void set_minimumDepth(int _minimumDepth) {
			this._minimumDepth = _minimumDepth;
		}
		public int get_minimumExp() {
			return _minimumExp;
		}
		public void set_minimumExp(int _minimumExp) {
			this._minimumExp = _minimumExp;
		}
		public boolean is_recursive() {
			return _recursive;
		}
		public void set_recursive(boolean _recursive) {
			this._recursive = _recursive;
		}
		public Production(Symbol... terms) {
			this();
			for (int i = 0; i < terms.length; i++) {
				this.add(terms[i]);
			}
		}
		
	
		public boolean equals(Production p2) {
			if(this.size()!=p2.size())return false;
			for(int i=0;i<this.size();i++) {
				if(!this.get(i).equals(p2.get(i)))return false;
			}
			return true;
		}
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (Symbol t : this) {
				if(t.type==AbstractGrammar.SymbolType.Terminal)sb.append("\'"+t+"\'");
				else sb.append(t);
				sb.append(' ');

			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}
		
		
	}
	public class Rule extends ArrayList<Production>{
		private Symbol _symbol;
		boolean _recursive;
		int _minimumDepth;
		int _minimumExp;
		public Rule() {
			super();
		}
		public Symbol get_symbol() {
			return _symbol;
		}
		public void set_symbol(Symbol _symbol) {
			this._symbol = _symbol;
		}
		public boolean is_recursive() {
			return _recursive;
		}
		public void set_recursive(boolean _recursive) {
			this._recursive = _recursive;
		}
		public int get_minimumDepth() {
			return _minimumDepth;
		}
		public void set_minimumDepth(int _minimumDepth) {
			this._minimumDepth = _minimumDepth;
		}
		
		
		
	}
	public Symbol getInitial() {
		return initial;
	}
	public Rule getRule(Symbol s) {
		return this._rulesProductions.get(s);
	}
	public void setInitial(Symbol initial) {
		if(initial.getType()==SymbolType.Terminal)throw new IllegalArgumentException("The initial symbol has to be non-Terminal");
		this.initial = initial;
	}
	public void calculateAttributes() {
		List<Symbol>visitedRules = new ArrayList<Symbol>();
		List<Symbol>calculated = new ArrayList<Symbol>();
		
		//clear
		for(Symbol s:this._rulesProductions.keySet()) {
			this._rulesProductions.get(s).set_minimumDepth(9999);
			this._rulesProductions.get(s)._minimumExp = 9999;
		}
		
		//rules mindepth
		for(Symbol s:this._rulesProductions.keySet()) {
			visitedRules.clear();
			this._calculateRuleMinDepth(this._rulesProductions.get(s), visitedRules);
			calculated.add(s);
			for(Symbol s2:this._rulesProductions.keySet()) {
				if(!calculated.contains(s2)) {
					this._rulesProductions.get(s2)._minimumDepth=9999;
				}
			}
		}
		
		//productions mindepth
		for(Symbol s:this._rulesProductions.keySet()) {
			this._calculateProductionsMinDepth(this._rulesProductions.get(s));
		}
		
		calculated.clear();
		//rules minExp
		for(Symbol s:this._rulesProductions.keySet()) {
			visitedRules.clear();
			this._calculateRuleMinExpansion(this._rulesProductions.get(s),visitedRules);
			calculated.add(s);
			for(Symbol s2:this._rulesProductions.keySet()) {
				if(!calculated.contains(s2)) {
					this._rulesProductions.get(s2)._minimumDepth=9999;
				}
			}
		}
		
		//productions minExp
		for(Symbol s:this._rulesProductions.keySet()) {
			this._calculateProductionsMinExpansion(this._rulesProductions.get(s));
		}
		
		for(Symbol s:this._rulesProductions.keySet()) {
			visitedRules.clear();
			//System.out.println("-----------------------------");
			//System.out.println("Calling "+s+" rule isRecursive");
			this._rulesProductions.get(s)._recursive = this._isRecursive(this._rulesProductions.get(s),visitedRules);
		}
		
	}
	private void _calculateRuleMinExpansion(Rule query, List<Symbol> visitedRules) {
		if(!visitedRules.contains(query.get_symbol())) {
			for(Production p:query) {
				p._minimumExp = 0;
				for(Symbol s:p) {
					
					if(s.type == SymbolType.NTerminal) {
						visitedRules.add(query.get_symbol());
						Rule r2 = this._rulesProductions.get(s);
						_calculateRuleMinExpansion(r2,visitedRules);
						p._minimumExp += r2._minimumExp+1;
					}
					else {
					}
					
				}
				query._minimumExp = Math.min(query._minimumExp, p._minimumExp);
			}
		}	
	}
	private void _calculateProductionsMinExpansion(Rule query) {
		for(Production p:query) {
			p._minimumExp = 0;
			for(Symbol s:p) {
				if(s.type == SymbolType.NTerminal) {
					p._minimumExp +=  this._rulesProductions.get(s)._minimumExp+1;
				}
				else {
				}
				
			}
		}
		
	}
	private void _calculateRuleMinDepth(Rule query, List<Symbol> visitedRules) {
		if(!visitedRules.contains(query.get_symbol())) {
			for(Production p:query) {
				p._minimumDepth = 0;
				for(Symbol s:p) {
					
					if(s.type == SymbolType.NTerminal) {
						visitedRules.add(query.get_symbol());
						Rule r2 = this._rulesProductions.get(s);
						_calculateRuleMinDepth(r2,visitedRules);
						p._minimumDepth = Math.max(p._minimumDepth, r2._minimumDepth+1);
					}
					else {
						p._minimumDepth = Math.max(p._minimumDepth, 1);
					}
					
				}
				query._minimumDepth = Math.min(query._minimumDepth, p._minimumDepth);
			}
		}	
	}
	private void _calculateProductionsMinDepth(Rule query) {
		for(Production p:query) {
			p._minimumDepth = 0;
			for(Symbol s:p) {
				if(s.type == SymbolType.NTerminal) {
					p._minimumDepth = Math.max(p._minimumDepth, this._rulesProductions.get(s)._minimumDepth+1);
				}
				else {
					p._minimumDepth = Math.max(p._minimumDepth, 1);
				}
				
			}
		}
	}
	private boolean _isRecursive(Rule query, List<Symbol> visitedRules) {
		//System.out.println("Entering "+query._symbol+" method");
		Rule r=null;
		if(visitedRules.size()==0)r = query;
		else r = this._rulesProductions.get(visitedRules.get(visitedRules.size()-1));
		if(visitedRules.contains(query._symbol)) {
			query._recursive=true;
			//System.out.println("visitedRules contains "+query._symbol);
			return true;
		}
		boolean b=false;
		for(Production p:r) {
			for(Symbol s:p) {
				if(s.type == SymbolType.NTerminal) {
					Rule query2 = this._rulesProductions.get(s);
					if(!visitedRules.contains(query2._symbol)) {
						visitedRules.add(s);
						//System.out.println(s+" added to visited rules");
						//System.out.print("visited: ");
						//for(Symbol ss:visitedRules)System.out.print(ss+ " ");
						//System.out.println();
						if(_isRecursive(query,visitedRules)) {
							p._recursive=true;
							b = true;
							//System.out.println(p+" is recursive");
							visitedRules.remove(visitedRules.size()-1);
							break;
						}
						else {
							visitedRules.remove(visitedRules.size()-1);
							//System.out.println(p+" is not recursive");
						}
					}
					
					
				}
			}
		}

		//System.out.println("Quitting "+query._symbol+" method");
		return b;
	}
	public HashMap<Symbol,Rule> getRules(){
		return this._rulesProductions;
	}
	public static void main(String args[]) {
		AbstractGrammar g = new StandardGrammar();
		g.parseBNF("test");
		g.calculateAttributes();
		
		for(Symbol k:g._rulesProductions.keySet()) {
			Rule r = g._rulesProductions.get(k);
			System.out.println("rule: "+r._symbol+" min: "+r._recursive);
			for(Production p:r) {
				System.out.println("prod: "+p+" min: "+p._recursive);
			}
		}
		
	}
	public void reset() {
		
	}
	
}
