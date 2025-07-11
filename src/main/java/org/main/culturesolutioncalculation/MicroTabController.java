package org.main.culturesolutioncalculation;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.converter.DefaultStringConverter;
import org.main.culturesolutioncalculation.model.CropNutrientStandard;
import org.main.culturesolutioncalculation.service.database.MediumService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MicroTabController {

    private static MainController mainController;

    private MediumService mediumService;

    private static RequestHistoryInfo requestHistoryInfo;// = MainController.getRequestHistoryInfo();

    SettingInfo settingInfo = MainController.getSettingInfo();

    private Map<String, Double> userFertilization = new LinkedHashMap<>(); // 처방 농도

    private Map<String, Double> consideredValues = new LinkedHashMap<>(); //고려 원수 값

    private Map<String, Double> standardValues = new LinkedHashMap<>();// 기준값

    List<String> userMicroNutrients = new LinkedList<>(); //유저가 선택했던 미량원소 비료 리스트

    private String microUnit; //다량원소 단위
    private boolean isConsidered; //원수 고려 여부


    @FXML
    private Tab microTab;
    @FXML
    private TableView<ObservableList<String>> tableView;

    private ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

    public MicroTabController(){
        mediumService = new MediumService();
    }
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }


    String[] columnTitles = {"미량원소", "Fe", "Cu", "B", "Mn", "Zn", "Mo", "산도(pH)", "농도(EC)", "중탄산(HCO3)"};
    String[] rowTitles =  {"기준량", "원수성분", "처방농도"};

    @FXML
    private void initialize() {
        requestHistoryInfo = MainController.getRequestHistoryInfo();
        initTableView();
    }
    public void initTableView() {
        // tableView 초기화
        data.clear();
        tableView.getColumns().clear();


        for (int i = 0; i < columnTitles.length; i++) {
            final int columnIndex = i;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnTitles[i]);

            column.setCellValueFactory(cellData -> {
                ObservableValue<String> cellValue = new SimpleStringProperty(cellData.getValue().get(columnIndex));
                return cellValue;
            });
            column.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
            column.setOnEditCommit(event -> {
                ObservableList<String> row = event.getRowValue();
                row.set(columnIndex, event.getNewValue());
            });

            tableView.getColumns().add(column);
        }

        //표의 행 데이터 초기화
        updateTableData();

        tableView.setEditable(true);
        tableView.setItems(data);
    }


    private void updateTableData() {
        data.clear();

        String[] values = getStandardValues(requestHistoryInfo.getCultureMediumId(), requestHistoryInfo.getSelectedCropName());
        Map<String, String> totalSetting = settingInfo.getTotalSetting();
        userMicroNutrients.add(totalSetting.get("몰리브뎀 비료"));
        microUnit = totalSetting.get("설정 미량원소 단위");
        System.out.println("microTab : microUnit = " + microUnit);
        boolean isConsiderFalse = "X".equals(totalSetting.get("원수 고려 유무"));

        Set<String> checkedConsideredOptionNames = new HashSet<>();

        //원수 고려한다면 -> 원수 고려 옵션들의 이름 필터링
        if(!isConsiderFalse) {
            // 원수 고려 옵션들의 이름 필터링
            checkedConsideredOptionNames = totalSetting.entrySet().stream()
                    .filter(entry -> entry.getValue().equals("1"))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
        }


        for (int i = 0; i < rowTitles.length; i++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(rowTitles[i]);

            if (i == 0) { // 기준량 행
                for (String value : values) {
                    row.add(value);
                }
            } else if (i == 1) { // 원수성분 행
                ObservableList<String> consideredRow = FXCollections.observableArrayList();
                for (String title : columnTitles) {
                    if(title.equals("미량원소")) continue;
                    boolean isEditable = checkedConsideredOptionNames.stream().anyMatch(name -> name.contains(title));
                    if(isEditable) {
                        consideredRow.add("");
                    }
                    else {
                        consideredRow.add("0");
                    }
                }
                row.addAll(consideredRow);
            } else { // 처방농도 행
                for (int j = 1; j < columnTitles.length; j++) {
                    row.add("");
                }
            }
            data.add(row);
        }

        tableView.setItems(data);

        // 원수 고려 유무에 따라 컬럼 편집 가능 여부 설정
        setCellEditability(isConsiderFalse, checkedConsideredOptionNames);
    }

    private void setCellEditability(boolean isConsiderFalse, Set<String> checkedConsideredOptionNames) {
        if (!isConsiderFalse) {

            tableView.getColumns().forEach(column -> {
                boolean isEditable = checkedConsideredOptionNames.stream().anyMatch(name -> column.getText().contains(name));
                column.setEditable(!isConsiderFalse && isEditable);
            });

        } else {
            tableView.getColumns().forEach(column -> column.setEditable(false));  // 모든 컬럼 편집 불가능
        }
    }

    @FXML
    public void refreshButton(ActionEvent actionEvent) {
        updateTableData();
        calculatePrescriptionDose();
    }

    @FXML
    private void calculatePrescriptionDose() {
        // "기준량" 행과 "원수성분" 행의 인덱스를 확인
        int baselineIndex = rowTitles.length > 0 ? 0 : -1;
        int rawWaterIndex = rowTitles.length > 1 ? 1 : -1;
        int prescriptionIndex = rowTitles.length > 2 ? 2 : -1;

        if (baselineIndex == -1 || rawWaterIndex == -1 || prescriptionIndex == -1) {
            System.out.println("Row indices are not set correctly.");
            return;
        }

        ObservableList<String> baselineRow = data.get(baselineIndex);
        ObservableList<String> rawWaterRow = data.get(rawWaterIndex);
        ObservableList<String> prescriptionRow = data.get(prescriptionIndex);

        userFertilization.clear(); // 처방농도 맵 초기화


        // 처방 농도 계산
        for (int i = 1; i < columnTitles.length; i++) {  // 인덱스 0은 행 제목이므로 1부터 시작
            try {
                double baselineValue = Double.parseDouble(baselineRow.get(i));
                double rawWaterValue = rawWaterRow.get(i).isEmpty() ? 0.0 : Double.parseDouble(rawWaterRow.get(i));
                double prescriptionValue = baselineValue - rawWaterValue;
                prescriptionRow.set(i, String.format("%.2f", prescriptionValue));

                if(columnTitles[i].equals("산도(pH)")){
                    standardValues.put("pH",baselineValue);
                    consideredValues.put("pH", rawWaterValue);
                    userFertilization.put("pH", prescriptionValue);
                    userMicroNutrients.add("pH");
                }
                else if(columnTitles[i].equals("농도(EC)")){
                    standardValues.put("EC",baselineValue);
                    consideredValues.put("EC", rawWaterValue);
                    userFertilization.put("EC", prescriptionValue);
                    userMicroNutrients.add("EC");
                }
                else if(columnTitles[i].equals("중탄산(HCO3)")){
                    standardValues.put("HCO3",baselineValue);
                    consideredValues.put("HCO3", rawWaterValue);
                    userFertilization.put("HCO3", prescriptionValue);
                    userMicroNutrients.add("HCO3");
                }
                else {
                    standardValues.put(columnTitles[i], baselineValue);
                    consideredValues.put(columnTitles[i], rawWaterValue);
                    userFertilization.put(columnTitles[i], prescriptionValue);
                }


            } catch (NumberFormatException e) {
                System.out.println("Error parsing numbers: " + e.getMessage());
                prescriptionRow.set(i, "Err");
            }
        }

        tableView.refresh();  // TableView 갱신
    }

    private String[] getStandardValues(int mediumCultureId, String crop) {
        String[] values = new String[9];

        // 선택한 배양액 아이디에 해당하는 NutrientSolution 객체 가져오기
        Optional<CropNutrientStandard> cropDataOP = mediumService.getCropData(mediumCultureId);

        // 안전하게 Optional 처리
        if (!cropDataOP.isPresent()) {
            System.out.println("Selected crop information not found.");
            return values;
        }

        CropNutrientStandard selectedCropNutrient = cropDataOP.get();

        // CropNutrientStandard 객체에서 각 값을 배열에 저장
        values[0] = String.valueOf(selectedCropNutrient.getFe());
        values[1] = String.valueOf(selectedCropNutrient.getCu());
        values[2] = String.valueOf(selectedCropNutrient.getB());
        values[3] = String.valueOf(selectedCropNutrient.getMn());
        values[4] = String.valueOf(selectedCropNutrient.getZn());
        values[5] = String.valueOf(selectedCropNutrient.getMo());
        values[6] = String.valueOf(selectedCropNutrient.getPH());
        values[7] = String.valueOf(selectedCropNutrient.getEC());
        values[8] = String.valueOf(selectedCropNutrient.getHCO3());

        return values;
    }

    @FXML
    public void prevButton(ActionEvent actionEvent) {
        switchScene(actionEvent);
    }

    @FXML
    public void saveInput(ActionEvent event) throws IOException {
        mainController.setMicroResultTabWithValues(userFertilization, consideredValues, standardValues, requestHistoryInfo, microUnit, isConsidered, userMicroNutrients);
        switchScene(event);
        // 테이블 저장
    }

    private void switchScene(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MicroResult.fxml"));
            Parent root = loader.load();

            TabPane tabPane = findTabPane(event);
            // 현재 선택된 탭을 새로운 내용으로 대체
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            selectedTab.setContent(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TabPane findTabPane(ActionEvent event) {
        Node source = (Node) event.getSource();
        Scene scene = source.getScene();
        if (scene != null) {
            VBox root = (VBox) scene.getRoot(); // Main.fxml의 root인 VBox를 찾음
            for (Node node : root.getChildren()) {
                if (node instanceof TabPane) {
                    return (TabPane) node;
                }
            }
        }
        return null;
    }

}