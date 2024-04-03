package org.main.culturesolutioncalculation.service.calculator;

import java.util.LinkedHashMap;
import java.util.Map;

/*

TODO 프론트에서 아래와 같은 방식으로 strategy 전달

 */
public class test {
    private boolean is4 = true;
    private boolean isConsidered = false;
    private Map<String, Double> fertilization = new LinkedHashMap<String, Double>() {
        {
            put("NO3N", 15.5);
            put("NH4N", 1.25);
            put("H2P04", 1.25);
            put("K", 6.5);
            put("Ca", 4.75);
            put("Mg", 1.5);
            put("SO4S", 1.75);
        }
    };
    private Map<String, Double> consideredValues = new LinkedHashMap<String, Double>() {
        {
            put("NO3N", 0.0);
            put("NH4N", 0.0);
            put("H2P04",0.0);
            put("K", 0.0);
            put("Ca", 0.0);
            put("Mg", 0.0);
            put("SO4S", 0.0);
        }
    };


    public void testSetStrategy(){
        CalculationStrategy strategy = new MacroCalculationStrategy(
               "mM" , is4, isConsidered, consideredValues, fertilization
        );
        CalculatorClient client = new CalculatorClient(strategy);
        strategy.calculateDistributedValues();
        strategy.save();


    }
}
