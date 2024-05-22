package org.main.culturesolutioncalculation.service.users;

import org.main.culturesolutioncalculation.UserInfo;
import org.main.culturesolutioncalculation.service.database.DatabaseConnector;
import org.main.culturesolutioncalculation.service.users.Users;

import java.net.ConnectException;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class UserService {

    private final String url = "jdbc:mysql://localhost:3306/CultureSolutionCalculation?useSSL=false";
    private final String user = "root";
    private final String password = "root";
    private DatabaseConnector conn;

    public UserService() {
        this.conn = DatabaseConnector.getInstance(url, user, password);
    }

    //유저 정보 저장 - 이름, 주소, 연락처

    //TODO 유저가 db에 있으면 유저 아이디 뱉고, db에 없으면 0반환
    public int findByContact(String contact){ //유저가 db에 있으면 true, 없으면 false

        String preQuery = "select * from users u where u.contact = ?";
        int userId = 0;
        try(Connection connection = conn.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(preQuery);
        ){

            pstmt.setString(1, contact);

            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    userId = resultSet.getInt("id");
                    System.out.println("userId = " + userId);
                }
            }
            return userId;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return userId;
    }
    public void save(UserInfo users){
        //이미 유저가 존재하면 user는 세이브 안하고 분석 기록 튜플 하나 생성
        if(findByContact(users.getContact()) != 0){
            System.out.println("해당 유저가 이미 존재합니다");
            return;
        }
        else {
            String query = "INSERT INTO users (name, address, contact, email) VALUES (?, ?, ?, ?)";

            try (Connection connection = conn.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, users.getName());
                pstmt.setString(2, users.getAddress());
                pstmt.setString(3, users.getContact());
                pstmt.setString(4, users.getEmail());

                int result = pstmt.executeUpdate();

                if (result > 0) {
                    System.out.println("Success insert users");

                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int userId = generatedKeys.getInt(1);
                            users.setId(userId);
                            System.out.println("userId = " + userId);
                        }
                    }
                } else {
                    System.out.println("Insert failed users");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
