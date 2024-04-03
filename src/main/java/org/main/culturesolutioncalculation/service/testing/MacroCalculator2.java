package org.main.culturesolutioncalculation.service.testing;

import org.main.culturesolutioncalculation.service.calculator.FinalCal;
import org.main.culturesolutioncalculation.service.macro.Macro;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MacroCalculator2 { //�Լ������� �����丵 ��

    private Map<String, Map<String, Double>> compoundsRatio = new LinkedHashMap<>();

    Map<Macro, List<Macro>> distributedValues = new LinkedHashMap<>(); //����Ʈ���� �������� �ڵ� ��� ���

    private List<Macro> standardValues = new LinkedList<>();
    private List<Macro> consideredValues = new LinkedList<>();
    private List<Macro> fertilizationValues = new LinkedList<>();

    private Map<String, FinalCal> molecularMass =  new LinkedHashMap<>();


    //ó�� �� ��� �Լ�. '���ط� - ���Ҽ��� = ó���' ����. ���� ��� ���ϸ� 0���� �Ѿ�;� ��
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
