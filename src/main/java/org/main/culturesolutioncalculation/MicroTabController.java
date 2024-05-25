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
import org.main.culturesolutioncalculation.model.NutrientSolution;
import org.main.culturesolutioncalculation.service.CSVDataReader;
import org.main.culturesolutioncalculation.service.database.MediumService;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class MicroTabController {
    UserInfo userInfo = MainController.getUserInfo();

    RequestHistoryInfo requestHistoryInfo = MainController.getRequestHistoryInfo();

    SettingInfo settingInfo = MainController.getSettingInfo();

    private MediumService mediumService;

    @FXML
    private Tab microTab;
    @FXML
    private TableView<ObservableList<String>> tableView;

    private ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

    public MicroTabController(){
        mediumService = new MediumService();
    }

    @FXML
    private void initialize() {
        initTableView();
    }

    public void initTableView() {
        // tableView 초기화
        String[] columnTitles = {"미량원소", "Fe", "Cu", "B", "Mn", "Zn", "Mo", "산도(pH)", "농도(EC)", "중탄산(HCO3)"};
        String[] rowTitles =  {"기준량", "원수성분", "처방농도"};

        // 기준량 행에 들어갈 값
        String[] values = new String[6];
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
        String[] columnTitles = {"미량원소", "Fe", "Cu", "B", "Mn", "Zn", "Mo", "산도(pH)", "농도(EC)", "중탄산(HCO3)"};
        String[] rowTitles = {"기준량", "원수성분", "처방농도"};

        data.clear();

        String[] values = getStandardValues(requestHistoryInfo.getMediumTypeId(), requestHistoryInfo.getSelectedCropName());

        Map<String, String> totalSetting = settingInfo.getTotalSetting();
        boolean isConsiderFalse = "X".equals(totalSetting.get("원수 고려 유무"));

        for (int i = 0; i < rowTitles.length; i++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(rowTitles[i]);

            if (i == 0) { // 기준량 행
                for (String value : values) {
                    row.add(value);
                }
            } else if (i == 1) { // 원수성분 행
                for (int j = 1; j < columnTitles.length; j++) {
                    if (isConsiderFalse) {
                        row.add("0");
                    } else {
                        String key = columnTitles[j];
                        row.add(totalSetting.containsKey(key) ? totalSetting.get(key) : "0");
                    }
                }
            } else { // 처방농도 행
                for (int j = 1; j < columnTitles.length; j++) {
                    row.add("");
                }
            }
            data.add(row);
        }

        tableView.setItems(data);

        // 원수 고려 유무에 따라 컬럼 편집 가능 여부 설정
        setCellEditability(isConsiderFalse);
    }

    private void setCellEditability(boolean isConsiderFalse) {
        for (TableColumn<ObservableList<String>, ?> column : tableView.getColumns()) {
            if (column.getText().equals("원수성분")) {
                column.setEditable(!isConsiderFalse);
            } else {
                for (ObservableList<String> row : data) {
                    if (!isConsiderFalse && "0".equals(row.get(tableView.getColumns().indexOf(column)))) {
                        column.setEditable(false);
                    }
                }
            }
        }
    }

    @FXML
    public void refreshButton(ActionEvent actionEvent) {
        updateTableData();
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

    private CropNutrientStandard findCropNutrient(ArrayList<CropNutrientStandard> cropList, String crop) {
        if (cropList == null) {
            return null;
        }

        for (CropNutrientStandard cropNutrientStandard : cropList) {
            if (cropNutrientStandard.getCropName().equals(crop)) {
                return cropNutrientStandard;
            }
        }
        return null; // 선택한 작물에 해당하는 정보를 찾을 수 없는 경우 null 반환
    }

    @FXML
    public void prevButton(ActionEvent actionEvent) {
        TabPane tabPane = microTab.getTabPane();
        int currentIndex = tabPane.getTabs().indexOf(microTab);
        tabPane.getSelectionModel().select(currentIndex - 1);  // 이전 탭으로 이동
    }

    public void saveInput(ActionEvent event) {
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