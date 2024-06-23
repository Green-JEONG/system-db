package org.main.culturesolutioncalculation;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.main.culturesolutioncalculation.service.calculator.CalculationStrategy;
import org.main.culturesolutioncalculation.service.calculator.FinalCal;
import org.main.culturesolutioncalculation.service.database.MediumService;
import org.main.culturesolutioncalculation.service.print.SangJuPrint;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class PrintTabController {

    MainController mainController = new MainController();
    private static UserInfo userInfo;// = MainController.getUserInfo();

    private static RequestHistoryInfo requestHistoryInfo = MainController.getRequestHistoryInfo();
    SettingInfo settingInfo = mainController.getSettingInfo();

    private MediumService mediumService;


    //왼쪽 AnchorPane의 라벨
    @FXML private Label name;
    @FXML private Label address;
    @FXML private Label contact;
    @FXML private Label cropName;
    @FXML private Label mediumType;

    //오른쪽 AnchorPane의 라벨
    @FXML private Label processNumber;
    @FXML private Label customerName;
    @FXML private Label customerAddress;
    @FXML private Label customerContact;
    @FXML private Label customerCropName;
    @FXML private Label ph;
    @FXML private Label ec;
    @FXML private Label hco3;

    private static CalculationStrategy macroStrategy;

    private Map<String, FinalCal> macroMolecularMass =  new LinkedHashMap<>();

    private Map<String, Double> macroConsideredValues = new LinkedHashMap<>();

    private Map<String, Double> macroUserFertilization = new LinkedHashMap<>();

    public void setStrategy(CalculationStrategy strategy) {
        this.macroStrategy = strategy;
    }

    public void setMacroMolecularMass(Map<String, FinalCal> macroMolecularMass) {
        this.macroMolecularMass = macroMolecularMass;
    }

    public void setMacroConsideredValues(Map<String, Double> macroConsideredValues) {
        this.macroConsideredValues = macroConsideredValues;
    }

    public void setMacroUserFertilization(Map<String, Double> macroUserFertilization) {
        this.macroUserFertilization = macroUserFertilization;
    }
//    public void setRequestHistoryInfo(RequestHistoryInfo requestHistoryInfo){
//        this.requestHistoryInfo = requestHistoryInfo;
//    }

    @FXML
    private Label processingDateLabel;
    @FXML
    private Label selectedCultureLabel;


    @FXML
    private Label settingsLabel;

    @FXML
    private TableView<DataItem> analysisTable;

    @FXML
    private TableView<DataItem> compositionTable;

    public PrintTabController() {
        this.mediumService = new MediumService();
    }

    @FXML
    public void initialize() {
        initializeAnalysisTable();
        initializeCompositionTable();
        if(macroStrategy!= null)
            macroStrategy.save(); //다량 원소 계산 결과 저장
    }


    private void initializeAnalysisTable() {

        TableColumn<DataItem, String> itemColumn = new TableColumn<>("항목");
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("item"));

        TableColumn<DataItem, String> valueColumn = new TableColumn<>("결과");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        TableColumn<DataItem, String> unitColumn = new TableColumn<>("단위");
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));

        TableColumn<DataItem, String> methodColumn = new TableColumn<>("시험방법");
        methodColumn.setCellValueFactory(new PropertyValueFactory<>("method"));

        itemColumn.setMinWidth(150);
        valueColumn.setMinWidth(100);
        unitColumn.setMinWidth(50);
        methodColumn.setMinWidth(100);

        analysisTable.getColumns().addAll(itemColumn, valueColumn, unitColumn, methodColumn);
    }

    private void initializeCompositionTable() {

        TableColumn<DataItem, String> tankColumn = new TableColumn<>("조제탱크");
        tankColumn.setCellValueFactory(new PropertyValueFactory<>("item"));

        TableColumn<DataItem, String> fertilizerColumn = new TableColumn<>("비료의 종류");
        fertilizerColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        TableColumn<DataItem, String> unitColumn = new TableColumn<>("단위");
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));

        TableColumn<DataItem, String> amountColumn = new TableColumn<>("100배 원액 소요량 (1000L 당)");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("method"));

        tankColumn.setMinWidth(50);
        fertilizerColumn.setMinWidth(150);
        unitColumn.setMinWidth(50);
        amountColumn.setMinWidth(200);

        compositionTable.getColumns().addAll(tankColumn, fertilizerColumn, unitColumn, amountColumn);
    }

    @FXML
    private void loadAnalysisData() {


        if (requestHistoryInfo != null && userInfo != null) {

            if (requestHistoryInfo.getSelectedCropName() == null || requestHistoryInfo.getSelectedCropName().isEmpty()) {
                //getCropNameFromDatabase();
            }

            if (requestHistoryInfo.getMediumTypeName()== null || requestHistoryInfo.getMediumTypeName().isEmpty()) {
                //getMeditumTypeFromDatabase();
            }

            Platform.runLater(() -> {
                name.setText("의뢰자 : " + requestHistoryInfo.getUserInfo().getName());
                contact.setText("전화번호 : " + requestHistoryInfo.getUserInfo().getContact());
                address.setText("주소 : " + requestHistoryInfo.getUserInfo().getAddress());
                cropName.setText("작물명 : "+  requestHistoryInfo.getSelectedCropName());
                mediumType.setText("시료 종류 : "+ requestHistoryInfo.getMediumTypeName());
                System.out.println(requestHistoryInfo.getId());
            });
        } else {
            System.out.println("UserInfo is null");
        }



        if (requestHistoryInfo != null && userInfo != null) {
            Platform.runLater(() -> {
                name.setText("의뢰자 : " + userInfo.getName().toString());
                contact.setText("전화번호 : " + userInfo.getContact());
                address.setText("주소 : " + userInfo.getAddress());
                cropName.setText("작물명 : "+requestHistoryInfo.getSelectedCropName());
                mediumType.setText("시료 종류 : "+requestHistoryInfo.getMediumTypeName());
            });
        } else {
            System.out.println("UserInfo is null");
        }

        ObservableList<DataItem> analysisData = FXCollections.observableArrayList(
                new DataItem("산도 (pH)", "", "", ""),
                new DataItem("농도 (EC)", "", "dS/m", ""),
                new DataItem("질산태질소 (NO3-N)", "", "ppm", ""),
                new DataItem("암모니아태질소 (NH4-N)", "", "ppm", ""),
                new DataItem("인 (P)", "", "ppm", ""),
                new DataItem("칼륨 (K)", "", "ppm", ""),
                new DataItem("칼슘 (Ca)", "", "ppm", ""),
                new DataItem("마그네슘 (Mg)", "", "ppm", ""),
                new DataItem("황 (S)", "", "ppm", ""),
                new DataItem("염소 (Cl)", "", "ppm", ""),
                new DataItem("나트륨 (Na)", "", "ppm", ""),
                new DataItem("철 (Fe)", "", "ppm", ""),
                new DataItem("붕소 (B)", "", "ppm", ""),
                new DataItem("망간 (Mn)", "", "ppm", ""),
                new DataItem("아연 (Zn)", "", "ppm", ""),
                new DataItem("구리 (Cu)", "", "ppm", ""),
                new DataItem("몰리브뎀 (Mo)", "", "ppm", ""),
                new DataItem("중탄산 (HCO3-)", "", "ppm", "")
        );
        analysisTable.setItems(analysisData);
    }

    @FXML
    private void loadCompositionData() {

        if (requestHistoryInfo != null && userInfo != null) {
            Platform.runLater(() -> {
                processNumber.setText("시료번호 : "+requestHistoryInfo.getRequestDate().toString());
                customerName.setText("의뢰인-이름 : " + userInfo.getName().toString());
                customerContact.setText("의뢰인-전화번호 : " + userInfo.getContact());
                customerAddress.setText("의뢰인-주소 : " + userInfo.getAddress());
                customerCropName.setText("품종 : "+requestHistoryInfo.getSelectedCropName());
                ph.setText("원수수질-pH : "+requestHistoryInfo.getPh());
                ec.setText("원수수질-EC(dS/m) : "+requestHistoryInfo.getEc());
                hco3.setText("원수수질-중탄산(mg/L) : "+requestHistoryInfo.getHco3());
            });
        } else {
            System.out.println("UserInfo is null");
        }


        ObservableList<DataItem> compositionData = FXCollections.observableArrayList(
                new DataItem("A", "질산칼슘(4수염) | Ca(NO3)23H2O", "kg", ""),
                new DataItem("A", "질산칼슘(10수염) | 5[Ca(NO3)2·2H2O]NH4NO3", "kg", ""),
                new DataItem("A", "질산암모늄(초안) | NH4NO3", "kg", ""),
                new DataItem("A", "황산암모늄(유안) | (NH4)2SO4", "kg", ""),
                new DataItem("A", "질산칼륨 | KNO3", "kg", ""),
                new DataItem("A", "킬레이트철 | EDTAFeNa·3H2O", "kg", ""),
                new DataItem("B", "질산칼륨 | KNO3", "kg", ""),
                new DataItem("B", "황산칼륨 | K2SO4", "kg", ""),
                new DataItem("B", "황산마그네슘 | MgSO4·7H2O", "kg", ""),
                new DataItem("B", "붕산 | H3BO3", "g", ""),
                new DataItem("B", "황산망간 | MnSO4·H2O", "g", ""),
                new DataItem("B", "황산아연 | ZnSO4·7H2O", "g", ""),
                new DataItem("B", "황산구리 | CuSo4·5H2O", "g", ""),
                new DataItem("B", "몰리브덴산나트륨 | Na2MoO4·2H2O", "g", "")
        );
        compositionTable.setItems(compositionData);
    }

    // userInfoTab -> PrintTab으로 올 때 & microResult -> PrintTab으로 올 때
    public void setHistoryInfo(RequestHistoryInfo selectedHistory) {
        System.out.println("selectedHistory.getRequestDate() = " + selectedHistory.getRequestDate());
        requestHistoryInfo = selectedHistory;
        userInfo = selectedHistory.getUserInfo();
    }



    public static class DataItem {
        private final String item;
        private final String value;
        private final String unit;
        private final String method;

        public DataItem(String item, String value, String unit, String method) {
            this.item = item;
            this.value = value;
            this.unit = unit;
            this.method = method;
        }

        public String getItem() {
            return item;
        }

        public String getValue() {
            return value;
        }

        public String getUnit() {
            return unit;
        }

        public String getMethod() {
            return method;
        }
    }

    @FXML
    public void load() {

//        try {
//            customerNameLabel.setText(userInfo.getName());
//            addressLabel.setText(userInfo.getAddress());
//            contactLabel.setText(userInfo.getContact());
//            emailLabel.setText(userInfo.getEmail());
//            processingDateLabel.setText(requestHistoryInfo.getRequestDate().toString());
//            selectedCultureLabel.setText(mediumService.getMediumTypeName(requestHistoryInfo.getMediumTypeId()));
//            selectedCropLabel.setText(requestHistoryInfo.getSelectedCropName());
//
//            for (Map.Entry<String, String> entry : settingInfo.getTotalSetting().entrySet()) {
//                String settingText = entry.getKey() + ": " + entry.getValue();
//                settingsLabel.setText(settingsLabel.getText() + settingText + "\n");
//            }
//
//            // Call generatePDF method to create and open the PDF
//            SangJuPrint sangJuPrint = new SangJuPrint();
//            sangJuPrint.generatePDF();
//
//        } catch (NullPointerException e) {
//            System.out.println("값이 입력되지 않았습니다.");
//        }
    }
}
