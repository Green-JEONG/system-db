package org.main.culturesolutioncalculation;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

import java.io.IOException;
import java.util.Map;

public class MacroResultController {
    private static MainController mainController;

    TableData tableData = mainController.getTableData();

    @FXML
    private TableView<ObservableList<String>> tableView;

    private ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }


    @FXML
    public void initialize() {

        Map<String, String> macroSettings = tableData.getMacroSettings();
        if (macroSettings == null) {
            initTableView();
        } else {
            showResultTable(macroSettings);
        }
    }

    private void initTableView() {
        // tableView 초기화
        if (tableView == null) {
            tableView = new TableView<>();
        }

        String[] columnTitles = {"(100 배액 기준)\n   비료염 종류", "분자식", "시비량", "단위", "NO3-N", "NH4-N", "P", "K", "Ca", "Mg", "SO4-S", "산도(pH)", "농도(EC)", "중탄산(HCO3)"};
        String[] rowTitles = {"설정농도(mM)", "시비농도(mM)", "질산칼륨", "질산칼슘(4수염)", "질산칼슘(10수염)", "제1인산암모늄", "제1인산칼륨", "황산마그네슘", "질산마그네슘", "질산암모늄(초안)",
                "황산암모늄(유안)", "염화암모늄(염안)", "황산칼륨", "염화칼륨", "염화칼슘", "중탄산칼륨", "수산화칼륨", "합계"};

        // 분자식에 들어갈 값
        String[] formula = {"", "", "KNO3", "Ca(NO3)2-4H2O", "5[Ca(NO3)2·2H2O]NH4NO3", "NH4H2PO4", "KH2PO4", "MgSO4-7H2O", "Mg(NO3)2-6H2O",
                "NH4NO3", "(NH4)2SO4", "NH4CI", "K2SO4", "KCI", "CaCI2·2H2O", "KHCO3", "KOH", ""};

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

        for (int i = 0; i < rowTitles.length; i++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(rowTitles[i]);
            row.add(formula[i]);
            for (int j = 2; j < columnTitles.length; j++) {
                row.add(""); // 빈 문자열로 초기화
            }
            data.add(row);
        }

        tableView.setItems(data);
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
//        TabPane tabPane = findTabPane(event);
//        if (tabPane != null) {
//            int currentIndex = tabPane.getSelectionModel().getSelectedIndex();
//            tabPane.getSelectionModel().select(currentIndex - 1);
//        }
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
