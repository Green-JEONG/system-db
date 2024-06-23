package org.main.culturesolutioncalculation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.main.culturesolutioncalculation.service.database.MediumService;
import org.main.culturesolutioncalculation.service.requestHistory.RequestHistoryService;
import org.main.culturesolutioncalculation.service.users.UserService;
import org.main.culturesolutioncalculation.service.users.Users;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

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


    private UserInfo userInfo;// = MainController.getUserInfo();
    private RequestHistoryInfo requestHistoryInfo = MainController.getRequestHistoryInfo();

    private RequestHistoryService requestHistoryService;

    private UserService userService;
    private MediumService mediumService;

    private static MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public UserInfoTabController() {
        //this.mainController = new MainController();
        this.requestHistoryService = new RequestHistoryService();
        this.userService = new UserService();
        this.mediumService = new MediumService();
    }

    public void initialize() {

        // ListView에 CellFactory 설정
        historyListView.setCellFactory(param -> new ListCell<RequestHistoryInfo>() {
            @Override
            protected void updateItem(RequestHistoryInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Date formatting을 원하는 방식으로 설정
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    setText(dateFormat.format(item.getRequestDate()));
                }
            }
        });

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
    private ListView<RequestHistoryInfo> historyListView;
    @FXML
    private void getRequestHistory() {

        userInfo = userService.findByContact(contact.getText());
        List<RequestHistoryInfo> findHistory = requestHistoryService.findByUser(userInfo.getId());
        ObservableList<RequestHistoryInfo> items = FXCollections.observableArrayList(findHistory);
        historyListView.setItems(items);
    }

    @FXML
    private void handleListViewClick() { // 사용자가 ListView의 항목을 클릭했을 때 호출
        RequestHistoryInfo selectedHistory = historyListView.getSelectionModel().getSelectedItem();

        //해당 분석 기록에서 사용된 배양액 종류 이름 갖고오기
        selectedHistory.setMediumTypeName(mediumService.getMediumTypeName(selectedHistory.getMediumTypeId()));

        //requestHistoryInfo에 대한 유저 정보 채워넣기
        selectedHistory.setUserInfo(userInfo);

        if (selectedHistory != null) {
            mainController.showPrintTabWithHistory(selectedHistory);
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

        userInfo = new UserInfo();
        userInfo.setName(username.getText().toString());
        userInfo.setAddress(address.getText().toString());
        userInfo.setContact(contact.getText().toString());
        userInfo.setEmail(email.getText().toString());


        //유저 DB 저장
        int userId = userService.save(userInfo);
        userInfo.setId(userId);

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
        mainController.setRequestHistoryInfo(requestHistoryInfo);

        //분석 기록 DB 저장
        int savedId = requestHistoryService.save(requestHistoryInfo);
        requestHistoryInfo.setId(savedId);
        mainController.setRequestHistoryInfo(requestHistoryInfo);
    }
}
