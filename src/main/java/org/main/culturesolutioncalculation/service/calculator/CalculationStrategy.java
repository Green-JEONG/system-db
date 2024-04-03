package org.main.culturesolutioncalculation.service.calculator;

import java.util.Map;

public interface CalculationStrategy {
    Map<String, Map<String, Double>> calculateDistributedValues();
    void save();
    void insertIntoRequestHistory();
}
