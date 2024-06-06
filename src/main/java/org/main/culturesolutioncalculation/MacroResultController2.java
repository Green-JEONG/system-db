package org.main.culturesolutioncalculation;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.main.culturesolutioncalculation.service.calculator.CalculationStrategy;
import org.main.culturesolutioncalculation.service.calculator.CalculatorClient;
import org.main.culturesolutioncalculation.service.calculator.MacroCalculationStrategy;

import java.util.*;

public class MacroResultController2 {
    private static MainController mainController;

    private TableData tableData;

    //TableData  tableData = mainController.getTableData();

    private static RequestHistoryInfo requestHistoryInfo;

    @FXML private TableView<ObservableList<SimpleStringProperty>> tableView;

    //@FXML private TableView<ObservableList<String>> tableView;

    //private ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

    private ObservableList<ObservableList<SimpleStringProperty>> data = FXCollections.observableArrayList();
    //private ObservableList<SimpleStringProperty> data = FXCollections.observableArrayList();


    private static Map<String, Double> userFertilization = new LinkedHashMap<>(); // 처방 농도

    private static Map<String, Double> consideredValues = new LinkedHashMap<>(); //고려 원수 값

    private static Map<String, Double> standardValues = new LinkedHashMap<>();// 기준값
    private static boolean is4; //질산칼슘 4수염이면 true 10수염이면 false
    private static String macroUnit;

    private boolean isConsidered; //원수 고려 여부
    public void setMainController(MainController mainController) {

        this.mainController = mainController;
        this.tableData = mainController.getToolbarController().tableData;
    }

    public void setIs4(boolean is4) {
        this.is4 = is4;
    }

    public void setMacroUnit(String macroUnit) {
        this.macroUnit = macroUnit;
    }

    public void setConsidered(boolean considered) {
        isConsidered = considered;
    }

    public void setRequestHistoryInfo(RequestHistoryInfo requestHistoryInfo) {
        this.requestHistoryInfo = requestHistoryInfo;
    }
    public void setUserFertilization(Map<String, Double> userFertilization) {
        this.userFertilization = userFertilization;
    }

    public void setConsideredValues(Map<String, Double> consideredValues) {
        this.consideredValues = consideredValues;
    }

    public void setStandardValues(Map<String, Double> standardValues) {
        this.standardValues = standardValues;
    }

    String[] columnTitles = {"(100 배액 기준)\n   비료염 종류", "분자식", "시비량", "단위", "NO3N", "NH4N", "H2PO4", "K", "Ca", "Mg", "SO4S", "산도(pH)", "농도(EC)", "중탄산(HCO3)"};
    String[] rowTitles = {"설정농도(mM)", "시비농도(mM)", "질산칼륨", "질산칼슘(4수염)", "질산칼슘(10수염)", "제1인산암모늄", "제1인산칼륨", "황산마그네슘", "질산마그네슘", "질산암모늄(초안)",
            "황산암모늄(유안)", "염화암모늄(염안)", "황산칼륨", "염화칼륨", "염화칼슘", "중탄산칼륨", "수산화칼륨", "합계"};

    // 분자식에 들어갈 값
    String[] formula = {"", "", "KNO3", "Ca(NO3)2·4H2O", "5[Ca(NO3)2·2H2O]NH4NO3", "NH4H2PO4", "KH2PO4", "MgSO4·7H2O", "Mg(NO3)2·6H2O",
            "NH4NO3", "(NH4)2SO4", "NH4CI", "K2SO4", "KCI", "CaCI2·2H2O", "KHCO3", "KOH", ""};


    @FXML
    public void initialize() {

        initTableView();  // 항상 tableView를 초기화
//        if (tableData != null) {
//            showResultTable(tableData.getMacroSettings());
//        }

//        Map<String, String> macroSettings = tableData.getMacroSettings();
//        if (macroSettings == null) {
//            initTableView();
//        } else {
//            showResultTable(macroSettings);
//        }
    }

