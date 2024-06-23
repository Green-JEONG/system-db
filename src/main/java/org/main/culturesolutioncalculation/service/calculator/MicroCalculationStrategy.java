package org.main.culturesolutioncalculation.service.calculator;

import org.main.culturesolutioncalculation.RequestHistoryInfo;
import org.main.culturesolutioncalculation.service.database.DatabaseConnector;
import org.main.culturesolutioncalculation.service.users.Users;

import java.sql.*;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MicroCalculationStrategy implements CalculationStrategy{

    private static final String url = "jdbc:mysql://localhost:3306/CultureSolutionCalculation?useSSL=false";
    private static final String user = "root";
    private static final String password = "root";
    private DatabaseConnector conn;
    private Users users;

    private RequestHistoryInfo requestHistoryInfo;

    private boolean isConsidered;
    private int requestHistory_id;
    private Timestamp request_date;
    private String unit;
    private int users_micro_consideredValues_id;
    private Map<String, Map<String, Double>> compoundsRatio = new LinkedHashMap<>(); // ex; {NH4NO3 , {NH4N=1.0, NO3N=1.0}}

    Map<String, Map<String, Double>> distributedValues = new LinkedHashMap<>(); //프론트에서 보여지는 자동 계산 결과

    private Map<String, FinalCal> molecularMass =  new LinkedHashMap<>();

    @Override
    public Map<String, FinalCal> getMolecularMass() {
        return molecularMass;
    }

    List<String> userMicroNutrients = new LinkedList<>(); //유저가 선택했던 미량원소 비료 리스트

    //1. 기준값 - 프론트에서 넘어옴
    //private Map<String, Double> standardValues = new LinkedHashMap<>();
    //2. 원수 고려값 - 프론트에서 넘어옴
    private Map<String, Double> consideredValues = new LinkedHashMap<>();

    //넘어와야 할 처방 농도 양식 - 순서 그대로 유지되어야 함. front에서 넘어와야함
    private Map<String, Double> userFertilization = new LinkedHashMap<>(); //db에 저장되어야 할 처방 농도 (계산 수행 X)

    @Override
    public Map<String, Double> getUserFertilization() {
        return userFertilization;
    }

    @Override
    public Map<String, Double> getConsideredValues() {
        return consideredValues;
    }

    private Map<String, Double> calFertilization = new LinkedHashMap<>(); //계산 수행할 처방 농도
//    private Map<String, Double> fertilization = new LinkedHashMap<String, Double>(){
//        {
//            put("Fe", 15.5);
//            put("Cu", 0.75);
//            put("B", 30.0);
//            put("Mn", 10.0);
//            put("Zn", 5.0);
//            put("Mo",0.5);
//        }
//    };

    public MicroCalculationStrategy(RequestHistoryInfo requestHistoryInfo, String unit, boolean isConsidered,  List<String> userMicroNutrients, Map<String, Double> consideredValues, Map<String, Double> userFertilization){
        this.requestHistoryInfo = requestHistoryInfo;
        this.unit = unit;
        this.isConsidered = isConsidered;
        this.userMicroNutrients = userMicroNutrients;
        this.consideredValues = consideredValues;
        this.userFertilization = userFertilization;
        this.calFertilization = userFertilization;
        request_date = Timestamp.from(Instant.now());
        this.conn = DatabaseConnector.getInstance(url, user, password);
        getMajorCompoundRatio(userMicroNutrients);
    }

    //분자 별 갖고 있는 미량 원소 비율을 가져옴. userMicroNutrients : 사용자가 선택한 미량원소 리스트
    private void getMajorCompoundRatio(List<String> userMicroNutrients){ // 몰리브뎀 화합물 종류만 들어옴

        String query = "select * from micronutrients where micro in ('CuSO4·5H2O', 'ZnSO4·7H2O', 'Fe-EDTA', 'H3BO3', 'MnSO4·5H2O'"; //황산 구리, 황산 아연, Fe-EDTA, H3BO3, MnSO4·5H2O 화합물은 무조건 선택
        //Na2MoO4·2H2O
        for (String micro : userMicroNutrients) {
            query += ", '"+micro+"'";
        }
        query += ");";

        try (Connection connection = conn.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(query);) {

            while(resultSet.next()){
                String micro = resultSet.getString("micro"); //질산칼슘4수염, 질산칼륨, 질산암모늄 등등
                String solution = resultSet.getString("solution");
                double mass = resultSet.getDouble("mass");
                int contentCount = resultSet.getInt("content_count");

                molecularMass.put(micro, new FinalCal(solution, mass)); //100배액 계산을 위해 화합물과 그 질량, 양액 저장

                Map<String, Double> compoundRatio = new LinkedHashMap<>(); //ex. 질산칼슘4수염이 갖는 원수의 이름과 질량비를 갖는 map
                for (String major : userFertilization.keySet()) {
                    if (resultSet.getDouble(major) != 0) {
                        compoundRatio.put(major, resultSet.getDouble(major));
                        try (Statement innerStmt = connection.createStatement();
                             ResultSet set = innerStmt.executeQuery("select mass from micronutrients_mass where micro = '" + major + "'")) {
                            if (set.next()) {
                                double micro_mass = set.getDouble("mass");
                                //int content_count = set.getInt("content_count");
                                compoundRatio.put("mass", micro_mass); // 원자량도 같이 저장해야 함
                                compoundRatio.put("content_count", contentCount*1.0);
                            }
                        }

                    }
                }
                compoundsRatio.put(micro, compoundRatio);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // 분자량*시비량/원자량/함량갯수 = 100배액
    /*
      name = ZnSO4·7H2O
      compoundsRatio = {content_count=1.0, Zn=1.0, mass=65.37}
     */
    public Map<String, Map<String, Double>> calculateDistributedValues() {
        Map<String, Double> results = userFertilization;

        for (String compound : compoundsRatio.keySet()) {
            Map<String, Double> calculatedCompounds = calculateCompoundDistribution(compound, calFertilization);
            distributedValues.put(compound, calculatedCompounds); // 계산된 화합물 분포 저장
        }

        return distributedValues;
    }
    // 계산된 화합물 분포를 반환
    private Map<String, Double> calculateCompoundDistribution(String compound, Map<String, Double> calFertilization) {
        Map<String, Double> distributionResult = new LinkedHashMap<>();
        double atomicWeight = compoundsRatio.get(compound).get("mass"); // 원자량
        double molecularWeight = molecularMass.get(compound).getMass(); // 분자량
        double contentCount = compoundsRatio.get(compound).get("content_count"); // 함량 개수

        for (String nutrient : calFertilization.keySet()) {
            if (compoundsRatio.get(compound).containsKey(nutrient)) {
                double fertilizationAmount = calFertilization.get(nutrient);
                double calculatedValue = calculateMicroElementValue(molecularWeight, fertilizationAmount, atomicWeight, contentCount);
                distributionResult.put(nutrient, fertilizationAmount);
                molecularMass.get(compound).setMass(calculatedValue); // 최종 값 업데이트
            }
        }

        return distributionResult;
    }

    // 미량 원소 값을 계산
    private double calculateMicroElementValue(double molecularWeight, double fertilizationAmount, double atomicWeight, double contentCount) {
        return molecularWeight * fertilizationAmount / atomicWeight / contentCount;
    }

    //원수 고려 여부, 처방 농도, 고려 원수, 기준값 -> db에 저장하는 함수
    public void save(){
        //insertIntoRequestHistory();
        insertIntoUsersMicroConsideredValues(); //원수 고려 값 테이블에 저장
        insertIntoUsersMicroFertilization();
        insertIntoUsersMicroCalculatedMass();

    }

//    public void insertIntoRequestHistory() {
//        String query = "insert into requestHistory (user_id, request_date) " +
//                "values ("+users.getId()+", '"+request_date+"')";
//
//        try(Connection connection = conn.getConnection();
//            PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
//            int result = pstmt.executeUpdate();
//            if(result>0) {
//                System.out.println("insert success in requestHistory");
//                // 생성된 pk get
//                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
//                    if (generatedKeys.next()) {
//                        requestHistory_id = generatedKeys.getInt(1); // 생성된 ID
//                        System.out.println("Generated Request ID: " + requestHistory_id);
//                    } else {
//                        System.out.println("No ID was generated.");
//                    }
//                }
//
//            }
//            else System.out.println("insert fail in requestHistory");
//
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
//    }

    //TODO - insert test
    //미량원소 100배액식 저거 맞나 확인받기
    private void insertIntoUsersMicroCalculatedMass() {
        String unit = "'kg'";
        for (String micro : molecularMass.keySet()) {
            double concentration_100fold = molecularMass.get(micro).getMass()*100/1000;

            String query = "insert into users_micro_calculatedMass (user_id, users_micro_consideredValues_id, micro, mass, unit, solution, requestHistory_id) " +
                    "values ("+ users.getId() +", "+users_micro_consideredValues_id+", "+"'"+micro+"'"+", "
                    +concentration_100fold+", "+unit+", "+molecularMass.get(micro).getSolution()+", "+requestHistory_id+")";

            try(Connection connection = conn.getConnection();
                Statement stmt = connection.createStatement();){
                int result = stmt.executeUpdate(query);
                if(result>0) System.out.println("success");
                else System.out.println("insert failed");
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

    }
    private void insertIntoUsersMicroFertilization(){
        String query = "insert into users_micro_fertilization (user_id";
        for (String micro : userFertilization.keySet()) {
            query += ", "+micro;
        }
        query += ", requestHistory_id) "; //여기까지 query = insert into user_macro_fertilization (macro, NO3N, Ca, requestHistory_id)
        query += "values (" + users.getId();

        for (String micro : userFertilization.keySet()) {
            query += ", "+userFertilization.get(micro);
        }
        query += requestHistory_id + ")";
        try(Connection connection = conn.getConnection();
            Statement stmt = connection.createStatement();){
            //int result = stmt.executeUpdate(query);
            //if(result>0) System.out.println("success insert users_macro_fertilization");
            //else System.out.println("insert failed users_macro_fertilization");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

//    private void insertIntoUsersMicroFertilization() {
//        for (String micro : distributedValues.keySet()) {
//            String query = "insert into users_micro_fertilization (user_id, micro";
//            for (String element : distributedValues.get(micro).keySet()) {
//                query += ", "+element;
//            }
//            query += ") "; //여기까지 query = insert into user_macro_fertilization (macro, NO3N, Ca)
//            query += "values (" +users_id+", "+"'"+micro+"'";
//            for (String element : distributedValues.get(micro).keySet()) {
//                query += ", "+distributedValues.get(micro).get(element);
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

    //TODO - insert test
    private void insertIntoUsersMicroConsideredValues() { //고려 원수 값 DB 저장
        String query = "insert into users_micro_consideredValues ";
        String values = "(is_considered, Fe, Cu, " +
                "B, Mn, Zn, Mo, unit, user_id, requestHistory_id) values (";

        if(!isConsidered){
            query += "(is_considered, unit, user_id, requestHistory_id) values (false, '"+unit+"', "+users.getId()+", "+requestHistory_id+")";
        } else{
            values += "true";
            for (String value : consideredValues.keySet()) {
                values += ", "+consideredValues.get(value);
            }
            values += ", '"+unit+"', ";
            values += users.getId()+", ";
            values += requestHistory_id+")";
            query += values;
        }
        try (Connection connection = conn.getConnection();
             Statement stmt = connection.createStatement()) {
            int result = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            if (result > 0) {
                System.out.println("success insert users_micro_consideredValues");
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if(generatedKeys.next()){
                    int id = generatedKeys.getInt(1);
                    users_micro_consideredValues_id = id; //fk로 사용하기 위해 배정
                }
            } else {
                System.out.println("insert failed users_micro_consideredValues");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
