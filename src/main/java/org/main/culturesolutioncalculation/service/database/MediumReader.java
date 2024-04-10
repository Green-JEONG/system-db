package org.main.culturesolutioncalculation.service.database;

import java.sql.*;
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

    //배양액에 따른 재배 작물 이름 데이터
    public List<String> getCrops(String mediumType){
        List<String> crops = new ArrayList<>();
        String query = "select * from culture_medium where medium_type_id = (select id from medium_types where name = ?)";

        try(Connection connection = conn.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query)
        ){
            pstmt.setString(1, mediumType);
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){
                    crops.add(resultSet.getString("fertilizer_salts"));
                }
            }
            return crops;
        }catch (SQLException e){
            e.printStackTrace();
        }
        //crops.add("재배 작물이 존재하지 않습니다.");
        return crops;
    }

    //해당 배양액의 모든 기준값 불러오기 + 디폴트로 네덜란드 배양액을 항상 불러오고 있어야 함 (프론트 측에서 파라미터가 선택되기 전까지 네덜란드 아이디 파라미터만 보내도록 설정)
    public List<Map<String, Object>> getCultureMediumData(String mediumType) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        String query =
                "SELECT * FROM culture_medium " +
                        "WHERE medium_type_id = (SELECT id FROM medium_types WHERE name = ?)";

        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, mediumType);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("fertilizer_salts", resultSet.getString("fertilizer_salts"));
                    data.put("EC(dSㆍm-1)", resultSet.getObject("EC(dSㆍm-1)"));
                    data.put("NO3 (mmol/L)", resultSet.getObject("NO3 (mmol/L)"));
                    data.put("NH4", resultSet.getObject("NH4"));
                    data.put("H2PO4", resultSet.getObject("H2PO4"));
                    data.put("K", resultSet.getObject("K"));
                    data.put("Ca", resultSet.getObject("Ca"));
                    data.put("Mg", resultSet.getObject("Mg"));
                    data.put("SO4", resultSet.getObject("SO4"));
                    data.put("Fe (μmol/L)", resultSet.getObject("Fe (μmol/L)"));
                    data.put("Cu", resultSet.getObject("Cu"));
                    data.put("B", resultSet.getObject("B"));
                    data.put("Mn", resultSet.getObject("Mn"));
                    data.put("Zn", resultSet.getObject("Zn"));
                    data.put("Mo", resultSet.getObject("Mo"));
                    dataList.add(data);
                }
                return dataList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }
}