    public void calculateAndDisplayResults() {

        System.out.println("Calculating and displaying results");
        //data.clear();

        // 계산 전략 설정
        CalculationStrategy strategy = new MacroCalculationStrategy(macroUnit, is4, isConsidered, consideredValues, userFertilization, requestHistoryInfo);
        CalculatorClient client = new CalculatorClient(strategy);

        // 계산 수행
        Map<String, Map<String, Double>> calculatedValues = client.calculate();

        for (String s : calculatedValues.keySet()) {
            System.out.println(s + " : "+calculatedValues.get(s));
        }

        // tableView에 데이터 표시
        displayResults(calculatedValues);
    }
//    private void displayResults(Map<String, Map<String, Double>> calculatedValues) {
//
//        Platform.runLater(() -> {
//            for (Map.Entry<String, Map<String, Double>> entry : calculatedValues.entrySet()) {
//                String chemical = entry.getKey();
//                Map<String, Double> values = entry.getValue();
//
//                int rowIndex = findRowIndexForChemical(chemical);
//                if (rowIndex == -1) continue;
//                System.out.println("Chemical: " + chemical + ", Row index: " + rowIndex);
//
//                ObservableList<SimpleStringProperty> row = data.get(rowIndex);
//                updateRowWithValues(row, values);
//
//            }
//            tableView.refresh(); //모든 업데이트 후 -> 테이블 뷰 강제 새로고침
//        });
//    }

    private void displayResults(Map<String, Map<String, Double>> calculatedValues) {
        Platform.runLater(() -> {
            System.out.println("Displaying results");
            for (Map.Entry<String, Map<String, Double>> entry : calculatedValues.entrySet()) {
                String chemical = entry.getKey();
                Map<String, Double> values = entry.getValue();

                int rowIndex = findRowIndexForChemical(chemical);
                System.out.println("Chemical: " + chemical + ", Row index: " + rowIndex);
                if (rowIndex == -1) continue;

                if (rowIndex < data.size()) {
                    ObservableList<SimpleStringProperty> row = data.get(rowIndex);
                    updateRowWithValues(row, values, rowIndex);
                    System.out.println("row = " + row);
                } else {
                    System.out.println("Row index " + rowIndex + " is out of bounds for data size " + data.size());
                }
            }
            tableView.refresh(); // 테이블 뷰 강제 새로고침
       });
    }

    private int findRowIndexForChemical(String chemical) {
        return Arrays.asList(formula).indexOf(chemical);
    }


//    private void updateRowWithValues(ObservableList<SimpleStringProperty> row, Map<String, Double> values) {
//            for (Map.Entry<String, Double> valueEntry : values.entrySet()) {
//                int colIndex = findColumnIndexForComponent(valueEntry.getKey());
//                System.out.println("Component: " + valueEntry.getKey() + ", Column index: " + colIndex + ", Value: " + valueEntry.getValue());
//                if (colIndex != -1) {
//                    SimpleStringProperty simpleStringProperty = new SimpleStringProperty(String.format("%.2f", valueEntry.getValue()));
//                    row.set(colIndex, simpleStringProperty);
//                }
//            }
//    }

    private void updateRowWithValues(ObservableList<SimpleStringProperty> row, Map<String, Double> values, int rowIndex) {
        for (Map.Entry<String, Double> valueEntry : values.entrySet()) {
            int colIndex = findColumnIndexForComponent(valueEntry.getKey());
            System.out.println("Component: " + valueEntry.getKey() + ", Column index: " + colIndex + ", Value: " + valueEntry.getValue());
            if (colIndex != -1) {
                // SimpleStringProperty 객체 생성
                SimpleStringProperty simpleStringProperty = new SimpleStringProperty(String.format("%.2f", valueEntry.getValue()));

                // row 리스트의 크기가 colIndex보다 작으면 빈 값으로 채움
                while (row.size() <= colIndex) {
                    row.add(new SimpleStringProperty(""));
                }

                // row 리스트의 colIndex 위치에 값을 설정
                row.set(colIndex, simpleStringProperty);
            }
        }
        // formula 값을 설정
        if (row.size() > 1) {
            row.set(1, new SimpleStringProperty(formula[rowIndex]));
        } else {
            row.add(1, new SimpleStringProperty(formula[rowIndex]));
        }
    }

