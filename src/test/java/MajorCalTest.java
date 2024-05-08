import javafx.scene.control.skin.HyperlinkSkin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.main.culturesolutioncalculation.service.calculator.FinalCal;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MajorCalTest {


    private final String url = "jdbc:mysql://localhost:3306/CultureSolutionCalculation?useSSL=false";
    private final String user = "root";
    private final String password = "root";

    private Connection connection;

    Map<String, Map<String, Double>> distributedValues = new LinkedHashMap<>(); //����Ʈ���� �������� �ڵ� ��� ���


    private Map<String, Map<String, Double>> compoundsRatio = new LinkedHashMap<>();

    private Map<String, Double> fertilization = new LinkedHashMap<>(){
        {
            put("NO3N", 15.5);
            put("NH4N", 1.25);
            put("H2PO4",1.25);
            put("K",6.5);
            put("Ca",4.75);
            put("Mg",1.5);
            put("SO4S",1.75);
        }
    };

    private Map<String, Double> calculated100 = new LinkedHashMap<>();

    private Map<String, FinalCal> molecularMass =  new LinkedHashMap<>();

    @BeforeEach
    void connection() throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);

    }

    @Test
    void getRatioDirectly(){
        boolean is4 = true;
        String query = "select * from macronutrients";
        query += is4? " where id != 2" : " where id != 1"; //id=1 : 질산칼슘4수염, id=2 : 질산칼슘10수염

        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(query);) {

            while(resultSet.next()){
                String macro = resultSet.getString("macro"); //질산칼슘4수염, 질산칼륨, 질산암모늄 등등
                Map<String, Double> compoundRatio = new LinkedHashMap<>(); //ex. 질산칼슘4수염이 갖는 원수의 이름과 질량비를 갖는 map
                for (String major : fertilization.keySet()) {
                    if(resultSet.getDouble(major) != 0){
                        compoundRatio.put(major,resultSet.getDouble(major));
                    }
                }
                compoundsRatio.put(macro,compoundRatio);
            }


        }catch (SQLException e){
            e.printStackTrace();
        }

    }


    //분자 별 갖고 있는 다량 원소 비율을 가져옴
    @Test
    void getMajorCompoundRatio(){ //4수염인지 10수염인지를 판단하는 파라미터

        boolean is4 = true;
        String query = "select * from macronutrients";
        query += is4? " where id != 2" : " where id != 1"; //id=1 : 질산칼슘4수염, id=2 : 질산칼슘10수염

        try (
                Statement stmt = connection.createStatement();
                ResultSet resultSet = stmt.executeQuery(query);) {

            while(resultSet.next()){
                String macro = resultSet.getString("macro"); //질산칼슘4수염, 질산칼륨, 질산암모늄 등등
                String solution = resultSet.getString("solution"); //양액 타입 (A,B, C)
                double mass = resultSet.getDouble("mass");//화합물 질량

                molecularMass.put(macro, new FinalCal(solution, mass));

                Map<String, Double> compoundRatio = new HashMap<>(); //ex. 질산칼슘4수염이 갖는 원수의 이름과 질량비를 갖는 map
                for (String major : fertilization.keySet()) {
                    if(resultSet.getDouble(major) != 0){
                        compoundRatio.put(major,resultSet.getDouble(major));
                    }
                }
                compoundsRatio.put(macro,compoundRatio);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }


//        for (String name : molecularMass.keySet()) {
//            System.out.println("name = " + name);
//            System.out.println("molecularMass.get(name) = " + molecularMass.get(name).getMass());
//            System.out.println("type : "+molecularMass.get(name).getSolution());
//        }
    }
    //자동 계산 시 프론트에 보여지는 분배된 값
    public Map<String, Map<String, Double>> calculateDistributedValues() {
        for (String compound : compoundsRatio.keySet()) {
            distributedValues.put(compound, calculateCompoundDistribution(compound, fertilization));
        }

        return distributedValues;
    }
    //화합물에 포함된 원수 질량비에 따른 분산 계산
    private Map<String, Double> calculateCompoundDistribution(String compound, Map<String, Double> userFertilization) {
        double minRatioValue = calculateMinimumRatioForCompound(compound, userFertilization);
        Map<String, Double> compoundDistribution = new LinkedHashMap<>();

        for (String nutrient : compoundsRatio.get(compound).keySet()) {
            double allocation = calculateNutrientAllocation(nutrient, compound, minRatioValue);
            fertilization.put(nutrient, fertilization.get(nutrient) - allocation); //추가한 코드 TODO
            compoundDistribution.put(nutrient, allocation);
        }
//        System.out.println("화합물에 포함된 원수 질량비에 따른 분산 계산");
//        for (String nutrient : compoundDistribution.keySet()) {
//            System.out.println(nutrient+"  = " +compoundDistribution.get(nutrient));
//        }
//        System.out.println("\n\n");

        updateMolecularMass(compound, minRatioValue);
        return compoundDistribution;
    }
    //화합물에 포함된 원수에 대해 최소 비율 값 계산
    private double calculateMinimumRatioForCompound(String compound, Map<String, Double> userFertilization) {
        double minRatioValue = Double.MAX_VALUE;


//        System.out.print("화합물 "+compound+"에 대한 최소 비율 값 계산 : ");
        for (String nutrient : compoundsRatio.get(compound).keySet()) { //해당 화합물의 원수
            double availableAmount = userFertilization.get(nutrient); //해당 원수의 처방 농도
            double ratio = compoundsRatio.get(compound).get(nutrient); //해당 원수의 화합물 첨가 비율
            double amountBasedOnRatio = availableAmount / ratio;
            minRatioValue = Math.min(minRatioValue, amountBasedOnRatio);
//            System.out.println(nutrient + "에 대한 최소 비율 값 : "+minRatioValue);
        }
        System.out.println("\n\n");

        return minRatioValue;
    }
    //최소 비율 값을 사용해 원수 배당량 계산
    private double calculateNutrientAllocation(String nutrient, String compound, double minRatioValue) {
        double ratio = compoundsRatio.get(compound).get(nutrient);
        System.out.println("compound = "+compound+" nutrient = " + nutrient +" amount = "+ratio*minRatioValue);
        return ratio * minRatioValue;
    }
    //최종 계산된 최소 비율 값에 따라 화합물 질량 업데이트
    private void updateMolecularMass(String compound, double minRatioValue) {
        double mass = molecularMass.get(compound).getMass();
        molecularMass.get(compound).setMass(minRatioValue * mass);
    }

    @BeforeEach
    void setup(){
        getMajorCompoundRatio();
        //calculateWithRatio();
    }


    @Test
    void calculateWithRatio(){

        calculateDistributedValues();
        System.out.println("\n\n\n\n\n\n\n\n=========");
        for (String name : distributedValues.keySet()) {
            System.out.println("화합물 = " + name);
            System.out.println("distributedValues = " + distributedValues.get(name));
        }

    }


    //===========================================================================
    //자동계산 시 프론트에서 보여지는 분배된 값
//    public Map<String, Map<String, Double>> calculateDistributedValues(Map<String, Double> userFertilization){
//        double minRatioValue = Double.MAX_VALUE;
//        //Map<String, Double> result = fertilization; //나중에 userFertilization이 들어오면 바꿀것 (result = userFertilization으로)
//
//        for (String compound : compoundsRatio.keySet()) { //ex; {NH4NO3 , {NH4N=1.0, NO3N=1.0}}에서 NH4NO3가 compound
//            Map<String, Double> result = new LinkedHashMap<>();
//            double ratio = 0.0, allocatedAmount = 0.0;
//
//            //각 성분의 가장 낮은 비율에 해당하는 원수와 양 계산
//            minRatioValue = getMinRatioValue(compoundsRatio.get(compound), fertilization, minRatioValue); //나중에 front에서 값 들어오면 userFertilization으로 바꿀것
//
//            //가장 낮은 비율 기반으로 배당되는 처방 농도 계산 후 갱신
//            for (String macro : compoundsRatio.get(compound).keySet()) {
//                ratio = compoundsRatio.get(compound).get(macro);
//                allocatedAmount = ratio * minRatioValue;
//                result.put(macro, allocatedAmount);
//            }
//            distributedValues.put(compound, result);
//            molecularMass.get(compound).setMass(minRatioValue * molecularMass.get(compound).getMass());//최종 minValue * mass 한 값
//        }
//        return distributedValues;
//    }
//
//    private double getMinRatioValue(Map<String, Double> innerRatio, Map<String, Double> result, double minRatioValue) {
//        double ratio, available, amountBasedOnRatio;
//        for (String macro : innerRatio.keySet()) { // ex; compound에 대한 {NH4N=1.0, NO3N=1.0}, NH4N과 NO3N이 macro
//            available = result.get(macro); //해당 원수의 처방농도
//            ratio = innerRatio.get(macro); //해당 원수의 화합물에 대한 첨가 비율
//            amountBasedOnRatio = available / ratio;
//            minRatioValue = Math.min(minRatioValue, amountBasedOnRatio);
//        }
//        return minRatioValue;
//    }



    @Test
    void users_macro_fertilization_insert_test(){

        String users_id = "1";


        String query = "insert into users_macro_fertilization (users_id";
        for (String macro : fertilization.keySet()) {
            query += ", "+macro;
        }
        query += ") "; //여기까지 query = insert into user_macro_fertilization (macro, NO3N, Ca)
        query += "values (" + users_id;

        for (String macro : fertilization.keySet()) {
            query += ", "+fertilization.get(macro);
        }
        query += ")";
        try(Statement stmt = connection.createStatement();){
            int result = stmt.executeUpdate(query);

            if(result>0) System.out.println("success");
            else System.out.println("insert failed");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


}









