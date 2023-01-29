package model;

import java.util.Properties;
import java.util.Random;

import model.grammar.Parser;
import model.individual.Individual;
import model.individual.Population;
import model.module.operator.fitness.FitnessEvaluationOperator;

public class PlaneFitnessOperator extends FitnessEvaluationOperator{
	public PlaneFitnessOperator(Properties properties, Random rnd) {
		super(properties, rnd);
	}

	@Override
	public void setProperties(Properties properties) {
		super.setProperties(properties);
	}

	@Override
	public float evaluate(Individual ind) {
		return 0f;
	}
}
