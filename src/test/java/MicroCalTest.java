import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.main.culturesolutioncalculation.service.calculator.FinalCal;

//import javax.swing.plaf.basic.BasicLookAndFeel;
import java.sql.*;
import java.util.*;

public class MicroCalTest {

    private final String url = "jdbc:mysql://localhost:3306/CultureSolutionCalculation?useSSL=false";
    private final String user = "root";
    private final String password = "root";

    private Connection connection;

    private Map<String, Map<String, Double>> compoundsRatio = new HashMap<>();

    private Map<String, FinalCal> molecularMass =  new LinkedHashMap<>();

    Map<String, Map<String, Double>> distributedValues = new LinkedHashMap<>(); //프론트에서 보여지는 자동 계산 결과



    private Map<String, Double> fertilization = new LinkedHashMap<String, Double>() {
        {
            put("Fe", 15.0);
            put("Cu", 0.75);
            put("B", 30.0);
            put("Mn",10.0);
            put("Zn", 5.0);
            put("Mo",0.5);
        }
    };

    @BeforeEach
    void connection() throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
    }

    @Test
        //@BeforeEach
    void getMajorCompoundRatio() {

        List<String> userMicroNutrients = new LinkedList<>(Arrays.asList("Fe-EDTA", "H3BO3", "MnSO4·H2O", "Na2MoO4·2H2O"));

        String query = "select * from micronutrients where micro in ('CuSO4·5H2O', 'ZnSO4·7H2O'";


        for (String micro : userMicroNutrients) {
            query += ", '"+micro+"'";
        }
        query += ");";

        //System.out.println("query = " + query);

        try(Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query)){

            while(resultSet.next()) {
                String micro = resultSet.getString("micro"); //질산칼슘4수염, 질산칼륨, 질산암모늄 등등
                String solution = resultSet.getString("solution");
                double mass = resultSet.getDouble("mass");
                int content_count = resultSet.getInt("content_count");

                molecularMass.put(micro, new FinalCal(solution, mass)); //100배액 계산을 위해 화합물과 그 질량, 양액 저장

                Map<String, Double> compoundRatio = new LinkedHashMap<>(); //ex. 질산칼슘4수염이 갖는 원수의 이름과 질량비를 갖는 map

                compoundRatio.put("content_count", content_count*1.0);
                for (String major : fertilization.keySet()) {
                    if (resultSet.getDouble(major) != 0) {
                        compoundRatio.put(major, resultSet.getDouble(major));

                        try (Statement innerStmt = connection.createStatement();
                             ResultSet set = innerStmt.executeQuery("select mass from micronutrients_mass where micro = '" + major + "'")) {
                            if (set.next()) {
                                double micro_mass = set.getDouble("mass");
                                compoundRatio.put("mass", micro_mass); // 원자량도 같이 저장해야 함
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

    @BeforeEach
    void setUp(){
        getMajorCompoundRatio();
        //calculateWithRatio();

    }

    @Test
    public void calculateWithRatio2() {
        Map<String, Double> results = fertilization;

        for (String compound : compoundsRatio.keySet()) {
            Map<String, Double> calculatedCompounds = calculateCompoundDistribution(compound, results);
            distributedValues.put(compound, calculatedCompounds); // 계산된 화합물 분포 저장
        }
        for (String name : distributedValues.keySet()) {
            System.out.println("name = " + name);
            System.out.println("distributedValues = " + distributedValues.get(name));
        }
        System.out.println("\n\n===========아래는 molecularMass=============");
        for (String name : molecularMass.keySet()) {
            System.out.println("name = " + name);
            System.out.println("molecularMass = " + molecularMass.get(name).getMass());
        }
    }
    // 계산된 화합물 분포를 반환하는 메서드
    private Map<String, Double> calculateCompoundDistribution(String compound, Map<String, Double> fertilizationRates) {
        Map<String, Double> distributionResult = new LinkedHashMap<>();
        double atomicWeight = compoundsRatio.get(compound).get("mass"); // 원자량
        double molecularWeight = molecularMass.get(compound).getMass(); // 분자량
        double contentCount = compoundsRatio.get(compound).get("content_count"); // 함량 개수

        for (String nutrient : fertilizationRates.keySet()) {
            if (compoundsRatio.get(compound).containsKey(nutrient)) {
                double fertilizationAmount = fertilizationRates.get(nutrient);
                double calculatedValue = calculateMicroElementValue(molecularWeight, fertilizationAmount, atomicWeight, contentCount);
                distributionResult.put(nutrient, fertilizationAmount);
                molecularMass.get(compound).setMass(calculatedValue); // 최종 값 업데이트
            }
        }

        return distributionResult;
    }

    // 미량 원소 값을 계산하는 메서드
    private double calculateMicroElementValue(double molecularWeight, double fertilizationAmount, double atomicWeight, double contentCount) {
        return molecularWeight * fertilizationAmount / atomicWeight / contentCount;
    }

    @Test
    void calculateWithRatio(){
        getMajorCompoundRatio();
        double ratioValue = Double.MAX_VALUE;
        Map<String, Double> results = fertilization; //나중에 userFertilization이 들어오면 바꿀것 (result = userFertilization으로)

        for (String compound : compoundsRatio.keySet()) { //ex; {ZnSO4·7H2O , {Zn=1.0, mass=65.37}}에서 ZnSO4·7H2O이 compound
            // 분자량*시비량/원자량/함량갯수 = 100배액
        /*
          name = ZnSO4쨌7H2O
          compoundsRatio = {content_count=1.0, Zn=1.0, mass=65.37}
         */
            Map<String, Double> result = new LinkedHashMap<>();
            double atomicWeight = compoundsRatio.get(compound).get("mass"); //원자량
            double molecularWeight =  molecularMass.get(compound).getMass(); //분자량
            double contentCount = compoundsRatio.get(compound).get("content_count"); //함량갯수
            double fertilizationAmount; //시비량
            double microValue =0.0;

            for (String micro : results.keySet()) { //처방농도 micro
                if(compoundsRatio.get(compound).containsKey(micro)){
                    fertilizationAmount = results.get(micro);
                    microValue = molecularWeight * fertilizationAmount / atomicWeight / contentCount;
                    result.put(micro, fertilizationAmount);
                }
            }
            distributedValues.put(compound, result); //해당 화합물의 원수 처방량
            molecularMass.get(compound).setMass(microValue);//최종 minValue * mass 한 값
        }

//            for (String name : distributedValues.keySet()) {
//                System.out.println("name = " + name);
//                System.out.println("distributedValues = " + distributedValues.get(name));
//            }
        for (String mass : molecularMass.keySet()) {
            System.out.println("mass = " + mass);
            System.out.println("molecularMass = " + molecularMass.get(mass).getMass());
        }

    }



//    public Map<String, Map<String, Double>> calculateDistributedValues(Map<String, Double> userFertilization){
//        double ratioValue = Double.MAX_VALUE;
//        Map<String, Double> results = fertilization; //나중에 userFertilization이 들어오면 바꿀것 (result = userFertilization으로)
//
//        for (String compound : compoundsRatio.keySet()) { //ex; {ZnSO4·7H2O , {Zn=1.0, mass=65.37}}에서 ZnSO4·7H2O이 compound
//            // 분자량*시비량/원자량/함량갯수 = 100배액
//            /*
//              name = ZnSO4쨌7H2O
//              compoundsRatio = {content_count=1.0, Zn=1.0, mass=65.37}
//             */
//            Map<String, Double> result = new LinkedHashMap<>();
//            double atomicWeight = compoundsRatio.get(compound).get("mass"); //원자량
//            double molecularWeight =  molecularMass.get(compound).getMass(); //분자량
//            double contentCount = compoundsRatio.get(compound).get("content_count"); //함량갯수
//            double fertilizationAmount; //시비량
//            double microValue = 0.0; //계산 결과
//
//            for (String micro : results.keySet()) { //처방농도 micro
//                if(compoundsRatio.get(compound).containsKey(micro)){
//                    fertilizationAmount = results.get(micro);
//                    microValue = molecularWeight * fertilizationAmount / atomicWeight / contentCount;
//                    result.put(micro, fertilizationAmount);
//                }
//            }
//            distributedValues.put(compound, result); //해당 화합물의 원수 처방량
//            molecularMass.get(compound).setMass(microValue);//최종 minValue * mass 한 값
//        }
//
//        return distributedValues;
//    }

    @Test
    void insertIntoUsersMicroCalculatedMass() {

        int user_id = 1;
        int users_micro_consideredValues_id = 1;

        String unit = "'kg'";
        for (String micro : molecularMass.keySet()) {
            double concentration_100fold = molecularMass.get(micro).getMass()*100/1000;

            String query = "insert into users_micro_calculatedMass (user_id, users_micro_consideredValues_id, micro, mass, unit, solution) " +
                    "values ("+user_id+", "+users_micro_consideredValues_id+", "+"'"+micro+"'"+", "+concentration_100fold+", "+unit+", '"+molecularMass.get(micro).getSolution()+"')";

            System.out.println("query = " + query);
            try(
                    Statement stmt = connection.createStatement();){
                int result = stmt.executeUpdate(query);

                //if(result>0) System.out.println("success");
                //else System.out.println("insert failed");
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

    }
    @Test
    void users_micro_fertilization삽입테스트(){
        int users_micro_consideredValues_id = 1;
        for (String micro : distributedValues.keySet()) {
            String query = "insert into users_micro_fertilization (users_micro_consideredValues_id, micro";
            for (String element : distributedValues.get(micro).keySet()) {
                query += ", "+element;
            }
            query += ") "; //여기까지 query = insert into user_macro_fertilization (macro, NO3N, Ca)
            query += "values (" +users_micro_consideredValues_id+", "+"'"+micro+"'";
            for (String element : distributedValues.get(micro).keySet()) {
                query += ", "+distributedValues.get(micro).get(element);
            }
            query += ")";
            System.out.println("query = " + query);
            try(//Connection connection = conn.getConnection();
                Statement stmt = connection.createStatement();){
                int result = stmt.executeUpdate(query);

                //if(result>0) System.out.println("success insert users_macro_fertilization");
                //else System.out.println("insert failed users_macro_fertilization");
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    @Test
    void users_micro_consideredValues삽입테스트(){
        boolean isConsidered = true;
        Map<String, Double> standardValues = new LinkedHashMap<>();

        String query = "insert into users_micro_consideredValues ";

        String user_id = "1";
        String unit = "'ppm'";

        String values = "(is_considered, Fe, Cu, " +
                "B, Mn, Zn, Mo, unit, user_id) values (";

        if(!isConsidered){
            query += "(is_considered, unit, user_id) values (false, "+unit+", "+user_id+")";
        }else{
            //원수 고려값 map 돌면서 채워넣어야 함
            values += "true, 12.243, 24.222, 3.5463, 4, 5, 6, "+unit+", "+user_id+")";
            query += values;
            System.out.println("query = " + query);
        }

        try (Statement stmt = connection.createStatement()) {
            int result = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            if (result > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if(generatedKeys.next()){
                    int id = generatedKeys.getInt(1);
                    System.out.println("id = " + id);
                }
            } else {
                System.out.println("insert failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
