package org.main.culturesolutioncalculation;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;
import org.main.culturesolutioncalculation.service.calculator.CalculationStrategy;
import org.main.culturesolutioncalculation.service.calculator.CalculatorClient;
import org.main.culturesolutioncalculation.service.calculator.MacroCalculationStrategy;
import org.main.culturesolutioncalculation.service.calculator.MicroCalculationStrategy;

import java.util.*;

public class MicroResultController {
    @FXML private TableView<Map<String, SimpleStringProperty>> tableView;
    private ObservableList<Map<String, SimpleStringProperty>> data = FXCollections.observableArrayList();

    private static Map<String, Double> userFertilization = new LinkedHashMap<>(); // 처방 농도

    private static Map<String, Double> consideredValues = new LinkedHashMap<>(); //고려 원수 값

    private static Map<String, Double> standardValues = new LinkedHashMap<>();// 기준값
    private static String microUnit;

    private boolean isConsidered; //원수 고려 여부
    private static List<String> userMicroNutrients = new LinkedList<>();

    private static MainController mainController;

    private TableData tableData;

    private static RequestHistoryInfo requestHistoryInfo;
    private static CalculationStrategy strategy;


    public void setMainController(MainController mainController) {

        this.mainController = mainController;
        this.tableData = mainController.getToolbarController().tableData;
    }

    public void setMicroUnit(String microUnit) {
        this.microUnit = microUnit;
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

    public void setUserMicroNutrients(List<String> userMicroNutrients) {
        this.userMicroNutrients = userMicroNutrients;
    }

    public void setConsideredValues(Map<String, Double> consideredValues) {
        this.consideredValues = consideredValues;
    }

    public void setStandardValues(Map<String, Double> standardValues) {
        this.standardValues = standardValues;
    }


    String[] columnTitles = {"(100 배액 기준)\n   비료염 종류", "분자식", "시비량", "단위", "Fe", "Cu", "B", "Mn", "Zn", "Mo",  "산도(pH)", "농도(EC)", "중탄산(HCO3)"};
    String[] rowTitles =  {"설정농도(mM)", "시비농도(mM)", "킬레이트 철", "붕산", "황산망간", "황산아연", "황산구리", "몰리브덴산나트륨", "몰리브덴산암모늄", "합계"};

    // 분자식에 들어갈 값
    String[] formula = {"", "", "Fe-EDTA", "H3BO3", "MnSO4·H2O", "ZnSO4·7H2O", "CuSO4·5H2O", "Na2MoO4·2H2O", "(NH4)6Mo7O24·4H2O", ""};


    @FXML
    public void initialize() {
        initTableView();
        populateInitialData();
    }

    private void initTableView() {
        for (String title : columnTitles) {
            TableColumn<Map<String, SimpleStringProperty>, String> column = new TableColumn<>(title);
            column.setCellValueFactory(cellData -> cellData.getValue().get(title));

            // 열 너비 설정
            if (title.equals("Fe") || title.equals("Cu") || title.equals("B") || title.equals("Mn") || title.equals("Zn") || title.equals("Mo")) {
                column.setMinWidth(30);  // 최소 너비 설정
                column.setPrefWidth(50); // 선호 너비 설정
                column.setMaxWidth(100);  // 최대 너비 설정
            }

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

        strategy = new MicroCalculationStrategy(requestHistoryInfo ,microUnit, isConsidered, userMicroNutrients, consideredValues, userFertilization );
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
            for (Map.Entry<String, Double> valueEntry : consideredValues.entrySet()) {
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

            //합계 설정
            Map<String, SimpleStringProperty> totalValueProperty = data.get(Arrays.asList(rowTitles).indexOf("합계"));
            String[] components = {"Fe", "Cu", "B", "Mn", "Zn", "Mo",  "산도(pH)", "농도(EC)", "중탄산(HCO3)"};
            for (String component : components) {
                double sum = 0.0;
                for(int i = 2; i < data.size()-1; i++){ //"설정농도(mM)"와 "시비농도(mM)" 제외하고 합계 계산
                    Map<String, SimpleStringProperty> row = data.get(i);
                    String value = row.get(component).get();
                    if(!value.isEmpty()) sum += Double.parseDouble(value);
                }
                String formattedValue = String.format("%.2f", sum);
                if(totalValueProperty.containsKey(component)) totalValueProperty.get(component).set(formattedValue);
            }


            tableView.refresh();
        });
    }

    private void displayResults(Map<String, Map<String, Double>> calculatedValues) {
        for (Map.Entry<String, Map<String, Double>> entry : calculatedValues.entrySet()) {
            String chemical = entry.getKey();
            Map<String, Double> values = entry.getValue();
            int rowIndex = findRowIndexForChemical(chemical);

            if (rowIndex == -1) {
                continue;
            }

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
        mainController.setMicroDataToPrintTab(strategy);
        mainController.movePrintTab();

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