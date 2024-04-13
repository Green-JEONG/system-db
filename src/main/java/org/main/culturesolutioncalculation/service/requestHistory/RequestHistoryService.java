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
    //�ش� ������ �м� ����Ʈ ��ȯ
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
                "<p>�Ƿ��� ����: "+users.getName()+"</p>" +
                        "<p>�Ƿ� �Ͻ�: "+requestHistory.getRequest_date()+"</p>" +
                        "<p>��� �۹�: "+users.getCropName()+"</p>" +
                        "<p>���� ����: "+users.getMediumType()+"</p>" +
                        "<hr>";
    } ����۹��̶�, ���� ���� ã�ƾ� ��
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
    public String getMediumType(RequestHistory requestHistory){ //���� ���� �������� (�״�����, �߸���Ű ��)
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
