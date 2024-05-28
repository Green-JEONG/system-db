package org.main.culturesolutioncalculation;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {


    @FXML private TabPane mainTabPane;
    @FXML private static Tab printTab;
    @FXML private static Tab macroTab;
    @FXML private static Tab microTab;
    @FXML private static Tab settingTab;
    @FXML private static Tab userInfoTab;
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
        setMacroResultController();
        setMacroTabController();
        setMicroResultController();
        setSettingTabController();



        if (mainTabPane != null) {
            for (Tab tab : mainTabPane.getTabs()) {
                if ("printTab".equals(tab.getId())) {
                    printTab = tab; // printTab 찾아서 초기화 해야 함
                }
                else if("macroTab".equals(tab.getId())){
                    macroTab = tab;
                }
                else if("microTab".equals(tab.getId())){
                    microTab = tab;
                }
                else if("settingTab".equals(tab.getId())){
                    settingTab = tab;
                }else if("userInfoTab".equals(tab.getId())){
                    userInfoTab = tab;
                }
            }
        }
    }
    public void setUserInfoTabController(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserInfoTab.fxml"));
            loader.load();
            UserInfoTabController userInfoTabController = loader.getController();
            userInfoTabController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSettingTabController(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SettingTab.fxml"));
            loader.load();
            SettingTabController settingTabController = loader.getController();
            settingTabController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMacroResultController(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MacroResult.fxml"));
            loader.load();
            MacroResultController macroResultController = loader.getController();
            macroResultController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setMicroResultController(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MicroResult.fxml"));
            loader.load();
            MicroResultController microResultController = loader.getController();
            microResultController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMacroTabController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MacroTab.fxml"));
            loader.load();
            MacroTabController macroTabController = loader.getController();
            macroTabController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public PrintTabController getPrintTabController(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PrintTab.fxml"));
            loader.load();
            PrintTabController printTabController = loader.getController();
            return printTabController;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void moveToSettingTab(){
        if (mainTabPane != null && settingTab != null) {
            Platform.runLater(() -> {
                loadTabContent(settingTab, "SettingTab.fxml");  //MacroTab 재로드
                mainTabPane.getSelectionModel().select(settingTab);
                System.out.println("SettingTab content reloaded.");
            });
        }
    }
    public void moveToUserInfoTab(){
        if (mainTabPane != null && userInfoTab != null) {
            Platform.runLater(() -> {
                loadTabContent(userInfoTab, "UserInfoTab.fxml");  //MacroTab 재로드
                mainTabPane.getSelectionModel().select(userInfoTab);
                System.out.println("SettingTab content reloaded.");
            });
        }
    }

    public void moveToMacroTab(){
        if (mainTabPane != null && macroTab != null) {
            Platform.runLater(() -> {
                loadTabContent(macroTab, "MacroTab.fxml");  //MacroTab 재로드
                mainTabPane.getSelectionModel().select(macroTab);
                System.out.println("MacroTab content reloaded.");
            });
        }
    }

    public void moveToMicroTab(){
        if (mainTabPane != null && microTab != null) {
            Platform.runLater(() -> {
                loadTabContent(macroTab, "MicroTab.fxml");  //MacroTab 재로드
                mainTabPane.getSelectionModel().select(microTab);
                System.out.println("MicroTab content reloaded.");
            });
        }
    }

    public void loadTabContent(Tab tab, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Tab content = loader.load();
            tab.setContent(content.getContent());
        } catch (IOException e) {
            e.printStackTrace();
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
