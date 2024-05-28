package org.main.culturesolutioncalculation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

public class MainController {


    @FXML
    private TabPane mainTabPane;
    @FXML
    private static Tab printTab;
    private static UserInfo userInfo = new UserInfo();
    private static RequestHistoryInfo requestHistoryInfo = new RequestHistoryInfo();
    private static SettingInfo settingInfo = new SettingInfo();
    private static TableData tableData = new TableData();
    private static PrintTabController printTabController;
    public static UserInfo getUserInfo() {
        return userInfo;
    }

    public static SettingInfo getSettingInfo() {
        return settingInfo;
    }

    public static TableData getTableData() {
        return tableData;
    }

    public static RequestHistoryInfo getRequestHistoryInfo() {
        return requestHistoryInfo;
    }
    @FXML
    public void initialize(){

        System.out.println("Initializing MainController...");
        setUserInfoTabController();

        System.out.println("mainTabPane = " + mainTabPane);
        if (mainTabPane != null) {
            for (Tab tab : mainTabPane.getTabs()) {
                System.out.println("Tab ID: " + tab.getId());
                if ("printTab".equals(tab.getId())) {
                    printTab = tab; // 이렇게 printTab을 찾아서 초기화
                }
            }
        }
    }
    public void setUserInfoTabController(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserInfoTab.fxml"));
            Tab tab = loader.load();
            UserInfoTabController userInfoTabController = loader.getController();
            userInfoTabController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MacroTabController getMacroTabController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MacroTab.fxml"));
            Tab tab = loader.load();
            MacroTabController macroTabController = loader.getController();
            return macroTabController;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public PrintTabController getPrintTabController(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PrintTab.fxml"));
            Tab tab = loader.load();
            PrintTabController printTabController = loader.getController();
            return printTabController;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void showPrintTabWithHistory(RequestHistoryInfo selectedHistory) {

        printTabController = getPrintTabController();

        System.out.println("selectedHistory = " + selectedHistory);

        // 선택된 history 정보를 설정
        if (printTabController != null) {
            printTabController.setHistoryInfo(selectedHistory);
        }

        // 탭 전환 로직
        mainTabPane.getSelectionModel().select(printTab);
    }
}
