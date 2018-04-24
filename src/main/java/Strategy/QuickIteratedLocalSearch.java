package main.java.Strategy;

import main.java.Exception.EmptyPopulationException;
import main.java.Fitness.Contracts.FitnessCalculator;
import main.java.Individual.Contracts.Individual;
import main.java.Mutator.Contracts.Mutator;
import main.java.Mutator.CreepMutator;
import main.java.Population.Population;
import main.java.StopCondition.Contracts.StopCondition;
import main.java.Strategy.Contracts.Strategy;

public class QuickIteratedLocalSearch extends Strategy {
    public QuickIteratedLocalSearch (Population initialPopulation, FitnessCalculator fitnessCalculator, StopCondition stopCondition) {
        super(initialPopulation, fitnessCalculator, stopCondition);
    }

    @Override
    public void run () {
        Individual starter;
        try {
            starter = this.initialPopulation.getBetter(fitnessCalculator);
        } catch (EmptyPopulationException e) {
            e.printStackTrace();
            return;
        }

        globalOptimum = starter;
        globalGeneration = 0;

        Mutator smallMutator = new CreepMutator(0.125);
        Mutator bigMutator = new CreepMutator(1.0);

        Individual localBetter = starter;

        Integer currentGenerationNumber = 1;

        do {
            do {
                Individual neighbor = localBetter.mutate(smallMutator);
                if (neighbor.isBetterThan(globalOptimum, fitnessCalculator)) {
                    localBetter = neighbor;
                } else {
                    break;
                }
            } while (true);
            localBetter = localBetter.mutate(bigMutator);
            if (localBetter.isBetterThan(globalOptimum, fitnessCalculator)) {
                globalOptimum = localBetter;
                globalGeneration = currentGenerationNumber;
            }
            stopCondition.report(currentGenerationNumber, fitnessCalculator.getFitness(globalOptimum));
            currentGenerationNumber++;
        } while (!stopCondition.mustStop());
    }
}