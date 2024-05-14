package org.main.culturesolutioncalculation;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.main.culturesolutioncalculation.service.requestHistory.RequestHistory;
import org.main.culturesolutioncalculation.service.requestHistory.RequestHistoryService;
import org.main.culturesolutioncalculation.service.users.UserService;
import org.main.culturesolutioncalculation.service.users.Users;

import java.time.LocalDate;
import java.util.List;

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
    private DatePicker date;

    @FXML
    private TextField scale;

    private UserInfo userInfo = MainController.getUserInfo();

    private RequestHistoryService requestHistoryService;

    private UserService userService;

    public UserInfoTabController() {

        this.requestHistoryService = new RequestHistoryService();
        this.userService = new UserService();
    }

    public void initialize() {
        date.setValue(LocalDate.now());
    }

    @FXML
    private void requestHistory() {

        int userId = userService.findByContact(contact.getText().toString());

        List<RequestHistory> findHistory = requestHistoryService.findByUser(userId);


        System.out.println("requestHistory 호출됨");

        for (RequestHistory requestHistory : findHistory) {
            System.out.println("requestHistory = " + requestHistory.getUserId());
            System.out.println("requestHistory.getCultureMediumId() = " + requestHistory.getCultureMediumId());
            System.out.println("requestHistory.getRequestDate() = " + requestHistory.getRequestDate());

        }
    }

    @FXML
    private void saveUserInfo() {
        if(userInfo != null) {
            userInfo.setCustomerName(username.getText().toString());
            userInfo.setAddress(address.getText().toString());
            userInfo.setContact(contact.getText().toString());
            userInfo.setProcessingDate(date.getValue());
            userInfo.setScale(scale.getText().toString());

        } else {
            System.err.println("UserInfo 객체가 초기화되지 않았습니다.");
        }
        TabPane tabPane = userInfoTab.getTabPane();
        int currentIndex = tabPane.getTabs().indexOf(userInfoTab);
        tabPane.getSelectionModel().select(currentIndex + 1);
    }
}
