package org.main.culturesolutioncalculation.service.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO - test
public class MediumReader {

    DatabaseConnector conn;

    //화면에 배양액 종류 보여주기
    public Map<Integer, String> getMediumType(){
        Map<Integer, String> mediumTypes = new HashMap<>();
        String query = "select name from medium_types";
        try(Connection connection = conn.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query)
        ){
            while(resultSet.next()){
                mediumTypes.put(resultSet.getInt("id"), resultSet.getString("name"));
            }
            return mediumTypes;

        }catch (SQLException e){
            e.printStackTrace();
        }
        //mediumType.add("배양액 종류가 존재하지 않습니다");
        return mediumTypes;
    }

    //배양액에 따른 재배 작물 보여주기
    public List<String> getCrops(int mediumTypeId){
        List<String> crops = new ArrayList<>();
        String query = "select * from culture_medium where medium_type_id = "+mediumTypeId;

        try(Connection connection = conn.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query)
        ){
            while(resultSet.next()){
                crops.add(resultSet.getString("fertilizer_salts"));
            }

            return crops;
        }catch (SQLException e){
            e.printStackTrace();
        }
        //crops.add("재배 작물이 존재하지 않습니다.");
        return crops;
    }

    //해당 배양액의 재배작물 모든 기준값 불러오기
}
