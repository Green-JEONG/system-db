package org.main.culturesolutioncalculation.service.print;

import com.itextpdf.html2pdf.HtmlConverter;
import org.main.culturesolutioncalculation.service.requestHistory.RequestHistory;
import org.main.culturesolutioncalculation.service.requestHistory.RequestHistoryService;
import org.main.culturesolutioncalculation.service.users.Users;
import org.main.culturesolutioncalculation.service.calculator.FinalCal;

import java.io.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

import org.main.culturesolutioncalculation.service.database.DatabaseConnector;

public class AbstractPrint implements Print{

    private DatabaseConnector conn;
    private Map<String, FinalCal> MacroMolecularMass = new LinkedHashMap<>();
    private Map<String, FinalCal> MicroMolecularMass = new LinkedHashMap<>();

    /*
    분석 기록에 들어가야 할 정보들 :
    사용자 이름, 분석 날짜, 재배 작물, 배양액 종류(네덜란드, 야마자키:이건 프론트에서 받아오기로)
     */
    private Users users;

    private RequestHistory requestHistory;

    private RequestHistoryService requestHistoryService;
    private String pdfName;

    public void setPdfName() {

        this.pdfName = requestHistory.getRequest_date()+": "+users.getName()+"_분석 기록 개요";
    }

    public String getPdfName() {
        return pdfName;
    }
    public AbstractPrint(Users users, RequestHistory requestHistory){
        this.users = users;
        this.requestHistory = requestHistory;
    }


    //데이터베이스에서 꺼내와야함
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

    public void setUp(){
        //원수 고려값 세팅
        setMacroMolecularMass();
        setMicroMolecularMass();
        //유저 정보에 따른 PDF 세팅
        setPdfName();
    }

    public void getPDF() {
        try (FileOutputStream fos = new FileOutputStream(getPdfName() + ".pdf")) {
            String htmlStr = getAllHtmlStr();
            HtmlConverter.convertToPdf(htmlStr, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAllHtmlStr() {
        String htmlStr = "<html><head></head><body style='font-family: MalgunGothic;'>";

        htmlStr += getUserInfo();
        htmlStr += getTable(htmlStr);

        htmlStr += "</body></html>";
        return htmlStr;
    }

    private String getTable(String htmlStr) {
        htmlStr += "<table>" + "<tr>";

        htmlStr += getSolution("A");
        htmlStr += getSolution("B");
        htmlStr += getSolution("C");

        htmlStr += "</tr></table>";
        return htmlStr;
    }

    public String getUserInfo() {
        return "<p>의뢰자 성명: " + users.getName() + "</p>" +
                "<p>의뢰 일시: " + requestHistory.getRequest_date() + "</p>" +
                "<p>재배 작물: " + requestHistoryService.getCropName(requestHistory) + "</p>" +
                "<p>배양액 종류: " + requestHistoryService.getMediumType(requestHistory) + "</p>" +
                "<hr>";
    }

    private String getSolution(String solution) {
        String unit = "Kg";
        StringBuilder html = new StringBuilder();

        html.append("<th class=\"category\">").append(solution).append("액</th>")
                .append("<th colspan=\"2\">100배액 기준</th>")
                .append("</tr>");

        for (Map.Entry<String, FinalCal> entry : MacroMolecularMass.entrySet()) {
            if (entry.getValue().getSolution().equals(solution)) {
                html.append("<tr>")
                        .append("<td class=\"name\">").append(entry.getKey()).append("</td>")
                        .append("<td>").append(String.format("%.2f", entry.getValue().getMass())).append("</td>")
                        .append("<td class=\"unit\">").append(unit).append("</td>")
                        .append("</tr>");
            }
        }
        for (Map.Entry<String, FinalCal> entry : MicroMolecularMass.entrySet()) {
            if (entry.getValue().getSolution().equals(solution)) {
                html.append("<tr>")
                        .append("<td class=\"name\">").append(entry.getKey()).append("</td>")
                        .append("<td>").append(String.format("%.2f", entry.getValue().getMass())).append("</td>")
                        .append("<td class=\"unit\">").append(unit).append("</td>")
                        .append("</tr>");
            }
        }

        return html.toString();
    }


}
