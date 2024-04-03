package org.main.culturesolutioncalculation.service.testing;

import org.main.culturesolutioncalculation.service.calculator.FinalCal;
import org.main.culturesolutioncalculation.service.macro.Macro;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MacroCalculator2 { //함수형으로 리팩토링 중

    private Map<String, Map<String, Double>> compoundsRatio = new LinkedHashMap<>();

    Map<Macro, List<Macro>> distributedValues = new LinkedHashMap<>(); //프론트에서 보여지는 자동 계산 결과

    private List<Macro> standardValues = new LinkedList<>();
    private List<Macro> consideredValues = new LinkedList<>();
    private List<Macro> fertilizationValues = new LinkedList<>();

    private Map<String, FinalCal> molecularMass =  new LinkedHashMap<>();


    //처방 농도 계산 함수. '기준량 - 원소성분 = 처방농도' 수행. 원수 고려 안하면 0으로 넘어와야 함
    private void calculateFertilizationValue(List<Macro> standardValuesFront, List<Macro> consideredValuesFront ){

        for (Macro macro : standardValuesFront) {
            consideredValuesFront.stream()
                    .filter(c -> c.getName().equals(macro.getName()) && c.getValue() < macro.getValue())
                    .forEach(c ->{
                        double value = macro.getValue() - c.getValue();
                        String name = macro.getName();
                        fertilizationValues.add(new Macro(name, value));
                    });
        }
    }



}
