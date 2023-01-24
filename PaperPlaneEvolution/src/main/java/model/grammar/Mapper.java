package model.grammar;

import java.util.HashSet;
import java.util.LinkedList;

import model.grammar.AbstractGrammar.Symbol;


public interface Mapper {
	public abstract Object mapChromosome(Chromosome<?> c);
	public default LinkedList<Symbol> mapGrammar(Chromosome<Chromosome.Codon> c){return null;}
	public default HashSet<String> mapGenes(Chromosome<Boolean> c){return null;}

}
