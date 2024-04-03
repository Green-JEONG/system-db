package org.main.culturesolutioncalculation.service.calculator;

import org.main.culturesolutioncalculation.service.database.DatabaseConnector;
import org.main.culturesolutioncalculation.service.users.Users;

import java.sql.*;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class MacroCalculationStrategy implements CalculationStrategy{


    private DatabaseConnector conn;

    Timestamp request_date;

    private Users users;
    private Long requestHistory_id;

    private int users_macro_consideredValues_id;

    private boolean is4;
    private boolean isConsidered;
    private String unit;

    private Map<String, Map<String, Double>> compoundsRatio = new LinkedHashMap<>(); // ex; {NH4NO3 , {NH4N=1.0, NO3N=1.0}}

    Map<String, Map<String, Double>> distributedValues = new LinkedHashMap<>(); //프론트에서 보여지는 자동 계산 결과

    private Map<String, FinalCal> molecularMass =  new LinkedHashMap<>();

    //1. 기준값 - 프론트에서 넘어옴
    //private Map<String, Double> standardValues = new LinkedHashMap<>();
    //2. 원수 고려값 - 프론트에서 넘어옴
    private Map<String, Double> consideredValues = new LinkedHashMap<>();

    //3. 처방 값. 넘어와야 할 처방 농도 양식 예시 - 순서 그대로 유지되어야 함 (기준값 - 원수고려값)
    private Map<String, Double> userFertilization = new LinkedHashMap<>(); //db에 저장되어야 할 처방 농도 (계산 수행 X)
    private Map<String, Double> calFertilization = new LinkedHashMap<>(); //계산 수행할 처방 농도
//    private Map<String, Double> fertilization = new LinkedHashMap<String, Double>(){
//        {
//            put("NO3N", 15.5);
//            put("NH4N", 1.25);
//            put("H2P04",1.25);
//            put("K",6.5);
//            put("Ca",4.75);
//            put("Mg",1.5);
//            put("SO4S",1.75);
//        }
//    };
    public MacroCalculationStrategy(Users users, String unit, boolean is4, boolean isConsidered, Map<String, Double> consideredValues, Map<String, Double> userFertilization){
        this.users = users;
        this.unit = unit;
        this.is4 = is4;
        this.isConsidered = isConsidered;
        this.consideredValues = consideredValues;
        this.userFertilization = userFertilization;
        this.calFertilization = userFertilization;
        request_date = Timestamp.from(Instant.now());
        getMajorCompoundRatio(is4);
    }


    //분자 별 갖고 있는 다량 원소 비율을 가져옴
    private void getMajorCompoundRatio(boolean is4){ //4수염인지 10수염인지를 판단하는 파라미터

        String query = "select * from macronutrients";
        query += is4? " where id != 2" : " where id != 1"; //id=1 : 질산칼슘4수염, id=2 : 질산칼슘10수염

        try (Connection connection = conn.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(query);) {

            while(resultSet.next()){
                String macro = resultSet.getString("macro"); //질산칼슘4수염, 질산칼륨, 질산암모늄 등등
                String solution = resultSet.getString("solution"); //양액 타입 (A,B, C)
                double mass = resultSet.getDouble("mass");//화합물 질량

                molecularMass.put(macro, new FinalCal(solution, mass)); //100배액 계산을 위해 화합물과 그 질량 저장

                Map<String, Double> compoundRatio = new LinkedHashMap<>(); //ex. 질산칼슘4수염이 갖는 원수의 이름과 질량비를 갖는 map
                for (String major : userFertilization.keySet()) {
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
    //===========================================================================TODO=================
    //자동 계산 시 프론트에 보여지는 분배된 값
    public Map<String, Map<String, Double>> calculateDistributedValues() {

        for (String compound : compoundsRatio.keySet()) {
            distributedValues.put(compound, calculateCompoundDistribution(compound, calFertilization));
        }

        return distributedValues;
    }
    //화합물에 포함된 원수 질량비에 따른 분산 계산
    private Map<String, Double> calculateCompoundDistribution(String compound, Map<String, Double> calFertilization) {
        double minRatioValue = calculateMinimumRatioForCompound(compound, calFertilization);
        Map<String, Double> compoundDistribution = new LinkedHashMap<>();

        for (String nutrient : compoundsRatio.get(compound).keySet()) {
            double allocation = calculateNutrientAllocation(nutrient, compound, minRatioValue);
            calFertilization.put(nutrient, calFertilization.get(nutrient) - allocation);
            compoundDistribution.put(nutrient, allocation);
        }

        updateMolecularMass(compound, minRatioValue);
        return compoundDistribution;
    }
    //화합물에 포함된 원수에 대해 최소 비율 값 계산
    private double calculateMinimumRatioForCompound(String compound, Map<String, Double> calFertilization) {
        double minRatioValue = Double.MAX_VALUE;

        for (String nutrient : compoundsRatio.get(compound).keySet()) { //해당 화합물의 원수
            double availableAmount = userFertilization.get(nutrient); //해당 원수의 처방 농도
            double ratio = compoundsRatio.get(compound).get(nutrient); //해당 원수의 화합물 첨가 비율
            double amountBasedOnRatio = availableAmount / ratio;
            minRatioValue = Math.min(minRatioValue, amountBasedOnRatio);
        }

        return minRatioValue;
    }
    //최소 비율 값을 사용해 원수 배당량 계산
    private double calculateNutrientAllocation(String nutrient, String compound, double minRatioValue) {
        double ratio = compoundsRatio.get(compound).get(nutrient);
        return ratio * minRatioValue;
    }
    //최종 계산된 최소 비율 값에 따라 화합물 질량 업데이트
    private void updateMolecularMass(String compound, double minRatioValue) {
        double mass = molecularMass.get(compound).getMass();
        molecularMass.get(compound).setMass(minRatioValue * mass);
    }

    //원수 고려 여부, 처방 농도, 고려 원수, 기준값 -> db에 저장하는 함수
    public void save(){
        insertIntoRequestHistory();
        insertIntoUsersMacroConsideredValues(); //원수 고려 값 테이블에 저장
        insertIntoUsersMacroFertilization();
        insertIntoUsersMacroCalculatedMass();
    }

    public void insertIntoRequestHistory() {
        String query = "insert into requestHistory (users_id, request_date) " +
                "values (" + users.getId() + ", " + request_date + ")";

        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("insert success in requestHistory");
                // 생성된 pk get
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        requestHistory_id = generatedKeys.getLong(1); // 생성된 ID
                        System.out.println("Generated Request ID: " + requestHistory_id);
                    } else {
                        System.out.println("No ID was generated.");
                    }
                }

            } else System.out.println("insert fail in requestHistory");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //TODO - insert 테스트
    private void insertIntoUsersMacroFertilization(){
        String query = "insert into users_macro_fertilization (users_id";
        for (String macro : userFertilization.keySet()) {
            query += ", "+macro;
        }
        query += ", requestHistory_id) "; //여기까지 query = insert into user_macro_fertilization (macro, NO3N, Ca, requestHistory_id)
        query += "values (" + users.getId();

        for (String macro : userFertilization.keySet()) {
            query += ", "+userFertilization.get(macro);
        }
        query += ", "+requestHistory_id+")";
        try(Connection connection = conn.getConnection();
            Statement stmt = connection.createStatement();){
            int result = stmt.executeUpdate(query);

            //if(result>0) System.out.println("success insert users_macro_fertilization");
            //else System.out.println("insert failed users_macro_fertilization");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

//    private void insertIntoUsersMacroFertilization() { //계산된 처방값 DB 저장
//        for (String macro : distributedValues.keySet()) {
//            String query = "insert into users_macro_fertilization (users_id, macro";
//            for (String element : distributedValues.get(macro).keySet()) {
//                query += ", "+element;
//            }
//            query += ") "; //여기까지 query = insert into user_macro_fertilization (macro, NO3N, Ca)
//            query += "values (" + users_id +", "+"'"+macro+"'";
//            for (String element : distributedValues.get(macro).keySet()) {
//                query += ", "+distributedValues.get(macro).get(element);
//            }
//            query += ")";
//            try(Connection connection = conn.getConnection();
//                Statement stmt = connection.createStatement();){
//                int result = stmt.executeUpdate(query);
//
//                //if(result>0) System.out.println("success insert users_macro_fertilization");
//                //else System.out.println("insert failed users_macro_fertilization");
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//        }
//    }

    //TODO - insert 제대로 되는 지 확인
    //100배액(kg) 계산식 저거 맞나 확인받기
    private void insertIntoUsersMacroCalculatedMass() { //계산된 질량 값 DB 저장
        String unit = "'kg'";

        for (String macro : molecularMass.keySet()) {
            double concentration_100fold = molecularMass.get(macro).getMass() / 10;

            String query = "insert into users_macro_calculatedMass (user_id, users_macro_consideredValues_id, macro, mass, unit, solution, requestHistory_id) " +
                    "values ("+ users.getId() +", "+users_macro_consideredValues_id+", "+"'"+macro+"'"+", "
                    +concentration_100fold+", "+unit+", "+molecularMass.get(macro).getSolution()+", "+requestHistory_id+")";

            try(Connection connection = conn.getConnection();
                    Statement stmt = connection.createStatement();){
                int result = stmt.executeUpdate(query);

                //if(result>0) System.out.println("success");
                //else System.out.println("insert failed");
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    //TODO 테스트
    private void insertIntoUsersMacroConsideredValues() { //고려 원수 값 DB 저장
        String query = "insert into users_macro_consideredValues ";
        String values = "(is_considered, NO3N, NH4N, " +
                "H2PO4, K, Ca, Mg, SO4S, unit, user_id, requestHistory_id) values (";

        if(!isConsidered){
            query += "(is_considered, unit, user_id) values (false, "+unit+", "+users.getId()+", "+requestHistory_id+")";
        } else{
            values += "true";
            for (String value : consideredValues.keySet()) {
                values += ", "+consideredValues.get(value);
            }
            values += requestHistory_id+")";
            query += values;
        }

        try (Connection connection = conn.getConnection();
            Statement stmt = connection.createStatement()) {
            int result = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            if (result > 0) {
                System.out.println("success insert users_macro_consideredValues");
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if(generatedKeys.next()){
                    int id = generatedKeys.getInt(1);
                    users_macro_consideredValues_id = id; //fk로 사용하기 위해 배정
                }
            } else {
                System.out.println("insert failed users_macro_consideredValues");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
