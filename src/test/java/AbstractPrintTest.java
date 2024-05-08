//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.PageSize;
//import com.itextpdf.text.pdf.PdfWriter;
//import com.itextpdf.tool.xml.XMLWorker;
//import com.itextpdf.tool.xml.XMLWorkerFontProvider;
//import com.itextpdf.tool.xml.XMLWorkerHelper;
//import com.itextpdf.tool.xml.css.CssFile;
//import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
//import com.itextpdf.tool.xml.html.CssAppliers;
//import com.itextpdf.tool.xml.html.CssAppliersImpl;
//import com.itextpdf.tool.xml.html.Tags;
//import com.itextpdf.tool.xml.parser.XMLParser;
//import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
//import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
//import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
//import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
//import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.main.culturesolutioncalculation.service.users.Users;
//import org.main.culturesolutioncalculation.model.CropNutrientStandard;
//import org.main.culturesolutioncalculation.model.NutrientSolution;
//import org.main.culturesolutioncalculation.service.CSVDataReader;
//import org.main.culturesolutioncalculation.service.calculator.FinalCal;
//
//import java.io.*;
//import java.nio.charset.Charset;
//import java.sql.*;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//public class AbstractPrintTest {
//
//    private final String url = "jdbc:mysql://localhost:3306/CultureSolutionCalculation?useSSL=false";
//    private final String user = "root";
//    private final String password = "root";
//    private Users users;
//
//    private Connection connection;
//
//    private Map<String, FinalCal> MacroMolecularMass = new LinkedHashMap<>();
//    private Map<String, FinalCal> MicroMolecularMass = new LinkedHashMap<>();
//    private Map<String, Map<String, Double>> MacroCompoundsRatio = new LinkedHashMap<>(); // ex; {NH4NO3 , {NH4N=1.0, NO3N=1.0}}
//    private Map<String, Map<String, Double>> MicroCompoundsRatio = new LinkedHashMap<>(); // ex; {NH4NO3 , {NH4N=1.0, NO3N=1.0}}
//
//    private List<String> macroNutrientList = Arrays.asList(
//            "Ca","NO3N","NH4N","K","H2PO4","SO4S","Mg"
//    );
//    private List<String> microNutrientList = Arrays.asList(
//            "Fe","Cu","B","Mn","Zn","Mo"
//    );
//
//
//    //@BeforeEach
//    void connection() throws SQLException {
//        this.connection = DriverManager.getConnection(url, user, password);
//
//    }
//
//    @BeforeEach
//    public void setUp() throws SQLException {
//        connection();
//        setUsers();
////        setMacroMolecularMass();
////        setMicroMolecularMass();
//    }
//    private Users uesrs;
//
//    @Test
//    //@BeforeEach
//    public void setUsers(){
//        String query = "select * from users where id = 1";
//        try(Statement stmt = connection.createStatement();
//            ResultSet resultSet = stmt.executeQuery(query);
//        ){
//          while(resultSet.next()){
//              int userId = resultSet.getInt("id");
//              String userName = resultSet.getString("name");
//              String mediumType = resultSet.getString("medium_type");
//              String cropName = resultSet.getString("crop_name");
//              String address = resultSet.getString("address");
//              String contact = resultSet.getString("contact");
//              String cultivationScale = resultSet.getString("cultivation_scale");
//
//
//              users = new Users(userId, userName,mediumType,address, contact, cropName, cultivationScale );
//
//              System.out.println("users.getName() = " + users.getName());
//              System.out.println("mediumType = " + mediumType);
//
//          }
//
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
//
//    }
//
//    @Test
//    public void setMacroFertilization(){
//
//        String query = "select um.* from users_macro_fertilization um " +
//                "join users u on u.id = um.users_id " +
//                "where u.request_date = ? and u.id = ?";
//
//        try(
//            PreparedStatement pstmt = connection.prepareStatement(query)){
//
//            pstmt.setString(1, users.getRequestDate().toString());
//            pstmt.setInt(2, users.getId());
//
//            try(ResultSet resultSet = pstmt.executeQuery()){
//                while(resultSet.next()){
//                    String macro = resultSet.getString("macro");
//                    Map<String, Double> compoundRatio = new HashMap<>();
//                    for (String nutrient : macroNutrientList) {
//                        compoundRatio.put(nutrient, resultSet.getDouble(nutrient));
//                    }
//                    MacroCompoundsRatio.put(macro, compoundRatio);
//                }
//            }
//
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
//
//        for (String macro : MacroCompoundsRatio.keySet()) {
//            System.out.println("macro = " + macro);
//            System.out.println("MacroCompoundsRatio = " + MacroCompoundsRatio.get(macro));
//        }
//    }
//
//    @Test
//    public void setMicroFertilization(){
//        String query = "select um.* from users_micro_fertilization um " +
//                "join users u on u.id = um.users_id " +
//                "where u.request_date = ? and u.id = ?";
//
//        try(
//            PreparedStatement pstmt = connection.prepareStatement(query)){
//
//            pstmt.setString(1, users.getRequestDate().toString());
//            pstmt.setInt(2, users.getId());
//
//            try(ResultSet resultSet = pstmt.executeQuery()){
//                while(resultSet.next()){
//                    String micro = resultSet.getString("micro");
//                    Map<String, Double> compoundRatio = new LinkedHashMap<>();
//                    for (String nutrient : microNutrientList) {
//                        compoundRatio.put(nutrient, resultSet.getDouble(nutrient));
//                    }
//                    MicroCompoundsRatio.put(micro, compoundRatio);
//                }
//            }
//
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
//
//        for (String micro : MicroCompoundsRatio.keySet()) {
//            System.out.println("micro = " + micro);
//            System.out.println("MicroCompoundsRatio = " + MicroCompoundsRatio.get(micro));
//        }
//
//    }
//
//
//    //데이터베이스에서 꺼내와야함
//    @Test
//    public void setMacroMolecularMass() {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime requestDate = LocalDateTime.parse("2024-03-25 16:48:44", formatter);
//
//        // DateTimeFormatter를 사용하여 LocalDateTime을 적절한 문자열로 변환
//        String formattedDate = requestDate.format(formatter);
//
//        String query = "SELECT um.* FROM users_macro_calculatedMass um " +
//                "JOIN users u ON um.user_id = u.id " +
//                "WHERE u.id = ? AND u.request_date = ?";
//
//        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
//
//            // 파라미터 바인딩
//            pstmt.setInt(1, users.getId());
//            pstmt.setString(2, formattedDate);
//
//            try (ResultSet resultSet = pstmt.executeQuery()) {
//                System.out.println("쿼리 실행");
//                // 결과 처리
//                while (resultSet.next()) {
//                    String macro = resultSet.getString("macro"); //질산칼슘4수염, 질산칼륨, 질산암모늄 등등
//                    String solution = resultSet.getString("solution"); //양액 타입 (A,B, C)
//                    double mass = resultSet.getDouble("mass");//화합물 질량
//
//                    MacroMolecularMass.put(macro, new FinalCal(solution, mass)); //100배액 계산을 위해 화합물과 그 질량 저장
//                }
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        for (String name : MacroMolecularMass.keySet()) {
//            System.out.println("name = " + name);
//            System.out.println("MacroMolecularMass = " + MacroMolecularMass.get(name).getMass());
//            System.out.println("MacroMolecularMass = " + MacroMolecularMass.get(name).getSolution());
//        }
//
//    }
//
//    @Test
//    public void setMicroMolecularMass() {
//
//        int userId = 1;
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime requestDate = LocalDateTime.parse("2024-03-25 16:48:44", formatter);
//
//        // DateTimeFormatter를 사용하여 LocalDateTime을 적절한 문자열로 변환
//        String formattedDate = requestDate.format(formatter);
//
//        String query = "SELECT um.* FROM users_micro_calculatedMass um " +
//                "JOIN users u ON um.user_id = u.id " +
//                "WHERE u.id = ? AND u.request_date = ?";
//
//        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
//
//            // 파라미터 바인딩
//            pstmt.setInt(1, users.getId());
//            pstmt.setString(2, users.getRequestDate().toString());
//
//            try (ResultSet resultSet = pstmt.executeQuery()) {
//                while (resultSet.next()) {
//                    String micro = resultSet.getString("micro"); //질산칼슘4수염, 질산칼륨, 질산암모늄 등등
//                    String solution = resultSet.getString("solution"); //양액 타입 (A,B, C)
//                    double mass = resultSet.getDouble("mass");//화합물 질량
//
//                    MicroMolecularMass.put(micro, new FinalCal(solution, mass)); //100배액 계산을 위해 화합물과 그 질량 저장
//                }
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private String getUserInfo() {
//
//        return
//                "<p>의뢰자 성명: "+users.getName()+"</p>" +
//                        "<p>의뢰 일시: "+users.getRequestDate()+"</p>" +
//                        "<p>재배 작물: "+users.getCropName()+"</p>" +
//                        "<p>배양액 종류: "+users.getMediumType()+"</p>" +
//                        "<br></br><br></br><br></br> ";
//    }
//
//    @Test
//    public void getPDF(){
//
//        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
//        try{
//            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("test2.pdf"));
//            writer.setInitialLeading(12.5f);
//
//            document.open();
//            XMLWorkerHelper helper = XMLWorkerHelper.getInstance();
//
//            CSSResolver cssResolver = new StyleAttrCSSResolver();
//            CssFile cssFile = null;
//            try{
//                cssFile = helper.getCSS(new FileInputStream("pdf.css"));
//            }catch (FileNotFoundException e){
//                e.printStackTrace();
//            }
//            cssResolver.addCss(cssFile);
//
//            //HTML과 폰트 준비
//            XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
//            fontProvider.register("css/MALGUN.ttf","MalgunGothic");
//            CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);
//
//            HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
//            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
//
//            //Pipelines
//            PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
//            HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
//            CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);
//
//            XMLWorker worker = new XMLWorker(css, true);
//            XMLParser xmlParser = new XMLParser(worker, Charset.forName("UTF-8"));
//
//            String htmlStr = getAllHtmlStr();
//
//            StringReader stringReader = new StringReader(htmlStr);
//            xmlParser.parse(stringReader);
//            document.close();
//            writer.close();
//
//        }catch (DocumentException e){
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private String getAllHtmlStr() {
//        String htmlStr = "<html><head></head><body style='font-family: MalgunGothic;'> " +
//                "<h1>배양액 분석 기록 보고서</h1><br></br><hr> </hr>";
//
//
//        htmlStr += getUserInfo();
//
//        htmlStr += "<table style='width: 100%; margin-bottom: 20px;'>";
//        htmlStr += getSolution("A");
//        htmlStr += getSolution("B");
//        htmlStr += getSolution("C");
//
//        htmlStr += "</table></body></html>";
//
//        System.out.println("htmlStr = " + htmlStr);
//
//        return htmlStr;
//    }
//
//    private String getSolution(String solution) {
//        String unit = "Kg";
//        String Html =
//                        "<tr>" +
//                        "<th class=\"category\">"+solution+"액</th>" +
//                        "<th colspan=\"2\">100배액 기준</th>" +
//                        "</tr>";
//
//        for (String macro : MacroMolecularMass.keySet()) {
//            if(MacroMolecularMass.get(macro).getSolution().equals(solution)){
//                Html += "<tr>" +
//                        "<td class=\"name\">"+macro+"</td>" +
//                        "<td>"+String.format("%.2f",MacroMolecularMass.get(macro).getMass())+"</td>" +
//                        "<td class=\"unit\">"+unit+"</td>" +
//                        "</tr>";
//            }
//        }
//        for (String micro : MicroMolecularMass.keySet()) {
//            if(MicroMolecularMass.get(micro).getSolution().equals(solution)){
//                Html += "<tr>" +
//                        "<td class=\"name\">"+micro+"</td>" +
//                        "<td>"+String.format("%.2f",MicroMolecularMass.get(micro).getMass())+"</td>" +
//                        "<td class=\"unit\">"+unit+"</td>" +
//                        "</tr>";
//            }
//        }
//
//        return Html;
//
//    }
//
//    @Test
//    public void 다량원소기준값불러오기테스트(){
//        users.setMediumType("네덜란드 배양액");
//        users.setCropName("딸기(순)");
//        System.out.println("users.getMediumType() = " + users.getMediumType());
//        String macro = getMacro(getCropNutrients());
//        System.out.println("macro = " + macro);
//    }
//
//    private CropNutrientStandard getCropNutrients (){ //해당 배양액 종류에 해당하는 재배 작물의 원수 기준량 추출
//
//        CSVDataReader csvDataReader = new CSVDataReader();
//        //NutrientSolution nutrientSolution = csvDataReader.readFile(users.getMediumType()); //네덜란드, 야마자키 등
//        NutrientSolution nutrientSolution = csvDataReader.readFile("네덜란드 배양액"); //네덜란드, 야마자키 등
//        ArrayList<CropNutrientStandard> cropList = nutrientSolution.getCropList();
//        Optional<CropNutrientStandard> cropNutrients = cropList.stream().filter(c -> c.getCropName().equals(users.getCropName()))
//                .findFirst();
//
//        return cropNutrients.get();
//    }
//
//    private String getMicro(CropNutrientStandard cropNutrientStandard) {
//        String Html =
//                "<th class=\"category\">미량원소</th>" +
//                        "<th colspan=\"2\">Fe</th>" +
//                        "<th colspan=\"2\">Cu</th>" +
//                        "<th colspan=\"2\">B</th>" +
//                        "<th colspan=\"2\">Mn</th>" +
//                        "<th colspan=\"2\">Zn</th>" +
//                        "<th colspan=\"2\">Mo</th>" +
//                        "</tr>";
//
//        //미량원소 기준량
//        Html += "<td class=\"name\">기준량</td>" +
//                "<td class=\"value\">"+cropNutrientStandard.getFe() +"</td>"+
//                "<td class=\"value\">"+cropNutrientStandard.getCu() +"</td>"+
//                "<td class=\"value\">"+cropNutrientStandard.getB() +"</td>"+
//                "<td class=\"value\">"+cropNutrientStandard.getMn() +"</td>"+
//                "<td class=\"value\">"+cropNutrientStandard.getZn() +"</td>"+
//                "<td class=\"value\">"+cropNutrientStandard.getMo()+"</td>"+
//                "</tr>";
//        //고려원수값
//
//        //처방농도
//
//
//        return Html;
//    }
//
//    private String getMacro(CropNutrientStandard cropNutrientStandard){
//
//        String Html =
//                "<th class=\"category\">다량원소</th>" +
//                        "<th colspan=\"2\">Ca</th>" +
//                        "<th colspan=\"2\">NO3N</th>" +
//                        "<th colspan=\"2\">NH4N</th>" +
//                        "<th colspan=\"2\">K</th>" +
//                        "<th colspan=\"2\">H2PO4</th>" +
//                        "<th colspan=\"2\">SO4</th>" +
//                        "<th colspan=\"2\">Mg</th>" +
//                        "</tr>";
//
//        //다량원소 기준량
//        Html += "<td class=\"name\">기준량</td>" +
//                "<td class=\"value\">"+cropNutrientStandard.getCa() +"</td>"+
//                "<td class=\"value\">"+cropNutrientStandard.getNO3() +"</td>"+
//                "<td class=\"value\">"+cropNutrientStandard.getNH4() +"</td>"+
//                "<td class=\"value\">"+cropNutrientStandard.getK() +"</td>"+
//                "<td class=\"value\">"+cropNutrientStandard.getH2PO4() +"</td>"+
//                "<td class=\"value\">"+cropNutrientStandard.getSO4()+"</td>"+
//                "<td class=\"value\">"+cropNutrientStandard.getMg()+"</td>"+
//                "</tr>";
//
//        //고려원수값
//
//        //처방농도
//
//        return Html;
//    }
//
//}
