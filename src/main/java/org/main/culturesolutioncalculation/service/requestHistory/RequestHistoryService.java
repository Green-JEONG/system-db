package org.main.culturesolutioncalculation.service.requestHistory;

import org.main.culturesolutioncalculation.service.database.DatabaseConnector;
import org.main.culturesolutioncalculation.service.users.Users;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class RequestHistoryService {

    private DatabaseConnector conn;

    public void save(RequestHistory requestHistory){
        String query = "insert into requestHistory (request_date, user_id) values ("+
                requestHistory.getRequest_date()+", "+requestHistory.getUser_id()+")";

        try (Connection connection = conn.getConnection();
             Statement stmt = connection.createStatement();
        ){
            stmt.executeUpdate(query);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    //해당 유저의 분석 리스트 반환
    public List<RequestHistory> findByUser(Users users){
        List<RequestHistory> histories = new LinkedList<>();

        String query = "select * from requestHistory where " +
                "user_id = ?";
        try(Connection connection = conn.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query)
        ){
            pstmt.setInt(1, users.getId());
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    histories.add(new RequestHistory(
                            resultSet.getInt("id"),
                            resultSet.getInt("user_id"),
                            resultSet.getTimestamp("request_date"),
                            resultSet.getInt("culture_medium_id"),
                            resultSet.getInt("cultivation_scale")
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
            pstmt.setInt(1, requestHistory.getCulture_medium_id());
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
