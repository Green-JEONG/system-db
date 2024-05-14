package org.main.culturesolutioncalculation.service.print;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SangJuPrint {
    public void generatePDF() {
        String htmlStr = getHtmlStr();

        try (FileOutputStream outputStream = new FileOutputStream("수경재배배양액조성표.pdf")) {
            ConverterProperties properties = new ConverterProperties();
            DefaultFontProvider fontProvider = new DefaultFontProvider(false, false, false);
            fontProvider.addFont("src/main/resources/css/malgun.ttf");

            properties.setFontProvider(fontProvider);
            properties.setCharset("utf-8");

            HtmlConverter.convertToPdf(htmlStr, outputStream, properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getHtmlStr() {
        String htmlStr = "<html><head>" +
                "<style>" +
                "@font-face { font-family: 'MalgunGothic'; src: url('src/main/resources/css/malgun.ttf'); }" +
                "body { font-family: 'MalgunGothic'; }" +
                "h1 { text-align: center; }" +
                "table { width: 100%; border-collapse: collapse; }" +
                "th, td { border: 1px solid black; padding: 8px; text-align: left; }" +
                "th { background-color: #f2f2f2; }" +
                ".header { background-color: #f9f9f9; }" +
                "</style>" +
                "</head><body>";

        htmlStr += "<h1>수경재배 배양액 조성표</h1>" +
                "<table>" +
                "<tr><th>시료번호</th><td>8</td></tr>" +
                "<tr><th rowspan='3' class='header'>의뢰인</th><th>이름</th><td></td></tr>" +
                "<tr><th>주소</th><td></td></tr>" +
                "<tr><th>전화번호</th><td></td></tr>" +
                "<tr><th>재배작물</th><td></td><th>품종</th><td></td></tr>" +
                "<tr><th>원수수질</th><td>pH</td><td></td></tr>" +
                "<tr><th>EC(dS/m)</th><td></td></tr>" +
                "<tr><th>중탄산(mg/L)</th><td></td></tr>" +
                "</table><br>" +
                "<table>" +
                "<tr><th>조제탱크</th><th>비료의 종류</th><th>단위</th><th>100배 원액 소요량 (1000L 당)</th></tr>" +
                "<tr><th rowspan='6' class='header'>A</th><td>질산칼슘(4수염)</td><td>Ca(NO3)2·4H2O</td><td>kg</td><td></td></tr>" +
                "<tr><td>질산칼슘(10수염)</td><td>5[Ca(NO3)2·2H2O]NH4NO3</td><td>kg</td><td></td></tr>" +
                "<tr><td>질산암모늄(초안)</td><td>NH4NO3</td><td>kg</td><td></td></tr>" +
                "<tr><td>황산암모늄(유안)</td><td>(NH4)2SO4</td><td>kg</td><td></td></tr>" +
                "<tr><td>질산칼륨</td><td>KNO3</td><td>kg</td><td></td></tr>" +
                "<tr><td>킬레이트철</td><td>EDTAFeNa·3H2O</td><td>kg</td><td></td></tr>" +
                "<tr><th rowspan='3' class='header'>B</th><td>질산칼륨</td><td>KNO3</td><td>kg</td><td></td></tr>" +
                "<tr><td>황산칼륨</td><td>K2SO4</td><td>kg</td><td></td></tr>" +
                "<tr><td>황산마그네슘</td><td>MgSO4·7H2O</td><td>kg</td><td></td></tr>" +
                "<tr><td>붕산</td><td>H3BO3</td><td>g</td><td></td></tr>" +
                "<tr><td>황산망간</td><td>MnSO4·H2O</td><td>g</td><td></td></tr>" +
                "<tr><td>황산아연</td><td>ZnSO4·7H2O</td><td>g</td><td></td></tr>" +
                "<tr><td>황산구리</td><td>CuSO4·5H2O</td><td>g</td><td></td></tr>" +
                "<tr><td>몰리브덴산나트륨</td><td>Na2MoO4·2H2O</td><td>g</td><td></td></tr>" +
                "</table><br>" +
                "<p>주의사항: -. -. -. -. -. </p>" +
                "<p>※ 위 조성표는 의뢰한 원수를 분석하여 작성하였으며 영농참고자료로만 활용 가능하며 기타 법적효력은 없습니다.</p>" +
                "<p>원수 분석결과에 따른 배양액 조성표를 위와 같이 통지합니다.</p>" +
                "<p>2024년 01월 10일</p>" +
                "<p>상주시농업기술센터소장(직인생략)</p>";

        htmlStr += "</body></html>";
        return htmlStr;
    }

}
