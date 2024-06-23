package org.main.culturesolutioncalculation.service.calculator;

import org.main.culturesolutioncalculation.service.database.DatabaseConnector;

import java.util.Map;

public interface CalculationStrategy {
    Map<String, Map<String, Double>> calculateDistributedValues();
    void save();

    Map<String, FinalCal> getMolecularMass();

    Map<String, Double> getUserFertilization();

    Map<String, Double> getConsideredValues();
}
