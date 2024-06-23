package org.main.culturesolutioncalculation.service.print;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import javafx.collections.ObservableList;
import org.main.culturesolutioncalculation.PrintTabController;
import org.main.culturesolutioncalculation.PrintTabController.DataItem;
import org.main.culturesolutioncalculation.RequestHistoryInfo;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class SangJuPrint {


    public void generatePDF(RequestHistoryInfo requestHistoryInfo, ObservableList<DataItem> data, String gubun) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = requestHistoryInfo.getRequestDate().toLocalDateTime().format(formatter);
        String htmlStr = "";
        String outputPath = "";

        if(gubun.equals("analysis")) {
            htmlStr = getHtmlStr(requestHistoryInfo, data, formattedDate);
            outputPath = "보고서.pdf";
        }else{
            htmlStr = getCompositionHtmlStr(requestHistoryInfo, data, formattedDate);
            outputPath = "수경재배배양액조성표.pdf";
        }

        try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
            ConverterProperties properties = new ConverterProperties();
            DefaultFontProvider fontProvider = new DefaultFontProvider(false, false, false);
            //fontProvider.addFont("src/main/resources/css/BareunBatangM.ttf");
            fontProvider.addFont("src/main/resources/css/BareunBatangPro1.ttf");

            properties.setFontProvider(fontProvider);
            properties.setCharset("utf-8");

            HtmlConverter.convertToPdf(htmlStr, outputStream, properties);

            // Open the generated PDF file
            File pdfFile = new File(outputPath);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getCompositionHtmlStr(RequestHistoryInfo requestHistoryInfo, ObservableList<DataItem> compositionData, String formattedDate){
        String html =
                "<!DOCTYPE html>" +
                        "<html lang=\"ko\">" +
                        "<head>" +
                        "<meta charset=\"UTF-8\">" +
                        "<link rel=\"stylesheet\" href=\"src/main/resources/css/pdf.css\">" +
                        "</head>" +
                        "<body>" +
                        "<table>" +
                        "<th class=\"text\">수경재배 배양액 조성표</th>" +
                        "</table><br>" +
                        "<table>" +
                        "<tr>" +
                        "<th>시료번호</th>" +
                        "<td colspan=\"1\"></td>" +
                        "</tr>" +
                        "<tr>" +
                        "<th rowspan=\"4\">의뢰인</th>" +
                        "<tr>" +
                        "<td>이름</td>" +
                        "<td colspan=\"6\">"+requestHistoryInfo.getUserInfo().getName()+"</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<td>주소</td>" +
                        "<td colspan=\"6\">"+requestHistoryInfo.getUserInfo().getAddress()+"</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<td>전화번호</td>" +
                        "<td colspan=\"6\">"+requestHistoryInfo.getUserInfo().getContact()+"</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<th>재배작물</th>" +
                        "<td>"+requestHistoryInfo.getMediumTypeName()+"</td>" +
                        "<td></td>" +
                        "<td><b>품종</b></td>" +
                        "<td>"+requestHistoryInfo.getSelectedCropName()+"</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<th rowspan=\"4\">원수수질</th>" +
                        "</tr>" +
                        "<tr>" +
                        "<td>pH</td>" +
                        "<td colspan=\"6\">"+requestHistoryInfo.getPh()+"</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<td>EC(dS/m)</td>" +
                        "<td colspan=\"6\">"+requestHistoryInfo.getEc()+"</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<td>중탄산(mg/L)</td>" +
                        "<td colspan=\"6\">"+requestHistoryInfo.getHco3()+"</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<td colspan=\"5\"></td>" +
                        "</tr>" +
                        "<tr>"+
                        "<th>조제탱크</th>" +
                        "<th colspan=\"2\">비료의 종류</th>" +
                        "<th>단위</th>" +
                        "<th>100배 원액 소요량 (1000L 당)</th>" +
                        "</tr>" ;

        for (DataItem compositionDatum : compositionData) {
            html += "<tr>" +
                    "<th rowspan=\"7\">"+compositionDatum.getItem()+"</th>" +
                    "<td>"+ compositionDatum.getKor()+"</td>" +
                    "<td>"+compositionDatum.getValue()+"</td>" +
                    "<td>"+compositionDatum.getUnit()+"</td>" +
                    "<td>"+compositionDatum.getMethod()+"</td>" +
                    "</tr>";
        }
//
//        String k =
//
//                        "<tr>" +
//                        "<th rowspan=\"7\">A</th>" +
//                        "<td>질산칼슘(4수염)</td>" +
//                        "<td>Ca(NO3)2·4H2O</td>" +
//                        "<td>kg</td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<td>질산칼슘(10수염)</td>" +
//                        "<td>5[Ca(NO3)2·2H2O]NH4NO3</td>" +
//                        "<td>kg</td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<td>질산암모늄(초안)</td>" +
//                        "<td>NH4NO3</td>" +
//                        "<td>kg</td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<td>황산암모늄(유안)</td>" +
//                        "<td>(NH4)2SO4</td>" +
//                        "<td>kg</td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<td>질산칼륨 KNO3</td>" +
//                        "<td>(NH4)2SO4</td>" +
//                        "<td>kg</td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<td>킬레이트철</td>" +
//                        "<td>EDTAFeNa·3H2O</td>" +
//                        "<td>kg</td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<td></td>" +
//                        "<td></td>" +
//                        "<td></td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<th rowspan=\"10\">B</th>" +
//                        "<td>질산칼륨</td>" +
//                        "<td>KNO3</td>" +
//                        "<td>kg</td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<td>황산칼륨</td>" +
//                        "<td>K2NO3</td>" +
//                        "<td>kg</td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<td>황산마그네슘</td>" +
//                        "<td>MgSO4·7H2O</td>" +
//                        "<td>kg</td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<td></td>" +
//                        "<td></td>" +
//                        "<td></td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<td>붕산</td>" +
//                        "<td>H3BO3</td>" +
//                        "<td>g</td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<td>황산망간</td>" +
//                        "<td>MnSO4·H2O</td>" +
//                        "<td>g</td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<td>황산아연</td>" +
//                        "<td>ZnSO4·7H2O</td>" +
//                        "<td>g</td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<td>황산구리</td>" +
//                        "<td>CuSO4·5H2O</td>" +
//                        "<td>g</td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<td>몰리브덴산나트륨</td>" +
//                        "<td>Na2MoO4·2H2O</td>" +
//                        "<td>g</td>" +
//                        "<td></td>" +
//                        "</tr>" +
//                        "<tr>" +
//                        "<td></td>" +
//                        "<td></td>" +
//                        "<td></td>" +
//                        "<td></td>" +
//                        "</tr>" +
        html +=
                        "<tr>" +
                        "<th>주의사항</th>" +
                        "<td colspan=\"4\">-.<br>-.<br>-.<br>-.<br>-.</td>" +
                        "</tr>" +
                        "</table>" +
                        "<div class=\"footer\">" +
                        "<p class=\"note\">※ 위 조성표는 의뢰한 원수를 분석하여 작성하였으며 영농참고자료로만 활용 가능하며 기타 법적효력은 없습니다.</p><br>" +
                        "<p class=\"notes\">원수 분석결과에 따른 배양액 조성표를 위와 같이 통지합니다.</p><br>" +
                        "<p class=\"notes\">년 월 일</p><br>" +
                        "<p class=\"texts\">상주시농업기술센터소장<span class=\"small-text\">(직인생략)</span></p>" +
                        "</div>" +
                        "</body>" +
                        "</html>";
        return html;
    }

    private String getHtmlStr(RequestHistoryInfo requestHistoryInfo, ObservableList<DataItem> analysisData, String formattedDate) {
        String html = "<!DOCTYPE html>" +
                "<html lang=\"ko\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<link rel=\"stylesheet\" href=\"src/main/resources/css/pdf.css\">" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "<div class=\"header\">" +
                "<h2>수경재배 양액 분석결과서</h2>" +
                "</div>" +
                "<div>" +
                "<p>의뢰자: " + requestHistoryInfo.getUserInfo().getName() + "</p>" +
                "<p>전화번호: " + requestHistoryInfo.getUserInfo().getContact() + "</p>" +
                "<p>주소: " + requestHistoryInfo.getUserInfo().getAddress() + "</p>" +
                "<p>작물명: " + requestHistoryInfo.getSelectedCropName() + "</p>" +
                "<p>시료종류: " + requestHistoryInfo.getMediumTypeName() + "</p>" +
                "</div>" +
                "<div>" +
                "<h3>분석 결과</h3>" +
                "<table>" +
                "<tr>" +
                "<th>항목</th>" +
                "<th>결과</th>" +
                "<th>단위</th>" +
                "<th>시험방법</th>" +
                "</tr>";

        for (DataItem item : analysisData) {
            html += "<tr>" +
                    "<td>" + item.getItem() + "</td>" +
                    "<td>" + item.getValue() + "</td>" +
                    "<td>" + item.getUnit() + "</td>" +
                    "<td>" + item.getMethod() + "</td>" +
                    "</tr>";
        }


        html += "</table>" +
                "</div>" +
                "<div class=\"footer\">" +
                "<p>위 분석결과는 의뢰하신 용도 이외의 목적으로 사용할 수 없습니다.</p>" +
                "<p>의뢰하신 시료에 대한 시험 및 분석 결과를 위와 같이 통지합니다.</p>" +
                "<p>"+formattedDate+"</p>" +
                "<p>상주시농업기술센터소장(직인생략)</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        return html;
    }


}
