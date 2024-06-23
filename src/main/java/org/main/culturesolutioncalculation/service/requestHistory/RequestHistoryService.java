package org.main.culturesolutioncalculation.service.requestHistory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.main.culturesolutioncalculation.PrintTabController.DataItem;
import org.main.culturesolutioncalculation.RequestHistoryInfo;
import org.main.culturesolutioncalculation.UserInfo;
import org.main.culturesolutioncalculation.service.database.DatabaseConnector;
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
    public int save(RequestHistoryInfo requestHistory){
        int generatedId = 0;
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
             PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

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

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                        System.out.println("Generated ID: " + generatedId);
                    }
                }
            } else {
                System.out.println("Insert failed requestHistory");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
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
    public String getCropName(int cultureMediumId){
        String query = "select fertilizer_salts from culture_medium where id = ?";
        String cropName=  "";
        try(Connection connection = conn.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query)
        ){
            pstmt.setInt(1, cultureMediumId);
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

    public void setSelectedCropNameAndCultureMediumId(RequestHistoryInfo requestHistoryInfo) {
        String query = "update requestHistory set selected_crop_name = ?, culture_medium_id = ? where id = ?";
        try(Connection connection = conn.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query)
        ){
            pstmt.setString(1, requestHistoryInfo.getSelectedCropName());
            pstmt.setInt(2, requestHistoryInfo.getCultureMediumId());
            pstmt.setInt(3, requestHistoryInfo.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update requestHistory successful");
            } else {
                System.out.println("Fail: requestHistory -> No rows affected");
            }
        } catch (SQLException e) {
            System.err.println("Failed to update requestHistory");
            e.printStackTrace();
        }
    }

    public ObservableList<DataItem> getMacroAnalysisData(RequestHistoryInfo requestHistoryInfo) {
        ObservableList<DataItem> response = FXCollections.observableArrayList();
        String query = "select * from users_macro_fertilization where requestHistory_id = ?";

        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, requestHistoryInfo.getId());
            System.out.println("PreparedStatement created with id: " + requestHistoryInfo.getId());


            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    System.out.println("No data found for requestHistory_id: " + requestHistoryInfo.getId());
                }

                while (resultSet.next()) {
                    String unit = resultSet.getString("unit");
                    String method = requestHistoryInfo.getMediumTypeName();
                    System.out.println("method = " + method);

                    response.add(new DataItem("산도 (pH)", String.valueOf(resultSet.getDouble("pH")), unit, method));
                    response.add(new DataItem("농도 (EC)", String.valueOf(resultSet.getDouble("EC")), "dS/m", method));
                    response.add(new DataItem("질산태질소 (NO3-N)", String.valueOf(resultSet.getDouble("NO3N")), unit, method));
                    response.add(new DataItem("암모니아태질소 (NH4-N)", String.valueOf(resultSet.getDouble("NH4N")), unit, method));
                    response.add(new DataItem("인 (P)", String.valueOf(resultSet.getDouble("H2PO4")), "ppm", method));
                    response.add(new DataItem("칼륨 (K)", String.valueOf(resultSet.getDouble("K")), unit, method));
                    response.add(new DataItem("칼슘 (Ca)", String.valueOf(resultSet.getDouble("Ca")), unit, method));
                    response.add(new DataItem("마그네슘 (Mg)", String.valueOf(resultSet.getDouble("Mg")), unit, method));
                    response.add(new DataItem("황 (S)", String.valueOf(resultSet.getDouble("SO4S")), unit, method));
                    response.add(new DataItem("중탄산 (HCO3-)", String.valueOf(resultSet.getDouble("HCO3")), unit, method));
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to select users_macro_fertilization");
            e.printStackTrace();
        }

        return response;
    }
    public ObservableList<DataItem> getMicroAnalysisData(RequestHistoryInfo requestHistoryInfo) {
        ObservableList<DataItem> response = FXCollections.observableArrayList();
        String query = "select * from users_micro_fertilization where requestHistory_id = ?";

        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, requestHistoryInfo.getId());

            try (ResultSet resultSet = pstmt.executeQuery()) {

                if (!resultSet.isBeforeFirst()) {
                    System.out.println("No data found for requestHistory_id: " + requestHistoryInfo.getId());
                }

                while (resultSet.next()) {
                    String unit = resultSet.getString("unit");
                    String method = requestHistoryInfo.getMediumTypeName();

                    response.add(new DataItem("철 (Fe)", String.valueOf(resultSet.getDouble("Fe")), unit, method));
                    response.add(new DataItem("붕소 (B)", String.valueOf(resultSet.getDouble("B")), unit, method));
                    response.add(new DataItem("망간 (Mn)", String.valueOf(resultSet.getDouble("Mn")), unit, method));
                    response.add(new DataItem("아연 (Zn)", String.valueOf(resultSet.getDouble("Zn")), unit, method));
                    response.add(new DataItem("구리 (Cu)", String.valueOf(resultSet.getDouble("Cu")), unit, method));
                    response.add(new DataItem("몰리브뎀 (Mo)", String.valueOf(resultSet.getDouble("Mo")), unit, method));
                    response.add(new DataItem("중탄산 (HCO3-)", String.valueOf(resultSet.getDouble("HCO3")), unit, method));
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to select users_macro_fertilization");
            e.printStackTrace();
        }

        return response;
    }


    public ObservableList<DataItem> getMacroCompositionData(RequestHistoryInfo requestHistoryInfo) {
        ObservableList<DataItem> response = FXCollections.observableArrayList();
        boolean is4 = true, set = true;
        String query = "SELECT cm.*, cv.is4 " +
                "FROM users_macro_calculatedMass cm " +
                "JOIN users_macro_consideredValues cv ON cm.requestHistory_id = cv.requestHistory_id " +
                "WHERE cm.requestHistory_id = ?";

        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, requestHistoryInfo.getId());
            System.out.println("PreparedStatement created with id: " + requestHistoryInfo.getId());


            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    System.out.println("No data found for requestHistory_id: " + requestHistoryInfo.getId());
                }

                while (resultSet.next()) {
                    if(set) {
                        is4 = resultSet.getBoolean("is4");
                        set = false;
                    }

                    response.add(new DataItem(resultSet.getString("solution"), resultSet.getString("macro"), resultSet.getString("unit"), resultSet.getString("mass"), resultSet.getString("macro_kr")));
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to select users_macro_calculatedMass");
            e.printStackTrace();
        }

        if(is4) response.add(new DataItem("A", "5[Ca(NO3)2·2H2O]NH4NO3", "kg", "0.00", "질산칼슘(10수염)"));
        else response.add(new DataItem("A", "Ca(NO3)2·3H2O", "kg", "0.00" , "질산칼슘(4수염)"));

        return response;
    }

    public ObservableList<DataItem> getMicroCompositionData(RequestHistoryInfo requestHistoryInfo) {
        ObservableList<DataItem> response = FXCollections.observableArrayList();
        String query = "SELECT cm.*" +
                "FROM users_micro_calculatedMass cm " +
                "WHERE cm.requestHistory_id = ?";

        try (Connection connection = conn.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, requestHistoryInfo.getId());
            System.out.println("PreparedStatement created with id: " + requestHistoryInfo.getId());


            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    System.out.println("No data found for requestHistory_id: " + requestHistoryInfo.getId());
                }

                while (resultSet.next()) {
                    response.add(new DataItem(resultSet.getString("solution"), resultSet.getString("micro"), resultSet.getString("unit"), resultSet.getString("mass"), resultSet.getString("micro_kr")));
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to select users_micro_calculatedMass");
            e.printStackTrace();
        }

        return response;
    }
}
