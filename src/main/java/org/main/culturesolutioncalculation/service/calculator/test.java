package org.main.culturesolutioncalculation.service.calculator;

import org.main.culturesolutioncalculation.service.users.Users;

import java.util.*;

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
    List<String> userMicroNutrients = new LinkedList<>(Arrays.asList("Fe-EDTA", "H3BO3", "MnSO4·H2O", "Na2MoO4·2H2O"));
    private String unit = "ppm";


    public void testSetStrategy(){
        CalculationStrategy strategy = new MicroCalculationStrategy(
               new Users(),  unit, isConsidered,userMicroNutrients, consideredValues, fertilization
        );

        CalculatorClient client = new CalculatorClient(strategy);

        Map<String, Map<String, Double>> calculateValue = client.calculate();

        client.save();
    }
}
