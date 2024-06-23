package org.main.culturesolutioncalculation;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.main.culturesolutioncalculation.service.calculator.CalculationStrategy;
import org.main.culturesolutioncalculation.service.calculator.FinalCal;
import org.main.culturesolutioncalculation.service.database.MediumService;
import org.main.culturesolutioncalculation.service.print.SangJuPrint;
import org.main.culturesolutioncalculation.service.requestHistory.RequestHistoryService;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class PrintTabController {

    MainController mainController = new MainController();
    private static UserInfo userInfo = MainController.getUserInfo();
    private static ObservableList<DataItem> analysisData = FXCollections.observableArrayList();
    private static ObservableList<DataItem> compositionData = FXCollections.observableArrayList();
    private SangJuPrint sangJuPrint;

    private static RequestHistoryInfo requestHistoryInfo = MainController.getRequestHistoryInfo();
    SettingInfo settingInfo = mainController.getSettingInfo();

    private MediumService mediumService;
    private RequestHistoryService requestHistoryService;


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
    @FXML private Label customerCropMedium;
    @FXML private Label ph;
    @FXML private Label ec;
    @FXML private Label hco3;

    private static CalculationStrategy macroStrategy;
    private static CalculationStrategy microStrategy;


    public void setMicroStrategy(CalculationStrategy microStrategy) {
        PrintTabController.microStrategy = microStrategy;
    }

    public void setMacroStrategy(CalculationStrategy strategy) {
        this.macroStrategy = strategy;
    }



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
        this.sangJuPrint = new SangJuPrint();
        System.out.println("PrintTabController 인스턴스 생성됨");

    }

    @FXML
    public void initialize() {
        initializeAnalysisTable();
        initializeCompositionTable();
        if(macroStrategy!= null)
            macroStrategy.save(); //다량 원소 계산 결과 저장
        if(microStrategy!= null)
            microStrategy.save();
        requestHistoryService = new RequestHistoryService();
        mediumService = new MediumService();
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

        TableColumn<DataItem, String> fertilizerKorColumn = new TableColumn<>("비료명");
        fertilizerKorColumn.setCellValueFactory(new PropertyValueFactory<>("kor"));

        TableColumn<DataItem, String> unitColumn = new TableColumn<>("단위");
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));

        TableColumn<DataItem, String> amountColumn = new TableColumn<>("100배 원액 소요량 (1000L 당)");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("method"));

        tankColumn.setMinWidth(50);
        fertilizerColumn.setMinWidth(150);
        unitColumn.setMinWidth(50);
        amountColumn.setMinWidth(200);

        compositionTable.getColumns().addAll(tankColumn, fertilizerColumn,fertilizerKorColumn, unitColumn, amountColumn);
    }

    @FXML
    private void loadAnalysisData() {

        if (requestHistoryInfo != null) {
            Platform.runLater(() -> {
                name.setText("의뢰자 : " + requestHistoryInfo.getUserInfo().getName());
                contact.setText("전화번호 : " + requestHistoryInfo.getUserInfo().getContact());
                address.setText("주소 : " + requestHistoryInfo.getUserInfo().getAddress());
                cropName.setText("작물명 : "+  requestHistoryInfo.getSelectedCropName());
                mediumType.setText("시료 종류 : "+ requestHistoryInfo.getMediumTypeName());
                System.out.println(requestHistoryInfo.getId());
            });
        } else {
            System.out.println("RequestHistoryInfo is null");
        }

        ObservableList<DataItem> macroAnalysisData = requestHistoryService.getMacroAnalysisData(requestHistoryInfo);
        ObservableList<DataItem> microAnalysisData = requestHistoryService.getMicroAnalysisData(requestHistoryInfo);


        analysisData.addAll(macroAnalysisData);
        analysisData.addAll(microAnalysisData);
        /*
                new DataItem("염소 (Cl)", "", "ppm", ""),
                new DataItem("나트륨 (Na)", "", "ppm", "") 은 요구 사항 부족으로 넣지 못함
         */
        analysisTable.setItems(analysisData);
    }

    @FXML
    private void loadCompositionData() {

        if (requestHistoryInfo != null) {
            Platform.runLater(() -> {
                processNumber.setText("시료번호 : "+requestHistoryInfo.getRequestDate().toString());
                customerName.setText("의뢰인-이름 : " + requestHistoryInfo.getUserInfo().getName().toString());
                customerContact.setText("의뢰인-전화번호 : " + requestHistoryInfo.getUserInfo().getContact());
                customerAddress.setText("의뢰인-주소 : " + requestHistoryInfo.getUserInfo().getAddress());
                customerCropName.setText("재배작물 : "+requestHistoryInfo.getSelectedCropName());
                customerCropMedium.setText("품종 : "+requestHistoryInfo.getMediumTypeName());
                ph.setText("원수수질-pH : "+requestHistoryInfo.getPh());
                ec.setText("원수수질-EC(dS/m) : "+requestHistoryInfo.getEc());
                hco3.setText("원수수질-중탄산(mg/L) : "+requestHistoryInfo.getHco3());
            });
        } else {
            System.out.println("RequestHistoryInfo is null");
        }
        ObservableList<DataItem> macroCompositionData = requestHistoryService.getMacroCompositionData(requestHistoryInfo);
        ObservableList<DataItem> microCompositionData = requestHistoryService.getMicroCompositionData(requestHistoryInfo);

        compositionData.addAll(macroCompositionData);
        compositionData.addAll(microCompositionData);

//        ObservableList<DataItem> compositionData = FXCollections.observableArrayList(
//                new DataItem("A", "질산칼슘(4수염) | Ca(NO3)23H2O", "kg", ""),
//                new DataItem("A", "질산칼슘(10수염) | 5[Ca(NO3)2·2H2O]NH4NO3", "kg", ""),
//                new DataItem("A", "질산암모늄(초안) | NH4NO3", "kg", ""),
//                new DataItem("A", "황산암모늄(유안) | (NH4)2SO4", "kg", ""),
//                new DataItem("A", "질산칼륨 | KNO3", "kg", ""),
//                new DataItem("A", "킬레이트철 | EDTAFeNa·3H2O", "kg", ""),
//                new DataItem("B", "질산칼륨 | KNO3", "kg", ""),
//                new DataItem("B", "황산칼륨 | K2SO4", "kg", ""),
//                new DataItem("B", "황산마그네슘 | MgSO4·7H2O", "kg", ""),
//                new DataItem("B", "붕산 | H3BO3", "g", ""),
//                new DataItem("B", "황산망간 | MnSO4·H2O", "g", ""),
//                new DataItem("B", "황산아연 | ZnSO4·7H2O", "g", ""),
//                new DataItem("B", "황산구리 | CuSo4·5H2O", "g", ""),
//                new DataItem("B", "몰리브덴산나트륨 | Na2MoO4·2H2O", "g", "")
//        );
        compositionTable.setItems(compositionData);
    }

    // userInfoTab -> PrintTab으로 올 때 & microResult -> PrintTab으로 올 때
    public void setHistoryInfo(RequestHistoryInfo selectedHistory) {
        System.out.println("selectedHistory.getRequestDate() = " + selectedHistory.getRequestDate());
        requestHistoryInfo = selectedHistory;
        userInfo = selectedHistory.getUserInfo();
    }

    @FXML
    public void printAnalysisData(ActionEvent actionEvent) {
        try {
            sangJuPrint.generatePDF(requestHistoryInfo, analysisData, "analysis");
        }catch (NullPointerException e){
            System.err.print(e);
            e.printStackTrace();
        }
    }

    @FXML
    public void printCompositionData(ActionEvent actionEvent) {
        try{
            sangJuPrint.generatePDF(requestHistoryInfo, compositionData, "composition");
        }catch (NullPointerException e){
            System.err.print(e);
            e.printStackTrace();
        }
    }


    public static class DataItem {
        private final String item;
        private final String value;
        private final String unit;
        private final String method;
        private String kor;

        public DataItem(String item, String value, String unit, String method) {
            this.item = item;
            this.value = value;
            this.unit = unit;
            this.method = method;
        }

        public DataItem(String item, String value, String unit, String method, String kor) {
            this.item = item;
            this.value = value;
            this.unit = unit;
            this.method = method;
            this.kor = kor;
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

        public String getKor() {
            return kor;
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
