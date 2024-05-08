package org.main.culturesolutioncalculation.service.database;

import org.main.culturesolutioncalculation.model.CropNutrientStandard;

import java.sql.*;
import java.util.*;

//TODO - test
public class MediumService {

    private final String url = "jdbc:mysql://localhost:3306/CultureSolutionCalculation?useSSL=false";
    private final String user = "root";
    private final String password = "root";
    DatabaseConnector conn;

    public MediumService(){
        this.conn = DatabaseConnector.getInstance(url, user, password);
    }

    // 화면에 배양액 종류 보여주기
    public Map<Integer, String> getMediumType(){
        Map<Integer, String> mediumTypes = new HashMap<>();
        String query = "SELECT id, name FROM medium_types";

        try (Connection connection = conn.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {

            while (resultSet.next()){
                mediumTypes.put(resultSet.getInt("id"), resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mediumTypes;
    }

    // 배양액에 따른 재배 작물 이름 데이터
    public List<String> getCropNameList(String mediumType){
        List<String> crops = new ArrayList<>();
        String query = "SELECT * FROM culture_medium WHERE medium_type_id = (SELECT id FROM medium_types WHERE name = ?)";

        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, mediumType);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()){
                    crops.add(resultSet.getString("fertilizer_salts"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return crops;
    }

    public Optional<CropNutrientStandard> getCropData(int mediumTypeId){
        String query = "SELECT * FROM culture_medium WHERE id = ?";

        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, mediumTypeId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    CropNutrientStandard cropNutrientStandard = new CropNutrientStandard(
                            resultSet.getInt("id"),
                            resultSet.getString("fertilizer_salts"),
                            "",
                            resultSet.getDouble("EC(dSㆍm-1)"),
                            resultSet.getDouble("NO3(mmol/L)"),
                            resultSet.getDouble("NH4"),
                            resultSet.getDouble("H2PO4"),
                            resultSet.getDouble("K"),
                            resultSet.getDouble("Ca"),
                            resultSet.getDouble("Mg"),
                            resultSet.getDouble("SO4"),
                            resultSet.getDouble("Fe(μmol/L)"),
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
            System.err.println("해당 배양액 타입 아이디의 재배 작물이 존재하지 않음");
        }

        return Optional.empty();
    }

    // 해당 배양액의 모든 기준값 불러오기 + 디폴트로 네덜란드 배양액을 항상 불러오고 있어야 함 (프론트 측에서 파라미터가 선택되기 전까지 네덜란드 아이디 파라미터만 보내도록 설정)
    public List<CropNutrientStandard> getCultureMediumData(String mediumType) {
        List<CropNutrientStandard> cropNutrientStandards = new ArrayList<>();
        String query = "SELECT * FROM culture_medium WHERE medium_type_id = (SELECT id FROM medium_types WHERE name = ?)";

        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, mediumType);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    cropNutrientStandards.add(new CropNutrientStandard(
                            resultSet.getInt("id"),
                            resultSet.getString("fertilizer_salts"),
                            "",
                            resultSet.getDouble("EC(dSㆍm-1)"),
                            resultSet.getDouble("NO3(mmol/L)"),
                            resultSet.getDouble("NH4"),
                            resultSet.getDouble("H2PO4"),
                            resultSet.getDouble("K"),
                            resultSet.getDouble("Ca"),
                            resultSet.getDouble("Mg"),
                            resultSet.getDouble("SO4"),
                            resultSet.getDouble("Fe(μmol/L)"),
                            resultSet.getDouble("Cu"),
                            resultSet.getDouble("B"),
                            resultSet.getDouble("Mn"),
                            resultSet.getDouble("Zn"),
                            resultSet.getDouble("Mo")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cropNutrientStandards;
    }
    // 배양액 종류 추가 함수(네덜란드, 야마자키, 시립대 등등)
    public void addMediumType(String name) {
        String query = "INSERT INTO medium_types(name) VALUES(?)";

        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, name);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("success to insert medium_types");
            } else {
                System.out.println("fail to insert medium_types");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //해당 배양액에 해당하는 모든 기준값도 저장해야함 -> 이거 파일 넣으면 db 들어가도록?
}
