package org.main.culturesolutioncalculation;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Arrays;

public class TypeTabController {

    private TypeData data;

    @FXML
    private Tab typeTab;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private TableView<String[]> tableView;

    @FXML
    private ListView<String> listView;

    private UserInfo userInfo = MainController.getUserInfo();

    private String selectedCropName = "";

    public TypeTabController() throws IOException {
        data = new TypeData();
    }

    public void initialize() {
        listView.getItems().addAll("네덜란드 배양액", "야마자키 배양액");

        // 네덜란드 배양액을 기본 선택으로 설정
        listView.getSelectionModel().select("네덜란드 배양액");
        updateComboBox("네덜란드 배양액");
        updateTableView("네덜란드 배양액");

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateComboBox(newValue);
            updateTableView(newValue);

        });

        // TableView 리스너 추가
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("Selected item: " + newSelection[1]); // CropName 출력
                System.out.println("Selected item ID: " + newSelection[0]); // ID 출력 (culture_medium에 있는 튜플 id와 동일)
                selectedCropName = newSelection[1];
                System.out.println("Selected first item: " + selectedCropName);
                comboBox.setValue(selectedCropName);
            }
        });

        // ComboBox 리스너 추가
        comboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                selectedCropName = newValue;
                tableView.getItems().stream()
                        .filter(item -> item[1].equals(newValue))  // 첫 번째 값은 CropName
                        .findFirst()
                        .ifPresent(item -> tableView.getSelectionModel().select(item));
            }
        });

    }

    @FXML
    public void prevButton() {
        TabPane tabPane = typeTab.getTabPane();
        int currentIndex = tabPane.getTabs().indexOf(typeTab);
        if (currentIndex > 0) {  // 첫 번째 탭이 아닌 경우에만
            tabPane.getSelectionModel().select(currentIndex - 1);  // 이전 탭으로 이동
        }
    }

    @FXML
    public void saveType() {
        TabPane tabPane = typeTab.getTabPane();
        int currentIndex = tabPane.getTabs().indexOf(typeTab);

        if(userInfo != null) {
            userInfo.setSelectedCulture(listView.getSelectionModel().getSelectedItem());
            String selectedComboBoxValue = comboBox.getValue();
            String[] selectedItem = tableView.getSelectionModel().getSelectedItem();

            if (selectedComboBoxValue != null && !selectedComboBoxValue.isEmpty()) {
                userInfo.setSelectedCrop(selectedComboBoxValue);
            } else if (selectedCropName != null && !selectedCropName.isEmpty()) {
                userInfo.setSelectedCrop(selectedCropName);
            } else {
                System.err.println("ComboBox 값과 selectedCropName 값이 모두 설정되지 않았습니다.");
            }

            //선택한 재배 작물 id userInfo에 주입
            if (selectedItem != null) {
                int selectedItemId = Integer.parseInt(selectedItem[0]);
                userInfo.setCultureMediumId(selectedItemId); //(culture_medium에 있는 튜플 id와 동일)
            } else {
                System.err.println("TableView에서 선택된 항목이 없습니다.");
            }

            tabPane.getSelectionModel().select(currentIndex + 1);
        } else {
            System.err.println("UserInfo 객체가 초기화되지 않았습니다.");
        }
    }

    private void updateComboBox(String newValue) {
        //ObservableList<String> cropList = TypeData.getCropList(newValue);
        ObservableList<String> cropList = data.getCropList(newValue);

        comboBox.setItems(cropList);

    }

    private void updateTableView(String newValue) {
        ObservableList<String[]> compositionData = data.getMediumTypeData(newValue);

        // 테이블 뷰에 컬럼 추가
        tableView.getColumns().clear();
        if (!compositionData.isEmpty()) {
            String[] headers = compositionData.remove(0); // 첫 번째 배열은 헤더
            for (int i = 1; i < headers.length; i++) {
                final int index = i;
                TableColumn<String[], String> column = new TableColumn<>(headers[i]);
                column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[index]));
                tableView.getColumns().add(column);
            }
        }

        tableView.setItems(compositionData);
    }

}
