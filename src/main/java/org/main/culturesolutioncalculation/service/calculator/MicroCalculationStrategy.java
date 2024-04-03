package org.main.culturesolutioncalculation.service.calculator;

import org.main.culturesolutioncalculation.service.database.DatabaseConnector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MicroCalculationStrategy implements CalculationStrategy{
    private DatabaseConnector conn;
    private int users_id;
    private int getUsers_id(){
        return this.users_id;
    }

    private boolean isConsidered;
    private String unit;
    private int users_micro_consideredValues_id;
    private Map<String, Map<String, Double>> compoundsRatio = new LinkedHashMap<>(); // ex; {NH4NO3 , {NH4N=1.0, NO3N=1.0}}

    Map<String, Map<String, Double>> distributedValues = new LinkedHashMap<>(); //����Ʈ���� �������� �ڵ� ��� ���

    private Map<String, FinalCal> molecularMass =  new LinkedHashMap<>();

    List<String> userMicroNutrients = new LinkedList<>(); //������ �����ߴ� �̷����� ��� ����Ʈ

    //1. ���ذ� - ����Ʈ���� �Ѿ��
    //private Map<String, Double> standardValues = new LinkedHashMap<>();
    //2. ���� ����� - ����Ʈ���� �Ѿ��
    private Map<String, Double> consideredValues = new LinkedHashMap<>();

    //�Ѿ�;� �� ó�� �� ��� - ���� �״�� �����Ǿ�� ��. front���� �Ѿ�;���
    private Map<String, Double> userFertilization = new LinkedHashMap<>(); //db�� ����Ǿ�� �� ó�� �� (��� ���� X)
    private Map<String, Double> calFertilization = new LinkedHashMap<>(); //��� ������ ó�� ��
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

    public MicroCalculationStrategy(boolean isConsidered, String unit, List<String> userMicroNutrients, Map<String, Double> consideredValues, Map<String, Double> userFertilization){
        this.unit = unit;
        this.isConsidered = isConsidered;
        this.userMicroNutrients = userMicroNutrients;
        this.consideredValues = consideredValues;
        this.userFertilization = userFertilization;
        this.calFertilization = userFertilization;
        getMajorCompoundRatio(userMicroNutrients);
    }

    //���� �� ���� �ִ� �̷� ���� ������ ������. userMicroNutrients : ����ڰ� ������ �̷����� ����Ʈ
    private void getMajorCompoundRatio(List<String> userMicroNutrients){

        String query = "select * from micronutrients where micro in ('CuSO4��5H2O', 'ZnSO4��7H2O'"; //Ȳ�� ����, Ȳ�� �ƿ� ȭ�չ��� ������ ����

        for (String micro : userMicroNutrients) {
            query += ", '"+micro+"'";
        }
        query += ");";

        try (Connection connection = conn.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(query);) {

            while(resultSet.next()){
                String micro = resultSet.getString("micro"); //����Į��4����, ����Į��, ����ϸ� ���
                String solution = resultSet.getString("solution");
                double mass = resultSet.getDouble("mass");

                molecularMass.put(micro, new FinalCal(solution, mass)); //100��� ����� ���� ȭ�չ��� �� ����, ��� ����

                Map<String, Double> compoundRatio = new LinkedHashMap<>(); //ex. ����Į��4������ ���� ������ �̸��� ������ ���� map
                for (String major : userFertilization.keySet()) {
                    if (resultSet.getDouble(major) != 0) {
                        compoundRatio.put(major, resultSet.getDouble(major));
                        try (Statement innerStmt = connection.createStatement();
                             ResultSet set = innerStmt.executeQuery("select mass from micronutrients_mass where micro = '" + major + "'")) {
                            if (set.next()) {
                                double micro_mass = set.getDouble("mass");
                                int content_count = set.getInt("content_count");
                                compoundRatio.put("mass", micro_mass); // ���ڷ��� ���� �����ؾ� ��
                                compoundRatio.put("content_count", content_count*1.0);
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

    // ���ڷ�*�ú�/���ڷ�/�Է����� = 100���
    /*
      name = ZnSO4·7H2O
      compoundsRatio = {content_count=1.0, Zn=1.0, mass=65.37}
     */
    public Map<String, Map<String, Double>> calculateDistributedValues() {
        Map<String, Double> results = userFertilization;

        for (String compound : compoundsRatio.keySet()) {
            Map<String, Double> calculatedCompounds = calculateCompoundDistribution(compound, calFertilization);
            distributedValues.put(compound, calculatedCompounds); // ���� ȭ�չ� ���� ����
        }

        return distributedValues;
    }
    // ���� ȭ�չ� ������ ��ȯ
    private Map<String, Double> calculateCompoundDistribution(String compound, Map<String, Double> calFertilization) {
        Map<String, Double> distributionResult = new LinkedHashMap<>();
        double atomicWeight = compoundsRatio.get(compound).get("mass"); // ���ڷ�
        double molecularWeight = molecularMass.get(compound).getMass(); // ���ڷ�
        double contentCount = compoundsRatio.get(compound).get("content_count"); // �Է� ����

        for (String nutrient : calFertilization.keySet()) {
            if (compoundsRatio.get(compound).containsKey(nutrient)) {
                double fertilizationAmount = calFertilization.get(nutrient);
                double calculatedValue = calculateMicroElementValue(molecularWeight, fertilizationAmount, atomicWeight, contentCount);
                distributionResult.put(nutrient, fertilizationAmount);
                molecularMass.get(compound).setMass(calculatedValue); // ���� �� ������Ʈ
            }
        }

        return distributionResult;
    }

    // �̷� ���� ���� ���
    private double calculateMicroElementValue(double molecularWeight, double fertilizationAmount, double atomicWeight, double contentCount) {
        return molecularWeight * fertilizationAmount / atomicWeight / contentCount;
    }

    //���� ��� ����, ó�� ��, ��� ����, ���ذ� -> db�� �����ϴ� �Լ�
    public void save(){
        insertIntoUsersMicroConsideredValues(); //���� ��� �� ���̺� ����
        insertIntoUsersMicroFertilization();
        insertIntoUsersMicroCalculatedMass();

    }

    //�̷����� 100��׽� ���� �³� Ȯ�ιޱ�
    private void insertIntoUsersMicroCalculatedMass() {
        String unit = "'kg'";
        for (String micro : molecularMass.keySet()) {
            double concentration_100fold = molecularMass.get(micro).getMass()*100/1000;

            String query = "insert into users_micro_calculatedMass (user_id, users_micro_consideredValues_id, micro, mass, unit, solution) " +
                    "values ("+ users_id +", "+users_micro_consideredValues_id+", "+"'"+micro+"'"+", "+concentration_100fold+", "+unit+", '"+molecularMass.get(micro).getSolution()+"')";

            System.out.println("query = " + query);
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
    private void insertIntoUsersMicroFertilization(){
        String query = "insert into users_micro_fertilization (users_id";
        for (String micro : userFertilization.keySet()) {
            query += ", "+micro;
        }
        query += ") "; //������� query = insert into user_macro_fertilization (macro, NO3N, Ca)
        query += "values (" + users_id;

        for (String micro : userFertilization.keySet()) {
            query += ", "+userFertilization.get(micro);
        }
        query += ")";
        try(Connection connection = conn.getConnection();
            Statement stmt = connection.createStatement();){
            int result = stmt.executeUpdate(query);

            //if(result>0) System.out.println("success insert users_macro_fertilization");
            //else System.out.println("insert failed users_macro_fertilization");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

//    private void insertIntoUsersMicroFertilization() {
//        for (String micro : distributedValues.keySet()) {
//            String query = "insert into users_micro_fertilization (users_id, micro";
//            for (String element : distributedValues.get(micro).keySet()) {
//                query += ", "+element;
//            }
//            query += ") "; //������� query = insert into user_macro_fertilization (macro, NO3N, Ca)
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

    private void insertIntoUsersMicroConsideredValues() { //��� ���� �� DB ����
        String query = "insert into users_micro_consideredValues ";
        String user_id = getUsers_id()+"";
        String values = "(is_considered, Fe, Cu, " +
                "B, Mn, Zn, Mo, unit, user_id) values (";

        if(!isConsidered){
            query += "(is_considered, unit, user_id) values (false, "+unit+", "+user_id+")";
        } else{
            values += "true";
            for (String value : consideredValues.keySet()) {
                values += ", "+consideredValues.get(value);
            }
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
                    users_micro_consideredValues_id = id; //fk�� ����ϱ� ���� ����
                }
            } else {
                System.out.println("insert failed users_micro_consideredValues");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
