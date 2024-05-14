package org.main.culturesolutioncalculation.service.requestHistory;

import org.main.culturesolutioncalculation.service.database.DatabaseConnector;
import org.main.culturesolutioncalculation.service.users.Users;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class RequestHistoryService {

    private final String url = "jdbc:mysql://localhost:3306/CultureSolutionCalculation?useSSL=false";
    private final String user = "root";
    private final String password = "root";

    private DatabaseConnector conn;

    public RequestHistoryService() {
        this.conn = DatabaseConnector.getInstance(url, user, password);
    }

    //분석 기록 저장
    //TODO 테스트 해야 함
    public void save(RequestHistory requestHistory){
        String query = "insert into requestHistory (request_date, user_id, request_date, cultivacion_scale";
        boolean hasSampleType = false, hasVarietyName = false, hasSubstrateType = false, hasReportIssuanceMethod = false;

        //선택된 사항들도 추가
        if(requestHistory.getSampleType()!=null) {
            query += ", sample_type";
            hasSampleType = true;
        }
        if(requestHistory.getVarietyName()!=null) {
            query += ", variety_name";
            hasVarietyName = true;
        }
        if(requestHistory.getSubstrateType()!=null) {
            query += ", substrate_type";
            hasSubstrateType = true;
        }
        if(requestHistory.getReportIssuanceMethod()!=null) {
            query += ", report_issuance_method";
            hasReportIssuanceMethod = true;
        }

        query += ") values ("+
                requestHistory.getRequestDate()+", "+requestHistory.getUserId()
                +", "+requestHistory.getRequestDate()+", "+ requestHistory.cultivation_scale;

        if(hasSampleType) query = query +", '"+requestHistory.getSampleType()+"'";
        if(hasVarietyName) query = query +", '"+requestHistory.getVarietyName()+"'";
        if(hasSubstrateType) query = query +", '"+requestHistory.getSubstrateType()+"'";
        if(hasReportIssuanceMethod) query = query +", '"+requestHistory.getReportIssuanceMethod()+"'";

        query += ")";

        try (Connection connection = conn.getConnection();
             Statement stmt = connection.createStatement();
        ){
            stmt.executeUpdate(query);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //해당 유저의 분석 리스트 반환
    public List<RequestHistory> findByUser(int userId){
        List<RequestHistory> histories = new LinkedList<>();

        String query = "select * from requestHistory where " +
                "user_id = ?";
        try(Connection connection = conn.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query)
        ){
            pstmt.setInt(1, userId);
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){

                    System.out.println("resultSet.getTimestamp(\"request_date\") = " + resultSet.getTimestamp("request_date"));
                    histories.add(new RequestHistory(
                            resultSet.getInt("id"),
                            resultSet.getInt("user_id"),
                            resultSet.getTimestamp("request_date"),
                            resultSet.getInt("culture_medium_id"),
                            resultSet.getInt("cultivation_scale"),
                            resultSet.getString("sample_type"),
                            resultSet.getString("variety_name"),
                            resultSet.getString("substrate_type"),
                            resultSet.getString("report_issuance_method")
                    ));
                }
                for (RequestHistory history : histories) {
                    System.out.println("history = " + history);
                }

                return histories;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return histories;
    }

    /*


       public String getUserInfo() {

        return
                "<p>의뢰자 성명: "+users.getName()+"</p>" +
                        "<p>의뢰 일시: "+requestHistory.getRequest_date()+"</p>" +
                        "<p>재배 작물: "+users.getCropName()+"</p>" +
                        "<p>배양액 종류: "+users.getMediumType()+"</p>" +
                        "<hr>";
    } 재배작물이랑, 배양액 종류 찾아야 함
     */
    public String getCropName(RequestHistory requestHistory){
        String query = "select fertilizer_salts from culture_medium where id = ?";
        String cropName=  "";
        try(Connection connection = conn.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query)
        ){
            pstmt.setInt(1, requestHistory.getCultureMediumId());
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    cropName = resultSet.getString("fertilizer_salts");
                }
            }
            return cropName;

        }catch (SQLException e){

        }
        return cropName;
    }
    public String getMediumType(RequestHistory requestHistory){ //배양액 종류 내보내기 (네덜란드, 야마자키 등)
        String query = "select name from medium_types " +
                "where id = (select medium_type_id from culture_medium where id = ?)";
        String mediumType = "";
        try(Connection connection = conn.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query)
        ){
            pstmt.setInt(1, requestHistory.getId());
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    mediumType = resultSet.getString("name");
                }
                return mediumType;
            }

        }catch (SQLException e){

        }
        return mediumType;
    }
}
