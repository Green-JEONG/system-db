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

    Map<String, Map<String, Double>> distributedValues = new LinkedHashMap<>(); //����Ʈ���� �������� �ڵ� ��� ���

    private Map<String, FinalCal> molecularMass =  new LinkedHashMap<>();

    //1. ���ذ� - ����Ʈ���� �Ѿ��
    //private Map<String, Double> standardValues = new LinkedHashMap<>();
    //2. ���� ����� - ����Ʈ���� �Ѿ��
    private Map<String, Double> consideredValues = new LinkedHashMap<>();

    //3. ó�� ��. �Ѿ�;� �� ó�� �� ��� ���� - ���� �״�� �����Ǿ�� �� (���ذ� - ���������)
    private Map<String, Double> userFertilization = new LinkedHashMap<>(); //db�� ����Ǿ�� �� ó�� �� (��� ���� X)
    private Map<String, Double> calFertilization = new LinkedHashMap<>(); //��� ������ ó�� ��
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


    //���� �� ���� �ִ� �ٷ� ���� ������ ������
    private void getMajorCompoundRatio(boolean is4){ //4�������� 10���������� �Ǵ��ϴ� �Ķ����

        String query = "select * from macronutrients";
        query += is4? " where id != 2" : " where id != 1"; //id=1 : ����Į��4����, id=2 : ����Į��10����

        try (Connection connection = conn.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(query);) {

            while(resultSet.next()){
                String macro = resultSet.getString("macro"); //����Į��4����, ����Į��, ����ϸ� ���
                String solution = resultSet.getString("solution"); //��� Ÿ�� (A,B, C)
                double mass = resultSet.getDouble("mass");//ȭ�չ� ����

                molecularMass.put(macro, new FinalCal(solution, mass)); //100��� ����� ���� ȭ�չ��� �� ���� ����

                Map<String, Double> compoundRatio = new LinkedHashMap<>(); //ex. ����Į��4������ ���� ������ �̸��� ������ ���� map
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
    //�ڵ� ��� �� ����Ʈ�� �������� �й�� ��
    public Map<String, Map<String, Double>> calculateDistributedValues() {

        for (String compound : compoundsRatio.keySet()) {
            distributedValues.put(compound, calculateCompoundDistribution(compound, calFertilization));
        }

        return distributedValues;
    }
    //ȭ�չ��� ���Ե� ���� ������ ���� �л� ���
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
    //ȭ�չ��� ���Ե� ������ ���� �ּ� ���� �� ���
    private double calculateMinimumRatioForCompound(String compound, Map<String, Double> calFertilization) {
        double minRatioValue = Double.MAX_VALUE;

        for (String nutrient : compoundsRatio.get(compound).keySet()) { //�ش� ȭ�չ��� ����
            double availableAmount = userFertilization.get(nutrient); //�ش� ������ ó�� ��
            double ratio = compoundsRatio.get(compound).get(nutrient); //�ش� ������ ȭ�չ� ÷�� ����
            double amountBasedOnRatio = availableAmount / ratio;
            minRatioValue = Math.min(minRatioValue, amountBasedOnRatio);
        }

        return minRatioValue;
    }
    //�ּ� ���� ���� ����� ���� ��緮 ���
    private double calculateNutrientAllocation(String nutrient, String compound, double minRatioValue) {
        double ratio = compoundsRatio.get(compound).get(nutrient);
        return ratio * minRatioValue;
    }
    //���� ���� �ּ� ���� ���� ���� ȭ�չ� ���� ������Ʈ
    private void updateMolecularMass(String compound, double minRatioValue) {
        double mass = molecularMass.get(compound).getMass();
        molecularMass.get(compound).setMass(minRatioValue * mass);
    }

    //���� ��� ����, ó�� ��, ��� ����, ���ذ� -> db�� �����ϴ� �Լ�
    public void save(){
        insertIntoRequestHistory();
        insertIntoUsersMacroConsideredValues(); //���� ��� �� ���̺� ����
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
                // ������ pk get
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        requestHistory_id = generatedKeys.getLong(1); // ������ ID
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

    //TODO - insert �׽�Ʈ
    private void insertIntoUsersMacroFertilization(){
        String query = "insert into users_macro_fertilization (users_id";
        for (String macro : userFertilization.keySet()) {
            query += ", "+macro;
        }
        query += ", requestHistory_id) "; //������� query = insert into user_macro_fertilization (macro, NO3N, Ca, requestHistory_id)
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

//    private void insertIntoUsersMacroFertilization() { //���� ó�氪 DB ����
//        for (String macro : distributedValues.keySet()) {
//            String query = "insert into users_macro_fertilization (users_id, macro";
//            for (String element : distributedValues.get(macro).keySet()) {
//                query += ", "+element;
//            }
//            query += ") "; //������� query = insert into user_macro_fertilization (macro, NO3N, Ca)
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

    //TODO - insert ����� �Ǵ� �� Ȯ��
    //100���(kg) ���� ���� �³� Ȯ�ιޱ�
    private void insertIntoUsersMacroCalculatedMass() { //���� ���� �� DB ����
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

    //TODO �׽�Ʈ
    private void insertIntoUsersMacroConsideredValues() { //��� ���� �� DB ����
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
                    users_macro_consideredValues_id = id; //fk�� ����ϱ� ���� ����
                }
            } else {
                System.out.println("insert failed users_macro_consideredValues");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
