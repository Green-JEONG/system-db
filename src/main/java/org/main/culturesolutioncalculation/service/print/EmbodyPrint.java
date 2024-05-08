package org.main.culturesolutioncalculation.service.print;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import org.main.culturesolutioncalculation.service.database.MediumService;
import org.main.culturesolutioncalculation.service.requestHistory.RequestHistory;
import org.main.culturesolutioncalculation.service.requestHistory.RequestHistoryService;
import org.main.culturesolutioncalculation.service.users.Users;
import org.main.culturesolutioncalculation.model.CropNutrientStandard;
import org.main.culturesolutioncalculation.model.NutrientSolution;
import org.main.culturesolutioncalculation.service.CSVDataReader;
import org.main.culturesolutioncalculation.service.calculator.FinalCal;
import org.main.culturesolutioncalculation.service.database.DatabaseConnector;
import org.w3c.dom.*;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;

public class EmbodyPrint implements Print{

    private DatabaseConnector conn;
    private CSVDataReader csvDataReader;
    private RequestHistory requestHistory;
    private RequestHistoryService requestHistoryService;
    private MediumService mediumService;
    private String pdfName;
    private String mediumType;

    private CropNutrientStandard cropNutrients;
    private Map<String, FinalCal> MacroMolecularMass = new LinkedHashMap<>();
    private Map<String, FinalCal> MicroMolecularMass = new LinkedHashMap<>();

    private List<String> macroNutrientList = Arrays.asList(
            "Ca","NO3N","NH4N","K","H2PO4","SO4S","Mg"
    );
    private List<String> microNutrientList = Arrays.asList(
            "Fe","Cu","B","Mn","Zn","Mo"
    );

    private Map<String,  Double> MacroConsideredValues = new LinkedHashMap<>();
    private Map<String,  Double> MicroConsideredValues = new LinkedHashMap<>();


    private Map<String, Double> MacroFertilization = new LinkedHashMap<>(); // ex; {NH4NO3 , {NH4N=1.0, NO3N=1.0}}
    private Map<String, Double> MicroFertilization = new LinkedHashMap<>(); // ex; {NH4NO3 , {NH4N=1.0, NO3N=1.0}}
    /*
    분석 기록에 들어가야 할 정보들 :
    사용자 이름, 분석 날짜, 재배 작물, 배양액 종류(네덜란드, 야마자키:이건 프론트에서 받아오기로)
     */
    private Users users;

    public EmbodyPrint(Users users, RequestHistory requestHistory){
        this.users = users;
        this.requestHistory = requestHistory;
    }
    public void setRequestHistory(){
        mediumType = requestHistoryService.getMediumType(requestHistory);

        cropNutrients = mediumService.getCropData(requestHistory.getCulture_medium_id()).get();
    }


