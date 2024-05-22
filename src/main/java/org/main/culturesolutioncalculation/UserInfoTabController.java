package org.main.culturesolutioncalculation;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.main.culturesolutioncalculation.service.requestHistory.RequestHistory;
import org.main.culturesolutioncalculation.service.requestHistory.RequestHistoryService;
import org.main.culturesolutioncalculation.service.users.UserService;
import org.main.culturesolutioncalculation.service.users.Users;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

public class UserInfoTabController {
    @FXML
    private Tab userInfoTab;

    @FXML
    private TextField username;

    @FXML
    private TextField address;

    @FXML
    private TextField contact;
    @FXML
    private TextField email;


    @FXML
    private DatePicker date;


    // 시료 종류 라디오 버튼
    @FXML
    private RadioButton sampleType1;
    @FXML
    private RadioButton sampleType2;
    @FXML
    private RadioButton sampleType3;

    // 작물 명 라디오 버튼
    @FXML
    private RadioButton cropType1;
    @FXML
    private RadioButton cropType2;
    @FXML
    private RadioButton cropType3;
    @FXML
    private RadioButton cropType4;
    @FXML
    private RadioButton cropType5;

    // 품종 명
    @FXML
    private TextField varietyName;

    // 배지 종류 라디오 버튼
    @FXML
    private RadioButton substrateType1;
    @FXML
    private RadioButton substrateType2;
    @FXML
    private RadioButton substrateType3;
    @FXML
    private RadioButton substrateType4;

    // 교부 방법 라디오 버튼
    @FXML
    private RadioButton deliveryMethod1;
    @FXML
    private RadioButton deliveryMethod2;
    @FXML
    private RadioButton deliveryMethod3;
    @FXML
    private RadioButton deliveryMethod4;


    private ToggleGroup sampleTypeGroup;
    private ToggleGroup cropTypeGroup;
    private ToggleGroup substrateTypeGroup;
    private ToggleGroup deliveryMethodGroup;


    private UserInfo userInfo = MainController.getUserInfo();
    private RequestHistoryInfo requestHistoryInfo = MainController.getRequestHistoryInfo();

    private RequestHistoryService requestHistoryService;

    private UserService userService;


    public UserInfoTabController() {

        this.requestHistoryService = new RequestHistoryService();
        this.userService = new UserService();
    }

    public void initialize() {

        // 현재 날짜로 설정
        date.setValue(LocalDate.now());

        sampleTypeGroup = new ToggleGroup();
        sampleType1.setToggleGroup(sampleTypeGroup);
        sampleType2.setToggleGroup(sampleTypeGroup);
        sampleType3.setToggleGroup(sampleTypeGroup);

        cropTypeGroup = new ToggleGroup();
        cropType1.setToggleGroup(cropTypeGroup);
        cropType2.setToggleGroup(cropTypeGroup);
        cropType3.setToggleGroup(cropTypeGroup);
        cropType4.setToggleGroup(cropTypeGroup);
        cropType5.setToggleGroup(cropTypeGroup);

        substrateTypeGroup = new ToggleGroup();
        substrateType1.setToggleGroup(substrateTypeGroup);
        substrateType2.setToggleGroup(substrateTypeGroup);
        substrateType3.setToggleGroup(substrateTypeGroup);
        substrateType4.setToggleGroup(substrateTypeGroup);

        deliveryMethodGroup = new ToggleGroup();
        deliveryMethod1.setToggleGroup(deliveryMethodGroup);
        deliveryMethod2.setToggleGroup(deliveryMethodGroup);
        deliveryMethod3.setToggleGroup(deliveryMethodGroup);
        deliveryMethod4.setToggleGroup(deliveryMethodGroup);
    }

    @FXML
    private void getRequestHistory() {

        int userId = userService.findByContact(contact.getText().toString());

        if(userId==0) throw new NoSuchElementException("분석 기록이 존재하지 않습니다");

        List<RequestHistoryInfo> findHistory = requestHistoryService.findByUser(userId);

        for (RequestHistoryInfo requestHistory : findHistory) {
            System.out.println("requestHistory.getRequestDate() = " + requestHistory.getRequestDate());

        }
    }

    @FXML
    private void saveUserInfo() {

        //사용자 정보 입려 안하고 다음 탭 버튼 누르면 경고
        if (username.getText().isEmpty() || address.getText().isEmpty() || contact.getText().isEmpty() || email.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("입력 오류");
            alert.setHeaderText(null);
            alert.setContentText("사용자 정보를 입력해주세요");
            alert.showAndWait();
            return;
        }

        if(userInfo != null) {
            userInfo.setName(username.getText().toString());
            userInfo.setAddress(address.getText().toString());
            userInfo.setContact(contact.getText().toString());
            userInfo.setEmail(email.getText().toString());

        } else {
            System.err.println("UserInfo 객체가 초기화되지 않았습니다.");
        }

        //유저 DB 저장
        userService.save(userInfo);
        //분석 기록 저장
        saveRequestHistoryInfo();

        TabPane tabPane = userInfoTab.getTabPane();
        int currentIndex = tabPane.getTabs().indexOf(userInfoTab);
        tabPane.getSelectionModel().select(currentIndex + 1);
    }

    @FXML
    private void saveRequestHistoryInfo() {

        RadioButton selectedSampleType = (RadioButton) sampleTypeGroup.getSelectedToggle();
        if (selectedSampleType != null) {
            String selectedSampleTypeText = selectedSampleType.getText();
            requestHistoryInfo.setSampleType(selectedSampleTypeText);
        }

        RadioButton selectedCropType = (RadioButton) cropTypeGroup.getSelectedToggle();
        if (selectedCropType != null) {
            String selectedCropTypeText = selectedCropType.getText();
            requestHistoryInfo.setCropType(selectedCropTypeText);
        }

        String varietyNameText = varietyName.getText();
        requestHistoryInfo.setVarietyName(varietyNameText);

        RadioButton selectedMediumType = (RadioButton) substrateTypeGroup.getSelectedToggle();
        if (selectedMediumType != null) {
            String selectedMediumTypeText = selectedMediumType.getText();
            requestHistoryInfo.setSubstrateType(selectedMediumTypeText);
        }

        RadioButton selectedDeliveryMethod = (RadioButton) deliveryMethodGroup.getSelectedToggle();
        if (selectedDeliveryMethod != null) {
            String selectedDeliveryMethodText = selectedDeliveryMethod.getText();
            requestHistoryInfo.setDeliveryMethod(selectedDeliveryMethodText);
        }

        requestHistoryInfo.setRequestDate(Timestamp.valueOf(LocalDateTime.now()));
        requestHistoryInfo.setUserInfo(userInfo);

        //분석 기록 DB 저장
        requestHistoryService.save(requestHistoryInfo);
    }
}
