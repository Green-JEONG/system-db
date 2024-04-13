package org.main.culturesolutioncalculation.service.users;

import org.main.culturesolutioncalculation.service.database.DatabaseConnector;
import org.main.culturesolutioncalculation.service.users.Users;

import java.net.ConnectException;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class UserService {

    private DatabaseConnector conn;


    //유저 정보 저장 - 이름, 주소, 연락처

    public boolean findByUser(Users users){ //유저가 db에 있으면 true, 없으면 false

        String preQuery = "select * from users u where u.name = ? and u.address = ? and u.contact = ?";
        try(Connection connection = conn.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(preQuery);
        ){
            pstmt.setString(1, users.getName());
            pstmt.setString(2, users.getAddress());
            pstmt.setString(3, users.getContact());

            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    return true;
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public void save(Users users){
        if(findByUser(users)) return;

        String query = "insert into users (name, address, contact) " +
                "values ("+users.getName()+", "+users.getAddress()+", "+users.getContact()+")";

        try(Connection connection = conn.getConnection();
            Statement stmt = connection.createStatement();){
            int result = stmt.executeUpdate(query);

            if(result>0) System.out.println("success insert users");
            else System.out.println("insert failed users");

        }catch (SQLException e){
            e.printStackTrace();
        }
    }



    public Map<Integer, Timestamp> findRequestHistory(Users users){
        String query = "select id, request_date from requestHistory where user_id = ?";
        Map<Integer, Timestamp> requestHistory = new LinkedHashMap<>();

        try(Connection connection = conn.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query)){

            pstmt.setInt(1, users.getId());

            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    requestHistory
                            .put(resultSet.getInt("id"),resultSet.getTimestamp("request_date"));
                }
                return requestHistory;
            }catch (SQLException e){
                e.printStackTrace();
            }

        }catch (SQLException e){
        }
        return requestHistory;
    }
}
