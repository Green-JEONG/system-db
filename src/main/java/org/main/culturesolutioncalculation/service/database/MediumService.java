package org.main.culturesolutioncalculation.service.database;

import org.main.culturesolutioncalculation.model.CropNutrientStandard;

import java.sql.*;
import java.util.*;

//TODO - test
public class MediumService {

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
    public Optional<CropNutrientStandard> getCultureMediumData(String mediumType) {
        CropNutrientStandard cropNutrientStandard;
        String query =
                "SELECT * FROM culture_medium " +
                        "WHERE medium_type_id = (SELECT id FROM medium_types WHERE name = ?)";

        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, mediumType);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    cropNutrientStandard =
                            new CropNutrientStandard(
                                    resultSet.getString("fertilizer_salts"),
                                    "",
                                    resultSet.getDouble("EC(dSㆍm-1)"),
                                    resultSet.getDouble("NO3 (mmol/L)"),
                                    resultSet.getDouble("NH4"),
                                    resultSet.getDouble("H2PO4"),
                                    resultSet.getDouble("K"),
                                    resultSet.getDouble("Ca"),
                                    resultSet.getDouble("Mg"),
                                    resultSet.getDouble("SO4"),
                                    resultSet.getDouble("Fe (μmol/L)"),
                                    resultSet.getDouble("Cu"),
                                    resultSet.getDouble("B"),
                                    resultSet.getDouble("Mn"),
                                    resultSet.getDouble("Zn"),
                                    resultSet.getDouble("Mo")
                                    );
                    return Optional.of(cropNutrientStandard);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    //배양액 종류 추가 함수(네덜란드, 야마자키, 시립대 등등)
    public void addMediumType(String name){
        String query = "insert into medium_types(name) values('"+name+"')";
        try(Connection connection = conn.getConnection();
            Statement stmt = connection.createStatement();
        ){
            int result = stmt.executeUpdate(query);

            if(result>0) System.out.println("success to insert medium_types");
            else System.out.println("fail to insert medium_types");

        }catch (SQLException e){

        }
    }
    //해당 배양액에 해당하는 모든 기준값도 저장해야함 -> 이거 파일 넣으면 db 들어가도록?
}
