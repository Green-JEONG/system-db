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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class MacroResultController {
    @FXML private TableView<Map<String, SimpleStringProperty>> tableView;
    private ObservableList<Map<String, SimpleStringProperty>> data = FXCollections.observableArrayList();

    private static Map<String, Double> userFertilization = new LinkedHashMap<>(); // 처방 농도

    private static Map<String, Double> consideredValues = new LinkedHashMap<>(); //고려 원수 값

    private static Map<String, Double> standardValues = new LinkedHashMap<>();// 기준값
    private static boolean is4; //질산칼슘 4수염이면 true 10수염이면 false
    private static String macroUnit;

    private boolean isConsidered; //원수 고려 여부

    private static MainController mainController;

    private TableData tableData;

    private static RequestHistoryInfo requestHistoryInfo;

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
        System.out.println("userFertilization = " + userFertilization);
    }

    public void setConsideredValues(Map<String, Double> consideredValues) {
        this.consideredValues = consideredValues;
    }

    public void setStandardValues(Map<String, Double> standardValues) {
        this.standardValues = standardValues;
    }


    private String[] columnTitles = {"(100 배액 기준)\n   비료염 종류", "분자식", "시비량", "단위", "NO3N", "NH4N", "H2PO4", "K", "Ca", "Mg", "SO4S", "산도(pH)", "농도(EC)", "중탄산(HCO3)"};
    private String[] rowTitles = {"설정농도(mM)", "시비농도(mM)", "질산칼륨", "질산칼슘(4수염)", "질산칼슘(10수염)", "제1인산암모늄", "제1인산칼륨", "황산마그네슘", "질산마그네슘", "질산암모늄(초안)",
            "황산암모늄(유안)", "염화암모늄(염안)", "황산칼륨", "염화칼륨", "염화칼슘", "중탄산칼륨", "수산화칼륨", "합계"};
    private String[] formula = {"", "", "KNO3", "Ca(NO3)2·4H2O", "5[Ca(NO3)2·2H2O]NH4NO3", "NH4H2PO4", "KH2PO4", "MgSO4·7H2O", "Mg(NO3)2·6H2O",
            "NH4NO3", "(NH4)2SO4", "NH4CI", "K2SO4", "KCI", "CaCI2·2H2O", "KHCO3", "KOH", ""};

    @FXML
    public void initialize() {
        initTableView();
        populateInitialData();
    }

    private void initTableView() {
        for (String title : columnTitles) {
            TableColumn<Map<String, SimpleStringProperty>, String> column = new TableColumn<>(title);
            column.setCellValueFactory(cellData -> cellData.getValue().get(title));
            tableView.getColumns().add(column);
        }
        tableView.setItems(data);
    }

    private void populateInitialData() {
        for (int i = 0; i < rowTitles.length; i++) {
            Map<String, SimpleStringProperty> row = new LinkedHashMap<>();
            row.put(columnTitles[0], new SimpleStringProperty(rowTitles[i]));
            row.put(columnTitles[1], new SimpleStringProperty(formula[i]));
            for (int j = 2; j < columnTitles.length; j++) {
                row.put(columnTitles[j], new SimpleStringProperty(""));
            }
            data.add(row);
        }
    }

    @FXML
    public void calculateAndDisplayResults() {

        System.out.println("Calculating and displaying results");

//        int rowIndex = 3;
//        Map<String, SimpleStringProperty> row = data.get(rowIndex);
//        row.get("NO3N").set("5.50");
//        tableView.refresh();


        CalculationStrategy strategy = new MacroCalculationStrategy(macroUnit, is4, isConsidered, consideredValues, userFertilization, new RequestHistoryInfo());
        CalculatorClient client = new CalculatorClient(strategy);
        Map<String, Map<String, Double>> calculatedValues = client.calculate();

        // UI 업데이트만 Platform.runLater 내에서 실행
        Platform.runLater(() -> {

            // 기준 농도 설정
            Map<String, SimpleStringProperty> standardProperty = data.get(Arrays.asList(rowTitles).indexOf("설정농도(mM)"));
            for (Map.Entry<String, Double> valueEntry : standardValues.entrySet()) {
                Double value = valueEntry.getValue();
                String component = valueEntry.getKey();
                String formattedValue = String.format("%.2f", value);

                if (standardProperty.containsKey(component)) {
                    standardProperty.get(component).set(formattedValue);
                }
                else if(component.contains("pH") && standardProperty.containsKey("산도(pH)")){
                    standardProperty.get("산도(pH)").set(formattedValue);
                }
                else if(component.contains("EC") && standardProperty.containsKey("농도(EC)")){
                    standardProperty.get("농도(EC)").set(formattedValue);
                }
                else if(component.contains("HCO3") && standardProperty.containsKey("중탄산(HCO3)")){
                    standardProperty.get("중탄산(HCO3)").set(formattedValue);
                }
            }

            //시비 농도 설정 (fertilization)
            Map<String, SimpleStringProperty> fertilizationProperty = data.get(Arrays.asList(rowTitles).indexOf("시비농도(mM)"));
            for (Map.Entry<String, Double> valueEntry : userFertilization.entrySet()) {
                Double value = valueEntry.getValue();
                String component = valueEntry.getKey();
                String formattedValue = String.format("%.2f", value);

                if (fertilizationProperty.containsKey(component)) {
                    fertilizationProperty.get(component).set(formattedValue);
                }
                else if(component.contains("pH") && fertilizationProperty.containsKey("산도(pH)")){
                    fertilizationProperty.get("산도(pH)").set(formattedValue);
                }
                else if(component.contains("EC") && fertilizationProperty.containsKey("농도(EC)")){
                    fertilizationProperty.get("농도(EC)").set(formattedValue);
                }
                else if(component.contains("HCO3") && fertilizationProperty.containsKey("중탄산(HCO3)")){
                    fertilizationProperty.get("중탄산(HCO3)").set(formattedValue);
                }
            }






            System.out.println("Displaying results: " + calculatedValues);
            displayResults(calculatedValues);
            tableView.refresh();
        });
    }

    private void displayResults(Map<String, Map<String, Double>> calculatedValues) {
        for (Map.Entry<String, Map<String, Double>> entry : calculatedValues.entrySet()) {
            String chemical = entry.getKey();
            Map<String, Double> values = entry.getValue();
            int rowIndex = findRowIndexForChemical(chemical);

            if (rowIndex == -1) continue;

            Map<String, SimpleStringProperty> row = data.get(rowIndex);
            updateRowWithValues(row, values);
        }
    }

    private int findRowIndexForChemical(String chemical) {
        return Arrays.asList(formula).indexOf(chemical);
    }

    private void updateRowWithValues(Map<String, SimpleStringProperty> row, Map<String, Double> values) {
        for (Map.Entry<String, Double> valueEntry : values.entrySet()) {
            String component = valueEntry.getKey();
            Double value = valueEntry.getValue();
            String formattedValue = String.format("%.2f", value);

            if (row.containsKey(component)) {
                row.get(component).set(formattedValue);
                System.out.println("component = " + component + " & formattedValue : "+formattedValue);
            }
        }
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