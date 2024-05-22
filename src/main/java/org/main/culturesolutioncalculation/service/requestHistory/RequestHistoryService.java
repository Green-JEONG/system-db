package org.main.culturesolutioncalculation.service.requestHistory;

import org.main.culturesolutioncalculation.RequestHistoryInfo;
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
    public void save(RequestHistoryInfo requestHistory){
        String query = "INSERT INTO requestHistory " +
                "(request_date, user_id";
        String values = "VALUES (?, ?";

        if (requestHistory.getSampleType() != null) {
            query += ", sample_type";
            values += ", ?";
        }
        if (requestHistory.getVarietyName() != null) {
            query += ", variety_name";
            values += ", ?";
        }
        if (requestHistory.getSubstrateType() != null) {
            query += ", substrate_type";
            values += ", ?";
        }
        if (requestHistory.getDeliveryMethod() != null) {
            query += ", delivery_method";
            values += ", ?";
        }

        query += ") " + values + ")";

        System.out.println("query = " + query);

        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setTimestamp(1, requestHistory.getRequestDate());
            pstmt.setLong(2, requestHistory.getUserInfo().getId());

            int index = 3;

            if (requestHistory.getSampleType() != null) {
                pstmt.setString(index++, requestHistory.getSampleType());
            }
            if (requestHistory.getVarietyName() != null) {
                pstmt.setString(index++, requestHistory.getVarietyName());
            }
            if (requestHistory.getSubstrateType() != null) {
                pstmt.setString(index++, requestHistory.getSubstrateType());
            }
            if (requestHistory.getDeliveryMethod() != null) {
                pstmt.setString(index++, requestHistory.getDeliveryMethod());
            }

            int result = pstmt.executeUpdate();

            if (result > 0) {
                System.out.println("Success insert requestHistory");
            } else {
                System.out.println("Insert failed requestHistory");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //해당 유저의 분석 리스트 반환
    public List<RequestHistoryInfo> findByUser(int userId){
        List<RequestHistoryInfo> histories = new LinkedList<>();

        String query = "select * from requestHistory where " +
                "user_id = ?";
        try(Connection connection = conn.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query)
        ){
            pstmt.setInt(1, userId);
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    histories.add(new RequestHistoryInfo(
                            resultSet.getInt("id"),
                            resultSet.getTimestamp("request_date"),
                            resultSet.getInt("culture_medium_id"),
                            resultSet.getString("sample_type"),
                            resultSet.getString("variety_name"),
                            resultSet.getString("crop_type"),
                            resultSet.getString("substrate_type"),
                            resultSet.getString("delivery_method"),
                            resultSet.getString("selected_crop_name")
                    ));
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
