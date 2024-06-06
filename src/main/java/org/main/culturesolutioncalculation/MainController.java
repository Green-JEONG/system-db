package org.main.culturesolutioncalculation;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;

import java.io.IOException;
import java.util.Map;

public class MainController {


    @FXML public ToolBar toolBarTab;
    @FXML private TabPane mainTabPane;
    @FXML private static Tab printTab;
    @FXML private static Tab macroTab;
    @FXML private static Tab microTab;
    @FXML private static Tab settingTab;
    @FXML private static Tab userInfoTab;
    private static UserInfo userInfo = new UserInfo();
    private static RequestHistoryInfo requestHistoryInfo = new RequestHistoryInfo(); //이렇게 바로 생성자 주입 받아야 전역적으로 사용되는 거 같음
    private static SettingInfo settingInfo = new SettingInfo();
    private static TableData tableData = new TableData();
    private static PrintTabController printTabController;
    //public static MacroResultController2 macroResultController;
    public static MacroResultController macroResultController;
    public static UserInfo getUserInfo() {
        return userInfo;
    }
    public void setRequestHistoryInfo (RequestHistoryInfo requestHistoryInfo){
        this.requestHistoryInfo = requestHistoryInfo;
    }

    public static SettingInfo getSettingInfo() {
        return settingInfo;
    }

    public static TableData getTableData() {
        return tableData;
    }

    public static  RequestHistoryInfo getRequestHistoryInfo() {
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
        setToolbarController();

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

    public void setToolbarController(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Toolbar.fxml"));
            loader.load();
            ToolbarController toolbarController = loader.getController();
            toolbarController.setMacroResultController(macroResultController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMacroResultController(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MacroResult.fxml"));
            loader.load();
            macroResultController = loader.getController();
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

    public MacroResultController getMacroResultController(){
        return macroResultController;
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("MacroResult.fxml"));
//            loader.load();
//            MacroResultController macroResultController = loader.getController();
//            return macroResultController;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    public ToolbarController getToolbarController(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Toolbar.fxml"));
            loader.load();
            ToolbarController toolbarController = loader.getController();
            return toolbarController;
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

        // 선택된 history 정보를 설정
        if (printTabController != null) {
            printTabController.setHistoryInfo(selectedHistory);
        }

        // 탭 전환 로직
        mainTabPane.getSelectionModel().select(printTab);
    }

    public void setMacroResultTabWithValues(Map<String, Double> userFertilization, Map<String, Double> consideredValues, Map<String, Double>standardValues, boolean is4, RequestHistoryInfo requestHistoryInfo, String macroUnit, boolean isConsidered) {
        macroResultController = getMacroResultController();

        if(macroResultController != null){
            macroResultController.setUserFertilization(userFertilization);
            macroResultController.setConsideredValues(consideredValues);
            macroResultController.setStandardValues(standardValues);
            macroResultController.setIs4(is4);
            macroResultController.setRequestHistoryInfo(requestHistoryInfo);
            macroResultController.setMacroUnit(macroUnit);
            macroResultController.setConsidered(isConsidered);
        }
    }

}
