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
import org.main.culturesolutioncalculation.service.users.Users;
import org.main.culturesolutioncalculation.model.CropNutrientStandard;
import org.main.culturesolutioncalculation.model.NutrientSolution;
import org.main.culturesolutioncalculation.service.CSVDataReader;
import org.main.culturesolutioncalculation.service.calculator.FinalCal;
import org.main.culturesolutioncalculation.service.database.DatabaseConnector;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;

public class EmbodyPrint implements Print{

    private DatabaseConnector conn;
    private CSVDataReader csvDataReader;
    private int requestHistory_id;
    private Timestamp requestDate;
    private String pdfName;
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
    �м� ��Ͽ� ���� �� ������ :
    ����� �̸�, �м� ��¥, ��� �۹�, ���� ����(�״�����, �߸���Ű:�̰� ����Ʈ���� �޾ƿ����)
     */
    private Users users;

    public EmbodyPrint(Users users, int requestHistory_id, Timestamp requestDate){
        this.users = users;
        this.requestHistory_id = requestHistory_id;
        this.requestDate = requestDate;
    }

    @Override
    public void setMacroMolecularMass() {
        String query = "SELECT um.* FROM users_macro_calculatedMass um " +
                "WHERE um.requestHistory_id = ?";

        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // �Ķ���� ���ε�
            pstmt.setInt(1, requestHistory_id);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                // ��� ó��
                while (resultSet.next()) {
                    String macro = resultSet.getString("macro"); //����Į��4����, ����Į��, ����ϸ� ���
                    String solution = resultSet.getString("solution"); //��� Ÿ�� (A,B, C)
                    double mass = resultSet.getDouble("mass");//ȭ�չ� ����

                    MacroMolecularMass.put(macro, new FinalCal(solution, mass)); //100��� ����� ���� ȭ�չ��� �� ���� ����
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

            // �Ķ���� ���ε�
            pstmt.setInt(1, requestHistory_id);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                // ��� ó��
                while (resultSet.next()) {
                    String micro = resultSet.getString("micro"); //����Į��4����, ����Į��, ����ϸ� ���
                    String solution = resultSet.getString("solution"); //��� Ÿ�� (A,B, C)
                    double mass = resultSet.getDouble("mass");//ȭ�չ� ����

                    MicroMolecularMass.put(micro, new FinalCal(solution, mass)); //100��� ����� ���� ȭ�չ��� �� ���� ����
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

            pstmt.setInt(1, requestHistory_id);

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

            pstmt.setInt(1, requestHistory_id);

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
    //�ٷ����� ������ db���� �ҷ�����
    public void setMacroConsideredValue(){
        String query = "SELECT um.* FROM users_macro_consideredValues um " +
                "WHERE um.requestHistory_id = ?";
        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // �Ķ���� ���ε�
            pstmt.setInt(1, requestHistory_id);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                // ��� ó��
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
    //�̷����� ������ db���� �ҷ�����
    public void setMicroConsideredValue(){
        String query = "SELECT um.* FROM users_micro_consideredValues um " +
                "WHERE um.requestHistory_id = ?";
        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // �Ķ���� ���ε�
            pstmt.setInt(1, requestHistory_id);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                // ��� ó��
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
                "<p>�Ƿ��� ����: "+users.getName()+"</p>" +
                        "<p>�Ƿ� �Ͻ�: "+requestDate+"</p>" +
                        "<p>��� �۹�: "+users.getCropName()+"</p>" +
                        "<p>���� ����: "+users.getMediumType()+"</p>" +
                        "<br></br><br></br><br></br> ";
    }
    public void setPdfName() {
        this.pdfName = users.getName()+"_"+requestDate+"_"+users.getCropName()+".pdf";
    }

    public String getPdfName() {
        return pdfName;
    }
    public void setUp(){
        //���� ������ ����
        setMacroConsideredValue();
        setMicroConsideredValue();
        //ó�� �� ����
        setMacroFertilization();
        setMicroFertilization();
        //100��� ����
        setMacroMolecularMass();
        setMicroMolecularMass();
        //���� ������ ���� PDF ����
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

            //HTML�� ��Ʈ �غ�
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
                "<h1>���� �м� ��� ������</h1><br></br><hr> </hr>";

        htmlStr += getUserInfo();
        htmlStr += getTable(htmlStr);

        return htmlStr;
    }

    private String getTable(String htmlStr) {
        htmlStr += "<table>" ;

        CropNutrientStandard cropNutrients = getCropNutrients();
        htmlStr += getMacro(cropNutrients);
        htmlStr += getMicro(cropNutrients);

        htmlStr += getSolution("A");
        htmlStr += getSolution("B");
        htmlStr += getSolution("C");

        return htmlStr;
    }



    private CropNutrientStandard getCropNutrients (){ //�ش� ���� ������ �ش��ϴ� ��� �۹��� ���� ���ط� ����

        csvDataReader = new CSVDataReader();
        NutrientSolution nutrientSolution = csvDataReader.readFile(users.getMediumType()); //�״�����, �߸���Ű ��
        ArrayList<CropNutrientStandard> cropList = nutrientSolution.getCropList();
        Optional<CropNutrientStandard> cropNutrients = cropList.stream().filter(c -> c.getCropName().equals(users.getCropName()))
                .findFirst();

        return cropNutrients.get();
    }

    private String getMicro(CropNutrientStandard cropNutrientStandard) {
        String Html =
                "<tr>"+
                        "<th class=\"category\">�̷�����</th>" +
                        "<th>Fe</th>" +
                        "<th>Cu</th>" +
                        "<th>B</th>" +
                        "<th>Mn</th>" +
                        "<th>Zn</th>" +
                        "<th>Mo</th>" +
                        "<th>  </th>" +
                        "</tr>";

        //�̷����� ���ط�
        Html += "<tr>"+
                "<td class=\"name\">���ط�</td>" +
                "<td class=\"value\">"+cropNutrientStandard.getFe() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getCu() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getB() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getMn() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getZn() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getMo()+"</td>"+
                "<td class=\"value\">  </td>"+
                "</tr>";
        //����������
        Html += "<tr>"+
                "<td class=\"name\">��������</td>" +
                "<td class=\"value\">"+MicroConsideredValues.get("Fe") +"</td>"+
                "<td class=\"value\">"+MicroConsideredValues.get("Cu") +"</td>"+
                "<td class=\"value\">"+MicroConsideredValues.get("B") +"</td>"+
                "<td class=\"value\">"+MicroConsideredValues.get("Mn") +"</td>"+
                "<td class=\"value\">"+MicroConsideredValues.get("Zn")+"</td>"+
                "<td class=\"value\">"+MicroConsideredValues.get("Mo")+"</td>"+
                "<td class=\"value\">  </td>"+
                "</tr>";

        //ó���
        Html += "<tr>"+
                "<td class=\"name\">ó���</td>" +
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
                "<th class=\"category\">�ٷ�����</th>" +
                        "<th colspan=\"2\">Ca</th>" +
                        "<th colspan=\"2\">NO3N</th>" +
                        "<th colspan=\"2\">NH4N</th>" +
                        "<th colspan=\"2\">K</th>" +
                        "<th colspan=\"2\">H2PO4</th>" +
                        "<th colspan=\"2\">SO4</th>" +
                        "<th colspan=\"2\">Mg</th>" +
                        "</tr>";

        //�ٷ����� ���ط�
        Html += "<tr>"+
                "<td class=\"name\">���ط�</td>" +
                "<td class=\"value\">"+cropNutrientStandard.getCa() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getNO3() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getNH4() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getK() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getH2PO4() +"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getSO4()+"</td>"+
                "<td class=\"value\">"+cropNutrientStandard.getMg()+"</td>"+
                "</tr>";

        //����������
        Html += "<tr>"+
                "<td class=\"name\">��������</td>" +
                "<td class=\"value\">"+MacroConsideredValues.get("Ca") +"</td>"+
                "<td class=\"value\">"+MacroConsideredValues.get("NO3") +"</td>"+
                "<td class=\"value\">"+MacroConsideredValues.get("NH4")+"</td>"+
                "<td class=\"value\">"+MacroConsideredValues.get("K") +"</td>"+
                "<td class=\"value\">"+MacroConsideredValues.get("H2PO4") +"</td>"+
                "<td class=\"value\">"+MacroConsideredValues.get("SO4")+"</td>"+
                "<td class=\"value\">"+MacroConsideredValues.get("Mg")+"</td>"+
                "</tr>";

        //ó���
        Html += "<tr>"+
                "<td class=\"name\">ó���</td>" +
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
                        "<th class=\"category\">"+solution+"��</th>" +
                        "<th colspan=\"5\"></th>"+
                        "<th class=\"category\" colspan=\"2\">100��� ����</th>" +
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