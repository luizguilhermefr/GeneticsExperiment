package main.jnethica.Selector;

import main.jnethica.Fitness.Contracts.FitnessCalculator;
import main.jnethica.Individual.Contracts.Individual;
import main.jnethica.Population.Population;
import main.jnethica.Selector.Contracts.Selector;
import main.jnethica.Util.RandomSingleton;

import java.util.ArrayList;

public class RouletteSelection<T extends Individual> implements Selector {

    private FitnessCalculator fitnessCalculator;

    public RouletteSelection (FitnessCalculator fitnessCalculator) {
        this.fitnessCalculator = fitnessCalculator;
    }

    private ArrayList<Double> normalize (ArrayList<Double> fitnessValues, Double smallerFitness) {
        for (Integer i = 0; i < fitnessValues.size(); i++) {
            fitnessValues.set(i, fitnessValues.get(i) + smallerFitness);
        }
        return fitnessValues;
    }

    private Double sumOfFitness (ArrayList<Double> fitnessValues) {
        Double count = 0.0;
        for (Double fitness : fitnessValues) {
            count += fitness;
        }
        return count;
    }

    private ArrayList<Double> getFitnessValuesNormalizedIfNecessary (Population population) {
        ArrayList<T> individuals = population.getIndividuals();
        ArrayList<Double> fitnessValues = new ArrayList<>();
        Boolean negativeFitnessFound = Boolean.FALSE;
        Double smallerFitness = Double.POSITIVE_INFINITY;
        for (T individual : individuals) {
            Double fitness = fitnessCalculator.getFitness(individual);
            if (fitness < 0) {
                negativeFitnessFound = Boolean.TRUE;
            }
            if (fitness < smallerFitness) {
                smallerFitness = fitness;
            }
            fitnessValues.add(fitness);
        }
        if (negativeFitnessFound) {
            return normalize(fitnessValues, smallerFitness);
        }

        return fitnessValues;
    }

    @Override
    public T select (Population population) {
        ArrayList<Double> normalizedFitness = getFitnessValuesNormalizedIfNecessary(population);

        Double sumOfFitness = sumOfFitness(normalizedFitness);
        Double randomFitness = RandomSingleton.getInstance().doubleBetween(0.0, sumOfFitness);

        for (Integer i = 0; i < normalizedFitness.size() - 1; i++) {
            Double fitnessOfCurrentIndividual = normalizedFitness.get(i);
            Double fitnessOfNextIndividual = normalizedFitness.get(i + 1);

            if (fitnessOfCurrentIndividual >= randomFitness && randomFitness <= fitnessOfNextIndividual) {
                return (T) population.getIndividualByIndex(i);
            }
        }

        return (T) population.getLastIndexIndividual();
    }
}