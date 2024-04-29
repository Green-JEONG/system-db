package org.main.culturesolutioncalculation.service.calculator;

import java.util.Map;

public class CalculatorClient {
    private CalculationStrategy strategy;

    public CalculatorClient(CalculationStrategy strategy){
        this.strategy = strategy;
    }

    public void setStrategy(CalculationStrategy strategy){
        this.strategy = strategy;
    }

    public Map<String, Map<String, Double>> calculate(){
        return strategy.calculateDistributedValues();
    }

    public void save(){
        strategy.save();
    }
}