    private int findColumnIndexForComponent(String component) {
        return Arrays.asList(columnTitles).indexOf(component);
    }
    private void initTableView() {

        tableView.getColumns().clear();  // 기존 컬럼 초기화
        data.clear();  // 기존 데이터 초기화

        // 컬럼 초기화
        for (int i = 0; i < columnTitles.length; i++) {
            final int columnIndex = i;
            TableColumn<ObservableList<SimpleStringProperty>, String> column = new TableColumn<>(columnTitles[i]);
            column.setCellValueFactory(cellData -> cellData.getValue().get(columnIndex));
            tableView.getColumns().add(column);
        }

        // 데이터 행 추가
        for (int rowIndex = 0; rowIndex < rowTitles.length; rowIndex++) {
            ObservableList<SimpleStringProperty> row = FXCollections.observableArrayList();
            for (int colIndex = 0; colIndex < columnTitles.length; colIndex++) {
                if (colIndex == 0) {
                    row.add(new SimpleStringProperty(rowTitles[rowIndex]));
                } else if (colIndex == 1) {
                    row.add(new SimpleStringProperty(formula[rowIndex]));
                } else {
                    row.add(new SimpleStringProperty(""));
                }
            }
            data.add(row);
        }

        System.out.println("Table data initialized: " + data);

        tableView.setItems(data); // 데이터 설정

//        tableView.getColumns().clear();  // 기존 컬럼 초기화
//        data.clear();// 기존 데이터 초기화
//
//        // 컬럼 초기화
//        for (int i = 0; i < columnTitles.length; i++) {
//            final int columnIndex = i;
//            TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnTitles[i]);
//            column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(columnIndex)));
//            tableView.getColumns().add(column);
//        }
//
//        // 데이터 행 추가
//        for (int rowIndex = 0; rowIndex < rowTitles.length; rowIndex++) {
//            ObservableList<String> row = FXCollections.observableArrayList();
//            row.add(rowTitles[rowIndex]);  // 비료염 종류
//            row.add(formula[rowIndex]);    // 분자식
//            for (int colIndex = 2; colIndex < columnTitles.length; colIndex++) {
//                row.add("");  // 초기에는 빈 문자열로 설정
//            }
//            data.add(row);
//        }
//
//        tableView.setItems(data); // 데이터 설정

//        for (int i = 0; i < columnTitles.length; i++) {
//            final int columnIndex = i;
//            TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnTitles[i]);
//
//            column.setCellValueFactory(cellData -> {
//                ObservableValue<String> cellValue = new SimpleStringProperty(cellData.getValue().get(columnIndex));
//                return cellValue;
//            });
//            column.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
//            column.setOnEditCommit(event -> {
//                ObservableList<String> row = event.getRowValue();
//                row.set(columnIndex, event.getNewValue());
//            });
//
//            tableView.getColumns().add(column);
//        }
//
//        for (int i = 0; i < rowTitles.length; i++) {
//            ObservableList<String> row = FXCollections.observableArrayList();
//            row.add(rowTitles[i]);
//            row.add(formula[i]);
//            for (int j = 2; j < columnTitles.length; j++) {
//                row.add(""); // 빈 문자열로 초기화
//            }
//            data.add(row);
//        }
//
//        tableView.setItems(data);
    }


    //TODO -> 이거 뭐하는 함수인지 좀 더 까봐야겠음
    private void showResultTable(Map<String, String> macroSettings) {
        initTableView();

//        Integer settingElementUnit = macroSettings.get("설정 다량원소 단위").intValue();
//        Integer calciumNitrateFertilizer = macroSettings.get("질산칼슘 비료").intValue();
//
//        // 특정 행에 값 설정
//        int rowIndex = 4; // 5번째 행
//        data.get(rowIndex).set(6, String.valueOf(settingElementUnit)); // 설정 다량원소 단위 열
//        data.get(rowIndex).set(7, String.valueOf(calciumNitrateFertilizer)); // 질산칼슘 비료 열


        tableView.setItems(data);
    }

    @FXML
    public void prevButton(ActionEvent event) {
        if (mainController != null) {
            mainController.moveToMacroTab();
        } else {
            System.out.println("MainController is not set in MacroResultController");
        }
    }

    @FXML
    public void nextButton(ActionEvent event) {
        TabPane tabPane = findTabPane(event);
        if (tabPane != null) {
            int currentIndex = tabPane.getSelectionModel().getSelectedIndex();
            tabPane.getSelectionModel().select(currentIndex + 1);
        }
    }


    private TabPane findTabPane(ActionEvent event) {
        Node source = (Node) event.getSource();
        if (source != null) {
            Node parent = source.getParent();
            while (parent != null && !(parent instanceof TabPane)) {
                parent = parent.getParent();
            }
            return (TabPane) parent;
        }
        return null;
    }

}