    @Override
    public void setMacroMolecularMass() {
        String query = "SELECT um.* FROM users_macro_calculatedMass um " +
                "WHERE um.requestHistory_id = ?";

        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // 파라미터 바인딩
            pstmt.setInt(1, requestHistory.getId());

            try (ResultSet resultSet = pstmt.executeQuery()) {
                // 결과 처리
                while (resultSet.next()) {
                    String macro = resultSet.getString("macro"); //질산칼슘4수염, 질산칼륨, 질산암모늄 등등
                    String solution = resultSet.getString("solution"); //양액 타입 (A,B, C)
                    double mass = resultSet.getDouble("mass");//화합물 질량

                    MacroMolecularMass.put(macro, new FinalCal(solution, mass)); //100배액 계산을 위해 화합물과 그 질량 저장
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setMicroMolecularMass() {
        String query = "SELECT um.* FROM users_micro_calculatedMass um " +
                "WHERE um.requestHistory_id = ?";

        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // 파라미터 바인딩
            pstmt.setInt(1, requestHistory.getId());

            try (ResultSet resultSet = pstmt.executeQuery()) {
                // 결과 처리
                while (resultSet.next()) {
                    String micro = resultSet.getString("micro"); //질산칼슘4수염, 질산칼륨, 질산암모늄 등등
                    String solution = resultSet.getString("solution"); //양액 타입 (A,B, C)
                    double mass = resultSet.getDouble("mass");//화합물 질량

                    MicroMolecularMass.put(micro, new FinalCal(solution, mass)); //100배액 계산을 위해 화합물과 그 질량 저장
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void setMacroFertilization(){
        String query = "select um.* from users_macro_fertilization um " +
                "where um.requestHistory_id = ?";

        try(Connection connection = conn.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query)){

            pstmt.setInt(1, requestHistory.getId());

            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    for (String nutrient : macroNutrientList) {
                        MacroFertilization.put(nutrient, resultSet.getDouble(nutrient));
                    }
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void setMicroFertilization(){
        String query = "select um.* from users_micro_fertilization um " +
                "where um.requestHistory_id = ?";

        try(Connection connection = conn.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query)){

            pstmt.setInt(1, requestHistory.getId());

            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    for (String nutrient : microNutrientList) {
                        MicroFertilization.put(nutrient, resultSet.getDouble(nutrient));
                    }
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    //다량원수 고려값 db에서 불러오기
    public void setMacroConsideredValue(){
        String query = "SELECT um.* FROM users_macro_consideredValues um " +
                "WHERE um.requestHistory_id = ?";
        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // 파라미터 바인딩
            pstmt.setInt(1, requestHistory.getId());

            try (ResultSet resultSet = pstmt.executeQuery()) {
                // 결과 처리
                while (resultSet.next()) {
                    for (String nutrient : macroNutrientList) {
                        MacroConsideredValues.put(nutrient,resultSet.getDouble(nutrient));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //미량원수 고려값 db에서 불러오기
    public void setMicroConsideredValue(){
        String query = "SELECT um.* FROM users_micro_consideredValues um " +
                "WHERE um.requestHistory_id = ?";
        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // 파라미터 바인딩
            pstmt.setInt(1, requestHistory.getId());

            try (ResultSet resultSet = pstmt.executeQuery()) {
                // 결과 처리
                while (resultSet.next()) {
                    for (String nutrient : microNutrientList) {
                        MicroConsideredValues.put(nutrient,resultSet.getDouble(nutrient));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUserInfo() {
        return
                "<p>의뢰자 성명: "+users.getName()+"</p>" +
                        "<p>의뢰 일시: "+requestHistory.getRequest_date()+"</p>" +
                        "<p>재배 작물: "+requestHistoryService.getCropName(requestHistory)+"</p>" +
                        "<p>배양액 종류: "+requestHistoryService.getMediumType(requestHistory)+"</p>" +
                        "<br></br><br></br><br></br> ";
    }
    public void setPdfName() {

        this.pdfName = requestHistory.getRequest_date()+": "+users.getName()+"_분석 기록";
    }
    public String getPdfName() {
        return pdfName;
    }
    public void setUp(){
        //분석 기록에서 필요한 값 세팅
        setRequestHistory();
        //원수 고려값 세팅
        setMacroConsideredValue();
        setMicroConsideredValue();
        //처방 농도 세팅
        setMacroFertilization();
        setMicroFertilization();
        //100배액 세팅
        setMacroMolecularMass();
        setMicroMolecularMass();
        //유저 정보에 따른 PDF 세팅
        setPdfName();
    }

    @Override
    public void getPDF() {


        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        try{
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(getPdfName()));
            writer.setInitialLeading(12.5f);

            document.open();
            XMLWorkerHelper helper = XMLWorkerHelper.getInstance();

            CSSResolver cssResolver = new StyleAttrCSSResolver();
            CssFile cssFile = null;
            try{
                cssFile = helper.getCSS(new FileInputStream("src/main/resources/css/pdf.css"));
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            cssResolver.addCss(cssFile);

            //HTML과 폰트 준비
            XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
            fontProvider.register("css/MALGUN.ttf","MalgunGothic");
            CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);

            HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

            //Pipelines
            PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
            HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
            CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

            XMLWorker worker = new XMLWorker(css, true);
            XMLParser xmlParser = new XMLParser(worker, Charset.forName("UTF-8"));

            String htmlStr = getAllHtmlStr();

            StringReader stringReader = new StringReader(htmlStr);
            xmlParser.parse(stringReader);
            document.close();
            writer.close();

        }catch (DocumentException e){
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getAllHtmlStr() {

        String htmlStr = "<html><head></head><body style='font-family: MalgunGothic;'> " +
                "<h1>배양액 분석 기록 보고서</h1><br></br><hr> </hr>";

        htmlStr += getUserInfo();
        htmlStr += getTable(htmlStr);

        return htmlStr;
    }

    private String getTable(String htmlStr) {
        htmlStr += "<table>" ;

        htmlStr += getMacro(cropNutrients);
        htmlStr += getMicro(cropNutrients);

        htmlStr += getSolution("A");
        htmlStr += getSolution("B");
        htmlStr += getSolution("C");

        return htmlStr;
    }


    //#TODO - 기존 csv에서 읽던 것을 db에서 읽도록 전체 수정해야 함
//    private CropNutrientStandard getCropNutrients (){ //해당 배양액 종류에 해당하는 재배 작물의 원수 기준량 추출
//
//        csvDataReader = new CSVDataReader();
//        NutrientSolution nutrientSolution = csvDataReader.readFile(users.getMediumType()); //네덜란드, 야마자키 등
//        ArrayList<CropNutrientStandard> cropList = nutrientSolution.getCropList();
//        Optional<CropNutrientStandard> cropNutrients = cropList.stream().filter(c -> c.getCropName().equals(users.getCropName()))
//                .findFirst();
//
//        return cropNutrients.get();
//    }


    private String getMicro(CropNutrientStandard cropNutrientStandard) {
        String Html =
                "<tr>"+
                        "<th class=\"category\">미량원소</th>" +
                        "<th>Fe</th>" +
                        "<th>Cu</th>" +
                        "<th>B</th>" +
                        "<th>Mn</th>" +
                        "<th>Zn</th>" +
                        "<th>Mo</th>" +
                        "<th>  </th>" +
                        "</tr>";

        //미량원소 기준량
        Html += "<tr>"+
                "<td class=\"name\">기준량</td>" +
                "<td class=\"value\">"+cropNutrientStandard.getFe() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getCu() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getB() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getMn() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getZn() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getMo()+"</td>"+
                "<td class=\"value\">  </td>"+
                "</tr>";
        //고려원수값
        Html += "<tr>"+
                "<td class=\"name\">원수성분</td>" +
                "<td class=\"value\">"+MicroConsideredValues.get("Fe") +"</td>"+
                "<td class=\"value\">"+MicroConsideredValues.get("Cu") +"</td>"+
                "<td class=\"value\">"+MicroConsideredValues.get("B") +"</td>"+
                "<td class=\"value\">"+MicroConsideredValues.get("Mn") +"</td>"+
                "<td class=\"value\">"+MicroConsideredValues.get("Zn")+"</td>"+
                "<td class=\"value\">"+MicroConsideredValues.get("Mo")+"</td>"+
                "<td class=\"value\">  </td>"+
                "</tr>";

        //처방농도
        Html += "<tr>"+
                "<td class=\"name\">처방농도</td>" +
                "<td class=\"value\">"+MicroFertilization.get("Fe") +"</td>"+
                "<td class=\"value\">"+MicroFertilization.get("Cu") +"</td>"+
                "<td class=\"value\">"+MicroFertilization.get("B") +"</td>"+
                "<td class=\"value\">"+MicroFertilization.get("Mn") +"</td>"+
                "<td class=\"value\">"+MicroFertilization.get("Zn")+"</td>"+
                "<td class=\"value\">"+MicroFertilization.get("Mo")+"</td>"+
                "<td class=\"value\">  </td>"+
                "</tr>";

        return Html;
    }

    private String getMacro(CropNutrientStandard cropNutrientStandard){

        String Html =
                "<tr>"+
                        "<th class=\"category\">다량원소</th>" +
                        "<th colspan=\"2\">Ca</th>" +
                        "<th colspan=\"2\">NO3N</th>" +
                        "<th colspan=\"2\">NH4N</th>" +
                        "<th colspan=\"2\">K</th>" +
                        "<th colspan=\"2\">H2PO4</th>" +
                        "<th colspan=\"2\">SO4</th>" +
                        "<th colspan=\"2\">Mg</th>" +
                        "</tr>";

        //다량원소 기준량
        Html += "<tr>"+
                "<td class=\"name\">기준량</td>" +
                "<td class=\"value\">"+cropNutrientStandard.getCa() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getNO3() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getNH4() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getK() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getH2PO4() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getSO4()+"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getMg()+"</td>"+
                "</tr>";

        //고려원수값
        Html += "<tr>"+
                "<td class=\"name\">원수성분</td>" +
                "<td class=\"value\">"+MacroConsideredValues.get("Ca") +"</td>"+
                "<td class=\"value\">"+MacroConsideredValues.get("NO3") +"</td>"+
                "<td class=\"value\">"+MacroConsideredValues.get("NH4")+"</td>"+
                "<td class=\"value\">"+MacroConsideredValues.get("K") +"</td>"+
                "<td class=\"value\">"+MacroConsideredValues.get("H2PO4") +"</td>"+
                "<td class=\"value\">"+MacroConsideredValues.get("SO4")+"</td>"+
                "<td class=\"value\">"+MacroConsideredValues.get("Mg")+"</td>"+
                "</tr>";

        //처방농도
        Html += "<tr>"+
                "<td class=\"name\">처방농도</td>" +
                "<td class=\"value\">"+MacroFertilization.get("Ca") +"</td>"+
                "<td class=\"value\">"+MacroFertilization.get("NO3") +"</td>"+
                "<td class=\"value\">"+MacroFertilization.get("NH4")+"</td>"+
                "<td class=\"value\">"+MacroFertilization.get("K") +"</td>"+
                "<td class=\"value\">"+MacroFertilization.get("H2PO4") +"</td>"+
                "<td class=\"value\">"+MacroFertilization.get("SO4")+"</td>"+
                "<td class=\"value\">"+MacroFertilization.get("Mg")+"</td>"+
                "</tr>";

        return Html;
    }
    private String getSolution(String solution) {
        String unit = "Kg";
        String Html =
                "<tr>"+
                        "<th class=\"category\">"+solution+"액</th>" +
                        "<th colspan=\"5\"></th>"+
                        "<th class=\"category\" colspan=\"2\">100배액 기준</th>" +
                        "</tr>";

        for (String macro : MacroMolecularMass.keySet()) {
            if(MacroMolecularMass.get(macro).getSolution().equals(solution)){
                Html += "<tr>"+
                        "<td class=\"name\">"+macro+"</td>" +
                        "<td> </td>"+
                        "<td> </td>"+
                        "<td> </td>"+
                        "<td> </td>"+
                        "<td> </td>"+
                        "<td>"+String.format("%.2f",MacroMolecularMass.get(macro).getMass())+"</td>" +
                        "<td class=\"unit\">"+unit+"</td>" +
                        "</tr>";
            }
        }
        for (String micro : MicroMolecularMass.keySet()) {
            if(MicroMolecularMass.get(micro).getSolution().equals(solution)){
                Html += "<tr>"+
                        "<td class=\"name\">"+micro+"</td>" +
                        "<td> </td>"+
                        "<td> </td>"+
                        "<td> </td>"+
                        "<td> </td>"+
                        "<td> </td>"+
                        "<td>"+String.format("%.2f",MicroMolecularMass.get(micro).getMass())+"</td>" +
                        "<td class=\"unit\">"+unit+"</td>" +
                        "</tr>";
            }
        }

        return Html;

    }
}
